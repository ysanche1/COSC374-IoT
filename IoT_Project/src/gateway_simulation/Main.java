package gateway_simulation;


public class Main extends Thread {
    public static Gateway gateway = new Gateway();
    public static ThermostatControl tc;
    public static void main(String[] args) throws Exception {
        String sharedKey;
        System.out.print("Main Thread : ");
        Kerberos kdc = new Kerberos(gateway);
        App app = new App(kdc);
        Thread appThread = new Thread(app);
        Thread loginThread = new Thread(app.login);
        loginThread.start();
        appThread.start();



        System.out.println("Awaiting User Verification");
        while (!app.login.ticketRetrieved) {
            Thread.sleep(100);
        }

        Message sgt = app.sgtRetrieval();
        sharedKey = sgt.key;
        gateway.receiveSGT(sgt);
        sgt.ticket.displayContents();
        tc = new ThermostatControl(sharedKey);
    }

}

