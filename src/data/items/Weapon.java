package data.items;

import java.util.ArrayList;
import java.util.Map;

import data.DataContainer.DamageTypes;

public final class Weapon extends Item
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6378256851594347752L;

	public enum WeaponMastery{
		CLEAVE("Cleave"),
	    GRAZE("Graze"),
	    NICK("Nick"),
	    PUSH("Push"),
	    SAP("Sap"),
	    SLOW("Slow"),
	    TOPPLE("Topple"),
	    VEX("Vex");
	    
	    public final String lbl;
		
		WeaponMastery(String lbl) {
			this.lbl = lbl;
		}
		
		public String toString() {
			return lbl;
		}
	}
	
	public enum WeaponProperty {
	    AMMUNITION("Ammunition"),
	    FINESSE("Finesse"),
	    HEAVY("Heavy"),
	    LIGHT("Light"),
	    LOADING("Loading"),
	    REACH("Reach"),
	    SPECIAL("Special"),
	    THROWN("Thrown"),
	    TWO_HANDED("Two Handed"),
	    VERSATILE("Versatile"),
	    IMPROVISED("Improvised"),
	    BURST_FIRE("Burst Fire");
		
		public final String lbl;
		
		WeaponProperty(String lbl) {
			this.lbl = lbl;
		}
		
		public String toString() {
			return lbl;
		}
	}
	
	public static final Map<WeaponMastery, String> WEAPON_MASTERY_DESCR = Map.ofEntries(
	        Map.entry(WeaponMastery.CLEAVE,
	            "When you hit a creature with a melee attack, you can make a second attack against a different creature within 5 feet of the first target. " +
	            "That second attack deals weapon damage without adding your ability modifier."),
	        
	        Map.entry(WeaponMastery.GRAZE,
	            "If you miss a creature with an attack, the weapon still deals damage equal to your ability modifier."),
	        
	        Map.entry(WeaponMastery.NICK,
	            "When wielding two Light weapons and making an attack, you immediately make a second attack with your off-hand as part of that same Attack action—no Bonus Action required."),
	        
	        Map.entry(WeaponMastery.PUSH,
	            "Upon hitting, you can push a creature (Large or smaller) 10 feet straight away from you—no save allowed."),
	        
	        Map.entry(WeaponMastery.SAP,
	            "When you hit, the target gains disadvantage on their next attack roll before your next turn."),
	        
	        Map.entry(WeaponMastery.SLOW,
	            "When you hit and deal damage, the target's speed is reduced by 10 feet until the start of your next turn."),
	        
	        Map.entry(WeaponMastery.TOPPLE,
	            "When you hit, you force the target to make a Constitution save (DC = 8 + your proficiency bonus + your ability modifier) or become Prone."),
	        
	        Map.entry(WeaponMastery.VEX,
	            "When you hit, you gain advantage on your next attack roll against the same target before the end of your next turn.")
	    );
	
	public String damage, versDmg;
	public DamageTypes dmgType;
	public WeaponMastery mastery;
	public ArrayList<WeaponProperty> properties;
	
	public boolean ranged;
	public boolean martial;
	
	public boolean modern = false;
	public boolean future = false;
	
	public int rangeLow, rangeHigh, reload;
	
	public Weapon(String name) {
		super(name);
	}
	
}