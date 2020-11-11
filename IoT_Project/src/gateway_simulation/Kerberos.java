package gateway_simulation;

import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;

public class Kerberos implements PropertyChangeListener {
    AuthServer as;
    TGS tgs;
    String TGS_AS_SHARED_KEY = "TGS_AS_KEY";
    Date date;
    RSAKeyPairGenerator rsa;
    PublicKey gatewayPK;
    public Kerberos(Gateway gateway) throws NoSuchAlgorithmException {
        rsa = new RSAKeyPairGenerator();
        gatewayPK = rsa.getPublicKey();
        gateway.publicKey = gatewayPK;
        gateway.privateKey= rsa.getPrivateKey();

        System.out.println("Creating Kerberos Server");
        processing.processLong();
        as = new AuthServer();
        tgs = new TGS();
        date = new Date();
        System.out.println("Kerberos Server Created\n");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("M1")) {
            try {
                as.handleMessage((Message) evt.getNewValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

