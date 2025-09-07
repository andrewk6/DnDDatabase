package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import data.players.Species;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

public class SpeciesBuilderIFrame extends JInternalFrame
{
	private DataContainer data;
//	private GuiDirector gd;
	private HashMap<String, Species> sMap;
	
	
	private ReminderField nameField;
	private RichEditor edit;
	private JPanel sidePane;
	private JComboBox<Source> sourceCombo;
	
	public SpeciesBuilderIFrame(DataContainer data)
	{
		this.data = data;
		if(this.data.getSpecies() != null) {
			sMap = new HashMap<String, Species>(this.data.getSpecies());
		}else {
			sMap = new HashMap<String, Species>();
		}
		ConfigFrame();
		BuildContent(this.getContentPane());
		pack();
	}

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Species Room Builder");
		StyleContainer.SetIcon(this, StyleContainer.SPECIES_BUILDER_ICON_FILE);
	}	
	
	private void BuildContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		mPane.add(hPane, BorderLayout.NORTH);
		
		nameField = CompFactory.createReminderField("Species name...", ComponentType.HEADER);
		hPane.add(nameField, BorderLayout.CENTER);
		
		sourceCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		hPane.add(sourceCombo, BorderLayout.EAST);
		
		edit = new RichEditor(data);
		mPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mPane.add(btnPane, BorderLayout.SOUTH);
		
		btnPane.add(CompFactory.createNewButton("Reset", _->{
			if(ResetConfirm())
				Reset();
		}));
		
		btnPane.add(CompFactory.createNewButton("Add Species", _->{
			AddSpecies();
		}));
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		cPane.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = new JScrollPane(sidePane);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sideWrapper.add(sideScroll, BorderLayout.CENTER);
		
		sideWrapper.add(CompFactory.createNewButton("Save", _->{
			Save();
		}), BorderLayout.SOUTH);
		
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys = new ArrayList<String>(sMap.keySet());
		Collections.sort(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				Load(sMap.get(key));
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createNewButton("Delete", _->{
				if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Delete " + key, 
						"Delete Confirm", JOptionPane.YES_NO_OPTION)) {
					sMap.remove(key);
					FillSidePane();
				}
			});
			pane.add(delBtn, BorderLayout.EAST);
		}
		if(keys.size() == 1) {
			this.revalidate();
			this.repaint();
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void AddSpecies() {
		if(nameField.getText().length() <= 0 || 
				edit.getText().length() <= 0) 
			JOptionPane.showMessageDialog(this, "Please finish editing the race.",
					"Add Warning", JOptionPane.WARNING_MESSAGE);
		else {
			Species s = new Species();
			s.name = nameField.getText();
			s.desc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
			s.src = (Source) sourceCombo.getSelectedItem();
			sMap.put(s.name, s);
			Reset();
			FillSidePane();
		}
	}
	
	private void Reset() {
		nameField.setEditable(true);
		nameField.setFocusable(true);
		nameField.setText("");
		
		Container c = edit.getParent();
		c.remove(edit);
		edit = new RichEditor(data);
		c.add(edit, BorderLayout.CENTER);
		c.revalidate();
		c.repaint();
	}
	
	private boolean ResetConfirm() {
		if(nameField.getText().length() > 0 || edit.getText().length() > 0)
			return JOptionPane.showConfirmDialog(this, "Reset the editor, you will lose any un-added work",
					"Reset Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		return true;
	}
	
	private void Load(Species s) {
		Reset();
		nameField.setEditable(false);
		nameField.setFocusable(false);
		nameField.setText(s.name);
		
		edit.LoadDocument(s.desc);
	}
	
	private void Save() {
		data.SetSpeciesMap(sMap);
		data.SafeSaveData(MapType.SPECIES);
	}
}