package gui.species;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import data.DataContainer;
import data.players.Species;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class SpeciesPane extends JPanel
{
	private DataContainer data;
	private GuiDirector gd;
	private Species s;
	
	public SpeciesPane(DataContainer data, GuiDirector gd, Species s) {
		this.data = data;
		this.gd = gd;
		this.s = s;
		
		BuildContent();
	}
	
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		JLabel nameLbl = CompFactory.createNewLabel(s.name, ComponentType.HEADER);
		hPane.add(nameLbl, BorderLayout.CENTER);
		hPane.add(CompFactory.createNewLabel("Source: " + s.src.toString(), ComponentType.BODY),
				BorderLayout.EAST);
		
		HoverTextPane hDesc = new HoverTextPane(data, gd, gd.getDesktop());
		StyleContainer.SetFontMain(hDesc);
		hDesc.setDocument(s.desc);
		JScrollPane scroll = CompFactory.wrapPanelInScroll(hDesc);
		this.add(scroll, BorderLayout.CENTER);
	}
}