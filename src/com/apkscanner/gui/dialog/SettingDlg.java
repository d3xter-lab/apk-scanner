package com.apkscanner.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.apkscanner.gui.theme.TabbedPaneUIManager;
import com.apkscanner.gui.theme.tabbedpane.PlasticTabbedPaneUI;
import com.apkscanner.gui.util.ApkFileChooser;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.SystemUtil;

public class SettingDlg extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -854353051241196941L;

	private JTextField textExcutePath;

	private String strExecuteEditorPath;
	private String strframeworkResPath;

	private String strLanguage;
	private String strSetTheme;
	private String strSetTabbedUI;
	private String strFont;

	private boolean isSamePackage;
	private int changed = 0;

	JButton savebutton, exitbutton;
	JButton browser1,browser2,browser3;

	JComboBox<String> comboBox;
	JComboBox<String> themecomboBox;
	JComboBox<String> tabbedUIComboBox;
	JComboBox<String> fontComboBox;

	JCheckBox chckbxNewCheckBox;

	JList<String> jlist;
	ArrayList<String> resList = new ArrayList<String>();

	public SettingDlg() {
		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
			private static final long serialVersionUID = -8988954049940512230L;
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		readSettings();
	}

	private void readSettings()
	{
		strExecuteEditorPath = (String)Resource.PROP_EDITOR.getData();
		if(strExecuteEditorPath == null) {
			try {
				strExecuteEditorPath = SystemUtil.getDefaultEditor();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Resource.PROP_EDITOR.setData(strExecuteEditorPath);
		}

		strLanguage = (String)Resource.PROP_LANGUAGE.getData();
		if(strLanguage == null) {
			if(SystemUtil.getUserLanguage().indexOf("ko") > -1) {
				strLanguage = "ko";
			} else {
				strLanguage = "en";
			}
			Resource.PROP_LANGUAGE.setData(strLanguage);
		}

		strSetTheme = (String)Resource.PROP_CURRENT_THEME.getData();
		if(strSetTheme == null) {
			strSetTheme = UIManager.getSystemLookAndFeelClassName();			
			Resource.PROP_CURRENT_THEME.setData(strSetTheme);
		}

		strSetTabbedUI = (String)Resource.PROP_TABBED_UI_THEME.getData(PlasticTabbedPaneUI.class.getName());

		isSamePackage = (boolean)Resource.PROP_CHECK_INSTALLED.getData(false);

		strframeworkResPath = (String)Resource.PROP_FRAMEWORK_RES.getData();
		if(strframeworkResPath == null) {
			strframeworkResPath = "";
			Resource.PROP_FRAMEWORK_RES.setData(strframeworkResPath);
		}

		for(String s: strframeworkResPath.split(";")) {
			resList.add(s);
		}
	}

	private void saveSettings()
	{
		Resource.PROP_EDITOR.setData(strExecuteEditorPath);
		Resource.PROP_LANGUAGE.setData(strLanguage);
		Resource.PROP_CHECK_INSTALLED.setData(isSamePackage);
		Resource.PROP_FRAMEWORK_RES.setData(strframeworkResPath);

		if(Resource.PROP_CURRENT_THEME.getData().toString().equals(strSetTheme)==false) {
			Resource.PROP_CURRENT_THEME.setData(strSetTheme);
			changed = 1;
		}
		if(Resource.PROP_TABBED_UI_THEME.getData(PlasticTabbedPaneUI.class.getName()).toString().equals(strSetTabbedUI) == false) {
			Resource.PROP_TABBED_UI_THEME.setData(strSetTabbedUI);
			changed = 1;
		}

		if(strFont != fontComboBox.getSelectedItem()) {
			Resource.PROP_BASE_FONT.setData(fontComboBox.getSelectedItem());
			changed = 1;
		}

	}

	public int makeDialog(Component component) {
		this.setTitle(Resource.STR_SETTINGS_TITLE.getString());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setSize(new Dimension(480,245));
		this.setResizable( false );
		this.setLocationRelativeTo(component);
		this.setModal(true);
		getContentPane().add(makeLayoutPanel());
		//this.pack();
		//dlgDialog.setLocationRelativeTo(null);
		this.setVisible(true);
		//readSettingInfoFromFile();
		//readSettingInfoFromFile();
		return changed; 
	}

	JPanel makeLayoutPanel() {
		JPanel panel = new JPanel();

		panel.setLayout(null);
		panel.setOpaque(true);

		JLabel userLabel = new JLabel(Resource.STR_SETTINGS_EDITOR.getString());
		userLabel.setBounds(10, 10, 116, 25);
		panel.add(userLabel);

		textExcutePath = new JTextField(20);
		textExcutePath.setText(strExecuteEditorPath);

		textExcutePath.setBounds(121, 10, 283, 25);
		panel.add(textExcutePath);		

		JLabel frameworkLabel = new JLabel(Resource.STR_SETTINGS_RES.getString());
		frameworkLabel.setBounds(10, 40, 114, 25);
		panel.add(frameworkLabel);

		jlist = new JList<String>();
		JScrollPane scrollPane1 = new JScrollPane(jlist);
		scrollPane1.setPreferredSize(new Dimension(50, 400));
		scrollPane1.setBounds(121, 40, 283, 50);
		panel.add(scrollPane1);
		jlist.setListData(resList.toArray(new String[0]));

		savebutton = new JButton(Resource.STR_BTN_OK.getString());
		savebutton.setBounds(288, 185, 80, 25);
		savebutton.addActionListener(this);
		savebutton.setFocusable(false);
		panel.add(savebutton);


		exitbutton = new JButton(Resource.STR_BTN_CANCEL.getString());
		exitbutton.setBounds(380, 185, 80, 25);
		exitbutton.addActionListener(this);
		exitbutton.setFocusable(false);
		panel.add(exitbutton);


		browser1 = new JButton("...");
		browser1.setBounds(405, 10, 64, 25);
		browser1.addActionListener(this);
		browser1.setFocusable(false);
		panel.add(browser1);

		browser2 = new JButton(Resource.STR_BTN_ADD.getString());
		browser2.setBounds(405, 40, 64, 24);
		browser2.addActionListener(this);
		browser2.setFocusable(false);
		panel.add(browser2);

		browser3 = new JButton(Resource.STR_BTN_DEL.getString());
		browser3.setBounds(405, 65, 64, 24);
		browser3.addActionListener(this);
		browser3.setFocusable(false);
		panel.add(browser3);

		chckbxNewCheckBox = new JCheckBox(Resource.STR_SETTINGS_CHECK_INSTALLED.getString());

		chckbxNewCheckBox.setSelected(isSamePackage);

		chckbxNewCheckBox.addActionListener(this);
		chckbxNewCheckBox.setBounds(10, 93, 236, 25);
		panel.add(chckbxNewCheckBox);

		JLabel label = new JLabel(Resource.STR_SETTINGS_LANGUAGE.getString());
		label.setBounds(15, 120, 60, 25);
		panel.add(label);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(87, 120, 94, 24);	    
		comboBox.addItem("ko");
		comboBox.addItem("en");	    
		comboBox.setSelectedItem(strLanguage);
		panel.add(comboBox);

		JLabel themelabel = new JLabel("Theme");
		themelabel.setBounds(200, 120, 60, 25);
		panel.add(themelabel);

		themecomboBox = new JComboBox<String>();
		themecomboBox.setBounds(265, 120, 190, 25);
		panel.add(themecomboBox);

		String selItem = null;
		for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
			themecomboBox.addItem(info.getName());
			if(info.getClassName().equals(strSetTheme)) {
				selItem = info.getName();
			}
		}
		themecomboBox.getModel().setSelectedItem(selItem);

		JLabel tabbedUIlabel = new JLabel("Tabbed UI");
		tabbedUIlabel.setBounds(200, 150, 60, 25);
		panel.add(tabbedUIlabel);

		tabbedUIComboBox = new JComboBox<String>();
		tabbedUIComboBox.setBounds(265, 150, 190, 25);
		panel.add(tabbedUIComboBox);

		selItem = null;
		tabbedUIComboBox.addItem("None");
		for (TabbedPaneUIManager.TabbedPaneUIInfo info : TabbedPaneUIManager.getUIThemes()) {
			tabbedUIComboBox.addItem(info.getName());
			if(info.getClassName().equals(strSetTabbedUI)) {
				selItem = info.getName();
			}
		}
		if(selItem == null || strSetTabbedUI.isEmpty()) selItem = "None";
		tabbedUIComboBox.getModel().setSelectedItem(selItem);


		fontComboBox = new JComboBox<String>();
		fontComboBox.setBounds(10, 150, 170, 25);
		fontComboBox.setEditable(true);
		panel.add(fontComboBox);

		String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		Arrays.sort(fontFamilyNames);
		for (String font : fontFamilyNames) {
			fontComboBox.addItem(font);
		}
		strFont = UIManager.getFont("Panel.font").getFamily();
		fontComboBox.getModel().setSelectedItem(strFont);

		return panel;
	}

	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				SettingDlg dlg = new SettingDlg();
				dlg.makeDialog(null);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == savebutton) {
			//Log.i("save");

			String editPath = SystemUtil.getRealPath(textExcutePath.getText().trim());
			if(editPath != null && new File(editPath).canExecute()) {
				strExecuteEditorPath = textExcutePath.getText().trim();
			} else {
				textExcutePath.setText(strExecuteEditorPath);
			}

			strframeworkResPath = "";
			for(String f: resList) {
				strframeworkResPath += f + ";";
			}

			isSamePackage = chckbxNewCheckBox.isSelected();
			strLanguage = (String)comboBox.getSelectedItem();
			String selItem = (String)themecomboBox.getSelectedItem();
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if(info.getName().equals(selItem)) {
					selItem = info.getClassName();
					break;
				}
			}
			strSetTheme = selItem;

			selItem = (String)tabbedUIComboBox.getSelectedItem();
			for (TabbedPaneUIManager.TabbedPaneUIInfo info : TabbedPaneUIManager.getUIThemes()) {
				if(info.getName().equals(selItem)) {
					selItem = info.getClassName();
				}
			}
			if("None".equals(selItem)) selItem = "";
			strSetTabbedUI = selItem;

			saveSettings();

			this.dispose();
		} else if(e.getSource() == exitbutton) {
			//Log.i("exit");
			this.dispose();
		} else if(e.getSource() == browser1) {
			JFileChooser jfc = new JFileChooser();										
			if(jfc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;

			File dir = jfc.getSelectedFile();
			if(dir!=null) {
				strExecuteEditorPath = dir.getPath();
				textExcutePath.setText(dir.getPath()); 
			}
		} else if(e.getSource() == browser2) {
			String file = ApkFileChooser.openApkFilePath(this);

			if(file == null || file.isEmpty()) return;
			for(String f: resList) {
				if(file.equals(f)) return;
			}
			resList.add(file);
			jlist.setListData(resList.toArray(new String[0]));
		} else if(e.getSource() == browser3) {
			if(jlist.getSelectedIndex() < 0) return;
			resList.remove(jlist.getSelectedIndex());
			jlist.setListData(resList.toArray(new String[0]));
		}
	}
}
