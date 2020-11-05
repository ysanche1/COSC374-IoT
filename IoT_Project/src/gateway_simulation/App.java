package gateway_simulation;

public class App extends Thread{
    Login login;
    Kerberos kdc;
    public App(){
        kdc = new Kerberos();
        //---create device control window after login or now and hide it
    }
}
