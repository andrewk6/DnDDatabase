package gui.dungeon;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataContainer;
import data.dungeon.DungeonNote;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class NoteViewer extends JPanel
{
	public NoteViewer(DataContainer data, GuiDirector gd, DungeonNote n) {
		this.setLayout(new BorderLayout());
		
		HoverTextPane notePane = new HoverTextPane(data, gd, gd.getDesktop());
		notePane.setDocument(n.note);
		this.add(notePane, BorderLayout.CENTER);
		
		JLabel titleLbl = CompFactory.createNewLabel(n.title, ComponentType.HEADER);
		this.add(titleLbl, BorderLayout.NORTH);
	}
}