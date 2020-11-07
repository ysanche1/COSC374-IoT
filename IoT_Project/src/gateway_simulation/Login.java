package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class Login extends JFrame implements Runnable{
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
    String timestamp;
    Date date;
    String tgsID = "TGS374";
    String gatewayID = "gateway374";
    Message message;
    Ticket ticket;
    Boolean ticketRetrieved = false;

    public Login(Kerberos kdc) {
     //   System.out.println("MESSAGE UNIQUE HASH = "+ System.identityHashCode(m)+"\n");
        setTitle("Login");
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
            public synchronized void actionPerformed(ActionEvent e) {
                Message m = new Message();
                //get text in fields and close the window
                System.out.println("    ******LOGIN BUTTON PRESSED******\n");
              //  System.out.println("KDC UNIQUE HASH = " + System.identityHashCode(m) + "\n");
              //  System.out.println("MESSAGE UNIQUE HASH getSGT = "+ System.identityHashCode(m)+"\n");
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

    //send message 1 and be judged by Authorization Server
    public synchronized void authorizationExchangeInit(Kerberos kdc) throws Exception        //MESSAGE 1
    {
        message = new Message();
        date = new Date();
        timestamp = String.valueOf(date.getTime());
        message.createMessage1(clientID, tgsID, timestamp, message);
        System.out.println("	******MESSAGE 1 SENT**********\n");
        processing.processMed();
         kdc.as.handleMessage(message);//get message 2;
        if (message.error)     //error is set if clientID or tgsID are rejected
            setVisible(true);  //bring window back for another attempt
        else {
            int pl = password.length();  //key needs to be 16 bytes, 1234567890 repeating as padding
            int j = 1;
            for (int i = 1; i <= 16 - pl; i++) {
                if (j == 10)
                    j = 0;
                password += j;
                j++;
            }
            attemptDecryption(kdc, password, message);} // try to get ticket
        }




    //same as above but message 3 but with a call to createAuthenticator()
    public synchronized void ticketGrantingServiceExchangeInit(Kerberos kdc, Message message) throws Exception {
        message.createMessage3(gatewayID, ticket, createAuthenticator());
        System.out.println("	**********MESSAGE 3 SENT******\n");
        processing.processMed();
        kdc.tgs.handleMessage(message); //Get message 4
        if (message.error)
            setVisible(true);
        else
        attemptDecryption(kdc, sharedKey, message);
    }

    public synchronized void attemptDecryption(Kerberos kdc, String key, Message message) throws Exception {
        if (kdc.as.attemptsRemaining) {
            System.out.println("    ******ATTEMPTING DECRYPTION******\n");
            processing.processLong();
            processing.processLong();
            aes = new AESAlgorithm(key);
            try {
                aes.decryptMessage(message);
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
                            ticketGrantingServiceExchangeInit(kdc, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        message.ticket.displayContents();
                        message.createMessage5(message.ticket, createAuthenticator());
                        ticketRetrieved = true;

                        break;
                }
            }
        }
    }


    private Authenticator createAuthenticator() throws Exception {
        timestamp = String.valueOf(date.getTime());
        AESAlgorithm authAES = new AESAlgorithm(sharedKey);
        Authenticator auth = new Authenticator(clientID, "CLIENT_ADDRESS", timestamp);
        authAES.encryptAuthenticator(auth);
        return auth;
    }

    @Override
    public void run() {
        if(ticketRetrieved)
        {

        }
    }
}

