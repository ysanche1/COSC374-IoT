package gateway_simulation;

import javax.swing.*;
import java.awt.*;

public class AttackSim extends JFrame {
    private JButton suspiciousActionButton;
    private JPanel panel1;
    private JButton replayAttackButton;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int frameX; int frameY;
    public AttackSim() {
        setTitle("Gateway");
        setContentPane(panel1);
        pack();
        frameX = (screenSize.width / 2) - (getWidth() / 2);
        frameY = (screenSize.height / 2);
        setLocation(frameX, frameY);
        setVisible(true);
    }
}