package org.prince.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.prince.configuration.ConfigManager;
import org.prince.configuration.Fields;

public class SampleDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private JButton saveButton;
	
	private JLabel restoreDefaultLabel;
	private JLabel settingsStatus_LB;
	
	private JTextField showPathTF;
	
	private boolean isChange = false;
	private boolean isDefault = true;
	
	private String userLocation;

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
		
		JLabel infoLabel_1 = new JLabel("Current Settings :");
		infoLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		infoLabel_1.setBounds(31, 37, 109, 17);
		contentPanel.add(infoLabel_1);
		
		settingsStatus_LB = new JLabel("DEFAULT SETTINGS");
		settingsStatus_LB.setFont(new Font("Monospaced", Font.BOLD, 18));
		settingsStatus_LB.setBounds(150, 37, 256, 17);
		contentPanel.add(settingsStatus_LB);
		
		JLabel inputLabel_1 = new JLabel("Video Save To :");
		inputLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		inputLabel_1.setBounds(44, 87, 96, 17);
		contentPanel.add(inputLabel_1);
		
		showPathTF = new JTextField();
		showPathTF.setFocusTraversalKeysEnabled(false);
		showPathTF.setFont(new Font("Monospaced", Font.PLAIN, 15));
		showPathTF.setBackground(Color.WHITE);
		showPathTF.setEditable(false);
		showPathTF.setText(userLocation);
		showPathTF.setBounds(150, 82, 295, 27);
		contentPanel.add(showPathTF);
		showPathTF.setColumns(10);
		
		JLabel chooseLocationLabel = new JLabel("Choose Location");
		chooseLocationLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveFilePathSelection();
			}
		});
		chooseLocationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		chooseLocationLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chooseLocationLabel.setBounds(150, 112, 96, 14);
		contentPanel.add(chooseLocationLabel);
		
		restoreDefaultLabel = new JLabel("Restore Defaults ?");
		restoreDefaultLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				restoreDefaults(configManager);
			}
		});
		restoreDefaultLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		restoreDefaultLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
		restoreDefaultLabel.setBounds(336, 137, 109, 14);
		restoreDefaultLabel.setVisible(false);
		contentPanel.add(restoreDefaultLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				saveButton = new JButton("Save");
				saveButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveSettings(configManager);
					}
				});
				saveButton.setFocusPainted(false);
				saveButton.setActionCommand("OK");
				saveButton.setFocusable(false);
				saveButton.setVisible(isChange);
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
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
			restoreDefaultLabel.setVisible(true);
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
			userLocation = fileChooser.getSelectedFile().getAbsolutePath() + File.separator;
			System.out.println(userLocation);
			restoreDefaultLabel.setVisible(true);
			settingsStatus_LB.setText("USER SETTINGS");
			showPathTF.setText(userLocation);
			isChange = true;
			isDefault = false;
			saveButton.setVisible(isChange);
		}
	}
	
	private void restoreDefaults(ConfigManager configManager) {
		isChange = true;
		settingsStatus_LB.setText("DEFAULT SETTINGS");
		configManager.restoreDefaults();
		showPathTF.setText(configManager.getProperty(Fields.savePath.toString()));
		restoreDefaultLabel.setVisible(false);
		isDefault = true;
		saveButton.setVisible(isChange);
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
//	
//	private void uiChnages() {
//		
//	}
}
