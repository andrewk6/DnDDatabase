package data.dungeon;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

public class DungeonNote implements Serializable
{
	private static final long serialVersionUID = -1687414734918499572L;
	public StyledDocument note;
	public String title;
	public String check;
	
	public DungeonNote() {
		title = "";
		check = "";
		note = null;
	}
}