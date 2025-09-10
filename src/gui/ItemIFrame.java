package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import gui.gui_helpers.structures.ContentFrame;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import gui.item_panels.ArmorPanel;
import gui.item_panels.GearPanel;
import gui.item_panels.MagicItemPanel;
import gui.item_panels.ToolsPanel;
import gui.item_panels.WeaponPanel;

public class ItemIFrame extends JInternalFrame implements ContentFrame, DataChangeListener {
	private DataContainer data;
	private JDesktopPane dPane;
	private GuiDirector gd;

	private JTabbedPane tabs;
	private MagicItemPanel miPane;

	public ItemIFrame(DataContainer data, GuiDirector gd, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		this.gd = gd;
		
		data.registerListener(this);

		BuildFrame();
		BuildWeaponPane();
		BuildArmorPane();
		BuildToolPane();
		BuildGearPane();
		BuildMagicItemPane();

//		setVisible(true);
		StyleContainer.SetIcon(this, StyleContainer.ITEM_ICON_FILE);
	}
	
	private void BuildMagicItemPane() {
		miPane = new MagicItemPanel(data, gd, dPane);
		tabs.addTab("Magic Items", miPane);
	}
	
	private void BuildGearPane() {
		GearPanel gPane = new GearPanel(data);
		tabs.addTab("Gear", gPane);
	}
	
	private void BuildToolPane() {
		ToolsPanel tPane = new ToolsPanel(data);
		tabs.addTab("Tools", tPane);
	}
	
	private void BuildArmorPane() {
		ArmorPanel aPane = new ArmorPanel(data);
		tabs.addTab("Armor", aPane);
	}

	private void BuildWeaponPane() {
		System.out.println("Building weapons");
		WeaponPanel wPane = new WeaponPanel(data);
		System.out.println("Weapons pane made");
		tabs.addTab("Weapons", wPane);

	}

	private void BuildFrame() {
		tabs = new JTabbedPane();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);
//		setContentPane(tabs);
		setSize(800, 800);
		setTitle("Items Database");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
//		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {}
		});
		addInternalFrameListener(GuiDirector.getContentFrameListener(gd, this));
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

	}

	@Override
	public void handleLink(String obj) {
		tabs.setSelectedComponent(miPane);
		miPane.LoadItem(obj);
	}

	@Override
	public void onMapUpdated() {
		MagicItemPanel miPane = null;
		System.out.println("Itme I Frame in Map");
		for (int i = tabs.getTabCount() - 1; i >= 0; i--) {
		    if(tabs.getComponentAt(i) instanceof MagicItemPanel) {
		    	System.out.println("Found mi pane");
		    	miPane = (MagicItemPanel) tabs.getComponentAt(i);
		    }
		    tabs.removeTabAt(i);
		}
		if(miPane == null) {
			miPane = new MagicItemPanel(data, gd, dPane);
		}
		
//		BuildFrame();
		BuildWeaponPane();
		BuildArmorPane();
		BuildToolPane();
		BuildGearPane();
		tabs.addTab("Magic Items", miPane);
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.ITEMS) {
			MagicItemPanel miPane = null;
			System.out.println("Itme I Frame in Map");
			for (int i = tabs.getTabCount() - 1; i >= 0; i--) {
			    if(tabs.getComponentAt(i) instanceof MagicItemPanel) {
			    	System.out.println("Found mi pane");
			    	miPane = (MagicItemPanel) tabs.getComponentAt(i);
			    }
			    tabs.removeTabAt(i);
			}
			if(miPane == null) {
				miPane = new MagicItemPanel(data, gd, dPane);
			}
			
//			BuildFrame();
			BuildWeaponPane();
			BuildArmorPane();
			BuildToolPane();
			BuildGearPane();
			tabs.addTab("Magic Items", miPane);
		}
	}
}