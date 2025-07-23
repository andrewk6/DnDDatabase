package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.StyledDocument;

import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.DataContainer.Proficiency;;

public class Monster implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8875265389172654861L;
	public static final int STR = 0;
	public static final int DEX = 1;
	public static final int CON = 2;
	public static final int INT = 3;
	public static final int WIS = 4;
	public static final int CHA = 5;

	public String name, typeSizeAlignment, ac, init, hp, speed, senses, languages, dmgVul, dmgRes, immune;
	public int[]stats;
	public int lActNum, lActBns;
	public boolean[] saves;
	
	
	public HashMap<Skills, Proficiency> skills;
	public double cr;
	
	public StyledDocument traits, actions, bonusActions, reactions, legendActions;
	
	public ArrayList<String> tags;
	
	public boolean custom = false;
	public Source source = Source.MonsterManual2024;
	
	public Monster() {
		stats = new int[6];
		saves = new boolean[6];
		skills = new HashMap<Skills, Proficiency>();
		cr = -1;
	}
	
	public int profByCR() {
		if(cr < 5)
			return 2;
		else if(cr >= 5 && cr < 9)
			return 3;
		else if(cr >= 9 && cr < 13)
			return 4;
		else if(cr >= 13 && cr < 17)
			return 5;
		else if(cr>= 17 && cr < 21)
			return 6;
		else if(cr >= 21 && cr < 25)
			return 7;
		else if(cr >= 25 && cr < 29)
			return 8;
		else if(cr >= 29)
			return 9;
		else
			return 0;
		
	}
	
	public int getAbilityMod(int ability) {
		return (int) Math.floor((stats[ability] - 10) / 2.0);
	}
	
	public int getSkillMod(Skills s) {
		int aMod = 0;
		int profMod = 0;
		switch(s) {
		case Athletics: aMod += getAbilityMod(Monster.STR); break;
		case Acrobatices:
		case SleightofHand:
		case Stealth: aMod += getAbilityMod(Monster.DEX); break;
		case Arcana:
		case History:
		case Investigation:
		case Nature:
		case Religion: aMod += getAbilityMod(Monster.INT); break;
		case AnimalHandling:
		case Insight:
		case Medecine:
		case Perception:
		case Survival: aMod += getAbilityMod(Monster.WIS); break;
		case Deception:
		case Intimidation:
		case Performance:
		case Persuasion: aMod += getAbilityMod(Monster.CHA); break;
		default: aMod+= 0;
		}
		
		if(skills.keySet().contains(s))
			if(skills.get(s) == Proficiency.Expertise)
				profMod += profByCR() * 2;
			else if(skills.get(s) == Proficiency.Profieient)
				profMod += profByCR();
		
		return aMod + profMod;
	}
	
	public String getSkillString() {
		String toReturn = "";
		if(skills != null) {
			
		}
		for(Skills s : skills.keySet()) {
			toReturn += s.name() + " +" + getSkillMod(s) + ", ";
		}
		if(toReturn.length() > 2)
			return toReturn.substring(0, toReturn.length() - 2);
		else
			return toReturn;
	}
	
	public String getCRString() {
		if(cr == .125) {
			return "1/8 (PB +" + profByCR() + ")"; 
		}else if(cr == .25) {
			return "1/4 (PB +" + profByCR() + ")";
		}else if(cr == .5) {
			return "1/2 (PB +" + profByCR() + ")";
		}else {
			return ((int) cr) + " (PB +" + profByCR() + ")";
		}
	}
	
	public String getSaveString(int save) {
		int saveVal = (saves[save] ? (getAbilityMod(save) + profByCR()) : getAbilityMod(save));
		if(saveVal < 0)
			return "" + saveVal;
		return "+" + saveVal;
	}
	
	public String getAbilityModString(int ab) {
		if(getAbilityMod(ab) < 0)
			return "" + getAbilityMod(ab);
		return "+" + getAbilityMod(ab);
	}
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();

	    sb.append("Name: ").append(name).append("\n");
	    sb.append("Type/Size/Alignment: ").append(typeSizeAlignment).append("\n");
	    sb.append("AC: ").append(ac).append("\n");
	    sb.append("Initiative: ").append(init).append("\n");
	    sb.append("HP: ").append(hp).append("\n");
	    sb.append("Speed: ").append(speed).append("\n");
	    sb.append("Senses: ").append(senses).append("\n");
	    sb.append("Languages: ").append(languages).append("\n");

	    sb.append("Stats: ").append(java.util.Arrays.toString(stats)).append("\n");
	    sb.append("Saves: ").append(java.util.Arrays.toString(saves)).append("\n");

	    sb.append("Legendary Actions: ").append(lActNum).append(" (Bonus: ").append(lActBns).append(")\n");

	    sb.append("Skills: ").append(skills).append("\n");
	    sb.append("CR: ").append(cr).append("\n");

	    sb.append("Tags: ").append(tags).append("\n");

	    appendDocument(sb, "Traits Document", traits);
	    appendDocument(sb, "Actions Document", actions);
	    appendDocument(sb, "Bonus Actions Document", bonusActions);
	    appendDocument(sb, "Reactions Document", reactions);
	    appendDocument(sb, "Legendary Actions Document", legendActions);

	    return sb.toString();
	}

	private void appendDocument(StringBuilder sb, String label, StyledDocument doc) {
	    if (doc != null) {
	        try {
	            sb.append(label).append(":\n");
	            sb.append(doc.getText(0, doc.getLength())).append("\n\n");
	        } catch (Exception e) {
	            sb.append(label).append(": [Error reading document]\n");
	        }
	    } else {
	        sb.append(label).append(": [null]\n");
	    }
	}

	
	public static int ProfCRCalc(double cr) {
		if(cr < 5)
			return 2;
		else if(cr >= 5 && cr < 9)
			return 3;
		else if(cr >= 9 && cr < 13)
			return 4;
		else if(cr >= 13 && cr < 17)
			return 5;
		else if(cr>= 17 && cr < 21)
			return 6;
		else if(cr >= 21 && cr < 25)
			return 7;
		else if(cr >= 25 && cr < 29)
			return 8;
		else if(cr >= 29)
			return 9;
		else
			return 0;
	}
	
	public static int AbilityModCalc(int statVal) {
		return (statVal - 10) / 2;
	}
	
	public int GetInitBonus() {
		try {
			if(init.charAt(0) == '+') return Integer.parseInt(init.substring(1, init.lastIndexOf('(')));
			else return Integer.parseInt(init.substring(0, init.lastIndexOf('(')));
	    } catch (NumberFormatException e) {
	        return 0;
	    }
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;

	    Monster other = (Monster) obj;

	    return lActNum == other.lActNum &&
	           lActBns == other.lActBns &&
	           Double.compare(other.cr, cr) == 0 &&
	           custom == other.custom &&
	           java.util.Objects.equals(name, other.name) &&
	           java.util.Objects.equals(typeSizeAlignment, other.typeSizeAlignment) &&
	           java.util.Objects.equals(ac, other.ac) &&
	           java.util.Objects.equals(init, other.init) &&
	           java.util.Objects.equals(hp, other.hp) &&
	           java.util.Objects.equals(speed, other.speed) &&
	           java.util.Objects.equals(senses, other.senses) &&
	           java.util.Objects.equals(languages, other.languages) &&
	           java.util.Objects.equals(dmgVul, other.dmgVul) &&
	           java.util.Objects.equals(dmgRes, other.dmgRes) &&
	           java.util.Objects.equals(immune, other.immune) &&
	           java.util.Arrays.equals(stats, other.stats) &&
	           java.util.Arrays.equals(saves, other.saves) &&
	           java.util.Objects.equals(skills, other.skills) &&
	           java.util.Objects.equals(tags, other.tags) &&
	           source == other.source;
	}

	@Override
	public int hashCode() {
	    int result = java.util.Objects.hash(name, typeSizeAlignment, ac, init, hp, speed, senses, languages,
	                                        dmgVul, dmgRes, immune, lActNum, lActBns, skills, cr, tags, custom, source);
	    result = 31 * result + java.util.Arrays.hashCode(stats);
	    result = 31 * result + java.util.Arrays.hashCode(saves);
	    return result;
	}

}