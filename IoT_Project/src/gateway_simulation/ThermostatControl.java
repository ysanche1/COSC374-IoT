package gateway_simulation;


import javax.swing.*;
import java.awt.*;
import java.security.PublicKey;


public class ThermostatControl extends JFrame implements Runnable{
    JPanel main_panel;
    JPanel top_panel;
    JPanel space;
    JPanel headerPanel;
    JLabel updateField;
    JLabel tempDisplay;
    JButton increase_button;
    JButton decrease_button;
    JTextField custom_temp_field;
    JButton custom_button;
    JLabel gateway_notification;
    JButton cloudCrash;
    JButton suspiciousActionButton;
    JButton replayAttackButton;
    private JPanel custom_temp_panel;
    static Thermostat thermostat;
    AESAlgorithm aes;
    String aesKey;
    PublicKey gatewayPublicKey;
    AttackSim atk;


public void initialize(String user, String aesKey, Message replayMessage, Message finalMessage) throws Exception {
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
    setContentPane(main_panel);
    setTitle("Thermostat Controller");
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
    getRootPane().setDefaultButton(custom_button);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // put the window in a nice spot
    int frameX = (screenSize.width / 2) - (getWidth() / 2);             //
    int frameY = (screenSize.height / 2) - (getHeight());;                                 //
    setLocation(frameX, frameY);
    setVisible(true);
    thermostat.main();
    increase_button.addActionListener(e -> {
        try {
            newRequest(increase, "");
        } catch (Exception interruptedException) {
            interruptedException.printStackTrace();
        }
    });
    decrease_button.addActionListener(e -> {
        try {
            newRequest(decrease,"");
        } catch (Exception interruptedException) {
        }
    });
    custom_button.addActionListener(e -> {
        int customTemp = 0;
        try {
           customTemp = Integer.parseInt(custom_temp_field.getText());
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

        custom_temp_field.setText("");
        aes = new AESAlgorithm(aesKey);
        Message m = new Message(command, custom);
        aes.encryptMessage(m);
        m.key = Main.app.rsaE.encrypt(aesKey);
        m.key = Main.app.rsaE.encrypt(aesKey);
        Main.gateway.relayRequest(m, thermostat);
    }

    public void receiveResponse(Message m) throws Exception {
        aesKey = Main.app.rsaD.decrypt(m.key);
        atk.aesKey = aesKey;
        atk.captureSymmetricKey(aesKey);
        aes = new AESAlgorithm(aesKey);
        if(m.error == true) {
            increase_button.setEnabled(false);
            decrease_button.setEnabled(false);
            custom_button.setEnabled(false);
            updateField.setText(aes.decrypt(m.update));
        }
        else
            updateField.setText(aes.decrypt(m.update));
    }

    public void subscribe(Message m) throws Exception {
    tempDisplay.setText(Main.app.rsaD.decrypt(m.update)+"\u00B0");
    }

    public void setNotification(String s){
    gateway_notification.setText(s);
    }

    @Override
    public void run() {

    }
}


