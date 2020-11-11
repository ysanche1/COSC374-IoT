package gateway_simulation;

import java.beans.PropertyChangeSupport;
import java.security.PublicKey;

public class App implements Runnable {
    Login login;
    Kerberos kdc;
    PublicKey gwPublicKey;
    boolean loggedIn;

    public App(Kerberos kdc) {
        this.kdc = kdc;
        login = new Login(kdc);
    }

    public Message sgtRetrieval() {
        System.out.println("SGT CONTENTS: ");
        login.ticket.displayContents();
        return login.message;
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


