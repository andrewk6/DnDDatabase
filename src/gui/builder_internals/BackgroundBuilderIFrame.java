package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import data.Feat;
import data.Feat.FeatType;
import data.DataContainer;
import data.DataContainer.Abilities;
import data.DataContainer.MapType;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.items.Item;
import data.players.Background;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.DocumentHelper;

public class BackgroundBuilderIFrame extends JInternalFrame
{	
	private DataContainer data;
	private HashMap<String, Background> bMap;
	private ArrayList<Feat> originFeats;
	private ArrayList<String> itemNames;
	
	private ReminderField nameField, toolProfField, startGoldEquipField;
	private RichEditor edit;
	private JComboBox<Feat> featCombo;
	private JComboBox<Abilities> abCombo1, abCombo2, abCombo3;
	private JComboBox<Source> srcCombo;
	
	private DefaultListModel<Item> itemModel;
	private DefaultListModel<Skills> skillsModel;
	
	private JPanel sidePane;
	private JTabbedPane tabs;
	
	public BackgroundBuilderIFrame(DataContainer data) {
		this.data = data;
		if(data.getBackgrounds() != null)
			bMap = new HashMap<String, Background>(data.getBackgrounds());
		else
			bMap = new HashMap<String, Background>();
		
		originFeats = new ArrayList<Feat>();
		itemNames = new ArrayList<String>();
		itemNames.addAll(data.getArmorKeysSorted());
		itemNames.addAll(data.getWeaponKeysSorted());
		itemNames.addAll(data.getToolKeysSorted());
		itemNames.addAll(data.getGearKeysSorted());
		
		for(Feat f : data.getFeats().values())
			if(f.type == FeatType.Origin)
				originFeats.add(f);
		ConfigFrame();
		BuildContent(this.getContentPane());
		pack();
	}	

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Background Room Builder");
		StyleContainer.SetIcon(this, StyleContainer.BACKGROUND_BUILDER_ICON_FILE);
	}
	
	private void BuildContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JPanel mainPane = new JPanel();
		cPane.add(mainPane, BorderLayout.CENTER);
		BuildMainPane(mainPane);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		cPane.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0, 1));
		
		FillSidePane();
		
		sideWrapper.add(CompFactory.wrapPanelInScroll(sidePane, ScrollPolicy.VERTICAL), BorderLayout.CENTER);
		
		JButton saveBtn = CompFactory.createNewButton("Save", _->{
			data.SetBackgroundMap(bMap);
			data.SafeSaveData(MapType.BACKGROUNDS);
		});
		sideWrapper.add(saveBtn, BorderLayout.SOUTH);
	}
	
	private void BuildMainPane(JPanel mPane) {
		mPane.setLayout(new BorderLayout());
		
		JPanel headPane = new JPanel();
		headPane.setLayout(new BorderLayout());
		mPane.add(headPane, BorderLayout.NORTH);
		
		nameField = CompFactory.createReminderField("Background name...", ComponentType.HEADER);
		headPane.add(nameField, BorderLayout.CENTER);
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new BorderLayout());
		headPane.add(srcPane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source: ", ComponentType.HEADER);
		srcPane.add(srcLbl, BorderLayout.WEST);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		srcPane.add(srcCombo, BorderLayout.CENTER);
		
		tabs = new JTabbedPane();
		mPane.add(tabs, BorderLayout.CENTER);
		
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		tabs.addTab("Description", descPane);
		
		JPanel configPane = new JPanel();
		tabs.addTab("Config", configPane);
		BuildConfigPane(configPane);
		
		edit = new RichEditor(data);
		descPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Reset Editor", _->{
					if(WorkCheck()) {
						if(JOptionPane.showConfirmDialog(this, "Reset editor, will lose unsaved edits.", 
								"Reset Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							Reset();
					}else {
						Reset();
					}
				}),
				CompFactory.createNewButton("Add Background", _->{
					if(nameField.getText().length() <= 0)
						JOptionPane.showMessageDialog(this, "Please at least enter name", 
								"Add Warning", JOptionPane.WARNING_MESSAGE);
					else {
						AddBackground();
						Reset();
						FillSidePane();
					}
				})
		});
		mPane.add(btnPane, BorderLayout.SOUTH);
	}
	
	private void BuildConfigPane(JPanel configPane) {
		configPane.setLayout(new GridLayout(0,1));
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridLayout(2,4));
		configPane.add(topPane);
		
		featCombo = CompFactory.createCombo(Feat.class, originFeats, ComponentType.BODY);
		abCombo1 = CompFactory.createEnumCombo(Abilities.class, ComponentType.BODY);
		abCombo2 = CompFactory.createEnumCombo(Abilities.class, ComponentType.BODY);
		abCombo3 = CompFactory.createEnumCombo(Abilities.class, ComponentType.BODY);
		
		topPane.add(CompFactory.createNewLabel("Starting Feat", ComponentType.HEADER));
		topPane.add(CompFactory.createNewLabel("Ability 1", ComponentType.HEADER));
		topPane.add(CompFactory.createNewLabel("Ability 2", ComponentType.HEADER));
		topPane.add(CompFactory.createNewLabel("Ability 3", ComponentType.HEADER));
		
		topPane.add(featCombo);
		topPane.add(abCombo1);
		topPane.add(abCombo2);
		topPane.add(abCombo3);
		
		AddProfPane(configPane);
		AddItemPane(configPane);
	}
	
	private void AddProfPane(JPanel configPane) {
		skillsModel = new DefaultListModel<Skills>();
		
		JPanel skillsPane = new JPanel();
		skillsPane.setLayout(new BorderLayout());
		configPane.add(skillsPane);
		
		JPanel skillsHeader = new JPanel();
		skillsHeader.setLayout(new GridLayout(0,1));
		skillsPane.add(skillsHeader, BorderLayout.NORTH);
		
		JLabel skillsLbl = CompFactory.createNewLabel("Proficiencies", ComponentType.HEADER);
		skillsHeader.add(skillsLbl);
		
		JPanel toolPane = new JPanel();
		toolPane.setLayout(new BorderLayout());
		skillsHeader.add(toolPane);
		
		JLabel toolLbl = CompFactory.createNewLabel("Starting Tool Proficiency: ", ComponentType.HEADER);
		toolPane.add(toolLbl, BorderLayout.WEST);
		
		toolProfField = CompFactory.createReminderField("Starting equip prof...", ComponentType.BODY);
		toolPane.add(toolProfField, BorderLayout.CENTER);
		
		JPanel addSkillsPane = new JPanel();
		addSkillsPane.setLayout(new BorderLayout());
		skillsPane.add(addSkillsPane, BorderLayout.WEST);
		
		JComboBox<Skills> skillCombo = CompFactory.createEnumCombo(Skills.class, ComponentType.HEADER);
		addSkillsPane.add(skillCombo, BorderLayout.CENTER);
		
		JButton addSkillBtn = CompFactory.createNewButton("Add Skill", _->{
			if(!skillsModel.contains((Skills) skillCombo.getSelectedItem()))
				skillsModel.addElement((Skills) skillCombo.getSelectedItem());
		});
		addSkillsPane.add(addSkillBtn, BorderLayout.SOUTH);
		
		JList<Skills> skillsList = CompFactory.createJList(skillsModel, ComponentType.BODY);
		JScrollPane listScroll = CompFactory.wrapPanelInScroll(skillsList, ScrollPolicy.VERTICAL);
		skillsPane.add(listScroll, BorderLayout.CENTER);
	}
	
	private void AddItemPane(JPanel configPane) {
		itemModel = new DefaultListModel<Item>();
		
		JPanel itemsPane = new JPanel();
		itemsPane.setLayout(new BorderLayout());
		configPane.add(itemsPane);
		
		JPanel itemHeader = new JPanel();
		itemHeader.setLayout(new BorderLayout());
		itemsPane.add(itemHeader, BorderLayout.NORTH);
		
		JLabel equipLabel = CompFactory.createNewLabel("Background Starting Equipment", ComponentType.HEADER);
		itemHeader.add(equipLabel, BorderLayout.CENTER);
		
		JPanel goldPane = new JPanel();
		goldPane.setLayout(new BorderLayout());
		itemHeader.add(goldPane, BorderLayout.EAST);
		
		JLabel startGoldLbl = CompFactory.createNewLabel("Starting Gold with Equipment: ", ComponentType.HEADER);
		goldPane.add(startGoldLbl, BorderLayout.WEST);
		startGoldEquipField = CompFactory.createReminderField("Starting gold...", true, ComponentType.BODY);
		startGoldEquipField.setColumns(7);
		goldPane.add(startGoldEquipField, BorderLayout.CENTER);
		
		JList<Item> itemList = CompFactory.createJList(itemModel, ComponentType.BODY);
		JScrollPane itemScroll = CompFactory.wrapPanelInScroll(itemList, ScrollPolicy.VERTICAL);
		itemsPane.add(itemScroll, BorderLayout.CENTER);
		
		JPanel addItemsPane = new JPanel();
		addItemsPane.setLayout(new BorderLayout());
		itemsPane.add(addItemsPane, BorderLayout.WEST);
		
		JPanel itemSelPane = new JPanel();
		itemSelPane.setLayout(new BorderLayout());
		addItemsPane.add(itemSelPane, BorderLayout.CENTER);
		
		FilterCombo equipCombo = new FilterCombo(itemNames, 20);
		StyleContainer.SetFontMain(equipCombo);
		itemSelPane.add(equipCombo, BorderLayout.CENTER);
		
		ReminderField addNumField = CompFactory.createReminderField("Num add...", true, ComponentType.BODY);
		addNumField.setColumns(7);
		itemSelPane.add(addNumField, BorderLayout.EAST);
		
		JButton addItemBtn = CompFactory.createNewButton("Add Item", _->{
			int num;
			if(addNumField.getText().length() <= 0)
				num = 1;
			else if(Integer.parseInt(addNumField.getText()) <= 0)
				num = 1;
			else 
				num = Integer.parseInt(addNumField.getText());
			
			for(int i = 0; i < num; i ++)
				itemModel.addElement(data.getItems().get(equipCombo.getSelectedItem()));
			equipCombo.reset();
			equipCombo.requestFocus();
			addNumField.setText("");
		});		
		
		addItemsPane.add(addItemBtn, BorderLayout.SOUTH);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys = new ArrayList<String>(bMap.keySet());
		Collections.sort(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				LoadBackground(bMap.get(key));
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createNewButton("Delete", _->{
				if(JOptionPane.showConfirmDialog(this, "Delete " + key, 
						"Delete Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					bMap.remove(key);
					FillSidePane();
				}
			});
			pane.add(delBtn, BorderLayout.EAST);
		}
		
		if(bMap.size() == 1) {
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
		
		featCombo.setSelectedIndex(0);
		abCombo1.setSelectedIndex(0);
		abCombo2.setSelectedIndex(0);
		abCombo3.setSelectedIndex(0);
		toolProfField.setText("");
		startGoldEquipField.setText("");
		
		itemModel.removeAllElements();
		skillsModel.removeAllElements();
		
		Container c = edit.getParent();
		c.remove(edit);
		edit = new RichEditor(data);
		c.add(edit, BorderLayout.CENTER);
		c.revalidate();
		c.repaint();
		
		tabs.setSelectedIndex(0);
	}
	
	private boolean WorkCheck() {
		return nameField.getText().length() > 0 ||
				toolProfField.getText().length() > 0 ||
				edit.getText().length() > 0 ||
				itemModel.size() > 0 ||
				skillsModel.size() > 0;
	}
	
	private void LoadBackground(Background b) {
		boolean load = false;
		if(WorkCheck()) {
			if(JOptionPane.showConfirmDialog(this, "Reset editor and load " + b.name + ", will lose unsaved edits.", 
					"Reset Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				load = true;
			}
		}else {
			load = true;
		}
		if(load) {
			Reset();
			nameField.setText(b.name);
			nameField.setEditable(false);
			nameField.setFocusable(false);
			
			featCombo.setSelectedItem(b.startFeat);
			abCombo1.setSelectedItem(b.statBonus[0]);
			abCombo2.setSelectedItem(b.statBonus[1]);
			abCombo3.setSelectedItem(b.statBonus[2]);
			srcCombo.setSelectedItem(b.src);
			toolProfField.setText(b.toolProf);
			startGoldEquipField.setText(b.startGoldWithEquip + "");
			for(Skills s : b.skills)
				skillsModel.addElement(s);
			for(Item i : b.startEquip)
					itemModel.addElement(i);
			edit.LoadDocument(b.desc);
		}
	}
	
	private void AddBackground() {
		Background b = new Background();
		b.name = nameField.getText();
		b.startFeat = (Feat) featCombo.getSelectedItem();
		b.statBonus[0] = (Abilities) abCombo1.getSelectedItem();
		b.statBonus[1] = (Abilities) abCombo2.getSelectedItem();
		b.statBonus[2] = (Abilities) abCombo3.getSelectedItem();
		System.out.println(Arrays.toString(b.statBonus));
		b.src = (Source) srcCombo.getSelectedItem();
		b.toolProf = toolProfField.getText();
		b.startGoldWithEquip = Integer.parseInt(startGoldEquipField.getText());
		b.desc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
		
		ArrayList<Skills> skills = new ArrayList<Skills>(Collections.list(skillsModel.elements()));
		b.skills = skills;
		
		ArrayList<Item> items = new ArrayList<Item>(Collections.list(itemModel.elements()));
		b.startEquip = items;
		
		bMap.put(b.name, b);
	}
}