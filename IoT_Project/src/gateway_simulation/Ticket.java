package gateway_simulation;

import javax.swing.*;

//Class representing tickets

public class Ticket {
    String key;
    String clientID;
    String clientAD;
    String serverID;
    String timestamp;
    String lifetime;

    public Ticket(String key, String clientID, String clientAD, String serverID){
        this.key = key; this.clientID = clientID; this.clientAD = clientAD;
        this.serverID = serverID; createTimeStamps();
    }

    public Ticket(Ticket ticket) {
        this.key = ticket.key;
        this.clientID = ticket.clientID;
        this. clientAD = ticket.clientAD;
        this.serverID = ticket.serverID;
        this.timestamp = ticket.timestamp;
        this.lifetime = ticket.lifetime;
    }

    private void createTimeStamps()
    {
        timestamp = String.valueOf(System.currentTimeMillis());
        lifetime = String.valueOf(System.currentTimeMillis() + 30000);
    }

    public void displayContents(){
        System.out.println("Ticket contents :");              processing.processMed();
        System.out.print("  Key = "+key+" ||");             processing.processMed();
        System.out.print(" clientID = "+clientID+" ||");   processing.processMed();
        System.out.print(" clientAD = "+clientAD+" ||\n");   processing.processMed();
        System.out.print("  serverID = "+serverID+" ||");   processing.processMed();
        System.out.print(" timestamp = "+timestamp+" ||");   processing.processMed();
        System.out.print(" lifetime = "+lifetime+" ||\n\n");processing.processMed();
    }
}
