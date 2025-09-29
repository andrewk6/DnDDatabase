package builders.rule_builder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import data.Rule;
import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;

public class RuleBuildPane extends JPanel
{
	private final DataContainer data;
	private HashMap<String, Rule> rMap;
	
	private ReminderField nameField;
	private ReminderField insertField;
	private JComboBox<Source> srcCombo;
	private RichEditor edit;
	
	private JPanel sidePane;
	
	public RuleBuildPane(DataContainer data) {
		this.data = data;
		if(this.data.getRules() != null) 
			rMap = new HashMap<String, Rule>(data.getRules());
		else
			rMap = new HashMap<String, Rule>();
		
		BuildEditor();
	}
	
	private void BuildEditor() {
		this.setLayout(new BorderLayout());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		this.add(mainPane, BorderLayout.CENTER);
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		mainPane.add(topPane, BorderLayout.NORTH);
		
		JPanel headPane = new JPanel();
		headPane.setLayout(new BorderLayout());
		topPane.add(headPane, BorderLayout.CENTER);
		
		nameField = CompFactory.createReminderField("Rule name...", ComponentType.HEADER);
		headPane.add(nameField, BorderLayout.CENTER);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		headPane.add(CompFactory.createSplitPane("Source: ", srcCombo), BorderLayout.EAST);
		
		insertField = CompFactory.createReminderField("Insert string for Editor...");
		topPane.add(CompFactory.createSplitPane("Insert Text: ", insertField), BorderLayout.SOUTH);
		
		edit = new RichEditor(data);
		mainPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Reset", _->{
					if(ResetConfirm(null))
						Reset();
				}),
				CompFactory.createNewButton("Add Rule", this::Add)
		});
		mainPane.add(btnPane, BorderLayout.SOUTH);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		this.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		sideWrapper.add(CompFactory.wrapPanelInScroll(
				sidePane, ScrollPolicy.VERTICAL), BorderLayout.CENTER);
		
		JButton saveBtn = CompFactory.createNewButton("Save", this::Save);
		sideWrapper.add(saveBtn, BorderLayout.SOUTH);
		
		FillSidePane();
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys = new ArrayList<String>(rMap.keySet());
		Collections.sort(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createSideLabel(key);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				if(ResetConfirm(key))
					Load(rMap.get(key));
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createDeleteButton(rMap, key, null);
			pane.add(delBtn, BorderLayout.EAST);
		}
		
		if(keys.size() == 1) {
			this.revalidate();
			this.repaint();
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void Reset() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.setFocusable(true);
		nameField.requestFocus();
		insertField.setText("");
		
		Container c = edit.getParent();
		c.remove(edit);
		edit = new RichEditor(data);
		c.add(edit, BorderLayout.CENTER);
		c.revalidate();
		c.repaint();
	}
	
	private boolean ResetConfirm(String load) {
		String msg;
		if(load != null)
			msg = "Reset the editor to load: " + load + ", you will lose unsaved progress?";
		else
			msg = "Reset the editor?";
		
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
				this, msg, "Reset Confirm", JOptionPane.YES_NO_OPTION);
		
	}
	
	
	private void Add() {
		Rule r = new Rule();
		r.name = nameField.getText();
		r.insertString = insertField.getText();
		r.ruleDoc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
		r.src = (Source) srcCombo.getSelectedItem();
		
		rMap.put(r.name, r);
		Reset();
	}
	
	private void Load(Rule r) {
		Reset();
		nameField.setText(r.name);
		nameField.setEditable(false);
		nameField.setFocusable(false);
		
		insertField.setText(r.insertString);
		insertField.requestFocus();
		srcCombo.setSelectedItem(r.src);
		edit.LoadDocument(DocumentHelper.deepCopyDocument(r.ruleDoc));
	}
	
	private void Save() {
		data.setRuleMap(rMap);
		data.SafeSaveData(MapType.RULES);
	}
}