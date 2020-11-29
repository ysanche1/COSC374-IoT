package gateway_simulation;

import java.util.Date;

public class AuthServer {
    String clientID = "user";     //database of all registered users
    String password; // and their passwords
    AESAlgorithm aes;
    String tgsID = "TGS374";  //ID of the TGS, used in verifying message 1
    String keyAS_TGS = "TGS_AS_SHAREDKEY"; //secret key shared with TGS
    String keyC_TGS = "CLIENT_TGS_KEY++"; //secret key to be shared by client and TGS
    Ticket ticketGrantingTicket; //ticket that client will show to TGS to receive the service-granting ticket
    Date date; //for creating timestamps
    int attempts = 3; //for login lockout
    boolean attemptsRemaining = true;//^
    UserDB userDB;

    public AuthServer() {
        userDB = new UserDB();
        System.out.println("AS Created");
        processing.processMed();
    }

    private void padPassword(){
        int pl = password.length();  //key needs to be 16 bytes, 1234567890 repeating as padding
        int j = 1;
        for (int i = 1; i <= 16 - pl; i++) {
            if (j == 10)
                j = 0;
            password += j;
            j++;
        }
    }

    //deals with message 2 from client
    public Message handleMessage(Message m) throws Exception {
        if (attempts-- <= 0)  //check if user is locked out
        {
            attemptsRemaining = false;
            failureNotification("locked", m);
        } else{
                password = userDB.checkUserDB(m.clientID);
                if (password.equals("ERROR")) { //if not, retrieve password if user ID found in database
                    processing.processMed();
                    failureNotification("clientID", m);
                } else{
                        System.out.print("	CLIENT ID OK	");                    processing.processMed();
                        if (!m.serverID.equals(this.tgsID)) { //check the id of the target TGS
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
        padPassword();
        aes = new AESAlgorithm(password, keyAS_TGS);
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
