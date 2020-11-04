package gateway_simulation;

import java.sql.Timestamp;
import java.util.Date;

public class Authenticator{
    String key;
    String clientID;
    String clientAddress;
    String timestamp;
    Date date;

    public Authenticator(String key, String cID, String cAD)
    {
        this.key = key; this.clientID = cID;
        this.clientAddress = cAD;
        date = new Date(); timestamp = String.valueOf(date.getTime());


    }
}

