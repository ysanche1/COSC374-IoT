package gateway_simulation;

import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.Random;

// Class representing the proposed smart home gateway

public class Gateway extends JFrame  implements Runnable{
    public Broker broker;
    String oldAesKey;
    CloudBackup cloudBackup = new CloudBackup();
    GatewayCloud gatewayCloud = new GatewayCloud();
    String[] evaluations;
    String gatewayID = "gateway374";
    private String aesKey;
    String keyTGS_V = "TGS_V_SHAREDKEY+";
    AESAlgorithm aes;
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
    String[] deviceFunctions = new String[]{"sendEvent(name: set_target_temp value: increase)","sendEvent(name: set_target_temp value: decrease)",
            "sendEvent(name: set_target_temp value: custom)"};
    String[] threatAssessment = new String[]{"OK", "OK", "OK"};
    Message warning = new Message(true);
    CloudMonitor cloudMonitor;
    Boolean loggedIn = false;
    public Gateway() {
        try {
            generateKeyPair();
            rsaD = new RSAAlgorithm(privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        gatewayAlert = new GatewayAlert();
        cloudMonitor = new CloudMonitor(cloudBackup);
    }


    public void generateKeyPair() throws NoSuchAlgorithmException {
        rsaKG = new RSAKeyPairGenerator();
        publicKey = rsaKG.getPublicKey();
        privateKey = rsaKG.getPrivateKey();
    }

    //process SGT received from Login
    public Message receiveSGT(Message m) throws Exception {
        Message confirmation = new Message();
        Message copy = new Message(m);
        AESAlgorithm aes = new AESAlgorithm(keyTGS_V);
        aes.decryptMessage(copy);
        if (Long.parseLong(copy.ticket.lifetime) < System.currentTimeMillis()  //timestamp check
                | Long.parseLong(copy.ticket.lifetime) < Long.parseLong(copy.auth.timestamp)
                | copy.ticket.key.equals(oldAesKey)) {
            replayDetected(Long.parseLong(copy.ticket.lifetime));
        } else {
            System.out.println("    ******* LOGIN SUCCESSFUL *******\n");
            appPublicKey = rsaKG.strToKey(copy.pub_key);
            oldAesKey = copy.ticket.key;
            aes = new AESAlgorithm(oldAesKey);
            String newAeskey = generateNewAesKey();
            confirmation = new Message();
            rsaEApp = new RSAAlgorithm(appPublicKey);
            confirmation.pub_key = rsaKG.keyToStr(publicKey);
            confirmation.key = newAeskey;
            confirmation.pub_key = aes.encrypt(confirmation.pub_key);
            confirmation.key = aes.encrypt(confirmation.key);
            aesKey = newAeskey;
        }
        return confirmation;
    }

    //if ticket has been replayed, this method is called
    private void replayDetected(Long lifetime) {
        long cTime = System.currentTimeMillis();
        Double sec = Double.valueOf(cTime - lifetime) / Double.valueOf(1000);
        long dec = ((cTime - lifetime) % 1000);
        DecimalFormat f = new DecimalFormat("#0.00");


        gatewayAlert.setStatus("REPLAY DETECTED - ACCESS DENIED");

        if (sec > 0) {
            String seconds = "second";
            if (dec != 0) {
                seconds += "s";
            }

            gatewayAlert.setStatus2("Request expired for " + f.format(sec) + " " + seconds);
        } else
            gatewayAlert.setStatus("Login attempt using old key");

        gatewayAlert.pack();
        gatewayAlert.setVisible(true);
    }

    void initialize(Thermostat t) {
        thermPublicKey = t.getPublicKey();
        rsaETherm = new RSAAlgorithm(thermPublicKey);
        broker = new Broker(rsaEApp, rsaD);
    }


    private String checkThreatDB(String m) {
        for (int i = 0; i < deviceFunctions.length; i++) {
            if (m.equals(deviceFunctions[i]))
                if (threatAssessment[i].equals("WARNING") | threatAssessment[i].equals("OK"))
                    return threatAssessment[i];
        }
        System.out.println(("*********** UNRECOGNIZED REQUEST **********\n"));
        evaluations = new String[2];
        evaluations = gatewayCloud.evaluate(m);
        updateLocalThreatDB(evaluations);
        return evaluations[1];
    }

    private void updateLocalThreatDB(String[] evaluate) {
        int lengths = deviceFunctions.length;
        String[] df = new String[lengths + 1];
        String[] ta = new String[lengths + 1];
        for (int i = 0; i < lengths; i++) {
            df[i] = deviceFunctions[i];
            ta[i] = threatAssessment[i];
        }
        df[lengths] = evaluate[0];
        ta[lengths] = evaluate[1];
        deviceFunctions = df;
        threatAssessment = ta;
        System.out.println(("************ THREAT DB UPDATED ************\n"));
    }



    public void relayRequest(Message m, Thermostat t) throws Exception {
        aes = new AESAlgorithm(aesKey);
        if (lockdown == true)
            Main.tc.receiveResponse(warning);
        try {
            m.key = rsaD.decrypt(m.key);
            if (!aesKey.equals(m.key)) {
                Main.tc.setNotification("BAD KEY");
                Main.tc.receiveResponse(warning);
            }
        } catch (Exception e) {
            Main.tc.setNotification("BAD KEY");
            Main.tc.receiveResponse(warning);
        }
        m.key = rsaETherm.encrypt(m.key);
        activeRequest = true;
        t.receiveRequest(m);
    }


    public void relayResponse (Message r, Thermostat t) throws Exception {
        if(lockdown){
            Main.tc.receiveResponse(r);
            return;
        }
            t.main();
        activeRequest = false;
        r.update = aes.decrypt(r.update);
        String newAeskey = generateNewAesKey();
        aes = new AESAlgorithm(newAeskey);
        r.key = rsaEApp.encrypt(newAeskey);
        r.update = aes.encrypt(r.update);
        oldAesKey = aesKey;
        aesKey = newAeskey;
        Main.tc.receiveResponse(r);
    }

    private String generateNewAesKey() throws InterruptedException {
        String salt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        String newKey = "";
        for(int i = 0 ; i<16; i++)
            newKey += salt.charAt(new Random().nextInt(46));
        Thread.sleep(500);
        System.out.println("    NEW SHARED KEY: "+newKey+"\n");
        Thread.sleep(500);
        return newKey;
    }

    public void analyze(String command, Thermostat t) throws Exception {
        switch (checkThreatDB(command)){
            case "OK":
                break;
            case "WARNING":
                t.lock();
                lockdown = true;
                String newAeskey = generateNewAesKey();
                warning.key = rsaEApp.encrypt(newAeskey);
                Message warning = this.warning;
                AESAlgorithm aes = new AESAlgorithm(newAeskey);
                warning.update = aes.encrypt(warning.update);
                aesKey = newAeskey;
                Main.tc.receiveResponse(warning);
                gatewayAlert.setStatus("Unusual traffic from thermostat");
                gatewayAlert.setStatus2(command);
                gatewayAlert.pack();
                gatewayAlert.setVisible(true);
                break;
        }
    }

    @Override
    public void run() {

    }
}