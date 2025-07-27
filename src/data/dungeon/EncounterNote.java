package data.dungeon;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.text.StyledDocument;

import data.Monster;

public class EncounterNote implements Serializable
{
	private static final long serialVersionUID = 859185018864175697L;
	
	public ArrayList<Monster> enemies;
	public StyledDocument note;
	public String title;
	
	public EncounterNote() {
		enemies = new ArrayList<Monster>();
	}
}