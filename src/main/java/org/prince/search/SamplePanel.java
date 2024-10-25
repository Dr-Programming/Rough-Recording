//This sample panel is currently being developed and is of no use in actual application as of now.
//1500 x 988

package org.prince.search;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class SamplePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public SamplePanel() {
		setBackground(SystemColor.control);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JPanel panel_1 = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel_1, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panel_1, -693, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_1, 642, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, panel_1, -10, SpringLayout.EAST, this);
		panel_1.setBackground(Color.ORANGE);
		add(panel_1);
		
		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, panel_1);
		springLayout.putConstraint(SpringLayout.EAST, panel, -6, SpringLayout.WEST, panel_1);
		panel.setBackground(Color.WHITE);
		add(panel);

	}
}
