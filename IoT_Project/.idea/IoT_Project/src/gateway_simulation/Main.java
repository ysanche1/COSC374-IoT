package gateway_simulation;

import javax.swing.JFrame;

public class Main extends JFrame {



	public static void main(String[] args) {
		Kerberos kdc = new Kerberos();
		Login login = new Login("Login", kdc);

	}
}
