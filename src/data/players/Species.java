package data.players;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import data.DataContainer.Source;

public class Species implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8332857870833202566L;
	
	public String name;
	public StyledDocument desc;
	public Source src;
}