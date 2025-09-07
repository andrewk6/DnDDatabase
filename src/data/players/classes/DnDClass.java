package data.players.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.StyledDocument;

import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.items.Item;
import data.items.Armor.ArmorType;

public class DnDClass implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7913947562880195068L;
	
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
}