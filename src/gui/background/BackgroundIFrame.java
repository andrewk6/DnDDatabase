package gui.background;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data.DataContainer;
import data.DataContainer.MapType;
import data.interfaces.DataChangeListener;
import data.players.Background;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class BackgroundIFrame extends JInternalFrame implements DataChangeListener, ContentTab
{
	private DataContainer data; 
	private GuiDirector gd;
	
	private JTabbedPane tabs;
	
	private JPanel sidePane;
	private ReminderField filter;
	
	public BackgroundIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		
		this.data.registerListener(this);
		
		ConfigFrame();
		BuildContent(getContentPane());
	}
	
	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Background Database");
		StyleContainer.SetIcon(this, StyleContainer.BACKGROUND_ICON_FILE);
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
		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void removeUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		filter.setColumns(20);
		sideWrapper.add(filter, BorderLayout.NORTH);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = CompFactory.wrapPanelInScroll(sidePane, ScrollPolicy.VERTICAL);
		sideWrapper.add(sideScroll, BorderLayout.CENTER);
		
		FillSidePane();
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys;
		if(filter.getText().length() <= 0)
			keys = new ArrayList<String>(data.getBackgroundKeysSorted());
		else {
			keys = new ArrayList<String>();
			for(String key : data.getBackgroundKeysSorted())
				if(key.toLowerCase().contains(filter.getText().toString().toLowerCase()))
					keys.add(key);
		}
		
		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				AddTab(data.getBackgrounds().get(key));
			}));
			sidePane.add(lbl);
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	public void AddTab(Background b) {
		BackgroundPane back = new BackgroundPane(b, data, gd);
		tabs.addTab(b.name, back);
		tabs.setSelectedComponent(back);
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.BACKGROUNDS)
			FillSidePane();
	}

	@Override
	public JTabbedPane GetTabs() {
		return tabs;
	}
}