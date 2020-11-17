package gateway_simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GatewayAlert extends JDialog {
    private JPanel contentPane;
    private JLabel statusMessage;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX; int frameY;
    public GatewayAlert() {
        setTitle("Gateway");
        setContentPane(contentPane);
        pack();
        frameX = (screenSize.width / 2) - (getWidth() / 2);
        frameY = (screenSize.height / 2);
        setLocation(frameX, frameY);

        setVisible(true);
    }

    public void setStatus(String s) {
        statusMessage.setText(s);
    }
}



