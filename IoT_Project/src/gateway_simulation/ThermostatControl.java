package gateway_simulation;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;


public class ThermostatControl extends JFrame{
    Thermostat t;
    private JPanel panel1;
    private JButton increaseButton;
    private JButton decreaseButton;
    private JTextField customTempField;
    private JButton customButton;
    private JLabel header;
    AESAlgorithm aes;
    String aesKey;
    String thermostatResponse;
    enum Command {INCREASE, DECREASE, CUSTOM}

public ThermostatControl(String aesKey) throws NoSuchAlgorithmException {
        this.aesKey = aesKey;
        t = new Thermostat();
        String increase = "INCREASE";
        String decrease = "DECREASE";
        String custom = "CUSTOM";
        Main.gateway.initialize(t);
    setContentPane(panel1);
    setTitle("Thermostat Controller");
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
    getRootPane().setDefaultButton(customButton);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // put the window in a nice spot
    int frameX = (screenSize.width / 2) - (getWidth() / 2);             //
    int frameY = (screenSize.height / 2) - (getHeight());;                                 //
    setLocation(frameX, frameY);                                        //
    setVisible(true);
    t.main();
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
        try {
            newRequest(custom, customTempField.getText());
        } catch (Exception interruptedException) {
        }
    });
}
    private void newRequest(String command, String custom) throws Exception {
        aes = new AESAlgorithm(aesKey);
        Message m = new Message(command, custom);
        aes.encryptMessage(m);
        m = Main.gateway.relayRequest(m, t);
        aesKey = aes.decrypt(m.key);
        customTempField.setText(aes.decrypt(m.response));
    }
}


