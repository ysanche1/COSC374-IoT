package gateway_simulation;

import java.security.PrivateKey;

public class Broker {
    private final RSAAlgorithm rsaD;
    private final RSAAlgorithm rsaEApp;


    public Broker( RSAAlgorithm rsaEApp, RSAAlgorithm rsaD) {
        this.rsaD = rsaD;
        this.rsaEApp = rsaEApp;
    }

    public void publish(Message m) throws Exception {
    m.update = rsaEApp.encrypt(rsaD.decrypt(m.update));
    Main.tc.subscribe(m);
}


}
