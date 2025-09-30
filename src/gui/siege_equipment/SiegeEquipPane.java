package gui.siege_equipment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.siege_equipment.SiegeEquipment;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;
import utils.ErrorLogger;

public class SiegeEquipPane extends JPanel
{
	public static void main(String[]args) {
		DataContainer d = new DataContainer();
		d.init();
		GuiDirector gd = new GuiDirector(new JDesktopPane());
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, d));
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.setContentPane(new SiegeEquipPane(d.getSiegeEquipment().get("Ballista"), d, gd));
			frm.setVisible(true);
		});
	}
	private final DataContainer data;
	private final GuiDirector gd;
	private final SiegeEquipment siege;
	public SiegeEquipPane(SiegeEquipment siege, DataContainer data, GuiDirector gd) {
		this.siege = siege;
		this.data = data;
		this.gd = gd;
		
		BuildContent();
	}
	
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		this.add(topPane, BorderLayout.NORTH);
		
		JPanel headPane = new JPanel();
		headPane.setLayout(new BorderLayout());
		topPane.add(headPane, BorderLayout.CENTER);
		
		headPane.add(CompFactory.createNewLabel(
				siege.name, ComponentType.HEADER, 2f), BorderLayout.CENTER);
		JPanel srcPane = CompFactory.createDescriptionPane("Source: ", siege.source.toString());
		headPane.add(srcPane, BorderLayout.EAST);
		
		JPanel statPane = new JPanel();
		statPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPane.add(statPane, BorderLayout.SOUTH);
		
		statPane.add(CompFactory.createNewLabel("Armor Class: ", ComponentType.HEADER));
		statPane.add(CompFactory.createNewLabel(siege.ac + ""));
		statPane.add(CompFactory.createNewLabel("Hit Points: ", ComponentType.HEADER));
		statPane.add(CompFactory.createNewLabel(siege.hp + ""));
		
		HoverTextPane hPane = new HoverTextPane(data, gd, gd.getDesktop());
		
		hPane.setStyledDocument(siege.getFullDocument());
		this.add(hPane, BorderLayout.CENTER);
	}
}