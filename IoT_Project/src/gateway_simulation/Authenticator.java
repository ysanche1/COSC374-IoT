package gateway_simulation;

import java.util.Date;

public class Authenticator{
    String key;
    String clientID;
    String clientAddress;
    String timestamp;

    public Authenticator(String key, String cID, String cAD, String l)
    {
        timestamp = l;
        this.key = key; clientID = cID;
        clientAddress = cAD;

    }
}

