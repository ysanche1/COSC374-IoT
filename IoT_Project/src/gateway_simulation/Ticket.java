package gateway_simulation;

import javax.swing.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Ticket {
    String key;
    String clientID;
    String clientAD;
    String serverID;
    String timestamp;
    String lifetime;
    Boolean isSGT = false;
    Date date;
    public Ticket(String key, String clientID, String clientAD, String serverID){
        this.key = key; this.clientID = clientID; this.clientAD = clientAD;
        this.serverID = serverID; createTimeStamps();
        System.out.println(Thread.currentThread());
        System.out.println("\nTICKET TIMESTAMP" +timestamp+"\n");
    }

    public Ticket(Ticket ticket) {
        key = ticket.key;
        clientID = ticket.clientID;
        clientAD = ticket.clientAD;
        serverID = ticket.serverID;
    }

    private void createTimeStamps()
    {
        timestamp = String.valueOf(System.currentTimeMillis());
        lifetime = String.valueOf(System.currentTimeMillis() + 2000);
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
    public void displayContents(JLabel jl){

        StringBuilder sb = new StringBuilder();

        sb.append("Ticket contents :\n  ");
        sb.append("  Key = "+key+" ||");
        sb.append(" clientID = "+clientID+" ||");
        sb.append(" clientAD = "+clientAD+" ||\n");
        sb.append("  serverID = "+serverID+" ||");
        sb.append(" timestamp = "+timestamp+" ||");
        sb.append(" lifetime = "+lifetime+" ||\n\n");

        jl.setText(sb.toString());
    }
}
