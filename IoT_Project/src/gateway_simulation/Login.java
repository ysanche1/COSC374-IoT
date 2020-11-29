package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

// This class creates the login window and calls the various kerberos methods
// Execution ends with successful authentication of the service-granting-ticket

public class Login extends JFrame implements Runnable {
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
    String sharedKey;
    String tgsID = "TGS374";
    String gatewayID = "gateway374";
    Ticket ticket;
    Boolean ticketRetrieved = false;
    Message message;


    public Login(Kerberos kdc) {
        setTitle("Gateway Login");
        setContentPane(mainPanel);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        getRootPane().setDefaultButton(loginButton);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // put the window in a nice spot
        int frameX = (screenSize.width / 2) - (getWidth() / 2);             //
        int frameY = (screenSize.height / 2) - (getHeight());
        ;                                 //
        setLocation(frameX, frameY);                                        //
        setVisible(true);
        Main.gateway.cloudMonitor.monitor();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                //get text in fields and close the window
                System.out.println("    ******LOGIN BUTTON PRESSED******\n");
                clientID = clientIdField.getText();
                password = String.copyValueOf(passwordField.getPassword());
                setVisible(false);
                clientIdField.setText("");
                passwordField.setText("");
                processing.processMed();
                try {
                    authorizationExchangeInit(kdc);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void padPassword(){
        int pl = password.length();  //key needs to be 16 bytes, 1234567890 repeating as padding
        int j = 1;
        for (int i = 1; i <= 16 - pl; i++) {
            if (j == 10)
                j = 0;
            password += j;
            j++;
        }
    }

    //send message 1 to Authorization Server
    public synchronized void authorizationExchangeInit(Kerberos kdc) throws Exception        //MESSAGE 1
    {
        message = new Message();
        message = message.createMessage1(clientID, tgsID);
        System.out.println("	******MESSAGE 1 SENT**********\n");
        processing.processMed();
        message = kdc.as.handleMessage(message);//get message 2;
        if (message.error)     //error is set if clientID or tgsID are rejected
            setVisible(true);  //bring window back for another attempt
        else {
            padPassword();
            attemptDecryption(kdc, password);
        } // try to get ticket from message 2
    }

    // send message 3 to ticket-granting-server, create authenticator 1
    public synchronized void ticketGrantingServiceExchangeInit(Kerberos kdc) throws Exception {
        message = message.createMessage3(gatewayID, ticket, createAuthenticator());
        System.out.println("	**********MESSAGE 3 SENT******\n");
        processing.processMed();
        message = kdc.tgs.handleMessage(message); //Get message 4
        if (message.error)
            setVisible(true);
        else
            attemptDecryption(kdc, sharedKey);
    }

    // called for messages 2 and 4 to retrieve tickets and begin next phase of login
    public synchronized void attemptDecryption(Kerberos kdc, String key) throws Exception {
        if (kdc.as.attemptsRemaining) {
            System.out.println("    ******ATTEMPTING DECRYPTION******\n");
            processing.processLong();
            processing.processLong();
            aes = new AESAlgorithm(key);
            try {
                message = aes.decryptMessage(message);
            } catch (Exception e) {
                kdc.as.failureNotification("password", message);  // decryption failure = wrong password
                message.clear(); // reset message
                message.displayContents(); // check that message is reset
                message.ticketRetrieval = false;
                setVisible(true); // bring login window back
            }
            if (message.ticketRetrieval) {
                System.out.println("    ******DECRYPTION SUCCESSFUL******\n");
                processing.processMed();
                message.displayContents();
                ticket = message.ticket;
                sharedKey = message.key;
                switch (message.mNum) {
                    case 2:
                        try {
                            ticketGrantingServiceExchangeInit(kdc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        message.ticket.displayContents();
                        aes = new AESAlgorithm(sharedKey);
                        String pub_str = Main.app.rsaK.keyToStr(Main.app.appPublickKey);
                        message = message.createMessage5(aes.encrypt(pub_str), message.ticket, createAuthenticator());
                        ticketRetrieved = true;
                        Message finalMessage = Main.gateway.receiveSGT(message);
                        Main.tc.initialize(clientID, sharedKey, message, finalMessage);
                        Main.gateway.loggedIn = true;
                        break;
                }
            }
        }
    }

    // create a new authenticator
    private Authenticator createAuthenticator() throws Exception {
        Authenticator auth = new Authenticator(clientID, "CLIENT_ADDRESS", String.valueOf(System.currentTimeMillis()));
        AESAlgorithm authAES = new AESAlgorithm(sharedKey);
        // System.out.println("\nAUTH TIMESTAMP " + auth.timestamp + "\n");
        authAES.encryptAuthenticator(auth);
        return auth;
    }

    //when the ticket is retrieved, cease execution
    @Override
    public void run() {
        while (!ticketRetrieved) {
        }
    }

    // display contents of final ticket
    public Message getMessage() {
        System.out.println("SGT CONTENTS: ");
        ticket.displayContents();
        return message;
    }

}

