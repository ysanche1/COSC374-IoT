package gateway_simulation;

public class Authenticator{
    String key;
    String clientID;
    String clientAddress;
    String timestamp;

    public Authenticator(String cID, String cAD, String l)
    {
        timestamp = l;
        clientID = cID;
        clientAddress = cAD;
    }
    public Authenticator(Authenticator a)
    {
        this.timestamp = a.timestamp;
        this.clientID = a.clientID ;
        this.clientAddress = a.clientAddress;
    }
}

