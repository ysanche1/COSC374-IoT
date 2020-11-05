package gateway_simulation;

import javax.swing.JFrame;

public class Main extends JFrame {
    public Main() {
    }

    public static void main(String[] args) {
   // Gateway gateway = new Gateway(); create thread here?

       Kerberos kdc = new Kerberos();
        new Login("Login", kdc);
    }
}
