package builders.class_builder;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.items.Armor.ArmorType;
import data.players.classes.ClassAbility;
import data.players.classes.PlayerClass;
import data.players.classes.PlayerClass.HitDiceType;
import data.players.classes.PlayerClass.WeaponProficiency;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class ClassBuilder extends JFrame
{
	private DataContainer data;
	private HashMap<String, PlayerClass> classMap;
	private ArrayList<String> itemNames;
	
	
	private JPanel sidePane;
	
	private ReminderField className;
	private JComboBox<HitDiceType> hdCombo;
	private JComboBox<WeaponProficiency> weapCombo;
	private JComboBox<Abilities> primaryAbility, saveOne, saveTwo;
	
	private ArrayList<JCheckBox> armorProfs, skillProfs;
	private ArrayList<JLabel> items;
	private HashMap<String, ClassAbility> abilityDocs;
	private HashMap<String, HashMap<String, ClassAbility>> subclassAbilities;
	
	private ReminderField startGold, startGoldEquip;
	RichEditor classDesc;
	
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(()->{
			ClassBuilder build = new ClassBuilder(data);
			build.setVisible(true);
		});
	}
	
	public ClassBuilder(DataContainer data) {
		classMap = new HashMap<String, PlayerClass>();
		abilityDocs = new HashMap<String, ClassAbility>();
		subclassAbilities = new HashMap<String, HashMap<String, ClassAbility>>();
		this.data = data;
		this.addWindowListener(StyleContainer.GetDefaultCloseListener(data));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(800, 800);
		
		itemNames = new ArrayList<String>();
		itemNames.addAll(data.getArmorKeysSorted());
		itemNames.addAll(data.getWeaponKeysSorted());
		itemNames.addAll(data.getGearKeysSorted());
		itemNames.addAll(data.getToolKeysSorted());
		
		Initialize(this.getContentPane());
	}
	
	private void Initialize(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = new JScrollPane(sidePane);
		cPane.add(sideScroll, BorderLayout.WEST);
		
		className = CompFactory.createReminderField("Class name", ComponentType.HEADER);
		className.setFont(className.getFont().deriveFont(22f).deriveFont(Font.BOLD));
		cPane.add(className, BorderLayout.NORTH);
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		BuildPlayerPane(mPane);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton addClassBtn = CompFactory.createNewButton("Add Class", _->{
			AddClass();
			ResetEditor();
		});
		btnPane.add(addClassBtn);
	}
	
	private void BuildPlayerPane(JPanel mPane) {
		JTabbedPane tabs = CompFactory.createTabbedPane();
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
		
		AbilityPaneBuilder baseAbilitiesPane = new AbilityPaneBuilder(data, abilityDocs);
		tabs.addTab("Base Class Abilities", baseAbilitiesPane);
		
		SubclassPaneBuilder subPane = new SubclassPaneBuilder(data, subclassAbilities);
		tabs.addTab("Sub Class Abilities", subPane);
	}

	private void BuildDescPane(JPanel descPane) {
		JLabel classDescLbl = CompFactory.createNewLabel("Class Description:", ComponentType.HEADER);
		descPane.add(classDescLbl, BorderLayout.NORTH);
		
		classDesc = new RichEditor(data);
		classDesc.disableTables();
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
		
		JLabel skillsLbl = CompFactory.createNewLabel("Skill Proficiencies", ComponentType.HEADER);
		skillsPane.add(skillsLbl, BorderLayout.NORTH);
		
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
		
		JPanel itemListPane = new JPanel();
		itemListPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		itemsPane.add(itemListPane, BorderLayout.CENTER);
		
		FilterCombo equipCombo = new FilterCombo(itemNames, 20);
		itemSetPane.add(equipCombo);
		
		items = new ArrayList<JLabel>();
		JButton addItem = CompFactory.createNewButton("Add Item", _->{
			if(data.getItems().keySet().contains(equipCombo.getSelectedItem())) {
				JLabel lbl = CompFactory.createNewLabel((String) equipCombo.getSelectedItem(), ComponentType.BODY);
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
				itemListPane.revalidate();
				itemListPane.repaint();
				equipCombo.reset();
				equipCombo.requestFocus();
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
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			String key;
			if(s.length() > 10)
				key = s.substring(0,15);
			else
				key = s;
			
			JLabel keyLbl = CompFactory.createNewLabel(key, ComponentType.HEADER);
			keyLbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {/*TODO: Implement loading back into editor*/}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {keyLbl.setFont(keyLbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {keyLbl.setFont(keyLbl.getFont().deriveFont(Font.PLAIN));}
			});
			pane.add(keyLbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createNewButton("Delete", _->{
				classMap.remove(s);
				FillSidePane();
			});
			pane.add(delBtn, BorderLayout.EAST);
		}
	}
	
	private void ResetEditor() {
		
	}
	
	private void AddClass() {
		PlayerClass c = new PlayerClass();
		c.abilities = abilityDocs;
		c.subAbilities = subclassAbilities;
		c.name = className.getText();
		
	}
}