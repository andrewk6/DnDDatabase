package gui.builder_internals;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.Rule;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class RuleBuilderIFrame extends JInternalFrame{
	
	DataContainer data;
	private JPanel descPane, rulesPane, rulesListPane;
//	private RuleEditor rulesDesc;
	private RichEditor rulesDesc;
	private JTextField rulesName;

	private HashMap<String, Rule> rulesList;


	/**
	 * Create the application.
	 */
	public RuleBuilderIFrame(DataContainer d) {
		data = d;
		rulesList = new HashMap<String, Rule>(d.getRules());
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setPreferredSize(new Dimension(450, 450));
		this.setTitle("Rule Builder");
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		StyleContainer.SetIcon(this, StyleContainer.RULE_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});


		descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		this.getContentPane().add(descPane, BorderLayout.CENTER);

		JPanel rulesHeader = new JPanel();
		rulesHeader.setLayout(new BorderLayout());
		descPane.add(rulesHeader, BorderLayout.NORTH);

		rulesName = new JTextField();
		rulesName.setToolTipText("Enter a name for the rules.");
		rulesName.setFont(new Font("Monospaced", Font.BOLD, 20));
		rulesHeader.add(rulesName, BorderLayout.CENTER);

		JButton addBtn = new JButton();
		addBtn.setText("Add Rules");
		addBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rulesName.getText().length() > 0 && rulesDesc.getText().length() > 0) {
					StyledDocument styledDoc = rulesDesc.getStyledDocument();
					rulesList.put(rulesName.getText(),
							new Rule(rulesName.getText(), rulesDesc.getText(),
							rulesDesc.convertDocumentToHTML(), styledDoc));
					ResetEditor();
					BuildRulesList();
				} else {
					JOptionPane.showMessageDialog(null, "Please enter both name and description for rules",
							"Rules Creation Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addBtn.setFocusable(false);
		rulesHeader.add(addBtn, BorderLayout.EAST);

//		rulesDesc = new RuleEditor();
		rulesDesc = new RichEditor(data);
		descPane.add(rulesDesc, BorderLayout.CENTER);
		this.pack();
		this.revalidate();
		this.repaint();

		rulesPane = new JPanel();
		rulesPane.setLayout(new BorderLayout());
		this.getContentPane().add(rulesPane, BorderLayout.WEST);

		JTextField rulesHead = new JTextField("RULES LIST");
		rulesHead.setFont(new Font("Monospaced", Font.BOLD, 20));
		rulesHead.setEditable(false);
		rulesHead.setBorder(null);
		rulesHead.setHorizontalAlignment(SwingConstants.CENTER);
		rulesHead.setColumns(15);
		rulesHead.setFocusable(false);
		rulesPane.add(rulesHead, BorderLayout.NORTH);

		rulesListPane = new JPanel();
		rulesListPane.setLayout(new GridLayout(0, 1));
		JScrollPane rulesScroller = new JScrollPane(rulesListPane);
//		rulesScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rulesPane.add(rulesScroller, BorderLayout.CENTER);
		BuildRulesList();
		
		JButton saveBtn = new JButton("Save");
		saveBtn.setFont(new Font("Monospaced", Font.PLAIN, 16));
		saveBtn.setFocusable(false);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				data.setRuleMap(rulesList);
				data.SafeSaveData(DataContainer.RULES);
			}
		});
		rulesPane.add(saveBtn, BorderLayout.SOUTH);
	}

	public void BuildRulesList() {
		if (rulesListPane.getComponents().length > 0) {
			rulesListPane.removeAll();
		}

		ArrayList<Rule> rulesSorted = new ArrayList<Rule>();

		for (String r : rulesList.keySet()) {
			rulesSorted.add(rulesList.get(r));
		}

		Collections.sort(rulesSorted);

		for (Rule r : rulesSorted) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			
			JTextField ruleField = new JTextField(r.name);
			ruleField.setFont(new Font("Monospaced", Font.PLAIN, 16));
			ruleField.setEditable(false);
			ruleField.setFocusable(false);
			ruleField.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					LoadRule(r.name);
				}

				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			pane.add(ruleField, BorderLayout.CENTER);
			
			JButton del = new JButton("Delete");
			StyleContainer.SetFontBtn(del);
			del.addActionListener(e ->{
				rulesList.remove(r.name);
				BuildRulesList();
			});
			pane.add(del, BorderLayout.EAST);
			
			rulesListPane.add(pane);
		}
		rulesListPane.revalidate();
		rulesListPane.repaint();
	}
	
	public void ResetEditor() {
		descPane.remove(rulesDesc);
		descPane.revalidate();
		rulesDesc = new RichEditor(data);
		rulesName.setText("");
		rulesName.setEditable(true);
		rulesName.setFocusable(true);
		rulesName.requestFocusInWindow();
		descPane.add(rulesDesc, BorderLayout.CENTER);
		rulesDesc.revalidate();
		rulesDesc.repaint();
	}
	
	public void LoadRule(String key) {
		int opt = JOptionPane.NO_OPTION;
		if(rulesDesc.getText().length() > 0 || rulesName.getText().length() > 0)
			opt = JOptionPane.showConfirmDialog(this, "Load " + key +" you will lose unadded progress.",
					"Load Confirmation", JOptionPane.YES_NO_OPTION);
		else
			opt = JOptionPane.YES_OPTION;
		
		if(opt == JOptionPane.YES_OPTION) {
			ResetEditor();
			rulesDesc.LoadDocument(data.getRules().get(key).ruleDoc);
			rulesName.setText(key);
			rulesName.setEditable(false);
			rulesName.setFocusable(false);
		}
	}
}
