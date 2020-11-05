package gateway_simulation;

import java.sql.Timestamp;

public class Message {
    int mNum;
    String clientID;
    String timestamp;
    String lifetime;
    Ticket ticket;
    Authenticator auth;
    String serverID;
    String key;
    boolean ticketRetrieval = true; //checked after decryption of ticket-bearing message
    boolean containsTicket; //for displayContent()
    boolean containsAuth; //^
    boolean error = false; //set to true if bad info is given

    //sometimes you need one of these
    public Message(){

    }

    public void displayContents()
    {
        System.out.println("Contents of Message "+mNum+":");    processing.processMed();
        System.out.print("  Key = "+ key +" ||");               processing.processMed();
        System.out.print(" Client ID = "+ clientID +" ||");               processing.processMed();
        System.out.print(" Server ID = "+serverID+" ||\n");               processing.processMed();
        System.out.print("  Timestamp = "+timestamp+" ||");               processing.processMed();
        System.out.print(" Lifetime = "+lifetime+" ||\n");               processing.processMed();
        if(!containsTicket)
            System.out.print("      No Ticket ");
        if(!containsAuth){
            System.out.print("    No Authenticator");
            System.out.println("\n");}
            if(containsTicket && containsAuth)
                System.out.print("\n");
        processing.processMed();
    }

    //called after failed authentication
    public void clear(){
        mNum =-1;
        clientID = null;
        timestamp = null;
        lifetime = null;
        ticket = null;
        auth = null;
        serverID = null;
        key = null;
        containsTicket = false;
        containsAuth = false;
    }

    //lots of constructors for different messages

    //MESSAGE 1 Client to AS
    public Message(String cID, String tgsID, String timestamp){
        mNum = 1;
        this.clientID = cID;
        this.serverID = tgsID;
        this.timestamp = timestamp;
        containsTicket = false;
        containsAuth = false;
        displayContents();
    }

    //MESSAGE 2 AS to Client
    public Message(String keyC_TGS, String tgsID, Ticket ticketTGS, String timestamp, String lifetime ){
        mNum = 2;
        this.key = keyC_TGS;
        this.serverID = tgsID;
        this.ticket = ticketTGS;
        this.timestamp = timestamp;
        this.lifetime = lifetime;
        containsTicket = true;
        containsAuth = false;
        displayContents();
    }

    //MESSAGE 3 Client to TGS
    public Message(String gatewayID, Ticket t, Authenticator auth){
        mNum = 3;
        this.serverID = gatewayID;
        this.ticket = t;
        this.auth = auth;
        containsAuth = true;
        containsTicket = true;
        displayContents();
    }

    //MESSAGE 4 TGS to Client
    public Message(String keyC_V, String gatewayID, Ticket ticketGateway, String timestamp){
        mNum = 4;
        this.key = keyC_V;
        this.serverID = gatewayID;
        this.ticket = ticketGateway;
        this.timestamp = timestamp;
        containsTicket = true;
        containsAuth = false;
        displayContents();
    }

    //MESSAGE 5 Client to Gateway
    public Message(Ticket ticketGateway, Authenticator auth){
        mNum = 5;
        this.ticket = ticketGateway;
        this.auth = auth;
        containsTicket = true;
        containsAuth = true;
        displayContents();
    }
}