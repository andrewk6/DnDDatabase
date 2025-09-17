package gui.hazard;

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
import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import data.hazards.Hazard;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

@SuppressWarnings("serial")
public class HazardIFrame extends JInternalFrame implements ContentTab, DataChangeListener
{
	private DataContainer data;
	private GuiDirector gd;
	
	private JTabbedPane tabs;
	private ReminderField filter;
	private JPanel sidePane;
	
	public HazardIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.data.registerListener(this);
		this.gd = gd;
		
		ConfigFrame();
		BuildContent(this.getContentPane());
	}
	
	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Hazard Database");
		StyleContainer.SetIcon(this, StyleContainer.HAZARD_ICON_FILE);
		addInternalFrameListener(CompFactory.createNonCloseListener(this));
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		gd.NotifyFocus(this);
	}
	
	private void BuildContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		cPane.add(sideWrapper, BorderLayout.WEST);
		
		filter = CompFactory.createReminderField("Filter...", ComponentType.BODY);
		sideWrapper.add(filter, BorderLayout.NORTH);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = CompFactory.wrapPanelInScroll(sidePane);
		
		cPane.add(sideScroll, BorderLayout.WEST);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		List<String> hazKeys;
		if(filter.getText().length() > 0) {
			hazKeys = new ArrayList<String>();
			for(String s : data.getHazardKeysSorted())
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					hazKeys.add(s);
		}else
			hazKeys = data.getHazardKeysSorted();
		
		List<String> trapKeys;
		if(filter.getText().length() > 0) {
			trapKeys = new ArrayList<String>();
			for(String s : data.getTrapKeysSorted())
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					trapKeys.add(s);
		}else
			trapKeys = data.getTrapKeysSorted();
		
		JLabel hazLbl = CompFactory.createNewLabel("Hazards", ComponentType.HEADER, 4f);
		sidePane.add(hazLbl);
		FillSideList(hazKeys);
		
		JLabel trapLbl = CompFactory.createNewLabel("Traps", ComponentType.HEADER, 4f);
		trapLbl.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, Color.black));
		sidePane.add(trapLbl);
		FillSideList(trapKeys);
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void FillSideList(List<String> keys) {
		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				AddTab(data.getHazards().get(key));
			}));
			sidePane.add(lbl);
		}
	}
	
	public void AddTab(Hazard haz) {
		JPanel tab = new JPanel();
		tab.setLayout(new BorderLayout());
		
		HazardPane sPane = new HazardPane(haz, data, gd);
		tab.add(sPane, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tab.add(btnPane, BorderLayout.SOUTH);
		
		btnPane.add(CompFactory.createNewButton("Remove " + haz.name + " Tab", _->{
			tabs.removeTabAt(tabs.indexOfComponent(tab));
		}));
		tabs.addTab(haz.name, tab);
	}
	
	public JTabbedPane GetTabs() {
		return tabs;
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.HAZARDS)
			FillSidePane();
	}
	
	
}