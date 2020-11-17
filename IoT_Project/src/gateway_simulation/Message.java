package gateway_simulation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Observable;

public class Message implements Cloneable{
    public String pub_key;
    protected PropertyChangeSupport propertyChangeSupport;
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
    String command;
    String custom;
    String update;

    public Object clone() throws
            CloneNotSupportedException
    {
        Message cloned = (Message)super.clone();
        return cloned;
    }

    public Message(){}

    public Message(String update) {
        this.update = update;
    }

    public Message(String command, String custom) {
        mNum = 7;
        this.command = command;
        this.custom = custom;
    }

   /* public Message(Message m) {
        this.mNum = Integer.valueOf(m.mNum);
        this.key = String.valueOf(m.key);
        this.ticket = new Ticket(m.ticket);
    }*/

    public Message(Message m) {
        this.mNum = m.mNum;
        this.ticket = new Ticket(m.ticket);
        this.auth = new Authenticator(m.auth);
        this.pub_key = m.pub_key;
    }

    public Message(boolean b) {
        update = "Possible intrusion detected - device quarantined";
        error = b;
    }

    public void displayContents() {
        System.out.println("Contents of Message " + mNum + ":");
        processing.processMed();
        System.out.print("  Key = " + key + " ||");
        processing.processMed();
        System.out.print(" Client ID = " + clientID + " ||");
        processing.processMed();
        System.out.print(" Server ID = " + serverID + " ||\n");
        processing.processMed();
        System.out.print("  Timestamp = " + timestamp + " ||");
        processing.processMed();
        System.out.print(" Lifetime = " + lifetime + " ||\n");
        processing.processMed();
        System.out.print(" Public Key = " + pub_key + " ||\n");
        processing.processMed();
        if (!containsTicket)
            System.out.print("      No Ticket ");
        if (!containsAuth) {
            System.out.print("    No Authenticator");
            System.out.println("\n");
        }
        if (containsTicket && containsAuth)
            System.out.print("\n");
        processing.processMed();
    }

    //called after failed authentication
    public void clear() {
        mNum = 0;
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

    private void createtimeStamps() {
        timestamp = String.valueOf(System.currentTimeMillis());
        lifetime = String.valueOf(System.currentTimeMillis() + 30000);
    }

    public Message createMessage1(String cID, String tgsID) {
        Message m = new Message();
        m.mNum = 1;
        m.clientID = cID;
        m.serverID = tgsID;
        m.containsTicket = false;
        m.containsAuth = false;
        m.createtimeStamps();
        m.displayContents();
        return m;
    }

    //MESSAGE 2 AS to Client
    public Message createMessage2(String keyC_TGS, String tgsID, Ticket ticketTGS) {
        Message m = new Message();
        m.clear();
        m.mNum = 2;
        m.key = keyC_TGS;
        m.serverID = tgsID;
        m.ticket = ticketTGS;
        m.containsTicket = true;
        m.containsAuth = false;
        m.createtimeStamps();
        m.displayContents();
        return m;
    }

    //MESSAGE 3 Client to TGS
    public Message createMessage3(String gatewayID, Ticket t, Authenticator auth) {
        Message m = new Message();
        m.mNum = 3;
        m.serverID = gatewayID;
        m.ticket = t;
        m.auth = auth;
        m.containsAuth = true;
        m.containsTicket = true;
        m.createtimeStamps();
        m.displayContents();
        return m
                ;
    }

    //MESSAGE 4 TGS to Client
    public Message createMessage4(String keyC_V, String gatewayID, Ticket ticketGateway) {
        Message m = new Message();
        m.mNum = 4;
        m.key = keyC_V;
        m.serverID = gatewayID;
        m.ticket = ticketGateway;
        m.containsTicket = true;
        m.containsAuth = false;
        m.createtimeStamps();
        m.displayContents();
        return m;
    }

    //MESSAGE 5 Client to Gateway
    public Message createMessage5(String pub, Ticket ticketGateway, Authenticator auth) {
        Message m = new Message();
        m.mNum = 5;
        m.pub_key = pub;
        m.ticket = ticketGateway;
        m.auth = auth;
        m.containsTicket = true;
        m.containsAuth = true;
        m.displayContents();
        return m;
    }

 /*   public Message createPublicKeyMessage(PublicKey key) {
        this.clear();
        mNum = 6;
        this.pk = key;
        displayContents();
        return
    }*/
}

   /* public void newMessage(String clientID, String serverID, String timestamp, Message m) {


            case 2:        propertyChangeSupport.firePropertyChange("M2",oldmNum, mNum); break;
            case 3:        propertyChangeSupport.firePropertyChange("M3",oldmNum, mNum); break;
            case 4:        propertyChangeSupport.firePropertyChange("M4",oldmNum, mNum); break;
            case 5:        propertyChangeSupport.firePropertyChange("M5",oldmNum, mNum); break;
        }
    }*/

