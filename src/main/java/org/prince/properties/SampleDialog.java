package org.prince.properties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.prince.configuration.ConfigManager;
import org.prince.configuration.Fields;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SampleDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtSample;
	private JLabel Restore_LB;
	private JLabel settingsStatus_LB;
	private JButton okButton;
	private boolean isChange = false;
	private boolean isDefault = true;
	private String userLocation;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			SampleDialog dialog = new SampleDialog();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public SampleDialog(JFrame parent, ConfigManager configManager) {
		setTitle("Settings");
		setResizable(false);
		setBounds(100, 100, 505, 264);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(parent);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		userLocation = configManager.getProperty(Fields.savePath.toString());
		
		JLabel lblNewLabel = new JLabel("Current Settings :");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(31, 37, 109, 17);
		contentPanel.add(lblNewLabel);
		
		settingsStatus_LB = new JLabel("DEFAULT SETTINGS");
		settingsStatus_LB.setFont(new Font("Monospaced", Font.BOLD, 18));
		settingsStatus_LB.setBounds(150, 37, 256, 17);
		contentPanel.add(settingsStatus_LB);
		
		JLabel lblNewLabel_2 = new JLabel("Video Save To :");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(44, 87, 96, 17);
		contentPanel.add(lblNewLabel_2);
		
		txtSample = new JTextField();
		txtSample.setFocusTraversalKeysEnabled(false);
		txtSample.setFont(new Font("Monospaced", Font.PLAIN, 15));
		txtSample.setBackground(Color.WHITE);
		txtSample.setEditable(false);
		txtSample.setText(userLocation);
		txtSample.setBounds(150, 82, 295, 27);
		contentPanel.add(txtSample);
		txtSample.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Choose Location");
		lblNewLabel_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveFilePathSelection();
			}
		});
		lblNewLabel_3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel_3.setBounds(150, 112, 96, 14);
		contentPanel.add(lblNewLabel_3);
		
		Restore_LB = new JLabel("Restore Defaults ?");
		Restore_LB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				restoreDefaults(configManager);
			}
		});
		Restore_LB.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Restore_LB.setFont(new Font("Tahoma", Font.ITALIC, 12));
		Restore_LB.setBounds(336, 137, 109, 14);
		Restore_LB.setVisible(false);
		contentPanel.add(Restore_LB);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveSettings(configManager);
					}
				});
				okButton.setFocusPainted(false);
				okButton.setActionCommand("OK");
				okButton.setFocusable(false);
				okButton.setVisible(isChange);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						configManager.setProperty(Fields.savePath.toString(), userLocation);
						configManager.setProperty(Fields.CHANGE.toString(), "TRUE");
						configManager.saveUserProperties();
						SampleDialog.this.dispose();
					}
				});
				cancelButton.setFocusPainted(false);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		if(configManager.getProperty(Fields.CHANGE.toString()).equals("TRUE")) {
			Restore_LB.setVisible(true);
			settingsStatus_LB.setText("USER SETTINGS");
			isDefault = false;
		}
	}
	
	private void saveFilePathSelection() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Save Location");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int selection = fileChooser.showOpenDialog(SampleDialog.this);
		if(selection == JFileChooser.APPROVE_OPTION) {
			userLocation = fileChooser.getSelectedFile().getAbsolutePath();
			System.out.println(userLocation);
			Restore_LB.setVisible(true);
			settingsStatus_LB.setText("USER SETTINGS");
			txtSample.setText(userLocation);
			isChange = true;
			isDefault = false;
			okButton.setVisible(isChange);
		}
	}
	
	private void restoreDefaults(ConfigManager configManager) {
		isChange = true;
		settingsStatus_LB.setText("DEFAULT SETTINGS");
		configManager.restoreDefaults();
		txtSample.setText(configManager.getProperty(Fields.savePath.toString()));
		Restore_LB.setVisible(false);
		isDefault = true;
		okButton.setVisible(isChange);
	}
	
	private void saveSettings(ConfigManager configManager) {
		if(isDefault) {
			configManager.saveUserProperties();
		}else {
			configManager.setProperty(Fields.savePath.toString(), userLocation);
			configManager.setProperty(Fields.CHANGE.toString(), "TRUE");
			configManager.saveUserProperties();
		}
		
		SampleDialog.this.dispose();
	}
	
	private void uiChnages() {
		
	}
}
