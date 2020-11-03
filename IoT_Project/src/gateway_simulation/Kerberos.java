package gateway_simulation;

import java.util.Date;

public class Kerberos {
    AuthServer as;
    TGS tgs;
    String TGS_AS_SHARED_KEY = "TGS_AS_KEY";
    Date date;

    public Kerberos() {
        System.out.println("Creating Kerberos Server");
        processing.processLong();
        as = new AuthServer();
        tgs = new TGS();
        date = new Date();
        System.out.println("Kerberos Server Created\n");
    }
}