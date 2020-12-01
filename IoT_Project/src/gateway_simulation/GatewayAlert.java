package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Security alerts are displayed in this window

public class GatewayAlert extends JDialog {
    private JPanel contentPane;
    private JLabel statusMessage;
    private JLabel statusMessage2;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX;
    int frameY;

    public GatewayAlert() {
        setTitle("Gateway Alert");
        setContentPane(contentPane);
        pack();
        place();
    }

    private void place() {
        frameX = (screenSize.width / 2) - (getWidth() / 2);
        frameY = (screenSize.height / 2);
        setLocation(frameX, frameY);
    }

    public void setStatus(String s) {
        statusMessage.setText(s);
        pack();
        place();
    }

    //for ticket expiration time
    public void setStatus2(String s) {
        statusMessage2.setText(s);
        pack();
        place();
    }
}







