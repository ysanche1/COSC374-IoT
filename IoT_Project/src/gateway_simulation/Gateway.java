package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Gateway extends JFrame implements Runnable {
    String gatewayID = "gateway374";
    private String sharedKey;
    String keyTGS_V = "TGS_V_SHAREDKEY+";
    private JPanel mainPanel;
    private JLabel alert;
    String ticketContents;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX;
    int frameY;
    GatewayWaiting gw;
    RSAKeyPairGenerator rsa;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey thermPublicKey;
    boolean messageOK;

    public Gateway() {
        try {
            generateKeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        gw = new GatewayWaiting();
    }


    public void generateKeyPair(PublicKey k1, PrivateKey k2) throws NoSuchAlgorithmException {
        rsa = new RSAKeyPairGenerator();
        k1 = rsa.getPublicKey();
        k2 = rsa.getPrivateKey();
    }

    public void receiveSGT(Message m) throws Exception {
        if (m.mNum == 5) {
            m.ticket.displayContents();
            AESAlgorithm aes = new AESAlgorithm(keyTGS_V);
            aes.decryptMessage(m);
            System.out.println("\nAUTH TIMESTAMP" +m.auth.timestamp+"\n");
            System.out.println("\nTICKET TIMESTAMP" +m.ticket.timestamp+"\n");
            if (Long.parseLong(m.ticket.lifetime) < Long.parseLong(m.ticket.timestamp)  //timestamp check
                    | Long.parseLong(m.ticket.lifetime) < Long.parseLong(m.auth.timestamp))
                gw.setStatus("LOGIN TIMEOUT - TRY AGAIN");
                else{
                    sharedKey = m.ticket.key;
                    m.displayContents();
                    gw.dispatchEvent(new WindowEvent(gw, WindowEvent.WINDOW_CLOSING));
                }
            }

        }


        void initialize (Thermostat t) {
            thermPublicKey = t.getPublicKey();
            setTitle("Gateway");
            setContentPane(mainPanel);
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            pack();
            frameX = (screenSize.width / 2) - (getWidth() / 2);
            frameY = (screenSize.height / 2) - (getHeight() * 7 / 2);
            ;
            setLocation(frameX, frameY);
            setVisible(true);
        }



        public Message relayRequest (Message m, Thermostat t) throws Exception {
            //     System.out.println(Thread.currentThread());
            alert.setText("Request at gateway");
            //      System.out.println(System.identityHashCode(t));

            AESAlgorithm aes = new AESAlgorithm(sharedKey);
            aes.decryptMessage(m);
            RSAAlgorithm rsa = new RSAAlgorithm(thermPublicKey);
            rsa.encrypt(sharedKey);
            aes.encryptMessage(m);
            m.key = rsa.encrypt(sharedKey);


            messageOK = true;
            //   System.out.println(messageOK);
            alert.setText("Request sent to thermostat");;
            m = t.receiveRequest(m);
            generateNewAesKey();
            m.key = aes.encrypt(sharedKey);
            alert.setText("confirmation received from thermostat");
            t.main();
            return m;
        }
        @Override
        public void run () {
        }


    private void generateNewAesKey() {
        String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        sharedKey = "";
        for(int i = 0 ; i<16; i++)
            sharedKey += salt.charAt(new Random().nextInt(46));
        System.out.println("NEW SHARED KEY: "+sharedKey);
    }
}