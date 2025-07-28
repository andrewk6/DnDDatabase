package gui.dungeon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.DataContainer;
import data.Monster;
import data.dungeon.EncounterNote;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class EncounterViewer extends JPanel
{
	public EncounterViewer(DataContainer data, GuiDirector gd, EncounterNote n)
	{
		this.setLayout(new BorderLayout());
		JLabel titleLbl = CompFactory.createNewLabel(n.title, ComponentType.HEADER);
		this.add(titleLbl, BorderLayout.NORTH);
		
		HoverTextPane notePane = new HoverTextPane(data, gd, gd.getDesktop());
		notePane.setDocument(n.note);
		this.add(notePane, BorderLayout.CENTER);
		
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		this.add(sidePane, BorderLayout.WEST);
		
		JPanel enemyListPane = new JPanel();
		enemyListPane.setLayout(new GridLayout(0,1));
		JScrollPane listScroll = new JScrollPane(enemyListPane);
		sidePane.add(listScroll);
		
		for(Monster m : n.enemies) {
			JLabel mLabel = CompFactory.createNewLabel(m.name, ComponentType.HEADER);
			enemyListPane.add(mLabel);
		}
		
		JButton popInit = CompFactory.createNewButton("Go To Initiative", _->{
			gd.addInitiaitive(n.enemies);
		});
		sidePane.add(popInit, BorderLayout.SOUTH);
	}
}