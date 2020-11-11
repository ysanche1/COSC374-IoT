package gateway_simulation;

import java.util.Date;

public class TGS {
    String tgsID = "TGS374";
    String keyAS_TGS = "TGS_AS_SHAREDKEY";
    String keyC_TGS;
    String keyC_V = "CLIENTGATEWAYKEY";
    String keyTGS_V = "TGS_V_SHAREDKEY+";
    AESAlgorithm aes;
    Message message3;
    Message sgtMessage;
    Ticket sgt;
    Date date;
    public TGS() {
        System.out.println("TGS Created");
        processing.processMed();
    }

    // get key shared with client out of the encrypted ticket within the authenticator
    public Message handleMessage(Message m) throws Exception {
        aes = new AESAlgorithm(keyAS_TGS);
        aes.decryptMessage(m);
        System.out.println("    ********TICKET RETRIEVED******\n");
        processing.processFast();
        processing.processMed();
        if (Long.parseLong(m.ticket.lifetime) < Long.parseLong(m.ticket.timestamp)  //timestamp check
                | Long.parseLong(m.ticket.lifetime) < Long.parseLong(m.auth.timestamp)) {
            System.out.println("    EXPIRED TICKET - RE-AUTHORIZATION REQUIRED");
            m.clear();
            m.error = true;
        } else {
            System.out.println("    ******TIMESTAMP APPROVED******\n");
            processing.processMed();
            m.ticket.displayContents();
            keyC_TGS = m.ticket.key; //TGS and client now in possession of shared key
           m = createReply(m);
        }
        return m;
    }

    // construct and encrypt message 4
    private Message createReply(Message m) throws Exception {
        m.ticket = new Ticket(keyC_V, m.ticket.clientID, m.ticket.clientAD, m.serverID);
        m.displayContents();
        m=m.createMessage4(keyC_V, m.serverID, m.ticket); // message 4
        System.out.println("\nTICKET TIMESTAMP " +m.ticket.timestamp+"\n");
        aes = new AESAlgorithm(keyC_TGS, keyTGS_V);
        System.out.println("	*********ENCRYPTING MESSAGE 4*********\n");
        processing.processLong();processing.processLong();
        aes.encryptMessage(m);
        System.out.println("	*********MESSAGE 4 ENCRYPTED**********\n");
        processing.processMed();
        m.displayContents();
        m.ticket.displayContents();
        System.out.println("	******MESSAGE 4 SENT*************\n");
        processing.processMed();
        return m;
    }

}

