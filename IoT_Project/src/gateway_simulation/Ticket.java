package gateway_simulation;

import java.sql.Time;
import java.sql.Timestamp;

public class Ticket {
    String key;
    String clientID;
    String clientAD;
    String serverID;
    String timestamp;
    String lifetime;

    public Ticket(String key, String clientID, String clientAD, String serverID, String timestamp, String lifetime){
        this.key = key; this.clientID = clientID; this.clientAD = clientAD;
        this.serverID = serverID; this.timestamp = timestamp; this.lifetime = lifetime;
    }
    public void displayContents(){
        System.out.println("Ticket contents :");              processing.processFast();
        System.out.print("  Key = "+key+" ||");              processing.processFast();
        System.out.print(" clientID = "+clientID+" ||");    processing.processFast();
        System.out.print(" clientAD = "+clientAD+" ||\n");   processing.processFast();
        System.out.print("  serverID = "+serverID+" ||");   processing.processFast();
        System.out.print(" timestamp = "+timestamp+" ||");   processing.processFast();
        System.out.print(" lifetime = "+lifetime+" ||\n\n");processing.processFast();
    }
}
