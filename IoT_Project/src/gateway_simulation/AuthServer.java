package gateway_simulation;

import java.util.Date;

public class AuthServer {
    String clientID = "user";     //database of all registered users
    String password = "password"; // and their passwords
    String padding = "12345678";  //AES key needs to be 16 bits and "password" is 8 letters
    AESAlgorithm aes;
    String tgsID = "TGS374";  //ID of the TGS, used in verifying message 1
    String keyAS_TGS = "TGS_AS_SHAREDKEY"; //secret key shared with TGS
    String keyC_TGS = "CLIENT_TGS_KEY++"; //secret key to be shared by client and TGS
    Message authMessage;
    Ticket ticketGrantingTicket; //ticket that client will show to TGS to receive the service-granting ticket
    String timestamp;
    String lifetime;
    Date date; //for creating timestamps
    int attempts = 3; //for login lockout
    boolean attemptsRemaining = true;//^


    public AuthServer() {
        System.out.println("AS Created");
        processing.processMed();
    }

    //deals with message 2 from client
    public Message handleMessage(Message m) throws Exception {
        if (attempts-- <= 0)  //check if user is locked out
        {
            attemptsRemaining = false;
            failureNotification("locked", m);
        } else{
                if (m.clientID.equals(this.clientID) == false) { //if not, check userid
                    processing.processMed();
                    failureNotification("clientID", m);
                } else{
                        System.out.print("	CLIENT ID OK	");                    processing.processMed();
                        if (m.serverID.equals(this.tgsID) == false) { //check the id of the target TGS
                            processing.processMed();
                            failureNotification("tgsID", m);
                        }
                        else System.out.print("	TGS ID OK	\n\n");                    processing.processMed();
                        m =createReply(m);
                    }
            }
        return m;
    }

    public Message createReply(Message m) throws Exception {
        System.out.println("	******PRE-AUTH PASSED*********\n");
        date = new Date();
        ticketGrantingTicket = new Ticket(keyC_TGS, clientID, "CLIENT_ADDRESS", tgsID);
        m = m.createMessage2(keyC_TGS, tgsID, ticketGrantingTicket);
        aes = new AESAlgorithm(password + padding, keyAS_TGS);
        System.out.println("	*********ENCRYPTING MESSAGE 2*********\n");
        processing.processLong();        processing.processLong();
        aes.encryptMessage(m);
        System.out.println("	*********MESSAGE 2 ENCRYPTED**********\n");
        processing.processMed();
        m.displayContents();
        processing.processMed();

        System.out.println("	*********MESSAGE 2 SENT**********\n");
        processing.processLong();
        return m;
    }

    public void failureNotification(String error, Message m)
    {
        m.error = true;
        switch(error){
            case "clientID": System.out.println("	BAD CLIENT ID - "+attempts+" attempt"+ attemptsGrammar()+ " remaining\n"); break;
            case "password": System.out.println("	INCORRECT PASSWORD - "+attempts+" attempt"+ attemptsGrammar()+ " remaining\n"); break;
            case "tgsID": 	 System.out.println("	BAD TGS ID - (call customer service?)\n"); break;
            case "locked": 	 System.out.println("	LOCKED - too many failed attempts :/\n");
        }

    }
    // one attempt, two attempts, etc..
    private String attemptsGrammar() {
        String s = "";
        if( attempts != 1)
            s = "s";
        return s;
    }
}
