package data;

import java.io.Serializable;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import data.DataContainer.Source;

public class Spell implements Comparable<Spell>, Serializable
{
	private static final long serialVersionUID = -6989452218899645676L;
	public String name, descrBasic;
	public StyledDocument spellDoc;
	public boolean custom = false;
	public Source source = Source.PlayersHandbook2024;
	
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
			e.printStackTrace();
			return "Bad Spell ToString";
		}
	}
	
	@Override
	public int compareTo(Spell o) {
		return name.compareTo(o.name);
	}
	
}