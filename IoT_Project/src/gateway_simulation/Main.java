package gateway_simulation;

import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;
import javax.swing.JFrame;
import java.beans.PropertyChangeEvent;


public class Main extends Thread {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("Main Thread : ");
        Kerberos kdc = new Kerberos();
        App app = new App(kdc);
        Gateway gateway = new Gateway();
        Thread appThread = new Thread(app);
        Thread loginThread = new Thread(app.login);
        Thread gatewayThread = new Thread(gateway);
        loginThread.start();
        appThread.start();
        gatewayThread.start();

        while(!app.login.ticketRetrieved)
        {
            try {
                gatewayThread.wait();
            }catch (Exception e){}
        }

        Message sgt = app.sgtRetrieval();
        gateway.checkMessage(sgt);
    }
}

