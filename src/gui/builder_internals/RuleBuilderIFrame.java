package gui.builder_internals;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import data.Rule;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.structures.StyleContainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class RuleBuilderIFrame extends JInternalFrame{
	
	DataContainer data;
	private JPanel descPane, rulesPane, rulesListPane;
//	private RuleEditor rulesDesc;
	private RichEditor rulesDesc;
	private JTextField rulesName;
	private JComboBox<Source> srcCombo;

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
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new BorderLayout());
		rulesHeader.add(srcPane, BorderLayout.EAST);
		
		srcPane.add(CompFactory.createNewLabel("Source:", ComponentType.HEADER), BorderLayout.WEST);
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		srcPane.add(srcCombo, BorderLayout.CENTER);

		JButton addBtn = new JButton();
		addBtn.setText("Add Rules");
		addBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rulesName.getText().length() > 0 && rulesDesc.getText().length() > 0) {
					StyledDocument styledDoc = DocumentHelper.deepCopyDocument(rulesDesc.getStyledDocument());
					Rule r = new Rule();
					r.name = rulesName.getText();
					r.desc_basic = rulesDesc.getText();
					r.desc_HTML = rulesDesc.convertDocumentToHTML();
					r.ruleDoc = styledDoc;
					r.src = (Source) srcCombo.getSelectedItem();
					rulesList.put(r.name, r);
					ResetEditor();
					BuildRulesList();
				} else {
					JOptionPane.showMessageDialog(null, "Please enter both name and description for rules",
							"Rules Creation Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addBtn.setFocusable(false);
		JPanel btnFlow = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[0]);
		descPane.add(btnFlow, BorderLayout.SOUTH);
		
		btnFlow.add(addBtn);

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
		JScrollPane rulesScroller = CompFactory.wrapPanelInScroll(rulesListPane, ScrollPolicy.VERTICAL);
		rulesPane.add(rulesScroller, BorderLayout.CENTER);
		BuildRulesList();
		
		JButton saveBtn = new JButton("Save");
		saveBtn.setFont(new Font("Monospaced", Font.PLAIN, 16));
		saveBtn.setFocusable(false);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				data.setRuleMap(rulesList);
				data.SafeSaveData(MapType.RULES);
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
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			pane.setLayout(new BorderLayout());
			
			JLabel ruleLbl = CompFactory.createSideLabel(r.name, ComponentType.BODY);
			ruleLbl.addMouseListener(CompFactory.createSideMouseListener(ruleLbl, ()->{
				LoadRule(r.name);
			}));
			pane.add(ruleLbl, BorderLayout.CENTER);
			
			JButton del = new JButton("Delete");
			StyleContainer.SetFontBtn(del);
			del.addActionListener(_ ->{
				int delConf = JOptionPane.showConfirmDialog(this, "Delete " + 
						r.name + "?", "Delete Confirm", JOptionPane.YES_NO_OPTION);
				if(delConf == JOptionPane.YES_OPTION) {
					rulesList.remove(r.name);
					BuildRulesList();
				}
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
			srcCombo.setSelectedItem(data.getRules().get(key).getSource());
		}
	}
}
