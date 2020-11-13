package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Gateway extends JFrame{
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
    RSAKeyPairGenerator rsaKG;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey thermPublicKey;
    boolean activeRequest;
    RSAAlgorithm rsaD;
    RSAAlgorithm rsaE;
    boolean lockdown;
    String[] deviceFunctions = new String[]{"INCREASE","DECREASE","CUSTOM", "PORTSCAN"};
    String[] threatAssessment = new String[]{"OK","OK","OK","WARNING"};
    Message warning = new Message(true);
    public Gateway() {
        try {
            generateKeyPair();
            rsaD = new RSAAlgorithm(privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        gw = new GatewayWaiting();
    }


    public void generateKeyPair() throws NoSuchAlgorithmException {
        rsaKG = new RSAKeyPairGenerator();
        publicKey = rsaKG.getPublicKey();
        privateKey= rsaKG.getPrivateKey();
    }

    public String receiveSGT(Message m) throws Exception {
            m.ticket.displayContents();
            AESAlgorithm aes = new AESAlgorithm(keyTGS_V);
            aes.decryptMessage(m);
            System.out.println("\nAUTH TIMESTAMP" +m.auth.timestamp+"\n");
            System.out.println("\nTICKET TIMESTAMP" +m.ticket.timestamp+"\n");
            if (Long.parseLong(m.ticket.lifetime) < System.currentTimeMillis()  //timestamp check
                    | Long.parseLong(m.ticket.lifetime) < Long.parseLong(m.auth.timestamp)) {
                gw.setStatus("REPLAY DETECTED - ACCESS DENIED\n");
                gw.pack();
                gw.setVisible(true);
            }
                else{
                    sharedKey = m.ticket.key;
                    gw.dispatchEvent(new WindowEvent(gw, WindowEvent.WINDOW_CLOSING));
                }
        m.ticket = aes.encryptTicket(m.ticket);
        aes = new AESAlgorithm(sharedKey);
        aes.encryptAuthenticator(m.auth);
        return sharedKey;
        }


        void initialize (Thermostat t) {
            thermPublicKey = t.getPublicKey();
            rsaE = new RSAAlgorithm(thermPublicKey);
            setTitle("Gateway");
            setContentPane(mainPanel);
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            pack();
            frameX = (screenSize.width / 2) - (getWidth() / 2);
            frameY = (screenSize.height / 2) - (getHeight() * 19/5);
            ;
            setLocation(frameX, frameY);
            setVisible(true);
        }


        private String checkThreatDB(String m)
        {
            for(int i = 0; i < deviceFunctions.length; i++) {
                if (m.equals(deviceFunctions[i]))
                    if (threatAssessment[i].equals("WARNING")) {
                        return threatAssessment[i];
                    }
            }
            return "OK";
        }


        public void relayRequest (Message m, Thermostat t) throws Exception {
            //     System.out.println(Thread.currentThread());
            alert.setText("Request at gateway");
            //      System.out.println(System.identityHashCode(t));
            if (lockdown == true)
                Main.tc.receiveResponse(warning);
          //  aes.decryptMessage(m);
          //  aes.encryptMessage(m);
            m.key = rsaE.encrypt(sharedKey);
            activeRequest = true;
            //   System.out.println(messageOK);
            alert.setText("Request sent to thermostat");
            t.receiveRequest(m);
        }

    public void relayResponse (Message r, Thermostat t) throws Exception {
        AESAlgorithm aes = new AESAlgorithm(sharedKey);
       //   aes.decryptMessage(r);
        //  aes.encryptMessage(r);
        activeRequest = false;
        //   System.out.println(messageOK);

        r.update = aes.decrypt(r.update);
        String newAeskey = generateNewAesKey();
        switch (checkThreatDB(r.command)){
            case "OK":
                alert.setText("confirmation received from thermostat");
                r.key = newAeskey;
                t.main();
                break;
            case "WARNING":
                t.lock();
                lockdown = true;
                warning.key = newAeskey;
                r = warning;
                break;
        }
        r.key = aes.encrypt(r.key);
        aes = new AESAlgorithm(newAeskey);
        r.update = aes.encrypt(r.update);
        sharedKey = newAeskey;
        Main.tc.receiveResponse(r);
    }





    private String generateNewAesKey() {
        String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        String newKey = "";
        for(int i = 0 ; i<16; i++)
            newKey += salt.charAt(new Random().nextInt(46));
        System.out.println("NEW SHARED KEY: "+newKey+"\n");
        return newKey;
    }
}