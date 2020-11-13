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
    public AttackSim(Message message) {
        capturedMessage = message;
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
        Main.gateway.receiveSGT(capturedMessage);
    }

    private void attemptPortScan() throws Exception {
        Message portScan = new Message("PORTSCAN", "");
        RSAAlgorithm rsa = new RSAAlgorithm(thermPublicKey);
        AESAlgorithm aes = new AESAlgorithm(aesKey);
        portScan.command = aes.encrypt(portScan.command);
        portScan.key = rsa.encrypt(aesKey);
        ThermostatControl.thermostat.receiveRequest(portScan);
    }

    private void copyMessage(Message m){
        capturedMessage = new Message(m);
    }
}