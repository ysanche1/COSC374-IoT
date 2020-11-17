package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;

import static java.lang.Integer.valueOf;

public class AttackSim extends JFrame {
    public String aesKey;
    public PublicKey thermPublicKey;
    private JButton suspiciousActionButton;
    private JPanel panel1;
    private JButton replayAttackButton;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX; int frameY;
    Message capturedMessage;
    Message confirmation;
    public AttackSim() {
        setTitle("Gateway");
        setContentPane(panel1);
        pack();
        frameX = (screenSize.width / 2) - (getWidth() / 2);
        frameY = (screenSize.height / 2);
        setLocation(frameX, frameY);
        setVisible(true);
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
                    attemptPortScan();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void attemptLogin() throws Exception {
        System.out.println("\n(((Attempting Login Using Captured Ticket)))");
        confirmation = Main.gateway.receiveSGT(capturedMessage);
        confirmation.displayContents();
    }

    public void capture(Message m) throws CloneNotSupportedException {
        capturedMessage =  (Message) m.clone();

    }

    private void attemptPortScan() throws Exception {
        System.out.println("\n(((Attempting to unlock door)))");
        Message portScan = new Message("UNLOCK DOOR", "");
        RSAAlgorithm rsa = new RSAAlgorithm(thermPublicKey);
        AESAlgorithm aes = new AESAlgorithm(aesKey);
        portScan.command = aes.encrypt(portScan.command);
        portScan.key = rsa.encrypt(aesKey);
        ThermostatControl.thermostat.receiveRequest(portScan);
    }

    void copyMessage(Message m){
        capturedMessage = new Message(m);
        System.out.println("\n(((Service Granting Ticket Captured by Attacker)))");
    }
}