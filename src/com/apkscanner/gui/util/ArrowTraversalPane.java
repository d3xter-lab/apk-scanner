package com.apkscanner.gui.util;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.apkscanner.util.Log;

public class ArrowTraversalPane extends JOptionPane
{
	private static final long serialVersionUID = 4947402878882910721L;
	private static final int Integer = 0;
	
	public ArrowTraversalPane(Object message, int messageType, int optionType)
	{
		super(message, optionType, messageType);
		
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_UNDEFINED));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_UNDEFINED));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
	}

	static public int showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
	    JOptionPane pane = new ArrowTraversalPane(message, optionType, messageType);

		pane.setMessageType(messageType);
		pane.setIcon(icon);
		pane.setOptionType(optionType);
		pane.setOptions(options);
		pane.setInitialValue(initialValue);

		JDialog dialog = pane.createDialog(parentComponent, title);
		dialog.setVisible(true);
		dialog.dispose();
		
		int ret = -1;
		if(pane.getValue() != null && !pane.getValue().equals((Integer)-1)) {
			for(Object o: options) {
				ret++;
				if(o.equals(pane.getValue())) break;
			}
		}
		return ret;
	}

	static public JOptionPane makeOptionPane(Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
		
		messageType = JOptionPane.PLAIN_MESSAGE;
		
	    JOptionPane pane = new ArrowTraversalPane(message, optionType, messageType);
	    
		pane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		//pane.setIcon(icon);
		
		pane.setOptionType(optionType);
		pane.setOptions(options);
		pane.setInitialValue(initialValue);
		
		
        return titled(pane, title);
	}
	
    static <T extends JComponent> T titled(T c, String title) {
        c.setBorder(BorderFactory.createTitledBorder(title));
        return c;
    }
}