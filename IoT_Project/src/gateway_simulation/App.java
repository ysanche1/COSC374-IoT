package gateway_simulation;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class App{
    Login login;
    Kerberos kdc;
    PublicKey gwPublicKey;
    boolean loggedIn;
    PublicKey appPublickKey;
    PublicKey gatewayPublicKey;
    PrivateKey appPrivateKey;
    RSAAlgorithm rsaE;
    RSAAlgorithm rsaD;
    RSAKeyPairGenerator rsaK;

    public App(Kerberos kdc) throws NoSuchAlgorithmException {
        rsaK = new RSAKeyPairGenerator();
        appPublickKey = rsaK.getPublicKey();
        appPrivateKey = rsaK.getPrivateKey();
        rsaD = new RSAAlgorithm(appPrivateKey);
        this.kdc = kdc;
        login = new Login(kdc);
    }


    }


