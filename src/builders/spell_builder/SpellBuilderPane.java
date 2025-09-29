package builders.spell_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.PlayerClass;
import data.DataContainer.Source;
import data.Spell;
import data.Spell.SpellLevel;
import data.Spell.SpellSchool;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class SpellBuilderPane extends JPanel {
	
	private HashMap<String, Spell> spellMap;
//	private Map<String, Rule> rMap;
	private DataContainer data;

	private ReminderField nameField;
	private RichEditor editor;
	private JComboBox<Source> srcCombo;
	private JComboBox<SpellLevel> lvlCombo;
	private JComboBox<SpellSchool> schCombo;
	
	private DefaultListModel<PlayerClass> classListModel;
	
	private JPanel spellListPane;
	private JPanel centerPane;
	
	public SpellBuilderPane(DataContainer d) {
		data = d;
		ReadSpellList();
		classListModel = new DefaultListModel<PlayerClass>();
		
		this.setLayout(new BorderLayout());
		centerPane = new JPanel();
		centerPane.setLayout(new BorderLayout());
		this.add(centerPane, BorderLayout.CENTER);
		BuildHeader(centerPane);
		BuildSpellListPane();
		ResetEditor(null);
	}

	private void BuildSpellListPane() {
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		this.add(sidePane, BorderLayout.WEST);
	
		JPanel btnFlow = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Add Spell", this::AddSpell),
				CompFactory.createNewButton("Reset Editor", this::ResetConfirm)
		});
		this.add(btnFlow, BorderLayout.SOUTH);

		spellListPane = new JPanel();
		spellListPane.setLayout(new GridLayout(0, 1));
		JScrollPane spellListScroll = CompFactory.wrapPanelInScroll(spellListPane, ScrollPolicy.VERTICAL);
		FillSpellList();
		sidePane.add(spellListScroll, BorderLayout.CENTER);

		JButton saveBtn = new JButton("Save");
		saveBtn.setPreferredSize(new Dimension(150, 50));
		saveBtn.setFocusable(false);
		StyleContainer.SetFontHeader(saveBtn);
		saveBtn.addActionListener(e -> {
			Save();
		});
		sidePane.add(saveBtn, BorderLayout.SOUTH);
	}

	private void BuildHeader(Container cPane) {
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		cPane.add(hPane, BorderLayout.NORTH);

		nameField = CompFactory.createReminderField("Spell name...", ComponentType.HEADER);
		hPane.add(nameField, BorderLayout.CENTER);

		JPanel srcPane = new JPanel();
		srcPane.setLayout(new BorderLayout());
		hPane.add(srcPane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source:", ComponentType.HEADER);
		srcPane.add(srcLbl, BorderLayout.WEST);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		srcPane.add(srcCombo, BorderLayout.CENTER);
		
		JPanel optPane = new JPanel();
		optPane.setLayout(new BorderLayout());
		hPane.add(optPane, BorderLayout.SOUTH);
		
		JPanel enumPane = new JPanel();
		enumPane.setLayout(new GridLayout(1,0));
		optPane.add(enumPane, BorderLayout.NORTH);
		
		JPanel lvlPane = new JPanel();
		lvlPane.setLayout(new BorderLayout());
		enumPane.add(lvlPane);
		
		JLabel lvlLbl = CompFactory.createNewLabel("Spell Level:", ComponentType.HEADER);
		lvlPane.add(lvlLbl, BorderLayout.WEST);
		
		lvlCombo = CompFactory.createEnumCombo(SpellLevel.class, ComponentType.BODY);
		lvlPane.add(lvlCombo, BorderLayout.CENTER);
		
		JPanel schoolPane = new JPanel();
		schoolPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		schoolPane.setLayout(new BorderLayout());
		enumPane.add(schoolPane);
		
		JLabel schLbl = CompFactory.createNewLabel("Spell School:", ComponentType.HEADER);
		schoolPane.add(schLbl, BorderLayout.WEST);
		
		schCombo = CompFactory.createEnumCombo(SpellSchool.class, ComponentType.BODY);
		schoolPane.add(schCombo, BorderLayout.CENTER);
		
		JPanel classPane = new JPanel();
		classPane.setLayout(new BorderLayout());
		optPane.add(classPane, BorderLayout.CENTER);
		
		JList<PlayerClass> classList = CompFactory.createJList(classListModel, ComponentType.BODY);
		classPane.add(CompFactory.wrapPanelInScroll(classList), BorderLayout.CENTER);
		classList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = classList.locationToIndex(e.getPoint());
				if(index != -1) {
					if((e.getClickCount() == 2 && !e.isConsumed()) ||
							e.isAltDown()) {
						classListModel.remove(index);
					}
				}
			}
		});
		
		JPanel addClassPane = new JPanel();
		addClassPane.setLayout(new GridLayout(0,1));
		classPane.add(addClassPane, BorderLayout.WEST);
		
		JComboBox<PlayerClass> classCombo = CompFactory.createEnumCombo(PlayerClass.class, ComponentType.BODY);
		addClassPane.add(classCombo);
		
		JButton addClassBtn = CompFactory.createNewButton("Add Class", _->{
			if(!classListModel.contains(((PlayerClass)classCombo.getSelectedItem())))
				classListModel.addElement((PlayerClass) classCombo.getSelectedItem());
		});
		addClassPane.add(addClassBtn);
	}

	private void FillSpellList() {
		SwingUtilities.invokeLater(() -> {
			spellListPane.removeAll();
			
			ArrayList<String> keys = new ArrayList<String>(spellMap.keySet());
			Collections.sort(keys);
			
			for (String s : keys) {
				JPanel pane = new JPanel();
				pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
				pane.setLayout(new BorderLayout());
				spellListPane.add(pane);
				JLabel spellDispField = CompFactory.createSideLabel(s, ComponentType.BODY);
				spellDispField.addMouseListener(CompFactory.createSideMouseListener(spellDispField, ()->{
					LoadSpell(spellMap.get(s));
				}));
				pane.add(spellDispField, BorderLayout.CENTER);
				
				JButton delBtn = CompFactory.createNewButton("Delete", _->{
					spellMap.remove(s);
					FillSpellList();
				});
				pane.add(delBtn, BorderLayout.EAST);
			}
			spellListPane.revalidate();
			spellListPane.repaint();
		});
	}
	
	private void ResetConfirm() {
		int conf = JOptionPane.showConfirmDialog(this, 
				"Reset the editor, you will lose unsaved work?", "Reset Confirm", JOptionPane.YES_NO_OPTION);
		if(conf == JOptionPane.YES_OPTION) {
			ResetEditor(null);
		}
	}

	private boolean ResetEditor(String load) {
		int conf = JOptionPane.YES_OPTION;
		if(load != null) {
			conf = JOptionPane.showConfirmDialog(this, 
					"Reset the editor to load: " + load + ", you will lose unsaved work?", 
					"Reset Confirm", JOptionPane.YES_NO_OPTION);
		}
		if(conf == JOptionPane.YES_OPTION) {
			if (editor != null) {
				editor.close();
				centerPane.remove(editor);
			}
			editor = new RichEditor(data);
			centerPane.add(editor, BorderLayout.CENTER);
			nameField.setText("");
			nameField.setEditable(true);
			nameField.setFocusable(true);
			classListModel.removeAllElements();
			
			centerPane.revalidate();
			centerPane.repaint();
			return true;
		}else {
			return false;
		}
	}
	
	private void AddSpell() {
		if (nameField.getText().length() > 0) {
			Spell s = new Spell();
			
			s.name = nameField.getText();
			s.descrBasic = editor.getText();
			s.spellDoc = DocumentHelper.deepCopyDocument(editor.getStyledDocument());
			s.source = (Source) srcCombo.getSelectedItem();
			s.spellLevel = (SpellLevel) lvlCombo.getSelectedItem();
			s.spellSchool = (SpellSchool) schCombo.getSelectedItem();
			
			for(int i = classListModel.size() -1; i >= 0; i --) {
				s.classList.add(classListModel.getElementAt(i));
				classListModel.removeElementAt(i);
			}
			spellMap.put(s.name, s);
			
			ResetEditor(null);
			FillSpellList();
		}else {
			JOptionPane.showMessageDialog(this, "Please enter a name for the spell.");
		}
	}
	
	private void LoadSpell(Spell s) {
		if(ResetEditor(s.name)) {
			nameField.setText(s.name);
			nameField.setEditable(false);
			nameField.setFocusable(false);
			editor.LoadDocument(s.spellDoc);
			srcCombo.setSelectedItem(s.source);
			lvlCombo.setSelectedItem(s.spellLevel);
			schCombo.setSelectedItem(s.spellSchool);
			
			if(s.classList != null) {
				for(PlayerClass cl : s.classList) {
					classListModel.addElement(cl);
				}
			}else {
				s.classList = new ArrayList<PlayerClass>();
			}
		}		
	}
	
	public void Save() {
		data.setSpellMap(spellMap);
		data.SafeSaveData(MapType.SPELLS);
		
	}
	
	public void ReadSpellList() {
		spellMap = new HashMap<String, Spell>(data.getSpells());
	}
}