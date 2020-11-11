package gateway_simulation;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Thermostat implements Runnable {
    Thread t;
    final int avgTemp = 60;
    int targetTemp;
    int currentTemp;
    int anchorTemp;
    int tod ;
    boolean idle = true;
    double endTime = 0;
    boolean setUp;
    boolean justSet;
    PublicKey publicKey;
    PrivateKey privateKey;
    PublicKey gwPublicKey;
    private String aesKey;

    public Thermostat() throws NoSuchAlgorithmException {
        tod = (int) ((System.currentTimeMillis() % 8.64e+7) - 1.8e+7);
        if(tod<0){
            tod = (int) (tod+8.64e+7);
        }
        RSAKeyPairGenerator rsaKP = new RSAKeyPairGenerator();
        publicKey = rsaKP.getPublicKey();
        privateKey = rsaKP.getPrivateKey();
        gwPublicKey = Main.gateway.publicKey;
        System.out.println("Thermostat Running");
        //     System.out.println("THERMOSTAT I JUST MADE = "+System.identityHashCode(this));
    }

    private synchronized void setTemperature(int temp) throws InterruptedException {
        System.out.println("Set temp Thread = : " + Thread.currentThread());
        System.out.println(System.identityHashCode(this));
        targetTemp = temp;
        while (currentTemp != targetTemp) {
            Thread.sleep(500);
            System.out.println("Current Temperature: " + currentTemp);
            if (currentTemp < targetTemp)
                currentTemp++;
            else
                currentTemp--;
        }
        justSet = true;
        targetTemp = 0;
    }

    private void setup() {
        if (tod <= 2.16e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp - 10, avgTemp - 4);
            endTime = 2.16e+7;
        } else if (tod > 2.16e+7 & tod <= 4.32e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp, avgTemp + 11);
            endTime = 4.32e+7;
        } else if (tod > 4.32e+7 & tod <= 6.48e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp, avgTemp + 6);
            endTime = 6.48e+7;
        } else if (tod > 6.48e+7) {
            currentTemp = ThreadLocalRandom.current().nextInt(avgTemp - 8, avgTemp + 1);
            endTime = 8.64e+7;
        }
    }

    private synchronized void temperatureSimulation(Thermostat t) throws InterruptedException {
        if (!setUp) {
            setup();
            setUp = true;
        }
        int uptime = 0;
        while (!Main.gateway.messageOK) {
           // System.out.println("\n" + Thread.currentThread());
          //  System.out.println(System.identityHashCode(this));
            anchorTemp = currentTemp;
            //     System.out.println("Simulation Thread = : "+Thread.currentThread());
            if (t.tod >= endTime)
                setup();
              if (justSet) {
                  Thread.sleep(500);
                  justSet = false;
              }
            if (uptime == 3) {
                currentTemp = anchorTemp - 1;
                //ThreadLocalRandom.current().nextInt(t.anchorTemp - 1, t.anchorTemp + 1);
                System.out.println("Current Temperature: " + currentTemp);
                uptime = 0;
                currentTemp = anchorTemp;
            } else {
                System.out.println("Current Temperature: " + currentTemp);
                uptime++;
            }
            Thread.sleep(500);
            if (!Main.gateway.messageOK)
                Thread.sleep(500);
            if (!Main.gateway.messageOK)
                Thread.sleep(500);
            if (!Main.gateway.messageOK)
                Thread.sleep(500);
            if (!Main.gateway.messageOK)
                Thread.sleep(500);
        }
    }



    public Message receiveRequest(Message m) throws Exception {
        System.out.println("Request at thermostat");

        RSAAlgorithm rsa = new RSAAlgorithm(privateKey);
        aesKey = rsa.decrypt(m.key);
        AESAlgorithm aes = new AESAlgorithm(aesKey);
        aes.decryptMessage(m);
        switch (m.command) {
            case "INCREASE" -> {
                //          System.out.println(Thread.currentThread());

                setTemperature(currentTemp+1);
                System.out.println("Thermostat set to " + currentTemp);
            }
            case "DECREASE" -> {
                //           System.out.println(Thread.currentThread());
                setTemperature(currentTemp-1);
                System.out.println("Thermostat set to " + currentTemp);
            }
            case "CUSTOM" -> {
                //           System.out.println(Thread.currentThread());
                setTemperature(Integer.parseInt(m.custom));
                System.out.println("Thermostat set to " + currentTemp);
            }
        }
        rsa = new RSAAlgorithm(gwPublicKey);
        Message r = new Message("Thermostat set to " + currentTemp);
        r.response = aes.encrypt(r.response);
        return r;
    }

    public void main(){
        Main.gateway.messageOK = false;
        t = new Thread(this,"THERMOTHREAD");
        t.start();
    }
    public  void run() {
 //       System.out.println(Thread.currentThread());
        while(!Main.gateway.messageOK) {
            try {
                temperatureSimulation(this);
            } catch (InterruptedException e) {
                System.out.println("Interrupted*******************************");
            }
            System.out.println(Thread.currentThread());
        }

    }

    public void receiveAesKey(String sharedKey) {
        this.aesKey = sharedKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    //     int newTemp;
       //     Thermostat thermostat = new Thermostat();
       //     Thread t1 = new Thread(thermostat, "ThermoStat Thread");
       //     t1.start();
      //      Scanner k = new Scanner(System.in);
          //  do {
              //  newTemp = Integer.valueOf(k.next());
               // System.out.println("Thermostat set to " + newTemp);
            //    t1.interrupt();

            //    t1 = new Thread(thermostat);
          //      t1.start();
        //    } while (true);



    }