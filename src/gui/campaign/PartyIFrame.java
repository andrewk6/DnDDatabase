package gui.campaign;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.campaign.Player;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.PlayerNameTargets;
import gui.gui_helpers.structures.ContentFrame;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class PartyIFrame extends JInternalFrame implements ContentFrame{
	private final DataContainer data;
	private final GuiDirector gd;
	private JTabbedPane tabs;
	
	
	
	public PartyIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		this.setSize(800, 800);
		this.setIconifiable(true);
		this.setClosable(true);
		this.setResizable(false);
		BuildPartyContent(getContentPane());
		addInternalFrameListener(GuiDirector.getContentFrameListener(gd, this));
		StyleContainer.SetIcon(this, StyleContainer.PARTY_ICON_FILE);
	}
	
	private void BuildPartyContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		JPanel addPane = new JPanel();
		addPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		cPane.add(addPane, BorderLayout.NORTH);
		
		JButton addPlayer = CompFactory.createNewButton("Add Player", e->{
			String name = JOptionPane.showInputDialog("Player's Name?");
			Player p = new Player(name);
			data.AddPlayer(p);
			AddPlayerTab(p);
		});
		addPane.add(addPlayer);
		FillParty();
		
	}
	
	private void FillParty() {
		Map<String, Player> party = data.getParty();
		for(String s : party.keySet()) {
			Player p = party.get(s);
			AddPlayerTab(p);
		}
	}
	
	private void AddPlayerTab(Player p) {
		SwingUtilities.invokeLater(()->{
			JPanel contPane = new JPanel();
			contPane.setLayout(new BorderLayout());
			
			
			PlayerPane pPane = new PlayerPane(p, data, gd);
			contPane.add(pPane, BorderLayout.CENTER);
			
			JPanel btnPane = new JPanel();
			btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			contPane.add(btnPane, BorderLayout.SOUTH);
			
			JButton delPBtn = new JButton("Delete: " + p.name);
			delPBtn.addActionListener(e ->{
				removeTabByTitle(p.name);
				gd.deregisterPlayerPaneTabs(pPane);
				data.DeletePlayer(p);
			});
			StyleContainer.SetFontBtn(delPBtn);
			btnPane.add(delPBtn);
			gd.regsiterPlayerPaneTabs(pPane, new PlayerNameTargets(tabs, delPBtn));
			tabs.addTab(p.name, contPane);
		});		
	}
	
	public void setVisible(boolean vis) {
		super.setVisible(vis);
	}
	
	public int GetTabIndex(String name) {
		for(int i = 0; i <tabs.getTabCount(); i ++) {
			if(name.equals(tabs.getTitleAt(i)))
				return i;
		}
		return -1;
	}
	
	public void removeTabByTitle(String title) {
	    int in = GetTabIndex(title);
	    if(in != -1) {
	    	tabs.removeTabAt(in);
	    }
	}

	@Override
	public void handleLink(String obj) {
		tabs.setSelectedIndex(GetTabIndex(obj));
	}
}