package gui.dungeon;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.text.StyledDocument;

import data.dungeon.DungeonNote;

public class NoteDialog extends JDialog{
	private static final long serialVersionUID = 725990709923540407L;
	
	public NoteDialog(DungeonNote note) {
		super();
		setLayout(new BorderLayout());
	}
}