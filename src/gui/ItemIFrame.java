package gui;

import java.awt.BorderLayout;
import java.awt.Component;
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

import data.DataContainer;
import data.DataContainer.MapType;
import data.interfaces.DataChangeListener;
import gui.gui_helpers.structures.ContentFrame;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import gui.item_panels.ArmorPanel;
import gui.item_panels.GearPanel;
import gui.item_panels.MagicItemPanel;
import gui.item_panels.ToolsPanel;
import gui.item_panels.VehiclesPane;
import gui.item_panels.WeaponPanel;
import gui.siege_equipment.SiegeEquipmentPane;

public class ItemIFrame extends JInternalFrame implements ContentFrame, DataChangeListener {
	public static final int WEAPON_PANE = 0;
	public static final int ARMOR_PANE = 1;
	public static final int TOOL_PANE = 2;
	public static final int GEAR_PANE = 3;
	public static final int VEHICLE_PANE = 4;
	public static final int MAGIC_ITEM_PANE = 5;
	public static final int SIEGE_PANE = 6;
	
	private DataContainer data;
	private JDesktopPane dPane;
	private GuiDirector gd;

	private JTabbedPane tabs;
	private MagicItemPanel miPane;
	private final SiegeEquipmentPane sePane;

	public ItemIFrame(DataContainer data, GuiDirector gd, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		this.gd = gd;
		
		sePane = new SiegeEquipmentPane(data, gd);
		
		data.registerListener(this);

		BuildFrame();
		BuildWeaponPane();
		BuildArmorPane();
		BuildToolPane();
		BuildGearPane();
		BuildVehiclePane();
		BuildMagicItemPane();
		tabs.addTab("Siege Equipment",sePane);

//		setVisible(true);
		StyleContainer.SetIcon(this, StyleContainer.ITEM_ICON_FILE);
		gd.RegisterFrame(this);
	}
	
	
	private void BuildMagicItemPane() {
		miPane = new MagicItemPanel(data, gd, dPane);
		tabs.addTab("Magic Items", miPane);
	}
	
	private void BuildVehiclePane() {
		VehiclesPane vPane = new VehiclesPane(data, gd);
		tabs.addTab("Vehicles", vPane);
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
//		addInternalFrameListener(GuiDirector.getContentFrameListener(gd, this));
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

	}

	@Override
	public void handleLink(String obj) {
		if(data.getWeaponKeysSorted().contains(obj)) {
			tabs.setSelectedIndex(WEAPON_PANE);
		}else if(data.getArmorKeysSorted().contains(obj)) {
			tabs.setSelectedIndex(ARMOR_PANE);
		}else if(data.getToolKeysSorted().contains(obj)) {
			tabs.setSelectedIndex(TOOL_PANE);
			Component comp = tabs.getSelectedComponent();
	        if (comp instanceof ToolsPanel tPane) {
	            // Schedule the scroll after all layout passes
	            SwingUtilities.invokeLater(() -> {
	            	tPane.contentPanel.revalidate(); // ensure layout is computed
	            	tPane.contentPanel.repaint();    // optional
	                SwingUtilities.invokeLater(() -> tPane.scrollToTool(obj));
	            });
	        }
		}else  if (data.getGearKeysSorted().contains(obj)) {
	        tabs.setSelectedIndex(GEAR_PANE);
	        Component comp = tabs.getSelectedComponent();
	        if (comp instanceof GearPanel gPane) {
	            // Schedule the scroll after all layout passes
	            SwingUtilities.invokeLater(() -> {
	                gPane.contentPanel.revalidate(); // ensure layout is computed
	                gPane.contentPanel.repaint();    // optional
	                SwingUtilities.invokeLater(() -> gPane.scrollToGear(obj));
	            });
	        }
		}else if(data.getMountKeysSorted().contains(obj)) {
			tabs.setSelectedIndex(VEHICLE_PANE);
		}else if(data.getLargeVehicleKeysSorted().contains(obj)) {
			tabs.setSelectedIndex(VEHICLE_PANE);
		}else if(data.getMagicItemKeysSorted().contains(obj)) {
			tabs.setSelectedComponent(miPane);
			miPane.LoadItem(obj);	
		}else if(data.getSiegeEquipmentKeysSorted().contains(obj)) {
			sePane.AddTab(data.getSiegeEquipment().get(obj));
			tabs.setSelectedComponent(sePane);
		}
				
	}
	
	public void handleLink(int pane) {
		tabs.setSelectedIndex(pane);
	}

	@Override
	public void onMapUpdated() {
		int tab = tabs.getSelectedIndex();
		MagicItemPanel miPane = null;
		SiegeEquipmentPane sePane = null;
		for (int i = tabs.getTabCount() - 1; i >= 0; i--) {
		    if(tabs.getComponentAt(i) instanceof MagicItemPanel) {
		    	miPane = (MagicItemPanel) tabs.getComponentAt(i);
		    }else if(tabs.getComponentAt(i) instanceof SiegeEquipmentPane pane) {
		    	sePane = pane;
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
		BuildVehiclePane();
		tabs.addTab("Magic Items", miPane);
		tabs.addTab("Siege Equipment", sePane);
		tabs.setSelectedIndex(tab);
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.ITEMS) {
			onMapUpdated();
		}
	}
}