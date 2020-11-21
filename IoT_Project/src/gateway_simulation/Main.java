package gateway_simulation;


public class Main extends Thread {
    public static Gateway gateway = new Gateway();
    public static ThermostatControl tc;
    public static App app;
    public static ThermostatCloud tcCloud = new ThermostatCloud();
    public static void main(String[] args) throws Exception {
        System.out.print("Main Thread : ");
        Kerberos kdc = new Kerberos(gateway);
        app = new App(kdc);
    }
}