package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setTitle(name);
        setContentPane(mainPanel);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        getRootPane().setDefaultButton(loginButton);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // put the window in a nice spot
        int frameX = (screenSize.width / 2) - (getWidth() / 2);             //
        int frameY = (screenSize.height / 2) - (getHeight());;                                 //
        setLocation(frameX, frameY);                                        //
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get text in fields and close the window
                System.out.println("    ******LOGIN BUTTON PRESSED******\n");
                processing.processMed();
                clientID = clientIdField.getText();
                password = String.copyValueOf(passwordField.getPassword());
                setVisible(false);
                clientIdField.setText("");
                passwordField.setText("");
                try {
                    authorizationExchangeInit(kdc); //begin message 1
                } catch (Exception interruptedException) {
                    interruptedException.printStackTrace();
                }


                {

                }
            }
        });
    }

    //send message 1 and be judged by Authorization Server
    public void authorizationExchangeInit(Kerberos kdc) throws Exception        //MESSAGE 1
    {
        date = new Date();
        timestamp = String.valueOf(date.getTime());
        message = new Message(clientID, tgsID, timestamp);
        System.out.println("	******MESSAGE 1 SENT**********\n");
        processing.processMed();
        message = kdc.as.handleMessage(message);//get message 2;
        if (message.error)     //error is set if clientID or tgsID are rejected
            setVisible(true);  //bring window back for another attempt
        else
            attemptDecryption(kdc, password+padding); // try to get ticket

    }

    //same as above but message 3 but with a call to createAuthenticator()
    public void ticketGrantingServiceExchangeInit(Kerberos kdc) throws Exception {
        message = new Message(gatewayID, ticket, createAuthenticator());
        System.out.println("	**********MESSAGE 3 SENT******\n");
        processing.processMed();
        message = kdc.tgs.handleMessage(message); //Get message 4
        if (message.error)
            setVisible(true);
        else
        attemptDecryption(kdc, sharedKey);
    }

    public void attemptDecryption(Kerberos kdc, String key) throws Exception {
        if (kdc.as.attemptsRemaining) {
            System.out.println("    ******ATTEMPTING DECRYPTION******\n");
            processing.processLong();               processing.processLong();
                aes = new AESAlgorithm(key);
                message = aes.decryptMessage(message);
                if (!message.ticketRetrieval.equals("success")) { // key is user's password
                    kdc.as.failureNotification("password");  // decryption failure = wrong password
                    message.clear(); // reset message
                    message.displayContents(); // check that message is reset
                    setVisible(true); // bring login window back
                } else {
                    System.out.println("    ******DECRYPTION SUCCESSFUL******\n");
                    processing.processMed();
                    message.displayContents();
                    ticket = message.ticket;
                    sharedKey = message.key;
                    switch(message.mNum)
                    {
                        case 2: ticketGrantingServiceExchangeInit(kdc); break;
                        case 4: message.ticket.displayContents(); break;
                                    //this is next
                    }
                }
        }
        }

        //yes that is what it does
    private Authenticator createAuthenticator() throws Exception {
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
