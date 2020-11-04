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
        authMessage = new Message();
    }

    //deals with message 2 from client
    public Message handleMessage(Message m) throws Exception {
        if (attempts-- <= 0)  //check if user is locked out
        {
            attemptsRemaining = false;
            failureNotification("locked");
        } else{
                if (m.clientID.equals(this.clientID) == false) { //if not, check userid
                    processing.processMed();
                    failureNotification("clientID");
                } else{
                        System.out.print("	CLIENT ID OK	");                    processing.processMed();
                        if (m.serverID.equals(this.tgsID) == false) { //check the id of the target TGS
                            processing.processMed();
                            failureNotification("tgsID");
                        }
                        else System.out.print("	TGS ID OK	\n\n");                    processing.processMed();
                        createReply();
                    }
            }
        return authMessage;
    }

    public Message createReply() throws Exception {
        System.out.println("	******PRE-AUTH PASSED*********\n");
        date = new Date();
        timestamp = String.valueOf(date.getTime());
        lifetime = String.valueOf(date.getTime() + 30000); // 30 second lifetime
        ticketGrantingTicket = new Ticket(keyC_TGS, clientID, "CLIENT_ADDRESS", tgsID, timestamp, lifetime);
        authMessage = new Message(keyC_TGS, tgsID, ticketGrantingTicket, timestamp, lifetime);
        aes = new AESAlgorithm(password + padding, keyAS_TGS);
        System.out.println("	*********ENCRYPTING MESSAGE 2*********\n");
        processing.processLong();        processing.processLong();
        aes.encryptMessage(authMessage);
        System.out.println("	*********MESSAGE 2 ENCRYPTED**********\n");
        processing.processMed();
        authMessage.displayContents();
        processing.processMed();

        System.out.println("	*********MESSAGE 2 SENT**********\n");
        processing.processLong();
        return authMessage;
    }

    public void failureNotification(String error)
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
