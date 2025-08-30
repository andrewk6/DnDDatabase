package gui.classes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataContainer;
import data.players.classes.Subclass;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.GuiDirector;

public class SubclassPane extends JPanel
{
	private Subclass sub;
	private DataContainer data;
	private GuiDirector gd;
	
	public SubclassPane(DataContainer data, GuiDirector gd, Subclass sub){
		this.sub = sub;
		this.data = data;
		this.gd = gd;
		
		Init();
	}
	
	private void Init() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		JLabel subLbl = CompFactory.createNewLabel(sub.name, ComponentType.HEADER);
		hPane.add(subLbl, BorderLayout.CENTER);
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		hPane.add(srcPane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source: ", ComponentType.HEADER);
		srcPane.add(srcLbl);
		
		JLabel srcVal = CompFactory.createNewLabel(sub.src.toString(), ComponentType.BODY);
		srcPane.add(srcVal);
		
		this.add(new AbilityPane(data, gd, sub.abilities), BorderLayout.CENTER);
	}
}