package gateway_simulation;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ThreadLocalRandom;

public class Thermostat implements Runnable {
    Thread t;
    final int avgTemp = 60;
    int targetTemp;
    int currentTemp;
    int anchorTemp;
    int timeOfDay;
    double endTime = 0;
    boolean setUp;
    boolean justSet;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey gwPublicKey;
    private String aesKey;
    RSAAlgorithm rsaE;
    RSAAlgorithm rsaD;
    Boolean lockdown = false;
    DeviceCloud activeCloud;
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
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp - 8, avgTemp + 1);
            endTime = 8.64e+7;
        }
        activeCloud.targetTemp = currentTemp;
    }

    //Reports current temperature to console at regular intervals
    private synchronized void temperatureSimulation(Thermostat t) throws Exception {
        Message r = new Message();
        checkCloudStatus();
        if (!setUp) {
            setup();
            setUp = true;
        }
        int uptime = 0;
        while (!Main.gateway.activeRequest & !lockdown) {
            if (currentTemp != activeCloud.targetTemp) {
                Thread.sleep(500);
                if (currentTemp < activeCloud.targetTemp) {
                    currentTemp++;
                    r.update = rsaE.encrypt(String.valueOf(currentTemp));
                    Main.gateway.broker.publish(r);
                    Thread.sleep(500);
                } else {
                    currentTemp--;
                    r.update = rsaE.encrypt(String.valueOf(currentTemp));
                    Main.gateway.broker.publish(r);
                    Thread.sleep(500);
                }
            }
            else {
                anchorTemp = currentTemp;
                if (t.timeOfDay >= endTime)
                    setup();
                if (uptime == 10) {
                    //ThreadLocalRandom.current().nextInt(t.anchorTemp - 1, t.anchorTemp + 1);
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
        checkCloudStatus();
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
        switch (m.command) {
             default: {
                 r.update = aes.encrypt("UNLOCKING FRONT DOOR");
                Main.gateway.relayResponse(r, this);
                break;
            }
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

    private void checkCloudStatus() {
        if (Main.tcCloud.cloudOnline) {
            activeCloud = Main.tcCloud;
        }
        else activeCloud = Main.gateway.cloudBackup;
    }

    //used to reset the thread after requests
    public void main(){
        checkCloudStatus();
        gwPublicKey = Main.gateway.publicKey;
        rsaE = new RSAAlgorithm(gwPublicKey);
        Main.gateway.activeRequest = false;
        t = new Thread(this,"THERMOTHREAD");
        t.start();
    }
    public  void run() {
 //       System.out.println(Thread.currentThread());
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
           // System.out.println(Thread.currentThread());
        }

    }


    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void lock(){
        lockdown = true;
    }

    }