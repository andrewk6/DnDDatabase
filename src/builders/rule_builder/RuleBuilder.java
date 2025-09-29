package builders.rule_builder;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.Rule;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.structures.StyleContainer;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class RuleBuilder extends JFrame{

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			RuleBuilder frm = new RuleBuilder(data);
			frm.setVisible(true);
		});
	}

	/**
	 * Create the application.
	 */
	public RuleBuilder(DataContainer data) {
		this.setContentPane(new RuleBuildPane(data));
		ConfigFrame(data);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void ConfigFrame(DataContainer d) 
	{
		this.setTitle("Rule Builder");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(true);
		this.addWindowListener(CompFactory.createSafeExitWindowListener(this, d));
		this.pack();
		this.setSize(800,  800);
	}

}
