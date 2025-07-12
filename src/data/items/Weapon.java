package data.items;

import java.util.ArrayList;
import java.util.Map;

import data.DataContainer.DamageTypes;

public final class Weapon extends Item
{
	public enum WeaponMastery{
		CLEAVE,
	    GRAZE,
	    NICK,
	    PUSH,
	    SAP,
	    SLOW,
	    TOPPLE,
	    VEX
	}
	
	public enum WeaponProperty {
	    AMMUNITION,
	    FINESSE,
	    HEAVY,
	    LIGHT,
	    LOADING,
	    REACH,
	    SPECIAL,
	    THROWN,
	    TWO_HANDED,
	    VERSATILE,
	    IMPROVISED
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
	
	public int rangeLow, rangeHigh;
	
	public Weapon(String name) {
		super(name);
	}
	
}