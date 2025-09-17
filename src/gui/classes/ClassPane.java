package gui.classes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import data.DataContainer;
import data.DataContainer.Skills;
import data.items.Item;
import data.items.Armor.ArmorType;
import data.players.classes.DnDClass;
import data.players.classes.Subclass;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class ClassPane extends JPanel
{
	private final GuiDirector gd;
	private final DataContainer data;
	private final DnDClass classData;
	
	private JTabbedPane tabs;
	
	public ClassPane(DataContainer data, DnDClass c, GuiDirector gd) {
		super();
		this.classData = c;
		this.gd = gd;
		this.data = data;
		
		Init();
	}
	
	private void Init() {
		this.setLayout(new BorderLayout());
		JLabel title = CompFactory.createNewLabel(classData.name, ComponentType.HEADER);
		title.setFont(title.getFont().deriveFont(24f));
		this.add(title, BorderLayout.NORTH);
		
		tabs = new JTabbedPane();
		this.add(tabs, BorderLayout.CENTER);
		
		BuildDescPane();
		BuildDetailsPane();
		
		tabs.addTab("Base Class Abilities", new AbilityPane(data, gd, classData.abilities));
		
		BuildSubPane();
	}
	
	private void BuildSubPane() {
		JTabbedPane subTabs = new JTabbedPane();
		tabs.addTab("Subclass Abilities", subTabs);
		
		for(Subclass sub : classData.subclasses.values())
			subTabs.addTab(sub.name, new SubclassPane(data, gd, sub));
	}
	
	private void BuildDescPane() {
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		tabs.addTab("Class Description", descPane);
		
		JPanel headPane = new JPanel();
		headPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		descPane.add(headPane, BorderLayout.NORTH);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source: ", ComponentType.HEADER);
		headPane.add(srcLbl);
		
		JLabel srcDesc = CompFactory.createNewLabel(classData.src.toString(), ComponentType.BODY);
		headPane.add(srcDesc);
		
		HoverTextPane classDesc = new HoverTextPane(data, gd, gd.getDesktop());
		classDesc.setDocument(classData.desc);
		JScrollPane descScroll = CompFactory.wrapPanelInScroll(classDesc, ScrollPolicy.VERTICAL);
		descPane.add(descScroll, BorderLayout.CENTER);
	}
	
	private void BuildDetailsPane() {
		JPanel dPane = new JPanel();
		dPane.setLayout(new BoxLayout(dPane, BoxLayout.Y_AXIS));
		tabs.add("Details", dPane);
		
		JPanel primePane = new JPanel();
		primePane.setLayout(new FlowLayout(FlowLayout.LEFT));
		dPane.add(primePane);
		
		JLabel primeLbl = CompFactory.createNewLabel("Primary Ability: ", ComponentType.HEADER);
		primeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		primePane.add(primeLbl);
		
		JLabel primeVal = CompFactory.createNewLabel(classData.primaryAbility.toString(), ComponentType.BODY);
		primeVal.setAlignmentX(Component.LEFT_ALIGNMENT);
		primePane.add(primeVal);
		
		primePane.add(Box.createHorizontalGlue());
		
		JPanel hitDiePane = new JPanel();
		hitDiePane.setLayout(new FlowLayout(FlowLayout.LEFT));
		dPane.add(hitDiePane);
		
		JLabel hdLbl = CompFactory.createNewLabel("Hit Point Die: ", ComponentType.HEADER);
		hitDiePane.add(hdLbl);
		
		JLabel hdVal = CompFactory.createNewLabel(classData.hd.toString().toUpperCase() + 
				" per " + classData.name + " level", ComponentType.BODY);
		hitDiePane.add(hdVal);
		
		JPanel saveThrowPane = new JPanel();
		saveThrowPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		dPane.add(saveThrowPane);
		
		JLabel saveThrowLbl = CompFactory.createNewLabel("Saving Throw Proficiencies: ", ComponentType.HEADER);
		saveThrowPane.add(saveThrowLbl);
		
		JLabel saveThrowVal = CompFactory.createNewLabel(classData.savingThrows[0] 
				+ " and " + classData.savingThrows[1], ComponentType.BODY);
		saveThrowPane.add(saveThrowVal);
		
		JPanel weaponPane = new JPanel();
		weaponPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		dPane.add(weaponPane);
		
		JLabel weapProfLbl = CompFactory.createNewLabel("Weapon Proficiencies: ", ComponentType.HEADER);
		weaponPane.add(weapProfLbl);
		
		JLabel weaponProfVal;
		switch(classData.weaponProf) {
		case All: weaponProfVal = CompFactory.createNewLabel("Simple and Martial Weapons", ComponentType.BODY); break;
		case Simple: weaponProfVal = CompFactory.createNewLabel("Simple Weapons", ComponentType.BODY); break;
		case SimpleMartialFinesseLight:
			if(classData.name.toLowerCase().equals("monk"))
				weaponProfVal = CompFactory.createNewLabel(
						"Simple weapons and Martial weapons that have the Light property", ComponentType.BODY);
			else
				weaponProfVal = CompFactory.createNewLabel(
						"Simple weapons and Martial weapons that have the Finesse or Light property", ComponentType.BODY);
			break;
		default: weaponProfVal = CompFactory.createNewLabel("WHY!?!?!?!", ComponentType.HEADER);
		}
		weaponPane.add(weaponProfVal);
		
		JPanel armorPane = new JPanel();
		armorPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		dPane.add(armorPane);
		
		JLabel armorLbl = CompFactory.createNewLabel("Armor Training: ", ComponentType.HEADER);
		armorPane.add(armorLbl);
		
		JLabel armorVal = CompFactory.createNewLabel(buildArmorProfString(classData.armorProf), ComponentType.BODY);
		armorPane.add(armorVal);
		
		JPanel skillsPane = new JPanel();
		skillsPane.setLayout(new BorderLayout());
		dPane.add(skillsPane);
		
		JLabel skillsProfLbl = CompFactory.createNewLabel("Skill Proficiencies: ", ComponentType.HEADER);
		skillsPane.add(skillsProfLbl, BorderLayout.NORTH);
		
//		JLabel skillsProfVal = CompFactory.createNewLabel(
//				buildSkillsString(classData.numStartSkills, classData.startProf), ComponentType.BODY);
		JTextArea skillsProfVal = new JTextArea(buildSkillsString(classData.numStartSkills, classData.startProf));
		skillsProfVal.setAlignmentX(Component.LEFT_ALIGNMENT);
		skillsProfVal.setLineWrap(true);
		skillsProfVal.setWrapStyleWord(true);
		skillsProfVal.setEditable(false);
		skillsProfVal.setFocusable(false);
		StyleContainer.SetFontMain(skillsProfVal);
		skillsPane.add(skillsProfVal, BorderLayout.CENTER);
		
		JPanel startEquipPane = new JPanel();
		startEquipPane.setLayout(new BorderLayout());
		dPane.add(startEquipPane);
		
		JLabel startEquipLbl = CompFactory.createNewLabel("Starting Equipment: ", ComponentType.HEADER);
		startEquipLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		startEquipPane.add(startEquipLbl, BorderLayout.NORTH);
		
		JTextArea equipArea = new JTextArea(buildEquipString(classData.startingEquip));
		equipArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		equipArea.setLineWrap(true);
		equipArea.setWrapStyleWord(true);
		equipArea.setEditable(false);
		equipArea.setFocusable(false);
		StyleContainer.SetFontMain(equipArea);
		startEquipPane.add(equipArea, BorderLayout.CENTER);
		
	}
	
	private String buildArmorProfString(ArrayList<ArmorType> armorProf) {
		String toReturn = "";
		if(armorProf.size() <= 0)
			toReturn += "None";
		else {
			if(armorProf.contains(ArmorType.LIGHT))
				toReturn += "Light, ";
			if(armorProf.contains(ArmorType.MEDIUM))
				toReturn += "Medium, ";
			if(armorProf.contains(ArmorType.HEAVY))
				toReturn += "Heavy, ";
			if(armorProf.contains(ArmorType.SHIELD))
				toReturn += "Shields, ";
			toReturn = toReturn.substring(0, toReturn.length()-2);
		}
		return toReturn;
	}
	
	private String buildSkillsString(int numSkills, ArrayList<Skills> skills) {
		String toReturn = "Choose " + numSkills + ": ";
		for(Skills s : skills) {
			toReturn += s.toString() + ", ";
		}
		toReturn = toReturn.substring(0, toReturn.length() - 2);
		return toReturn;
	}
	
	private String buildEquipString(ArrayList<Item> items) {
		String toReturn = "";
		if(classData.name.toLowerCase().equals("fighter"))
			toReturn += "Choose A, B, or C: "
					+ "(A) Chain Mail, Greatsword, Flail, 8 Javelins, Dungeoneer's Pack, and 4GP; "
					+ "(B) Studded Leather Armor, Scimitar, Shortsword, Longbow, 20 Arrows, Quiver, "
						+ "Dungeoneer's Pack, and 11 GP; "
					+ "or (C) 155 GP";
		else {
			toReturn += "Choose A or B: (A) ";
			HashMap<String, AtomicInteger> itemCount = new HashMap<String, AtomicInteger>();
			for(Item i : items) {
				if(itemCount.containsKey(i.name))
					itemCount.get(i.name).incrementAndGet();
				else
					itemCount.put(i.name, new AtomicInteger(1));
			}
			
			for(String s : itemCount.keySet()) {
				if(itemCount.get(s).intValue() == 1)
					toReturn += s + ", ";
				else
					toReturn += itemCount.get(s).intValue() + " " + s + "s, ";
			}
			toReturn += " and " + classData.startingGoldEquip +" GP; or (B) " + classData.startingGoldNoEquip;
		}
		
		return toReturn;
	}
	/*
	 * private static final long serialVersionUID = 7913947562880195068L;
	
	public enum HitDiceType {d12, d10, d8, d6};
	public enum WeaponProficiency {Simple, All, SimpleMartialFinesseLight};
	
	public HashMap<String, ClassAbility> abilities; 
	public HashMap<String, Subclass> subclasses;
	
	public String name;
	public StyledDocument desc;
	
	public HitDiceType hd;
	public WeaponProficiency weaponProf;
	public Abilities primaryAbility;
	public Abilities[] savingThrows = new Abilities[2];
	
	
	public ArrayList<Skills> startProf;
	public ArrayList<ArmorType> armorProf;
	
	public ArrayList<Item> startingEquip;
	public int startingGoldNoEquip, startingGoldEquip;
	public int numStartSkills;
	public Source src;
	 */
}