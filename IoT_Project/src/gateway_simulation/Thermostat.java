package gateway_simulation;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ThreadLocalRandom;

public class Thermostat implements Runnable {
    Thread t;
    final int avgTemp = 70;
    int currentTemp;
    int anchorTemp;
    int timeOfDay;
    double endTime = 0;
    boolean setUp;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey gwPublicKey;
    private String aesKey;
    RSAAlgorithm rsaE;
    RSAAlgorithm rsaD;
    Boolean lockdown = false;
    DeviceCloud activeCloud = Main.tcCloud;
    public Thermostat() throws NoSuchAlgorithmException {
        timeOfDay = (int) ((System.currentTimeMillis() % 8.64e+7) - 1.8e+7);
        if(timeOfDay <0){
            timeOfDay = (int) (timeOfDay +8.64e+7);
        }
        RSAKeyPairGenerator rsaKP = new RSAKeyPairGenerator();
        publicKey = rsaKP.getPublicKey();
        privateKey = rsaKP.getPrivateKey();
        rsaD = new RSAAlgorithm(privateKey);
        System.out.println("Thermostat Running");
        //     System.out.println("THERMOSTAT I JUST MADE = "+System.identityHashCode(this));
    }

    // Function that sets the temperature

    private void setup() {
        if (timeOfDay <= 2.16e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp - 10, avgTemp - 4);
            endTime = 2.16e+7;
        } else if (timeOfDay > 2.16e+7 & timeOfDay <= 4.32e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp, avgTemp + 11);
            endTime = 4.32e+7;
        } else if (timeOfDay > 4.32e+7 & timeOfDay <= 6.48e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp, avgTemp + 6);
            endTime = 6.48e+7;
        } else if (timeOfDay > 6.48e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp - 5, avgTemp + 1);
            endTime = 8.64e+7;
        }
        activeCloud.targetTemp = currentTemp;
    }

    //Reports current temperature to console at regular intervals
    private synchronized void temperatureSimulation(Thermostat t) throws Exception {
        Message r = new Message();
        if (!setUp) {
            setup();
            setUp = true;
        }
        int uptime = 0;
        while (!Main.gateway.activeRequest & !lockdown) {
            if (currentTemp != activeCloud.targetTemp) {
                Thread.sleep(250);
                if (currentTemp < activeCloud.targetTemp) {
                    currentTemp++;
                    r.update = rsaE.encrypt(String.valueOf(currentTemp));
                    Main.gateway.broker.publish(r);
                    Thread.sleep(250);
                } else {
                    currentTemp--;
                    r.update = rsaE.encrypt(String.valueOf(currentTemp));
                    Main.gateway.broker.publish(r);
                    Thread.sleep(250);
                }
            }
            else {
                anchorTemp = currentTemp;
                if (t.timeOfDay >= endTime)
                    setup();
                if (uptime == 10) {
                    currentTemp = anchorTemp - 1;
                    uptime = 0;
                }
                uptime++;
                r.update = rsaE.encrypt(String.valueOf(currentTemp));
                Main.gateway.broker.publish(r);
                currentTemp = anchorTemp;
                Thread.sleep(500);
                if (!Main.gateway.activeRequest)
                    Thread.sleep(500);
                if (!Main.gateway.activeRequest)
                    Thread.sleep(500);
                if (!Main.gateway.activeRequest)
                    Thread.sleep(500);
                if (!Main.gateway.activeRequest)
                    Thread.sleep(500);
            }
        }
    }

    //Receives request AES encrypted message, decrypts AES key using RSA private key, decrypts message with recovered key,
    //Sends response back to gateway encrypted with AES key
    public void receiveRequest(Message m) throws Exception {
        System.out.println("\nRequest at thermostat");
        if (lockdown) {
            Message r = new Message();
            r.update = "Security breach detected - Lockdown in effect";
        }
        aesKey = rsaD.decrypt(m.key);
        AESAlgorithm aes = new AESAlgorithm(aesKey);
        aes.decryptMessage(m);
        Message r = new Message(m.command, m.custom);
        r.key = rsaE.encrypt(aesKey);
        r.command = aes.encrypt(r.command);
        r.custom = aes.encrypt(r.custom);
        Main.tc.atk.captureKey(publicKey);
        Main.gateway.analyze(m.command, this);
        switch (m.command) {
            case "INCREASE": {
                r.update = aes.encrypt("Thermostat set to " + (activeCloud.targetTemp+1));
                Main.gateway.relayResponse(r, this);
                activeCloud.increase();
                break;
            }
            case "DECREASE": {
                r.update = aes.encrypt("Thermostat set to " + (activeCloud.targetTemp-1));
                Main.gateway.relayResponse(r, this);
                activeCloud.decrease();
                break;
            }
            case "CUSTOM": {
                r.update = aes.encrypt("Thermostat set to " + m.custom);
                Main.gateway.relayResponse(r, this);
                activeCloud.setTemperature(Integer.parseInt(m.custom));
                break;
            }
        }
    }

    //used to reset the thread after requests
    public void main(){
        gwPublicKey = Main.gateway.publicKey;
        rsaE = new RSAAlgorithm(gwPublicKey);
        Main.gateway.activeRequest = false;
        t = new Thread(this,"THERMOTHREAD");
        t.start();
    }
    public  void run() {
        while(!Main.gateway.activeRequest) {
            try {
                temperatureSimulation(this);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void lock(){
        lockdown = true;
    }

    }