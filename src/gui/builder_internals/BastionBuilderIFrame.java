package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import data.DataContainer;
import data.DataContainer.MapType;
import data.players.BastionRoom;
import data.players.BastionRoom.Order;
import data.players.BastionRoom.SpaceRequired;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;

public class BastionBuilderIFrame extends JInternalFrame
{	
	private DataContainer data;
	private HashMap<String, BastionRoom> bastRoomMap;
	
	private JPanel sidePane;
	
	private JComboBox<SpaceRequired> spaceCombo;
	private JComboBox<Order> orderCombo;
	private ReminderField nameField, prereqField, hirelingField, levelField;
	private RichEditor edit;
	
	public BastionBuilderIFrame(DataContainer data)
	{
		this.data = data;
		if(this.data.getBastionRooms() != null) {
			bastRoomMap = new HashMap<String, BastionRoom>(data.getBastionRooms());
		}else {
			bastRoomMap = new HashMap<String, BastionRoom>();
		}
		
		init(this.getContentPane());
		
		pack();
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Bastion Room Builder");
		StyleContainer.SetIcon(this, StyleContainer.BASTION_ROOM_BUILD_ICON_FILE);
	}

	private void init(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		mPane.add(hPane, BorderLayout.NORTH);
		
		nameField = CompFactory.createReminderField("Room Name...", ComponentType.HEADER);
		hPane.add(nameField, BorderLayout.NORTH);
		
		JPanel statsPane = new JPanel();
		statsPane.setLayout(new GridLayout(2, 3));
		hPane.add(statsPane, BorderLayout.CENTER);
		BuildStatsPane(statsPane);
		
		JPanel comboPane = new JPanel();
		comboPane.setLayout(new GridLayout(2,2));
		hPane.add(comboPane, BorderLayout.SOUTH);
		BuildComboPane(comboPane);
		
		edit = new RichEditor(data);
		mPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton resetButton = CompFactory.createNewButton("Reset Editor", _->{
			if(ResetConfirm())
				Reset();
		});
		btnPane.add(resetButton);
		
		JButton addBtn = CompFactory.createNewButton("Add Room", _->{
			AddRoom();
		});
		btnPane.add(addBtn);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		cPane.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = CompFactory.wrapPanelInScroll(sidePane, ScrollPolicy.VERTICAL);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sideWrapper.add(sideScroll, BorderLayout.CENTER);
		
		JButton saveBtn = CompFactory.createNewButton("Save", _->{
			data.SetBastionRoomMap(bastRoomMap);
			data.SafeSaveData(MapType.BASTION_ROOMS);
		});
		sideWrapper.add(saveBtn, BorderLayout.SOUTH);
	}
	
	private void BuildStatsPane(JPanel sPane) {
		JLabel preLbl = CompFactory.createNewLabel("Prerequisites", ComponentType.BODY);
		sPane.add(preLbl);
		
		JLabel hireLbl = CompFactory.createNewLabel("HireLings", ComponentType.BODY);
		sPane.add(hireLbl);
		
		JLabel levelLbl = CompFactory.createNewLabel("Level Unlocked", ComponentType.BODY);
		sPane.add(levelLbl);
		
		prereqField = CompFactory.createReminderField("Room prerequisites.", ComponentType.BODY);
		sPane.add(prereqField);
		
		hirelingField = CompFactory.createReminderField("Hireling(s)", ComponentType.BODY);
		sPane.add(hirelingField);
		
		levelField = CompFactory.createReminderField("Level Unlocked.", true, ComponentType.BODY);
		sPane.add(levelField);
	}
	
	private void BuildComboPane(JPanel cPane) {
		JLabel spaceLbl = CompFactory.createNewLabel("Base Space Required", ComponentType.BODY);
		cPane.add(spaceLbl);
		
		JLabel orderLbl = CompFactory.createNewLabel("Room Order", ComponentType.BODY);
		cPane.add(orderLbl);
		
		spaceCombo = CompFactory.createEnumCombo(SpaceRequired.class, ComponentType.BODY);
		cPane.add(spaceCombo);
		
		orderCombo = CompFactory.createEnumCombo(Order.class, ComponentType.BODY);
		cPane.add(orderCombo);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys = new ArrayList<String>(bastRoomMap.keySet());
		Collections.sort(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				if(ResetConfirm())
					LoadRoom(bastRoomMap.get(key));
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton btn = CompFactory.createNewButton("Delete", _->{
				int conf = JOptionPane.showConfirmDialog(this, "Delete " + key, 
						"Delete Confirmation", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					bastRoomMap.remove(key);
					FillSidePane();
				}
			});
			pane.add(btn, BorderLayout.EAST);
		}
		
		sidePane.revalidate();
		sidePane.repaint();
		if(keys.size() == 1) {
			this.revalidate();
			this.repaint();
		}
	}
	
	private boolean ResetConfirm() {
		if(nameField.getText().length() > 0 || prereqField.getText().length() > 0 
				|| hirelingField.getText().length() > 0 || levelField.getText().length() > 0
				|| edit.getText().length() > 0) {
			int conf = JOptionPane.showConfirmDialog(this, "Reset the editor, you will lose any current work.",
					"Reset Confirm", JOptionPane.YES_NO_OPTION);
			return conf == JOptionPane.YES_OPTION;
		}else {
			return true;
		}
	}
	
	private void Reset() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.setFocusable(true);
		
		spaceCombo.setSelectedIndex(0);
		orderCombo.setSelectedIndex(0);
		
		prereqField.setText("");
		hirelingField.setText("");
		levelField.setText("");
		
		Container c = edit.getParent();
		c.remove(edit);
		edit = new RichEditor(data);
		c.add(edit, BorderLayout.CENTER);
		c.revalidate();
		c.repaint();
	}
	
	private void AddRoom() {
		if(nameField.getText().length() <= 0 || prereqField.getText().length() <= 0 
				|| hirelingField.getText().length() <= 0 || levelField.getText().length() <= 0
				|| edit.getText().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please finish all fields before adding",
					"Add Failure", JOptionPane.WARNING_MESSAGE);
		}else {
			BastionRoom room = new BastionRoom();
			room.name = nameField.getText();
			
			room.prereq = prereqField.getText();
			room.hirelings = hirelingField.getText();
			room.unlockLevel = Integer.parseInt(levelField.getText());
			
			room.spaceReq = (SpaceRequired) spaceCombo.getSelectedItem();
			room.roomOrder = (Order) orderCombo.getSelectedItem();
			
			room.desc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
			
			bastRoomMap.put(room.name, room);
			
			Reset();
			FillSidePane();
		}
	}
	
	private void LoadRoom(BastionRoom r) {
		Reset();
		nameField.setText(r.name);
		nameField.setEditable(false);
		nameField.setFocusable(false);
		
		prereqField.setText(r.prereq);
		hirelingField.setText(r.hirelings);
		levelField.setText("" + r.unlockLevel);
		
		spaceCombo.setSelectedItem(r.spaceReq);
		orderCombo.setSelectedItem(r.roomOrder);
		
		edit.LoadDocument(DocumentHelper.deepCopyDocument(r.desc));
	}
}