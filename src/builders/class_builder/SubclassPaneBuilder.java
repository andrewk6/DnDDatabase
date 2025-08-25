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
import javax.swing.JTextArea;

import data.DataChangeListener;
import data.DataContainer;
import data.players.classes.ClassAbility;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

public class SubclassPaneBuilder extends JPanel
{
	private DataContainer data;
	private HashMap<String, HashMap<String, ClassAbility>> subMap;
	
	private JPanel sidePane, mPane;
	
	public SubclassPaneBuilder(DataContainer data, HashMap<String, HashMap<String, ClassAbility>> subMap) {
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
				mPane.add(new AbilityPaneBuilder(data, subMap.get(key)), key);
		
		JTextArea nonLoad = new JTextArea("No subclass loaded, "
				+ "please click a subclass. If none on left, click add subclass button.");
		nonLoad.setLineWrap(true);
		nonLoad.setWrapStyleWord(true);
		nonLoad.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(60f));
		mPane.add(nonLoad, "NoLoad");
		
		CardLayout cl = (CardLayout) mPane.getLayout();
		cl.show(mPane, "NoLoad");
		

		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = new JScrollPane(sidePane);
		this.add(sideScroll, BorderLayout.WEST);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(buttonPane, BorderLayout.SOUTH);
		
		JButton addBtn = CompFactory.createNewButton("Add Subclass", _->{
			String subclass = JOptionPane.showInputDialog(this, "What is the subclasses name: ");
			if(subclass != null) {
				this.subMap.put(subclass, new HashMap<String, ClassAbility>());
				AbilityPaneBuilder aPane = new AbilityPaneBuilder(data, subMap.get(subclass));
				mPane.add(aPane, subclass);
				FillSidePane();
			}
		});
		buttonPane.add(addBtn);
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
				subMap.remove(s);
				FillSidePane();
			});
			p.add(del, BorderLayout.EAST);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
}