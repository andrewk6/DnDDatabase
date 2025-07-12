package data.campaign;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.text.StyledDocument;

public class Campaign implements Serializable
{
	private static final long serialVersionUID = -7676096596584890920L;
	
	public File saveLoc;
	public HashMap<String, Player> party;
	public LinkedHashMap<String, StyledDocument> notes;
	
//	public boolean updated = false;
	
	public Campaign(File f) {
		party = new HashMap<String, Player>();
		notes = new LinkedHashMap<String, StyledDocument>();
		
		this.saveLoc = f;
	}
	
	public void AddPlayer(Player p) {
		party.put(p.name, p);
	}
	
	
	public void RemovePlayer(String s) {
		party.remove(s);
	}
	
	public void RemovePlayer(Player p) {
		party.remove(p.name);
	}
	
	public void AddNote(String title, StyledDocument note) {
		notes.put(title, note);
	}
	
	public void RemoveNote(String noteTite) {
		notes.remove(noteTite);
	}
}