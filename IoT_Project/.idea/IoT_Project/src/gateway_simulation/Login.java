package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Date;

public class Login extends JFrame {

    private JPanel mainPanel;
    private JTextField clientIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel welcomeMessage;
    private JTextArea statusMessageBox;

    AESAlgorithm aes;
    String clientID;
    String password;
    String padding = "12345678";
    String sharedKey;
    String timestamp;
    Date date;
    String tgsID = "TGS374";
    String gatewayID = "gateway374";
    Message message;
    Ticket ticket;

    public Login(String name, Kerberos kdc) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle(name);
        setContentPane(mainPanel);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        getRootPane().setDefaultButton(loginButton);
        int frameX = (screenSize.width / 2) - (getWidth() / 2);
        int frameY = screenSize.height / 4;
        setLocation(frameX, frameY);
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("    ******LOGIN BUTTON PRESSED******\n");
                clientID = clientIdField.getText();
                password = String.copyValueOf(passwordField.getPassword());
                setVisible(false);
                clientIdField.setText("");
                passwordField.setText("");
                try {
                    authorizationExchangeInit(kdc);
                } catch (Exception interruptedException) {
                    interruptedException.printStackTrace();
                }


                {

                }
            }
        });
    }


    public void authorizationExchangeInit(Kerberos kdc) throws Exception        //MESSAGE 1
    {
        date = new Date();
        timestamp = String.valueOf(date.getTime());
        message = new Message(clientID, tgsID, timestamp);
        System.out.println("	******MESSAGE 1 SENT**********\n");
        message = kdc.as.handleMessage(message);//GET MESSAGE 2;
        if (message.error)
            setVisible(true);
        else
            attemptDecryption(kdc, password+padding);
    }

    public void ticketGrantingServiceExchangeInit(Kerberos kdc) throws Exception {
        message = new Message(gatewayID, ticket, createAuthenticator());
        System.out.println("	**********MESSAGE 3 SENT******\n");
        message = kdc.tgs.handleMessage(message); //Get message 4
        if (message.error)
            setVisible(true);
        else
            sharedKey = message.key;
            attemptDecryption(kdc, sharedKey);
    }

    public void attemptDecryption(Kerberos kdc, String key) throws Exception {
        if (kdc.as.attemptsRemaining) {
            System.out.println("    ******ATTEMPTING DECRYPTION******\n");
            aes = new AESAlgorithm(key);
            message = aes.decryptMessage(message);
            if (!message.ticketRetrieval.equals("success")) {
                kdc.as.failureNotficiation("password");
                message.clear();
                message.displayContents();
                setVisible(true);
            } else {
                System.out.println("    ******DECRYPTION SUCCESSFUL******\n");
                message.displayContents();
                ticket = message.ticket;
                sharedKey = message.key;
                ticketGrantingServiceExchangeInit(kdc);
            }
        }
    }

    private Authenticator createAuthenticator() {
        timestamp = String.valueOf(date.getTime());
        AESAlgorithm authAES = new AESAlgorithm(sharedKey);
        Authenticator auth = new Authenticator(clientID, "CLIENT_ADDRESS", timestamp);
        authAES.encryptAuthenticator(auth);
        return auth;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
