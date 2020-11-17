package gateway_simulation;

import java.beans.PropertyChangeSupport;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class App implements Runnable {
    Login login;
    Kerberos kdc;
    PublicKey gwPublicKey;
    boolean loggedIn;

    public App(Kerberos kdc) throws NoSuchAlgorithmException {
        this.kdc = kdc;
        login = new Login(kdc);
    }




    @Override
    public void run() {
        System.out.println("App Verifying USER");
        while(!loggedIn)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        }
    }


