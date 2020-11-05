package gateway_simulation;

import java.util.concurrent.locks.Lock;

public class Gateway{
    Kerberos kdc;
    App app;
    String gatewayID = "gateway374";
    String sharedKey;
    String keyTGS_V = "TGS_V_SHAREDKEY+";


    public Gateway(){
        kdc = new Kerberos();
        app = new App();

    }
}