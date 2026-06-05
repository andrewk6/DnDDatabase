package gui.bastions;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.MapType;
import data.interfaces.DataChangeListener;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class BastionPane extends JPanel implements DataChangeListener
{
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		GuiDirector gd = new GuiDirector(new JDesktopPane());
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setContentPane(new BastionPane(data, gd));
			frm.pack();
			frm.setSize(new Dimension(800,800));
			frm.setVisible(true);
		});
	}
	private DataContainer data;
	private GuiDirector gd;
	
	private CardLayout cl;
	private JTabbedPane roomTabs;
	private JPanel roomSide;
	private JButton swapBtn;
	
	private boolean rules = true;
	private final String roomBtnTxt = "View Rooms Descriptions";
	private final String rulesBtnTxt = "View Rules Descriptions";
	private final String ruleCard = "RULESCARD";
	private final String roomCard = "ROOMCARD";
	
	public BastionPane(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		
		init();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new CardLayout());
		this.add(mPane, BorderLayout.CENTER);
		cl = (CardLayout) mPane.getLayout();
		
		BuildRoomsPane(mPane);
		BuildRulePane(mPane);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(btnPane, BorderLayout.SOUTH);
		
		swapBtn = CompFactory.createNewButton(roomBtnTxt);
		swapBtn.addActionListener(_->{
			if(rules) {
				cl.show(mPane, roomCard);
				swapBtn.setText(rulesBtnTxt);
				
			}else {
				cl.show(mPane, ruleCard);
				swapBtn.setText(roomBtnTxt);
			}
			rules = !rules;
			mPane.revalidate();
			mPane.repaint();
		});
		btnPane.add(swapBtn);
		FillRoomSide();
		cl.show(mPane, ruleCard);
	}
	
	private void BuildRoomsPane(JPanel mPane) {
		JPanel rPane = new JPanel();
		rPane.setLayout(new BorderLayout());
		mPane.add(rPane, roomCard);
		
		roomTabs = new JTabbedPane();
		rPane.add(roomTabs, BorderLayout.CENTER);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		rPane.add(sideWrapper, BorderLayout.WEST);
		
		roomSide = new JPanel();
		roomSide.setLayout(new GridLayout(0,1));
		JScrollPane roomScroll = CompFactory.wrapPanelInScroll(roomSide, ScrollPolicy.VERTICAL);
		sideWrapper.add(roomScroll, BorderLayout.CENTER);
		FillRoomSide();
	}
	
	private void BuildRulePane(JPanel mPane) {
		JPanel rulePane = new JPanel();
		rulePane.setLayout(new BorderLayout());
		mPane.add(rulePane, ruleCard);

		JTabbedPane ruleTabs = new JTabbedPane();
		rulePane.add(ruleTabs, BorderLayout.CENTER);
		
		/*
		 * Bastion Overview
		 * Gaining a Bastion
		 * Bastion Map
		 * Basic Facilities
		 * Special Facilities
		 * Bastion Turns
		 * Bastion Events
		 * Fall of a Bastion
		 */
		ruleTabs.addTab("Bastion Overview", getRulePane("Bastion Overview"));
		ruleTabs.addTab("Gaining a Bastion", getRulePane("Gaining a Bastion"));
		ruleTabs.addTab("Bastion Map", getRulePane("Bastion Map"));
		ruleTabs.addTab("Basic Facilities", getRulePane("Basic Facilities"));
		ruleTabs.addTab("Special Facilities", getRulePane("Special Facilities"));
		ruleTabs.addTab("Bastion Turns", getRulePane("Bastion Turns"));
		ruleTabs.addTab("Bastion Events", getRulePane("Bastion Events"));
		ruleTabs.addTab("Fall of a Bastion", getRulePane("Fall of a Bastion"));
		ruleTabs.addTab("Mobile Bastions", getRulePane("Mobile Bastions"));
		ruleTabs.addTab("Haunted Bastions", getRulePane("Haunted Bastions"));
		
	}
	
	private JScrollPane getRulePane(String key) {
		HoverTextPane ruleDesc = new HoverTextPane(data, gd, gd.getDesktop());
		ruleDesc.setDocument(data.getBastionRules().get(key));
		return CompFactory.wrapPanelInScroll(ruleDesc, ScrollPolicy.VERTICAL);
	}
	
	private void FillRoomSide() {
		roomSide.removeAll();
		ArrayList<String> keys = new ArrayList<String>(data.getBastionRoomKeysSorted());
		Collections.sort(keys);

		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				JPanel tabPane = new JPanel();
				tabPane.setLayout(new BorderLayout());
				roomTabs.addTab(key, tabPane);
				
				tabPane.add(new BastionRoomPane(data, gd, data.getBastionRooms().get(key)), BorderLayout.CENTER);
				
				JPanel btnPane = new JPanel();
				btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				tabPane.add(btnPane, BorderLayout.SOUTH);
				
				JButton removeBtn = CompFactory.createNewButton("Close Tab: " + key, _->{
					roomTabs.removeTabAt(roomTabs.indexOfComponent(tabPane));
				});
				btnPane.add(removeBtn);
			}));
			roomSide.add(lbl);
		}
		
		roomSide.revalidate();
		roomSide.repaint();
	}

	@Override
	public void onMapUpdated() {
		FillRoomSide();
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.BASTION_ROOMS)
			FillRoomSide();
	}
}