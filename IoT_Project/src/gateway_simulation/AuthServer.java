package gateway_simulation;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthServer {
    String clientID = "user";
    String password = "password";
    String padding = "12345678";
    AESAlgorithm aes;
    String tgsID = "TGS374";
    String keyAS_TGS = "TGS_AS_SHAREDKEY";
    String keyC_TGS = "CLIENT_TGS_KEY++";
    Message authMessage;
    Ticket ticketGrantingTicket;
    String timestamp;
    String lifetime;
    Date date;
    int attempts = 3;
    boolean attemptsRemaining = true;
    public AuthServer() {
        System.out.println("AS Created");
        processing.processMed();
        authMessage = new Message();
    }


    public Message handleMessage(Message m) throws Exception {
        if (attempts-- <= 0)
        {
            attemptsRemaining = false;
            failureNotficiation("locked");
        }
        else
            switch (m.mNum) {
                case 1:
                    if (m.clientID.equals(this.clientID) == false) {
                        processing.processMed();
                        failureNotficiation("clientID");
                    }
                    else {
                        System.out.print("	CLIENT ID OK	");
                        if (m.serverID.equals(this.tgsID) == false) {
                            processing.processMed();
                            failureNotficiation("tgsID");
                        } else System.out.print("	TGS ID OK	\n\n");
                        createReply();
                    }
            }
        return authMessage;
    }

    public Message createReply() throws Exception {
        System.out.println("	******PRE-AUTH PASSED*********\n");
        date = new Date();
        timestamp = String.valueOf(date.getTime());
        lifetime = String.valueOf(date.getTime() + 30000);
        ticketGrantingTicket = new Ticket(keyC_TGS, clientID, "CLIENT_ADDRESS", tgsID, timestamp, lifetime);
        authMessage = new Message(keyC_TGS, tgsID, ticketGrantingTicket, timestamp, lifetime);
        aes = new AESAlgorithm(password + padding, keyAS_TGS);

        aes.encryptMessage(authMessage);
        System.out.println("	*********MESSAGE 2 ENCRYPTED**********\n");
        authMessage.displayContents();

        System.out.println("	*********MESSAGE 2 SENT**********\n");
        return authMessage;
    }

    public void failureNotficiation(String error)
    {
        authMessage.error = true;
        switch(error){
            case "clientID": System.out.println("	BAD CLIENT ID - "+attempts+" attempt"+ attemptsGrammar()+ " remaining\n"); break;
            case "password": System.out.println("	INCORRECT PASSWORD - "+attempts+" attempt"+ attemptsGrammar()+ " remaining\n"); break;
            case "tgsID": 	 System.out.println("	BAD TGS ID - (call customer service?)\n"); break;
            case "locked": 	 System.out.println("	LOCKED - too many failed attempts :/\n");
        }

    }

    private String attemptsGrammar() {
        String s = "";
        if( attempts != 1)
            s = "s";
        return s;
    }
}
