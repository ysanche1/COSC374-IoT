package gateway_simulation;


import javax.swing.*;
import java.awt.*;
import java.security.PublicKey;


public class ThermostatControl extends JFrame implements Runnable{
    static Thermostat thermostat;
    private JPanel panel1;
    private JButton increaseButton;
    private JButton decreaseButton;
    private JTextField customTempField;
    private JButton customButton;
    private JLabel updateField;
    private JPanel topPanel;
    private JLabel tempDisplay;
    private JPanel space;
    private JPanel headerPanel;
    private JPanel tempDisplayPanel;
    private JLabel gatewayNotification;
    private JButton replayAttackButton;
    private JButton suspiciousActionButton;
    private JButton cloudCrash;
    AESAlgorithm aes;
    String aesKey;
    PublicKey gatewayPublicKey;
    AttackSim atk;
public ThermostatControl(String aesKey, Message replayMessage, Message finalMessage) throws Exception {
    atk = new AttackSim(replayAttackButton, suspiciousActionButton, cloudCrash, replayMessage);
    this.aesKey = aesKey;
        aes = new AESAlgorithm(aesKey);
        gatewayPublicKey = Main.app.rsaK.strToKey(aes.decrypt(finalMessage.pub_key));
        Main.app.rsaE = new RSAAlgorithm(gatewayPublicKey);
        this.aesKey = aes.decrypt(finalMessage.key);
        thermostat = new Thermostat();
        String increase = "INCREASE";
        String decrease = "DECREASE";
        String custom = "CUSTOM";
        Main.gateway.initialize(thermostat);
    setContentPane(panel1);
    setTitle("Thermostat Controller");
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
    getRootPane().setDefaultButton(customButton);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // put the window in a nice spot
    int frameX = (screenSize.width / 2) - (getWidth() / 2);             //
    int frameY = (screenSize.height / 2) - (getHeight());;                                 //
    setLocation(frameX, frameY);
    setVisible(true);
    thermostat.main();
    increaseButton.addActionListener(e -> {
        try {
            newRequest(increase, "");
        } catch (Exception interruptedException) {
            interruptedException.printStackTrace();
        }
    });
    decreaseButton.addActionListener(e -> {
        try {
            newRequest(decrease,"");
        } catch (Exception interruptedException) {
        }
    });
    customButton.addActionListener(e -> {
        int customTemp = 0;
        try {
           customTemp = Integer.parseInt(customTempField.getText());
           newRequest(custom, String.valueOf(customTemp));
        }
        catch (NumberFormatException nfe) {
            updateField.setText("Non-numeric Value entered");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    });
}
    private void newRequest(String command, String custom) throws Exception {

        customTempField.setText("");
        aes = new AESAlgorithm(aesKey);
        Message m = new Message(command, custom);
        aes.encryptMessage(m);
        m.key = Main.app.rsaE.encrypt(aesKey);
        m.key = Main.app.rsaE.encrypt(aesKey);
        Main.gateway.relayRequest(m, thermostat);
    }

    public void receiveResponse(Message m) throws Exception {
        aesKey = aes.decrypt(m.key);
        atk.aesKey = aesKey;
        atk.captureSymmetricKey(aesKey);
        aes = new AESAlgorithm(aesKey);
        if(m.error == true) {
            increaseButton.setEnabled(false);
            decreaseButton.setEnabled(false);
            customButton.setEnabled(false);
            updateField.setText(aes.decrypt(m.update));
        }
        else
            updateField.setText(aes.decrypt(m.update));
    }

    public void subscribe(Message m) throws Exception {
    tempDisplay.setText(Main.app.rsaD.decrypt(m.update)+"\u00B0");
    }

    public void setNotification(String s){
    gatewayNotification.setText(s);
    }

    @Override
    public void run() {

    }
}


