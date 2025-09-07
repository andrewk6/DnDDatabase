package gui.species;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import data.players.Species;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class SpeciesIFrame extends JInternalFrame implements ContentTab, DataChangeListener
{
	private DataContainer data;
	private GuiDirector gd;
	
	private JTabbedPane tabs;
	private ReminderField filter;
	private JPanel sidePane;
	
	public SpeciesIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.data.registerListener(this);
		this.gd = gd;
		
		ConfigFrame();
		BuildContent(this.getContentPane());
	}
	
	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Species Database");
		StyleContainer.SetIcon(this, StyleContainer.SPECIES_ICON_FILE);
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
		JScrollPane sideScroll = new JScrollPane(sidePane);
		
		cPane.add(sideScroll, BorderLayout.WEST);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		List<String> keys;
		if(filter.getText().length() > 0) {
			keys = new ArrayList<String>();
			for(String s : data.getSpeciesKeysSorted())
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					keys.add(s);
		}else
			keys = data.getSpeciesKeysSorted();

		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				AddTab(data.getSpecies().get(key));
			}));
			sidePane.add(lbl);
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	public void AddTab(Species s) {
		JPanel tab = new JPanel();
		tab.setLayout(new BorderLayout());
		
		SpeciesPane sPane = new SpeciesPane(data, gd, s);
		tab.add(sPane, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tab.add(btnPane, BorderLayout.SOUTH);
		
		btnPane.add(CompFactory.createNewButton("Remove " + s.name + " Tab", _->{
			tabs.removeTabAt(tabs.indexOfComponent(tab));
		}));
		tabs.addTab(s.name, tab);
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
		if(mapType == MapType.SPECIES)
			FillSidePane();
	}
	
	
}