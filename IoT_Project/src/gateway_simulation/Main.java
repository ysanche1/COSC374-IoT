package gateway_simulation;


public class Main extends Thread {
    public static Gateway gateway = new Gateway();
    public static ThermostatControl tc;
    public  static AttackSim atk = new AttackSim();
    public static void main(String[] args) throws Exception {
        String sharedKey;
        System.out.print("Main Thread : ");
        Kerberos kdc = new Kerberos(gateway);
        Login login = new Login(kdc);
        Thread loginThread = new Thread(login);
        loginThread.start();


    }

}

