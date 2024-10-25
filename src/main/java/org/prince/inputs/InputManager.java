package org.prince.inputs;

import java.awt.Color;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class InputManager {
	
	private final Color DefaultOuterBorderColor = new Color(180, 180, 180);
	private final Color FocusBorderColor = new Color(30, 144, 255);
	private final Color ErrorBorderColor = new Color(235, 64, 52);
	
	private int LEFT = 1;
	private int RIGHT = 1;
	private int TOP = 1;
	private int BOTTOM = 1;
	
	private EmptyBorder InnerBorder;
	
	private MatteBorder DefaultOuterBorder;
	private MatteBorder FocusOuterBorder;
	private MatteBorder ErrorOuterBorder;
	
	public InputManager() {
		InnerBorder = new EmptyBorder(2,2,2,2);
		DefaultOuterBorder = new MatteBorder(TOP, LEFT, BOTTOM, RIGHT, (Color) DefaultOuterBorderColor);
		FocusOuterBorder = new MatteBorder(TOP, LEFT, BOTTOM, RIGHT, (Color) FocusBorderColor);
		ErrorOuterBorder = new MatteBorder(2,2,2,2, (Color) ErrorBorderColor);
	}
	
	public MatteBorder getCustomMatteBorder(int top, int left, int bottom, int right, BorderType borderType) {
		switch(borderType) {
			case DEFAULT:
				return new MatteBorder(top, left, bottom, right, (Color) DefaultOuterBorderColor);
			case FOCUS:
				return new MatteBorder(top, left, bottom, right, (Color) FocusBorderColor);
			case ERROR:
				return new MatteBorder(top, left, bottom, right, (Color) ErrorBorderColor);
			default:
				return null;
		}
	}
	
	public EmptyBorder getInnerBorder() {
		return InnerBorder;
	}

	public CompoundBorder getDefaultOuterBorder() {
		return new CompoundBorder((MatteBorder) DefaultOuterBorder, (EmptyBorder) InnerBorder);
	}

	public CompoundBorder getFocusOuterBorder() {
		return new CompoundBorder((MatteBorder) FocusOuterBorder, (EmptyBorder) InnerBorder);
	}

	public CompoundBorder getErrorOuterBorder() {
		return new CompoundBorder((MatteBorder) ErrorOuterBorder, (EmptyBorder) InnerBorder);
	}
	
	

}
