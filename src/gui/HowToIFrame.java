package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

public class HowToIFrame extends JInternalFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1386671278073762248L;
	
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		JFrame test = CompFactory.buildTestFrame(data, new HowToIFrame());
		test.setVisible(true);
	}

	private class KeyboardTip{
		public String key, tip;
		public KeyboardTip(String key, String tip) {
			this.key = key;
			this.tip = tip;
		}
	}
	
	KeyboardTip[] richEditorTips = new KeyboardTip[] {
			new KeyboardTip("CTRL + M  ", "Open Attack Builder Dialog"),
			new KeyboardTip("@Name then CTRL + Space with Caret after text  ", 
					"Opens dialog to add a link to the selected thing in the database"),
			new KeyboardTip("&Ref_Name then CTRL + Space with Caret after text  ", 
					"Search for a quick insert string by name"),
			new KeyboardTip("/m  ", "Insert a quick / empty Melee Attack"),
			new KeyboardTip("/r  ", "Insert a quick / empty Ranged Attack")
	};
	
	String[] initControls = new String[] {
		"ALT + Left Click on an Actor in Initiative to Remove",
		"Press Space to Advance Initiative Place",
		"It's Random Guys It's Not My Fault"
	};
	public HowToIFrame() {
		ConfigFrame();
		buildContent(this.getContentPane());
		pack();
	}

	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "How To Information");
		StyleContainer.SetIcon(this, StyleContainer.HELP_ICON_FILE);

		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void buildContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		buildEditorHintsPane(cPane);
		buildInitHintsPane(cPane);		
	}
	
	private void buildEditorHintsPane(Container cPane) {
		JPanel richEditorPane = new JPanel();
		richEditorPane.setLayout(new GridLayout(0,1));
		cPane.add(richEditorPane, BorderLayout.CENTER);
		
		richEditorPane.add(CompFactory.createNewLabel("Rich Editor Keyboard Shortcuts", ComponentType.HEADER, 22));
		for(KeyboardTip tip : richEditorTips) {
			JTextArea tipArea = CompFactory.createTextArea(tip.tip);
			tipArea.setLineWrap(true);
			tipArea.setWrapStyleWord(true);
			tipArea.setEditable(false);
			tipArea.setFocusable(false);
			
			JScrollPane tipScroll = CompFactory.wrapPanelInScroll(tipArea);
			JPanel pane = CompFactory.createDescriptionPane(tip.key, tipScroll);
			pane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
			richEditorPane.add(pane);
		}
	}
	
	private void buildInitHintsPane(Container cPane) {
		JPanel initPane = new JPanel();
		initPane.setLayout(new GridLayout(0,1));
		cPane.add(initPane, BorderLayout.SOUTH);
		
		initPane.add(CompFactory.createNewLabel("Initiative Support", ComponentType.HEADER, 22));
		for(String tip : initControls) {
			JTextArea tipArea = CompFactory.createTextArea(tip);
			tipArea.setLineWrap(true);
			tipArea.setWrapStyleWord(true);
			tipArea.setEditable(false);
			tipArea.setFocusable(false);
			tipArea.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
			
			initPane.add(tipArea);
		}
	}
}