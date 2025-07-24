package data.dungeon;

import java.io.Serializable;
import java.util.TreeMap;

import javax.swing.text.StyledDocument;

public class Dungeon implements Serializable
{
	private static final long serialVersionUID = -1538038676471563920L;
	
	public TreeMap<String, DungeonFloor> floors;
	public String dungeonName;
	public StyledDocument notes;
	
	public Dungeon() {
		floors = new TreeMap<String, DungeonFloor>();
	}
}