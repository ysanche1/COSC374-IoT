package gateway_simulation;

import java.util.Date;

public class TGS {
    String tgsID = "TGS374";
    String keyAS_TGS = "TGS_AS_SHAREDKEY";
    String keyC_TGS;
    String keyC_V;
    String keyTGS_V = "TGS_V_SHAREDKEY+";
    AESAlgorithm aes;
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
        if (Long.valueOf(m.ticket.lifetime) < Long.valueOf(m.ticket.timestamp)
                | Long.valueOf(m.ticket.lifetime) < Long.valueOf(m.auth.timestamp)) {
            System.out.println("    EXPIRED TICKET - RE-AUTHORIZATION REQUIRED");
            m.clear();
            m.error = true;
        } else {
            System.out.println("    ******TIMESTAMP APPROVED******");
            m.ticket.displayContents();
            createReply(m);
        }
        return sgtMessage;
    }

    private Message createReply(Message m) throws Exception {
        keyC_V = m.ticket.key;
        date = new Date();
        String timestamp = String.valueOf(date.getTime());
        sgt = new Ticket(keyC_V, m.ticket.clientID, m.ticket.clientAD, m.serverID,
                timestamp, timestamp+3000);
        sgtMessage = new Message(keyC_V, m.serverID, sgt, timestamp);
        aes = new AESAlgorithm(keyC_V, keyTGS_V);
        aes.encryptMessage(sgtMessage);
        System.out.println("	******MESSAGE 4 SENT**********\n");
        return sgtMessage;
    }

}

