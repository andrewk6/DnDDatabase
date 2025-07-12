package data;

import java.io.IOException;
import java.io.Serializable;
import javax.swing.text.StyledDocument;
import builders.rule_builder.CustomStyledDocument;

public class Rule implements Comparable<Rule>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String desc_basic, desc_HTML;
	public StyledDocument ruleDoc;

	public Rule() {
		name = "NONE";
		desc_basic = "NONE";
		desc_HTML = "<h>NONE</h>";
		ruleDoc = null;
	}

	public Rule(String name, String desc, String desc_HTML, StyledDocument ruleDoc) {
		this.name = name;
		this.desc_basic = desc;
		this.desc_HTML = desc_HTML;
		this.ruleDoc = ruleDoc;
	}

	public String toString() {
		return name + ": " + desc_basic;
	}

	public int compareTo(Rule r) {
		return name.compareTo(r.name);
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	    if(ruleDoc instanceof CustomStyledDocument) {
	    	((CustomStyledDocument) ruleDoc).stopEditingAllTables();
	    	System.out.println("Running");
	    }
	    out.defaultWriteObject();  // Proceed with default serialization
	}
}