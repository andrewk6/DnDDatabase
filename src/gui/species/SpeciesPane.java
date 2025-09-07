package gui.species;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.players.Species;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class SpeciesPane extends JPanel
{
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(()->{
			javax.swing.JFrame frm = new javax.swing.JFrame();
			frm.add(new SpeciesPane(data, new GuiDirector(new 
					javax.swing.JDesktopPane()), data.getSpecies().get("Aasimar")));
			frm.pack();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setVisible(true);			
		});
	}
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
		JScrollPane scroll = new JScrollPane(hDesc);
		this.add(scroll, BorderLayout.CENTER);
	}
}