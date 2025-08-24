package data.players;

import javax.swing.text.StyledDocument;

public class Ability
{
	public enum UseType { Action, BonusAction, Reaction, Free, Static };
	
	public String name;
	public StyledDocument desc;
	public UseType use;
}