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
        message3 = aes.decryptMessage(m);
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
            createReply(m);
        }
        return sgtMessage;
    }

    // construct and encrypt message 4
    private Message createReply(Message m) throws Exception {
        date = new Date();
        String timestamp = String.valueOf(date.getTime());
        sgt = new Ticket(keyC_V, m.ticket.clientID, m.ticket.clientAD, m.serverID, //service granting ticket
                timestamp, timestamp+3000);
        sgt.displayContents();
        sgtMessage = new Message(keyC_V, m.serverID, sgt, timestamp); // message 4
        aes = new AESAlgorithm(keyC_TGS, keyTGS_V);
        System.out.println("	*********ENCRYPTING MESSAGE 4*********\n");
        processing.processLong();processing.processLong();
        aes.encryptMessage(sgtMessage);
        System.out.println("	*********MESSAGE 4 ENCRYPTED**********\n");
        processing.processMed();
        sgtMessage.displayContents();
        sgtMessage.ticket.displayContents();
        System.out.println("	******MESSAGE 4 SENT*************\n");
        processing.processMed();
        return sgtMessage;
    }

}

