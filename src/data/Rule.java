package data;

import java.io.IOException;
import java.io.Serializable;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import data.DataContainer.Source;
import data.interfaces.InsertString;
import data.interfaces.SourceProvider;
import utils.ErrorLogger;

public class Rule implements Comparable<Rule>, Serializable, SourceProvider, InsertString{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String insertString = null;
	public StyledDocument ruleDoc;
	public Source src;

	public Rule() {
		name = "NONE";
		ruleDoc = null;
	}

	public Rule(String name, StyledDocument ruleDoc) {
		this.name = name;
		this.ruleDoc = ruleDoc;
	}

	public String toString() {
		try {
			return name + ": " + ruleDoc.getText(0, ruleDoc.getLength());
		} catch (BadLocationException e) {
			ErrorLogger.log(e);
			return name;
		}
	}

	public int compareTo(Rule r) {
		return name.compareTo(r.name);
	}

	@Override
	public Source getSource() {
		return src;
	}
	
	public String getInsert() {
		return insertString;
	}
}