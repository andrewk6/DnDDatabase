package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import builders.item_builder.ArmorBuilder;
import builders.item_builder.GearBuilder;
import builders.item_builder.MagicItemBuilder;
import builders.item_builder.ToolSetBuilder;
import builders.item_builder.WeaponBuilderPanel;
import data.DataContainer;
import data.items.Item;
import gui.gui_helpers.structures.StyleContainer;

public class ItemBuilderIFrame extends JInternalFrame
{
	private final DataContainer data;
	private JTabbedPane tabs;
	
	private WeaponBuilderPanel wBuild;
	private ArmorBuilder aBuild;
	private ToolSetBuilder tBuild;
	private GearBuilder gBuild;
	private MagicItemBuilder miBuild;
	
	public ItemBuilderIFrame(DataContainer data) {
		this.data = data;
		
		ConfigFrame();
		BuildContentPane(this.getContentPane());
		SetContent();
		LoadItems();
		this.pack();
	}
	
	public void ConfigFrame() {
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setTitle("Item Builder");
		StyleContainer.SetIcon(this, StyleContainer.ITEM_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
	}
	
	public void BuildContentPane(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		JPanel savePane = new JPanel();
		savePane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(savePane, BorderLayout.SOUTH);
		
		
		JButton saveBtn = new JButton("Save");
		StyleContainer.SetFontBtn(saveBtn);
		saveBtn.addActionListener(e ->{
			Save();
		});
		savePane.add(saveBtn);
	}
	
	public void SetContent() {
		wBuild = new WeaponBuilderPanel(data);
		aBuild = new ArmorBuilder(data);
		tBuild = new ToolSetBuilder(data);
		gBuild = new GearBuilder(data);
		miBuild = new MagicItemBuilder(data);
		
		tabs.addTab("Weapons", wBuild);
		tabs.addTab("Armor", aBuild);
		tabs.addTab("Tools", tBuild);
		tabs.addTab("Gear", gBuild);
		tabs.addTab("Magic Items", miBuild);
	}
	
	public void LoadItems() {
		wBuild.LoadItems();
		aBuild.LoadItems();
		tBuild.LoadItems();
		gBuild.LoadItems();
		miBuild.LoadItems();
	}
	
	public boolean Save() {
		HashMap<String, Item> itemMap = new HashMap<String, Item>();
		itemMap.putAll(wBuild.getWeaponMap());
		itemMap.putAll(aBuild.getArmorMap());
		itemMap.putAll(tBuild.getToolSetMap());
		itemMap.putAll(gBuild.getGearMap());
		itemMap.putAll(miBuild.getMagicItemMap());
		
		data.SetItemMap(itemMap);
		data.SafeSaveData(DataContainer.ITEMS);
		return true;
	}
}