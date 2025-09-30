package gui.siege_equipment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import data.DataContainer;
import data.DataContainer.MapType;
import data.hazards.Hazard;
import data.interfaces.DataChangeListener;
import data.siege_equipment.SiegeEquipment;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

@SuppressWarnings("serial")
public class SiegeEquipmentPane extends JPanel implements DataChangeListener
{
	private DataContainer data;
	private GuiDirector gd;
	
	private JTabbedPane tabs;
	private ReminderField filter;
	private JPanel sidePane;
	
	public SiegeEquipmentPane(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.data.registerListener(this);
		this.gd = gd;
		
		
		BuildContent();
	}
	
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		this.add(tabs, BorderLayout.CENTER);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		this.add(sideWrapper, BorderLayout.WEST);
		
		filter = CompFactory.createReminderField("Filter...", ComponentType.BODY);
		sideWrapper.add(filter, BorderLayout.NORTH);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = CompFactory.wrapPanelInScroll(sidePane);
		
		this.add(sideScroll, BorderLayout.WEST);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		List<String> keys;
		if(filter.getText().length() > 0) {
			keys = new ArrayList<String>();
			for(String s : data.getSiegeEquipmentKeysSorted())
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					keys.add(s);
		}else
			keys = data.getSiegeEquipmentKeysSorted();
		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				AddTab(data.getSiegeEquipment().get(key));
			}));
			sidePane.add(lbl);
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	public void AddTab(SiegeEquipment siege) {
		JPanel tab = new JPanel();
		tab.setLayout(new BorderLayout());
		
		SiegeEquipPane sPane = new SiegeEquipPane(siege, data, gd);
		tab.add(sPane, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tab.add(btnPane, BorderLayout.SOUTH);
		
		btnPane.add(CompFactory.createNewButton("Remove " + siege.name + " Tab", _->{
			tabs.removeTabAt(tabs.indexOfComponent(tab));
		}));
		tabs.addTab(siege.name, tab);
	}
	
	public JTabbedPane GetTabs() {
		return tabs;
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
		System.out.println("Map Update");
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.SIEGEEQUIP)
			FillSidePane();
		System.out.println("Map Update");
	}	
}