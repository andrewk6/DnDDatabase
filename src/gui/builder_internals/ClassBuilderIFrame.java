package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.StyledDocument;

import builders.class_builder.AbilityPaneBuilder;
import builders.class_builder.SubclassPaneBuilder;
import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.items.Item;
import data.items.Armor.ArmorType;
import data.players.classes.ClassAbility;
import data.players.classes.DnDClass;
import data.players.classes.DnDClass.HitDiceType;
import data.players.classes.DnDClass.WeaponProficiency;
import data.players.classes.Subclass;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import utils.ErrorLogger;

public class ClassBuilderIFrame extends JInternalFrame implements DataChangeListener
{
	private DataContainer data;
	private GuiDirector gd;
	
	private HashMap<String, DnDClass> classMap;
	private ArrayList<String> itemNames;
	
	
	private JPanel sidePane;
	
	private ReminderField className;
	private JComboBox<HitDiceType> hdCombo;
	private JComboBox<WeaponProficiency> weapCombo;
	private JComboBox<Abilities> primaryAbility, saveOne, saveTwo;
	private JComboBox<Source> sourceCombo;
	
	private ArrayList<JCheckBox> armorProfs, skillProfs;
	private ArrayList<JLabel> items;
	private HashMap<String, ClassAbility> abilityDocs;
	private HashMap<String, Subclass> subclassAbilities;
	
	private ReminderField startGold, startGoldEquip, skillsNum;
	private RichEditor classDesc;
	private JPanel itemListPane;
	
	private SubclassPaneBuilder subPane;
	private AbilityPaneBuilder baseAbilitiesPane;
	private JTabbedPane tabs;
	
	public ClassBuilderIFrame(DataContainer data, GuiDirector gd) {
		abilityDocs = new HashMap<String, ClassAbility>();
		subclassAbilities = new HashMap<String, Subclass>();
		this.data = data;
		this.gd = gd;
		
		if(data.getClasses() != null)
			classMap = new HashMap<String, DnDClass>(data.getClasses());
		else
			classMap = new HashMap<String, DnDClass>();
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		this.setSize(800, 800);
		
		itemNames = new ArrayList<String>();
		itemNames.addAll(data.getArmorKeysSorted());
		itemNames.addAll(data.getWeaponKeysSorted());
		itemNames.addAll(data.getGearKeysSorted());
		itemNames.addAll(data.getToolKeysSorted());
		
		Initialize(this.getContentPane());
		pack();
		this.setSize(new Dimension(800, 800));
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		this.setClosable(true);
	}
	
	private void Initialize(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JPanel sideOPane = new JPanel();
		sideOPane.setLayout(new BorderLayout());
		cPane.add(sideOPane, BorderLayout.WEST);
		
		JLabel sideLabel = CompFactory.createNewLabel("Class List", ComponentType.HEADER);
		sideOPane.add(sideLabel, BorderLayout.NORTH);
		
		JButton save = CompFactory.createNewButton("Save", _->{
			data.SetClassMap(classMap);
			data.SafeSaveData(DataContainer.CLASSES);
		});
		sideOPane.add(save, BorderLayout.SOUTH);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = new JScrollPane(sidePane);
		sideOPane.add(sideScroll, BorderLayout.CENTER);
		
		JPanel headerPane = new JPanel();
		headerPane.setLayout(new BorderLayout());
		cPane.add(headerPane, BorderLayout.NORTH);
		
		className = CompFactory.createReminderField("Class name", ComponentType.HEADER);
		className.setFont(className.getFont().deriveFont(22f).deriveFont(Font.BOLD));
		headerPane.add(className, BorderLayout.CENTER);
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		BuildPlayerPane(mPane);
		
//		JPanel btnPane = new JPanel();
//		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton addClassBtn = CompFactory.createNewButton("Add Class", _->{
			AddClass();
			ResetEditor();
			FillSidePane();
		});
		headerPane.add(addClassBtn, BorderLayout.EAST);
	}
	
	private void BuildPlayerPane(JPanel mPane) {
		tabs = CompFactory.createTabbedPane();
		mPane.add(tabs, BorderLayout.CENTER);
		
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		BuildDescPane(descPane);
		tabs.addTab("Class Description", descPane);
		
		JPanel statPane = new JPanel();
		statPane.setLayout(new BorderLayout());
		statPane.setPreferredSize(new Dimension(getWidth(), 500));;
		BuildStatsPane(statPane);
		tabs.addTab("Basic Stats", statPane);
		
		baseAbilitiesPane = new AbilityPaneBuilder(data, abilityDocs);
		tabs.addTab("Base Class Abilities", baseAbilitiesPane);
		
		subPane = new SubclassPaneBuilder(data, subclassAbilities);
		tabs.addTab("Subclass Abilities", subPane);
	}

	private void BuildDescPane(JPanel descPane) {
		JPanel descHead = new JPanel();
		descHead.setLayout(new BorderLayout());
		descPane.add(descHead, BorderLayout.NORTH);
		
		JLabel classDescLbl = CompFactory.createNewLabel("Class Description", ComponentType.HEADER);
		descHead.add(classDescLbl, BorderLayout.CENTER);
		
		JPanel sourcePane = new JPanel();
		sourcePane.setLayout(new BorderLayout());
		descHead.add(sourcePane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source:", ComponentType.BODY);
		sourcePane.add(srcLbl, BorderLayout.WEST);
		
		sourceCombo = new JComboBox<Source>();
		sourceCombo.setModel(new DefaultComboBoxModel<Source>(Source.values()));
		StyleContainer.SetFontHeader(sourceCombo);
		sourcePane.add(sourceCombo, BorderLayout.CENTER);
		
		classDesc = new RichEditor(data);
//		classDesc.disableTables();
		descPane.add(classDesc, BorderLayout.CENTER);
	}

	private void BuildStatsPane(JPanel topPane) {		
		JPanel configPane = new JPanel();
		configPane.setLayout(new GridLayout(0,1));
		topPane.add(configPane, BorderLayout.CENTER);
		
		JPanel comboPane = new JPanel();
		comboPane.setLayout(new GridLayout(2,0));
		configPane.add(comboPane);
		
		comboPane.add(CompFactory.createNewLabel("Hit Dice", ComponentType.HEADER));
		comboPane.add(CompFactory.createNewLabel("Weapon Prof", ComponentType.HEADER));
		comboPane.add(CompFactory.createNewLabel("Main Ability", ComponentType.HEADER));
		comboPane.add(CompFactory.createNewLabel("Save Prof 1", ComponentType.HEADER));
		comboPane.add(CompFactory.createNewLabel("Save Prof 2", ComponentType.HEADER));
		
//		hdCombo = new JComboBox<HitDiceType>();
//		hdCombo.setModel(new DefaultComboBoxModel<HitDiceType>(HitDiceType.values()));
		hdCombo = CompFactory.createEnumCombo(HitDiceType.class, ComponentType.HEADER);
//		StyleContainer.SetFontHeader(hdCombo);
		comboPane.add(hdCombo);
		
		weapCombo = new JComboBox<WeaponProficiency>();
		weapCombo.setModel(new DefaultComboBoxModel<WeaponProficiency>(WeaponProficiency.values()));
		StyleContainer.SetFontHeader(weapCombo);
		comboPane.add(weapCombo);
		
		primaryAbility = new JComboBox<Abilities>();
		primaryAbility.setModel(new DefaultComboBoxModel<Abilities>(Abilities.values()));
		StyleContainer.SetFontHeader(primaryAbility);
		comboPane.add(primaryAbility);
		
		saveOne = new JComboBox<Abilities>();
		saveOne.setModel(new DefaultComboBoxModel<Abilities>(Abilities.values()));
		StyleContainer.SetFontHeader(saveOne);
		comboPane.add(saveOne);
		
		saveTwo = new JComboBox<Abilities>();
		saveTwo.setModel(new DefaultComboBoxModel<Abilities>(Abilities.values()));
		StyleContainer.SetFontHeader(saveTwo);
		comboPane.add(saveTwo);
		
		JPanel armorPane = new JPanel();
		armorPane.setLayout(new BorderLayout());
		armorPane.setPreferredSize(new Dimension(getWidth(), 10));
		configPane.add(armorPane);
		
		JLabel armorLbl = CompFactory.createNewLabel("Armor Proficiencies", ComponentType.HEADER);
		armorPane.add(armorLbl, BorderLayout.NORTH);
		
		JPanel armorProfPane = new JPanel();
		armorProfPane.setLayout(new GridLayout(1, 0));
		armorProfPane.setPreferredSize(new Dimension(getWidth(), 10));
		armorPane.add(armorProfPane, BorderLayout.CENTER);
		
		armorProfs = new ArrayList<JCheckBox>();
		for(ArmorType a : ArmorType.values()) {
			JCheckBox chck = CompFactory.createNewCheckbox(a.name());
			armorProfs.add(chck);
			chck.setPreferredSize(new Dimension(getWidth(), 10));
			armorProfPane.add(chck);
		}
		
		JPanel skillsPane = new JPanel();
		skillsPane.setLayout(new BorderLayout());
		configPane.add(skillsPane);
		
		JPanel skillsHPane = new JPanel();
		skillsHPane.setLayout(new BorderLayout());
		skillsPane.add(skillsHPane, BorderLayout.NORTH);
		
		JLabel skillsLbl = CompFactory.createNewLabel("Skill Proficiencies", ComponentType.HEADER);
		skillsHPane.add(skillsLbl, BorderLayout.CENTER);
		
		JPanel numPane = new JPanel();
		numPane.setLayout(new BorderLayout());
		skillsHPane.add(numPane, BorderLayout.EAST);
		
		JLabel numLbl = CompFactory.createNewLabel("Number of Starting Proficiencies:", ComponentType.HEADER);
		numPane.add(numLbl, BorderLayout.WEST);
		
		skillsNum = CompFactory.createReminderField("Number of starting skills", ComponentType.BODY);
		skillsNum.setColumns(5);
		numPane.add(skillsNum, BorderLayout.CENTER);
		
		JPanel skillsProfPane = new JPanel();
		skillsProfPane.setLayout(new GridLayout(2,9));
		skillsPane.add(skillsProfPane, BorderLayout.CENTER);
		
		skillProfs = new ArrayList<JCheckBox>();
		for(Skills s : Skills.values()) {
			JCheckBox chck = CompFactory.createNewCheckbox(s.name());
			skillProfs.add(chck);
			skillsProfPane.add(chck);
		}
		
		JPanel itemsPane = new JPanel();
		itemsPane.setLayout(new BorderLayout());
		configPane.add(itemsPane);
		
		JLabel header = CompFactory.createNewLabel("Starting Equipment: "
				+ "Double/Alt Click on items in list to remove", ComponentType.HEADER);
		itemsPane.add(header, BorderLayout.NORTH);
		
		JPanel itemSetPane = new JPanel();
		itemSetPane.setLayout(new GridLayout(0,1));
		itemsPane.add(itemSetPane, BorderLayout.WEST);
		
		itemListPane = new JPanel();
		itemListPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		JScrollPane itemListScroll = new JScrollPane(itemListPane);
		itemListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		itemListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		itemsPane.add(itemListScroll, BorderLayout.CENTER);
		
		JPanel itemSel = new JPanel();
		itemSel.setLayout(new BorderLayout());
		itemSetPane.add(itemSel);
		
		FilterCombo equipCombo = new FilterCombo(itemNames, 20);
		itemSel.add(equipCombo, BorderLayout.CENTER);
		
		ReminderField addNum = CompFactory.createReminderField("How many?", ComponentType.BODY);
		addNum.setNumbersOnly();
		addNum.setColumns(5);
		itemSel.add(addNum, BorderLayout.EAST);
		
		items = new ArrayList<JLabel>();
		JButton addItem = CompFactory.createNewButton("Add Item", _->{
			if(data.getItems().keySet().contains(equipCombo.getSelectedItem())) {
				String item = (String) equipCombo.getSelectedItem();
				int addIter;
				if(addNum.getText().length() > 0) {
					addIter = Integer.parseInt(addNum.getText());
				}else {
					addIter = 1;
				}
				for(int i = 0; i < addIter; i ++) {
					JLabel lbl = CompFactory.createNewLabel(item, ComponentType.BODY);
					lbl.setBorder(BorderFactory.createCompoundBorder(
						    BorderFactory.createLineBorder(new Color(100,100,100,100), 2),
						    BorderFactory.createEmptyBorder(5, 5, 5, 5)
						));
					lbl.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent e) {
							if((e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) 
									|| (e.isAltDown() && SwingUtilities.isLeftMouseButton(e))){
								items.remove(lbl);
								itemListPane.remove(lbl);
								itemListPane.revalidate();
								itemListPane.repaint();
							}
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
						public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
					});
					
					items.add(lbl);
					itemListPane.add(lbl);
				}
				itemListPane.revalidate();
				itemListPane.repaint();
				equipCombo.reset();
				equipCombo.requestFocus();
				addNum.setText("");
			}
		});
		itemSetPane.add(addItem);
		
		JPanel goldPane = new JPanel();
		goldPane.setLayout(new GridLayout(2,2));
		itemSetPane.add(goldPane);
		
		JLabel startGoldLbl = CompFactory.createNewLabel("Gold No Equip", ComponentType.HEADER);
		goldPane.add(startGoldLbl);
		
		JLabel goldEquipLbl = CompFactory.createNewLabel("Gold + Equip", ComponentType.HEADER);
		goldPane.add(goldEquipLbl);
		
		startGold = CompFactory.createReminderField("Gold No Equip", true, ComponentType.BODY);
		goldPane.add(startGold);
		
		startGoldEquip = CompFactory.createReminderField("Gold + Equip", true, ComponentType.BODY);
		goldPane.add(startGoldEquip);
		
	}

	private void FillSidePane() {
		sidePane.removeAll();
		ArrayList<String> keys = new ArrayList<String>(classMap.keySet());
		Collections.sort(keys);
		
		for(String s : keys) 
		{
			JPanel pane = new JPanel();
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			String key;
			if(s.length() > 10)
				key = s.substring(0,15);
			else
				key = s;
			
			JLabel keyLbl = CompFactory.createNewLabel(key, ComponentType.HEADER);
			keyLbl.setFont(keyLbl.getFont().deriveFont(Font.PLAIN));
			keyLbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					ResetEditor();
					LoadClass(classMap.get(s));
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {keyLbl.setFont(keyLbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {keyLbl.setFont(keyLbl.getFont().deriveFont(Font.PLAIN));}
			});
			pane.add(keyLbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createNewButton("Delete", _->{
				int conf = JOptionPane.showConfirmDialog(this, "Delete: " + s, 
						"Delete Confirm", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					classMap.remove(s);
					FillSidePane();
				}
			});
			pane.add(delBtn, BorderLayout.EAST);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void ResetEditor() {
		className.setText("");
		className.setEditable(true);
		className.setFocusable(true);
		hdCombo.setSelectedIndex(0);
		weapCombo.setSelectedIndex(0);
		primaryAbility.setSelectedIndex(0);
		saveOne.setSelectedIndex(0);
		saveTwo.setSelectedIndex(0);
		sourceCombo.setSelectedItem(Source.PlayersHandbook2024);
		for(JCheckBox aBox : armorProfs)
			aBox.setSelected(false);
		skillsNum.setText("");
		for(JCheckBox sBox : skillProfs)
			sBox.setSelected(false);
		itemListPane.removeAll();
		items = new ArrayList<JLabel>();
		
		abilityDocs = new HashMap<String, ClassAbility>();
		subclassAbilities = new HashMap<String, Subclass>();
		
		tabs.removeTabAt(tabs.indexOfComponent(baseAbilitiesPane));
		baseAbilitiesPane = new AbilityPaneBuilder(data, abilityDocs);
		tabs.addTab("Base Class Abilities", baseAbilitiesPane);
		
		tabs.removeTabAt(tabs.indexOfComponent(subPane));
		subPane = new SubclassPaneBuilder(data, subclassAbilities);
		tabs.addTab("Subclass Abilities", subPane);
		
		startGold.setText("");
		startGoldEquip.setText("");
		
		Container c = classDesc.getParent();
		c.remove(classDesc);
		classDesc = new RichEditor(data);
		c.add(classDesc, BorderLayout.CENTER);
		
		this.revalidate();
		this.repaint();
		tabs.setSelectedIndex(0);
	}
	
	private void LoadClass(DnDClass c) {
		className.setText(c.name);
		className.setEditable(false);
		className.setFocusable(false);
		sourceCombo.setSelectedItem(c.src);
		
		hdCombo.setSelectedItem(c.hd);
		weapCombo.setSelectedItem(c.weaponProf);
		primaryAbility.setSelectedItem(c.primaryAbility);
		saveOne.setSelectedItem(c.savingThrows[0]);
		saveTwo.setSelectedItem(c.savingThrows[1]);
		
		for(ArmorType a : c.armorProf)
			for(JCheckBox aBox : armorProfs)
				if(aBox.getText().equals(a.toString()))
					aBox.setSelected(true);
		
		skillsNum.setText("" + c.numStartSkills);
		for(Skills s : c.startProf)
			for(JCheckBox sBox : skillProfs)
				if(sBox.getText().equals(s.toString()))
					sBox.setSelected(true);
		for(Item i : c.startingEquip) {
			JLabel lbl = CompFactory.createNewLabel(i.name, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createCompoundBorder(
				    BorderFactory.createLineBorder(new Color(100,100,100,100), 2),
				    BorderFactory.createEmptyBorder(5, 5, 5, 5)
				));
			lbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					if((e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) 
							|| (e.isAltDown() && SwingUtilities.isLeftMouseButton(e))){
						items.remove(lbl);
						itemListPane.remove(lbl);
						itemListPane.revalidate();
						itemListPane.repaint();
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			itemListPane.add(lbl);
			items.add(lbl);
		}
		
		abilityDocs = new HashMap<String, ClassAbility>(c.abilities);
		subclassAbilities = new HashMap<String, Subclass>(c.subclasses);
		
		tabs.removeTabAt(tabs.indexOfComponent(baseAbilitiesPane));
		baseAbilitiesPane = new AbilityPaneBuilder(data, abilityDocs);
		tabs.addTab("Base Class Abilities", baseAbilitiesPane);
		
		tabs.removeTabAt(tabs.indexOfComponent(subPane));
		subPane = new SubclassPaneBuilder(data, subclassAbilities);
		tabs.addTab("Subclass Abilities", subPane);
		
		startGold.setText(c.startingGoldNoEquip + "");
		startGoldEquip.setText(c.startingGoldEquip + "");
		
		Container cont = classDesc.getParent();
		
		cont.remove(classDesc);
		classDesc = new RichEditor(data);
		classDesc.LoadDocument(DocumentHelper.deepCopyDocument(c.desc));
		cont.add(classDesc, BorderLayout.CENTER);
		
		this.revalidate();
		this.repaint();
		tabs.setSelectedIndex(0);
	}
	
	private void AddClass() {
		if(className.getText().length() > 0) {
			DnDClass c = new DnDClass();
			
			c.abilities = abilityDocs;
			c.subclasses = subclassAbilities;
			c.name = className.getText();
			c.desc = DocumentHelper.deepCopyDocument(classDesc.getStyledDocument());
			c.src = (Source) sourceCombo.getSelectedItem();
			c.hd = (HitDiceType) hdCombo.getSelectedItem();
			c.weaponProf = (WeaponProficiency) weapCombo.getSelectedItem();
			c.primaryAbility = (Abilities) primaryAbility.getSelectedItem();
			c.savingThrows[0] = (Abilities) saveOne.getSelectedItem();
			c.savingThrows[1] = (Abilities) saveTwo.getSelectedItem();
			c.numStartSkills = Integer.parseInt(skillsNum.getText());
			ArrayList<Skills> skills = new ArrayList<Skills>();
			for(JCheckBox skill : skillProfs) {
				if(skill.isSelected()) {
					try {
						skills.add(Skills.valueOf(skill.getText()));
					}catch(IllegalArgumentException e) {
						ErrorLogger.log(e);
						e.printStackTrace();
					}
				}
			}
			c.startProf = skills;
			ArrayList<ArmorType> aProf = new ArrayList<ArmorType>();
			for(JCheckBox armor : armorProfs) {
				if(armor.isSelected()) {
					try {
						aProf.add(ArmorType.valueOf(armor.getText()));
					}catch(IllegalArgumentException e) {
						ErrorLogger.log(e);
						e.printStackTrace();
					}
				}
			}
			c.armorProf = aProf;
			ArrayList<Item> startItem = new ArrayList<Item>();
			for(JLabel item : items) {
				System.out.println(item.getText());
				startItem.add(data.getItems().get(item.getText()));
			}
			c.startingEquip = startItem;
			c.startingGoldNoEquip = Integer.parseInt(startGold.getText());
			c.startingGoldEquip = Integer.parseInt(startGoldEquip.getText());
			
			classMap.put(c.name, c);
		}else {
			JOptionPane.showMessageDialog(this, "Please at least name the damn class", 
					"No Name Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void onMapUpdated() {
		classDesc.onMapUpdated();
	}

	@Override
	public void onMapUpdated(int mapType) {
		classDesc.onMapUpdated(mapType);
	}
}