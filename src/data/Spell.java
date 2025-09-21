package data;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import data.DataContainer.PlayerClass;
import data.DataContainer.Source;
import data.interfaces.SourceProvider;
import utils.ErrorLogger;

public class Spell implements Comparable<Spell>, Serializable, SourceProvider
{
	public enum SpellLevel{
		Cantrip, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth
	}
	
	public enum SpellSchool{
		Abjuration, Conjuration, Divination, Enchantment, Evocation, Illusion,
		Necromancy, Transmutation
	}
	
	private static final long serialVersionUID = -6989452218899645676L;
	public String name, descrBasic;
	public StyledDocument spellDoc;
	
	public Source source = Source.PlayersHandbook2024;
	public SpellLevel spellLevel;
	public SpellSchool spellSchool;
	public ArrayList<PlayerClass> classList = new ArrayList<PlayerClass>();
	
	public Spell() {
		name = "";
		descrBasic = "";
		spellDoc = null;
	}
	
	public Spell(String name, String descrBasic, StyledDocument spellDoc) {
		this.name = name;
		this.descrBasic = descrBasic;
		this.spellDoc = spellDoc;
	}
	public String toString() {
		try {
			return name + ": " + descrBasic +"\n" + spellDoc.getText(0, spellDoc.getLength());
		} catch (BadLocationException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
			return "Bad Spell ToString";
		}
	}
	
	@Override
	public int compareTo(Spell o) {
		if(this.spellLevel == o.spellLevel)
			return name.compareTo(o.name);
		return spellLevel.compareTo(o.spellLevel);
	}

	@Override
	public Source getSource() {
		return source;
	}
	
}