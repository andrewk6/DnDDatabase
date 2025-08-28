package builders.class_builder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import data.DataChangeListener;
import data.DataContainer;
import data.players.classes.ClassAbility;
import data.players.classes.Subclass;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

public class SubclassPaneBuilder extends JPanel
{
	private DataContainer data;
	private HashMap<String, Subclass> subMap;
	
	private JPanel sidePane, mPane;
	private JTabbedPane subTabs;
	
	public SubclassPaneBuilder(DataContainer data, HashMap<String, Subclass> subMap) {
		this.data = data;
		this.subMap = subMap;
		
		this.setLayout(new BorderLayout());
		init();
	}
	
	private void init() {
		mPane = new JPanel();
		mPane.setLayout(new CardLayout());
		this.add(mPane, BorderLayout.CENTER);
		
		if(subMap.keySet().size() > 0)
			for(String key : subMap.keySet())
				mPane.add(new AbilityPaneBuilder(data, subMap.get(key).abilities), key);
		
		JTextArea nonLoad = new JTextArea("No subclass loaded, "
				+ "please click a subclass. If none on left, click add subclass button.");
		nonLoad.setEditable(false);
		nonLoad.setLineWrap(true);
		nonLoad.setWrapStyleWord(true);
		nonLoad.setFocusable(false);
		nonLoad.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(60f));
		mPane.add(nonLoad, "NoLoad");
		
		CardLayout cl = (CardLayout) mPane.getLayout();
		cl.show(mPane, "NoLoad");
		
		subTabs = new JTabbedPane();
		mPane.add(subTabs, "SubTabs");
		
		if(subMap.keySet().size() > 0) {
			BuildTabPane();
			cl.show(mPane, "SubTabs");
		}
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
//		FillSidePane();
//		JScrollPane sideScroll = new JScrollPane(sidePane);
		//this.add(sideScroll, BorderLayout.WEST);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(buttonPane, BorderLayout.SOUTH);
		
		JButton addBtn = CompFactory.createNewButton("Add Subclass", _->{
			String subclass = JOptionPane.showInputDialog(this, "What is the subclasses name: ");
			if(subclass != null) {
				BuildSubPane(subclass);
				cl.show(mPane, "SubTabs");
			}
		});
		buttonPane.add(addBtn);
	}
	
	private void BuildTabPane() {
		for(String subclass : subMap.keySet()) {
			BuildSubPane(subclass);
		}
	}
	
	private void BuildSubPane(String subclass) {
		JPanel subPane = new JPanel();
		subPane.setLayout(new BorderLayout());
		subTabs.addTab(subclass, subPane);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		subPane.add(hPane, BorderLayout.NORTH);
		
		JLabel subLbl = CompFactory.createNewLabel(subclass, ComponentType.HEADER);
		hPane.add(subLbl, BorderLayout.CENTER);
		
		JButton del = CompFactory.createNewButton("Delete", _->{
			int conf = JOptionPane.showConfirmDialog(this, "Delete: " + subclass, 
					"Delete Confirm", JOptionPane.YES_NO_OPTION);
			if(conf == JOptionPane.YES_OPTION) {
				subMap.remove(subclass);
				subTabs.removeTabAt(subTabs.indexOfComponent(subPane));
				
				if(subTabs.getTabCount() > 0)
					subTabs.setSelectedIndex(0);
				else {
					CardLayout cl = (CardLayout) mPane.getLayout();
					cl.show(mPane, "NoLoad");
				}
			}
		});
		hPane.add(del, BorderLayout.EAST);
		
		if(!subMap.keySet().contains(subclass)) {
			Subclass sub = new Subclass();
			sub.name = subclass;
			sub.abilities = new HashMap<String, ClassAbility>();
			this.subMap.put(subclass, sub);
		}
		
		AbilityPaneBuilder aPane = new AbilityPaneBuilder(data, subMap.get(subclass).abilities);
		subPane.add(aPane, BorderLayout.CENTER);
//		mPane.add(aPane, subclass);
		
//		FillSidePane();
	}
	
	private void FillSidePane() {
		if(sidePane.getComponents().length > 0)
			sidePane.removeAll(); 
		ArrayList<String> keys = new ArrayList<String>(subMap.keySet());
		Collections.sort(keys);
		
		for(String s : keys) {
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(p);
			
			JLabel lbl = CompFactory.createNewLabel(s, ComponentType.BODY);
			lbl.setFont(lbl.getFont().deriveFont(22f));
			lbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					((CardLayout)mPane.getLayout()).show(mPane, s);
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			
			p.add(lbl, BorderLayout.CENTER);
			
			JButton del = CompFactory.createNewButton("Delete", _->{
				int conf = JOptionPane.showConfirmDialog(this, "Delete: " + s, 
						"Delete Confirm", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					subMap.remove(s);
					FillSidePane();
				}
			});
			p.add(del, BorderLayout.EAST);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
}