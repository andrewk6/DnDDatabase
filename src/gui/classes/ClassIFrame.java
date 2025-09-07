package gui.classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import data.players.classes.ClassAbility;
import data.players.classes.DnDClass;
import gui.FeatIFrame;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class ClassIFrame extends JInternalFrame implements ContentTab, DataChangeListener
{
	private DataContainer data;
	private GuiDirector gd;
	
	private JTabbedPane tabs;
	private JPanel listPane;
	private ReminderField filter;
	
	public ClassIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.data.registerListener(this);
		this.gd = gd;
		toFront();
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				gd.NotifyFocus(ClassIFrame.this);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegister(ClassIFrame.this);
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.NotifyFocus(ClassIFrame.this);
			}
		});
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		if(gd.getClFrame() == null)
			gd.NotifyFocus(this);
		
		StyleContainer.ConfigIFrame(this, "Feats Database");
		StyleContainer.SetIcon(this, StyleContainer.CLASS_ICON_FILE);
		
		
		this.getContentPane().setLayout(new BorderLayout());
		Init(this.getContentPane());
	}
	
	private void Init(Container cPane) {
		tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		cPane.add(sidePane, BorderLayout.WEST);
		
		filter = CompFactory.createReminderField("Class filter", ComponentType.HEADER);
		filter.setColumns(15);
		sidePane.add(filter, BorderLayout.NORTH);
		
		listPane = new JPanel();
		listPane.setLayout(new GridLayout(0,1));
		sidePane.add(listPane, BorderLayout.CENTER);
		FillSidePane();
	}
	
	public void AddTab(DnDClass classData) {
		JPanel tab = new JPanel();
		tab.setLayout(new BorderLayout());
		tabs.addTab(classData.name, tab);
		
		tab.add(new ClassPane(data, classData, gd), BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tab.add(btnPane, BorderLayout.SOUTH);
		
		JButton closeTab = CompFactory.createNewButton("Close " + classData.name, _->{
			int index = tabs.indexOfComponent(tab);
			if(index >= 0)
				tabs.removeTabAt(index);
			else
				System.out.println("Issue, Index still -1");
		});
		btnPane.add(closeTab);
	}
	
	private void FillSidePane() {
		listPane.removeAll();
		
		ArrayList<String> keys;
		if(filter.getText().length() > 0) {
			keys = new ArrayList<String>();
			for(String s : data.getClassKeysSorted()) {
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					keys.add(s);
			}
		}else {
			keys = new ArrayList<String>(data.getClassKeysSorted());
		}
		Collections.sort(keys);
		
		for(String key : keys) {
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {AddTab(data.getClasses().get(key));}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			listPane.add(lbl);
		}
		listPane.revalidate();
		listPane.repaint();
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
		if(mapType == MapType.CLASSES)
			FillSidePane();
	}
	
}