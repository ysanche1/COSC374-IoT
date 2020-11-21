package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Gateway extends JFrame{
    public Broker broker;
    String message5AESKey;
    CloudBackup cloudBackup = new CloudBackup();
    GatewayCloud gatewayCloud = new GatewayCloud();
    String[] evaluations;
    String gatewayID = "gateway374";
    private String sharedKey;
    String keyTGS_V = "TGS_V_SHAREDKEY+";
    private JPanel mainPanel;
    private JLabel alert;
    String ticketContents;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX;
    int frameY;
    GatewayAlert gatewayAlert;
    RSAKeyPairGenerator rsaKG;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey thermPublicKey;
    PublicKey appPublicKey;
    boolean activeRequest;
    RSAAlgorithm rsaD;
    RSAAlgorithm rsaEApp;
    RSAAlgorithm rsaETherm;
    boolean lockdown;
    String[] deviceFunctions = new String[]{"INCREASE","DECREASE","CUSTOM"};
    String[] threatAssessment = new String[]{"OK","OK","OK"};
    Message warning = new Message(true);
    public Gateway() {
        try {
            generateKeyPair();
            rsaD = new RSAAlgorithm(privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        gatewayAlert = new GatewayAlert();
    }


    public void generateKeyPair() throws NoSuchAlgorithmException {
        rsaKG = new RSAKeyPairGenerator();
        publicKey = rsaKG.getPublicKey();
        privateKey= rsaKG.getPrivateKey();
    }

    public Message receiveSGT(Message m) throws Exception {
            Message confirmation = new Message();
            m.ticket.displayContents();
            Message copy = new Message(m);
            AESAlgorithm aes = new AESAlgorithm(keyTGS_V);
            aes.decryptMessage(copy);
            if (Long.parseLong(copy.ticket.lifetime) < System.currentTimeMillis()  //timestamp check
                   | Long.parseLong(copy.ticket.lifetime) < Long.parseLong(copy.auth.timestamp)
                    | copy.ticket.key.equals(message5AESKey)) {
                replayDetected(Long.parseLong(copy.ticket.lifetime));
           }
               else {
                    appPublicKey = rsaKG.strToKey(copy.pub_key);
                    message5AESKey = copy.ticket.key;
                    aes = new AESAlgorithm(message5AESKey);
                    String newAeskey = generateNewAesKey();
                    confirmation = new Message();
                    rsaEApp = new RSAAlgorithm(appPublicKey);
                    confirmation.pub_key = rsaKG.keyToStr(publicKey);
                    confirmation.key = newAeskey;
                    confirmation.pub_key=  aes.encrypt(confirmation.pub_key);
                    confirmation.key= aes.encrypt(confirmation.key);
                    sharedKey = newAeskey;
                    gatewayAlert.dispatchEvent(new WindowEvent(gatewayAlert, WindowEvent.WINDOW_CLOSING));

                }
        return confirmation;
        }

    private void replayDetected(Long lifetime) {
        long sec = ((System.currentTimeMillis()-lifetime)/1000);
        long dec = ((System.currentTimeMillis()-lifetime)%1000);

        gatewayAlert.setStatus("REPLAY DETECTED - ACCESS DENIED");

        if (sec > 0)
        { String seconds = "second";
            if (dec!=0){
                seconds+="s";
            }
            gatewayAlert.setStatus2("Request expired for "+sec+"."+dec+" "+seconds);
        }
        else
            gatewayAlert.setStatus("Expired key");

        gatewayAlert.pack();
        gatewayAlert.setVisible(true);
    }
        void initialize (Thermostat t) {
            thermPublicKey = t.getPublicKey();
            rsaETherm = new RSAAlgorithm(thermPublicKey);
            broker = new Broker(rsaEApp, rsaD);
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
                    if (threatAssessment[i].equals("WARNING") | threatAssessment[i].equals("OK"))
                        return threatAssessment[i];
            }
                        evaluations = new String[2];
                        evaluations = gatewayCloud.evaluate(m);
                        updateLocalThreatDB(evaluations);
                        return evaluations[1];
        }

    private void updateLocalThreatDB(String[] evaluate) {
        int lengths = deviceFunctions.length;
        String[] df = new String[lengths+1];
        String[] ta = new String[lengths+1];
        for (int i = 0; i < lengths; i++)
        {
            df[i] = deviceFunctions[i];
            ta[i] = threatAssessment[i];
        }
        df[lengths] = evaluate[0];
        ta[lengths] = evaluate[1];
        deviceFunctions = df;
        threatAssessment = ta;
    }

    public String getAesKey(){
        return sharedKey;
    }

    private void checkCloudStatus(Thermostat t) {
        if (Main.tcCloud.cloudOnline) {
            t.activeCloud = Main.tcCloud;
            Main.tc.setNotification("Cloud Backup: Inactive");
        }
        else {
            cloudBackup.targetTemp = t.activeCloud.targetTemp;
            t.activeCloud = cloudBackup;
            Main.tc.setNotification("Cloud Backup: Active");
        }
    }

    public void relayRequest (Message m, Thermostat t) throws Exception {
            //     System.out.println(Thread.currentThread());
            //      System.out.println(System.identityHashCode(t));
            if (lockdown == true)
                Main.tc.receiveResponse(warning);

            checkCloudStatus(t);

            try{
                m.key = rsaD.decrypt(m.key);
               if(!sharedKey.equals(m.key)){
                   Main.tc.setNotification("BAD KEY");
                    Main.tc.receiveResponse(warning);
                }
            } catch (Exception e) {
                Main.tc.setNotification("BAD KEY");
                Main.tc.receiveResponse(warning);
            }
        m.key = rsaETherm.encrypt(m.key);
        //  aes.decryptMessage(m);
          //  aes.encryptMessage(m);
        Message temp = new Message();
            activeRequest = true;
            //   System.out.println(messageOK);
            t.receiveRequest(m);
        }

    public void relayResponse (Message r, Thermostat t) throws Exception {
        AESAlgorithm aes = new AESAlgorithm(sharedKey);
       //   aes.decryptMessage(r);
        //  aes.encryptMessage(r);
        activeRequest = false;
        //   System.out.println(messageOK);

        r.update = aes.decrypt(r.update);
        r.command = aes.decrypt(r.command);
        String newAeskey = generateNewAesKey();
        switch (checkThreatDB(r.command)){
            case "OK":
                r.key = newAeskey;
                break;
            case "WARNING":
                t.lock();
                gatewayAlert.setStatus("Request: "+r.command+" from THERMOSTAT matches known intrusion pattern");
                gatewayAlert.pack();
                gatewayAlert.setVisible(true);
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
        if(!lockdown)
            t.main();
    }

    private String generateNewAesKey() throws InterruptedException {
        String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        String newKey = "";
        for(int i = 0 ; i<16; i++)
            newKey += salt.charAt(new Random().nextInt(46));
        Thread.sleep(500);
        System.out.println("\nNEW SHARED KEY: "+newKey+"\n");
        Thread.sleep(500);
        return newKey;
    }
}