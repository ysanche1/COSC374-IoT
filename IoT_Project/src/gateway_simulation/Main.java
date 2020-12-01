package gateway_simulation;


public class Main extends Thread {
    public static Gateway gateway = new Gateway();
    public static ThermostatControl tc = new ThermostatControl();
    public static ProviderCloud tcCloud = new ProviderCloud();
    public static App app;
    public static void main(String[] args) throws Exception {
        gateway.cloudMonitor.monitor();
        Kerberos kdc = new Kerberos(gateway);
        app = new App(kdc);
        Main.gateway.cloudMonitor.monitor();
    }
}