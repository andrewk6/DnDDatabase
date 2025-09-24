package data.players;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public class Species implements Serializable, SourceProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8332857870833202566L;
	
	public String name;
	public StyledDocument desc;
	public Source src;
	
	public Source getSource() {
		return src;
	}
}