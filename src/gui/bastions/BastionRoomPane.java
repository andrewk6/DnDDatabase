package gui.bastions;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.players.BastionRoom;
import data.players.BastionRoom.Order;
import data.players.BastionRoom.SpaceRequired;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class BastionRoomPane extends JPanel
{
	private DataContainer data;
	private GuiDirector gd;
	private BastionRoom room;
	/*
	 * public enum SpaceRequired { Cramped, Roomy, Vast };
	public enum Order { Craft, Empower, Harvest, Maintain, Recruit, Research, Trade };
	
	public String name, prereq, hirelings;
	public StyledDocument desc;
	public int unlockLevel;
	public SpaceRequired spaceReq;
	public Order roomOrder;
	 */
	
	public BastionRoomPane(DataContainer data, GuiDirector gd, BastionRoom room) {
		this.data = data;
		this.gd = gd;
		this.room = room;
		
		init();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		JLabel nameLbl = CompFactory.createNewLabel(room.name, ComponentType.HEADER);
		hPane.add(nameLbl, BorderLayout.NORTH);
		
		JPanel hdPane = new JPanel();
		hdPane.setLayout(new GridLayout(0,1));
		hPane.add(hdPane, BorderLayout.CENTER);
		hdPane.add(buildPane("Prerequisites: ", room.prereq));
		hdPane.add(buildPane("Hirelings: ", room.hirelings));
		hdPane.add(buildPane("Level: ", "Level " + room.unlockLevel + " Bastion Facility."));
		hdPane.add(buildPane("Space: ", room.spaceReq.toString()));
		hdPane.add(buildPane("Order: ", room.roomOrder.toString()));
		
		HoverTextPane desc = new HoverTextPane(data, gd, gd.getDesktop());
		desc.setDocument(room.desc);
		this.add(desc,BorderLayout.CENTER);
	}
	
	private JPanel buildPane(String title, String desc) {
		JPanel out = new JPanel();
		out.setLayout(new BorderLayout());
		out.add(CompFactory.createNewLabel(title, ComponentType.HEADER), BorderLayout.WEST);
		out.add(CompFactory.createNewLabel(desc, ComponentType.BODY), BorderLayout.CENTER);
		return out;
	}
}