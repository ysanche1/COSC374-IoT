package gateway_simulation;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

//This class holds all of the smart phone asymmetric keys and algorithms
//creates login window which begins the process of accessing the gateway

public class App{
    Login login;
    Kerberos kdc;
    PublicKey appPublickKey;
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


