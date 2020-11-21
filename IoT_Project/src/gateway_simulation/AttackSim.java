package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;

import static java.lang.Integer.valueOf;

public class AttackSim{
    public String aesKey;
    public PublicKey thermPublicKey;
    Message capturedMessage;
    Boolean ticketCaptured = false;
    Boolean keyCaptured = false;
    Message confirmation;

    public AttackSim(JButton replayAttackButton, JButton suspiciousActionButton, JButton cloudCrash, Message capturedMessage) {
        copyMessage(capturedMessage);
        replayAttackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    attemptLogin();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        suspiciousActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    attemptDoorUnlock();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        cloudCrash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
        cloudToggle();
            }
        });
    }

    private void cloudToggle() {
        if(Main.tcCloud.cloudOnline){
            Main.tcCloud.cloudOnline = false;
        }
        else
            Main.tcCloud.cloudOnline = true;
    }

    private void attemptLogin() throws Exception {
        if(!ticketCaptured) {
            System.out.println("(((SGT not captured)))");
        }
        else {
            System.out.println("\n(((Attempting Login Using Captured Ticket)))");
            confirmation = Main.gateway.receiveSGT(capturedMessage);
            Thread t = new Thread(confirmation);
            t.start();
        }
    }

    private void attemptDoorUnlock() throws Exception {
        if(!keyCaptured) {
            System.out.println("(((AES + RSA keys not captured)))");
        }
        else {
            System.out.println("\n(((Attempting to unlock door)))");
            Message unlock_door = new Message("UNLOCK DOOR", "");
            RSAAlgorithm rsa = new RSAAlgorithm(thermPublicKey);
            AESAlgorithm aes = new AESAlgorithm(aesKey);
            unlock_door.command = aes.encrypt(unlock_door.command);
            unlock_door.key = rsa.encrypt(aesKey);
            ThermostatControl.thermostat.receiveRequest(unlock_door);
        }
    }

    void copyMessage(Message m){
        capturedMessage = new Message(m);
        ticketCaptured = true;
        System.out.println("\n(((Service Granting Ticket Captured by Attacker)))\n");
    }

    public void captureSymmetricKey(String aesKey) {
        this.aesKey = aesKey;
        keyCaptured = true;
    }

    public void captureKey(PublicKey publicKey) {
        thermPublicKey = publicKey;
    }
}