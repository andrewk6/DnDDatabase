package builders.item_builder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.items.Item;
import gui.gui_helpers.structures.StyleContainer;

public class ItemBuilder extends JFrame
{
	private final DataContainer data;
	private JTabbedPane tabs;
	
	private WeaponBuilderPanel wBuild;
	private ArmorBuilder aBuild;
	private ToolSetBuilder tBuild;
	private GearBuilder gBuild;
	private MagicItemBuilder miBuild;
	
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(()->{
			ItemBuilder builder = new ItemBuilder(new DataContainer());
			builder.setVisible(true);
		});
	}
	
	public ItemBuilder(DataContainer data) {
		this.data = data;
		
		ConfigFrame();
		BuildContentPane(this.getContentPane());
		SetContent();
		LoadItems();
		this.pack();
	}
	
	public void ConfigFrame() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				int opt = JOptionPane.showOptionDialog(
						ItemBuilder.this,
						"Would you like to Save before Exiting?",
						"Save Confirm",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						new String[] {"Save", "Don't Save"},
						0);
				if(opt == 0) {
					Save();
					data.Exit();
				}else {
					data.Exit();
				}
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
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