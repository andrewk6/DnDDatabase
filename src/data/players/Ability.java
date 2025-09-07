package data.players;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

public class Ability implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338681996081385138L;
	public enum UseType { Action, BonusAction, Reaction, Free, Static };
	
	public String name;
	public StyledDocument desc;
	public UseType use;
}