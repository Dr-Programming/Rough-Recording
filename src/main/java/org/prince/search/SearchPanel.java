//This sample panel is currently being developed and is of no use in actual application as of now.
//1500 x 988

package org.prince.search;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Cursor;
import javax.swing.SwingConstants;

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public SearchPanel() {
		setBackground(SystemColor.control);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panel, -699, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, this);
		panel.setBackground(Color.WHITE);
		add(panel);
		
		JPanel panel_1 = new JPanel();
		springLayout.putConstraint(SpringLayout.WEST, panel_1, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, panel_1, -6, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, panel_1, 10, SpringLayout.NORTH, this);
		panel_1.setBackground(Color.WHITE);
		add(panel_1);
		
		JPanel panel_2 = new JPanel();
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -6, SpringLayout.NORTH, panel_2);
		springLayout.putConstraint(SpringLayout.WEST, panel_2, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, panel_2, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_1, -6, SpringLayout.NORTH, panel_2);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		panel_1.add(panel_3, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Search View");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		panel_3.add(lblNewLabel);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		panel_1.add(panel_4, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Play Video");
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setFocusPainted(false);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_4.add(btnNewButton);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		panel_1.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Kindly Select the Video");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_5.add(lblNewLabel_1, BorderLayout.CENTER);
		springLayout.putConstraint(SpringLayout.NORTH, panel_2, -229, SpringLayout.SOUTH, this);
		panel_2.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_2, -10, SpringLayout.SOUTH, this);
		add(panel_2);

	}
}
