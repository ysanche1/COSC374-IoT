import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class Main extends JFrame {

	AESAlgorithm aesAlgo;
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		
		aesAlgo = new AESAlgorithm("lv39eptlvuhaqqsr");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblPlaintext = new JLabel("PlainText");
		GridBagConstraints gbc_lblPlaintext = new GridBagConstraints();
		gbc_lblPlaintext.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlaintext.gridx = 0;
		gbc_lblPlaintext.gridy = 0;
		contentPane.add(lblPlaintext, gbc_lblPlaintext);
		
		JLabel lblNewLabel = new JLabel("CipherText");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		JTextArea plainTextField = new JTextArea();
		scrollPane.setViewportView(plainTextField);
		plainTextField.setWrapStyleWord(true);
		plainTextField.setLineWrap(true);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 1;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		JTextArea cipherTextField = new JTextArea();
		scrollPane_1.setViewportView(cipherTextField);
		cipherTextField.setWrapStyleWord(true);
		cipherTextField.setLineWrap(true);
		
		JButton btn_encrypt = new JButton("Encrypt");
		GridBagConstraints gbc_btn_encrypt = new GridBagConstraints();
		gbc_btn_encrypt.insets = new Insets(0, 0, 0, 5);
		gbc_btn_encrypt.gridx = 0;
		gbc_btn_encrypt.gridy = 3;
		contentPane.add(btn_encrypt, gbc_btn_encrypt);
		
		JButton btn_decrypt = new JButton("Decrypt");
		GridBagConstraints gbc_btn_decrypt = new GridBagConstraints();
		gbc_btn_decrypt.gridx = 1;
		gbc_btn_decrypt.gridy = 3;
		contentPane.add(btn_decrypt, gbc_btn_decrypt);
		

		btn_encrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String plainText = plainTextField.getText();
					String cipherText = aesAlgo.Encrypt(plainText);
					cipherTextField.setText(cipherText);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
				
			}
		});
		

		btn_decrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String cipherText = cipherTextField.getText();
					String plainText = aesAlgo.Decrypt(cipherText);
					cipherTextField.setText(plainText);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
				
			}
		});
		
		


		
	}

}
