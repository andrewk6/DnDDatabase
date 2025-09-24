package gui.hazard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.DataContainer;
import data.hazards.Hazard;
import data.hazards.Trap;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

@SuppressWarnings("serial")
public class HazardPane extends JPanel
{
	private Hazard haz;
	private DataContainer data;
	private GuiDirector gd;
	
	public HazardPane(Hazard h, DataContainer data, GuiDirector gd) {
		super();
		this.haz = h;
		this.data = data;
		this.gd = gd;
		
		BuildContent();
	}
	
	public HazardPane(String h, DataContainer data, GuiDirector gd) {
		this(data.getHazards().get(h), data, gd);
	}
	
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		BuildHeader(hPane);
		
		HoverTextPane descPane = CompFactory.createHoverTextPane(data, gd, haz.desc, ComponentType.BODY);
		this.add(descPane, BorderLayout.CENTER);
	}
	
	private void BuildHeader(JPanel hPane) {
		if(haz instanceof Trap) {
			JPanel namePane = new JPanel();
			namePane.setLayout(new BorderLayout());
			namePane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
			hPane.add(namePane, BorderLayout.NORTH);
			
			JLabel nameLbl = CompFactory.createNewLabel(haz.name, ComponentType.HEADER);
			nameLbl.setFont(nameLbl.getFont().deriveFont(nameLbl.getFont().getSize() + 4f));
			namePane.add(nameLbl, BorderLayout.CENTER);
			
			JLabel srcLbl = CompFactory.createNewLabel(haz.src.toString(), ComponentType.BODY);
			namePane.add(srcLbl, BorderLayout.EAST);
			
			JPanel trapPane = new JPanel();
			trapPane.setLayout(new GridLayout(0,1));
			hPane.add(trapPane, BorderLayout.CENTER);
			
			JPanel trigPane = new JPanel();
			trigPane.setLayout(new BorderLayout());
			trapPane.add(trigPane);
			
			JLabel trigLbl = CompFactory.createNewLabel("Trigger: ", ComponentType.HEADER);
			trigPane.add(trigLbl, BorderLayout.WEST);
			
			JLabel trigVal = CompFactory.createNewLabel(((Trap)haz).trigger, ComponentType.BODY);
			trigPane.add(trigVal, BorderLayout.CENTER);
			
			JPanel durPane = new JPanel();
			durPane.setLayout(new BorderLayout());
			trapPane.add(durPane);
			
			JLabel durLbl = CompFactory.createNewLabel("Duration: ", ComponentType.HEADER);
			durPane.add(durLbl, BorderLayout.WEST);
			
			JLabel durVal = CompFactory.createNewLabel(((Trap)haz).duration, ComponentType.BODY);
			durPane.add(durVal, BorderLayout.CENTER);
		}else {
			JLabel nameLbl = CompFactory.createNewLabel(haz.name, ComponentType.HEADER);
			hPane.add(nameLbl, BorderLayout.CENTER);
			
			JLabel srcLbl = CompFactory.createNewLabel(haz.src.toString(), ComponentType.BODY);
			hPane.add(srcLbl, BorderLayout.EAST);
		}
	}
}