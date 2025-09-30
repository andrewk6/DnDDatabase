package data.siege_equipment;

import java.io.Serializable;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;
import data.items.Item;
import gui.gui_helpers.DocumentHelper;
import utils.ErrorLogger;

public class SiegeEquipment implements Serializable, SourceProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6480567900516674182L;

	public enum SiegeUse{
		NONE("No Requirements",""),
		LOADAIM("Load and Aim", "Requires"),
		AIM("Aim", "Requires"),
		POSITION("Position", "Requires"),
		CAULDRON("Full Cauldron", "Requires a");
		
		private String desc;
		private String requireText;
		
		SiegeUse(String desc, String requireText){
			this.desc = desc;
			this.requireText = requireText;
		}
		
		public String toString() {
			return desc;
		}
		
		public String getRequired() {
			return this.requireText + " " + desc;
		}
	}
	
	public enum SiegeSize{
		MEDIUM("Medium Object"),
		LARGE("Large Object"),
		HUGE("Huge Object"),
		GARGANTUAN("Gargantuan Object");
		
		private String desc;
		
		SiegeSize(String desc){
			this.desc = desc;
		}
		
		public String toString() {
			return desc;
		}
	}
	
	public String name, attackName;
	public int ac, hp;
	public Source source;
	public SiegeUse use;
	public SiegeSize size;
	public StyledDocument attack, desc;
	
	public StyledDocument getFullAttackDoc() {
		StyledDocument doc = DocumentHelper.deepCopyDocument(attack);
		String attackLead = attackName + " (" + use.getRequired() + "). ";
		try {
			SimpleAttributeSet attrs = new SimpleAttributeSet();
	        StyleConstants.setBold(attrs, true);
			doc.insertString(0, attackLead, attrs);
		} catch (BadLocationException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
		}
		return doc;
	}
	
	public StyledDocument getFullDocument() {
		StyledDocument doc = DocumentHelper.deepCopyDocument(desc);
		if(attack!= null) {
			if(attack.getLength() > 0) {
				StyledDocument aDoc = DocumentHelper.deepCopyDocument(attack);
				try {
					aDoc.insertString(attack.getLength() - 1, "\n", null);
				} catch (BadLocationException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
				try {
					DocumentHelper.insertStyledDocument(
							doc, aDoc, 0);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		return doc;
	}
	
	@Override
	public Source getSource() {
		return source;
	}
}