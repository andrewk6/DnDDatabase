package gui.classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import data.DataContainer;
import data.players.classes.ClassAbility;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class AbilityPane extends JPanel
{
	private final DataContainer data;
	private final GuiDirector gd;
	
	private JPanel listPane, mainPane;
	private ReminderField filter;
	private JLabel nameLbl;
	private HoverTextPane hPane;
	
	private final HashMap<String, ClassAbility> aMap;
	
	public AbilityPane(DataContainer data, GuiDirector gd, HashMap<String, ClassAbility> abilityMap) {
		this.data = data;
		this.gd = gd;
		this.aMap = abilityMap;
		
		Init();
	}
	
	private void Init() {
		this.setLayout(new BorderLayout());
		
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		this.add(sidePane, BorderLayout.WEST);
		
		filter = CompFactory.createReminderField("Ability Filter", ComponentType.HEADER);
		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void removeUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		sidePane.add(filter, BorderLayout.NORTH);
		
		listPane = new JPanel();
		listPane.setLayout(new GridLayout(0, 1));
		JScrollPane listScroll = new JScrollPane(listPane);
		sidePane.add(listScroll, BorderLayout.CENTER);
		
		mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		this.add(mainPane, BorderLayout.CENTER);
		
		nameLbl = CompFactory.createNewLabel("", ComponentType.HEADER);
		mainPane.add(nameLbl, BorderLayout.NORTH);
		
		hPane = new HoverTextPane(data, gd, gd.getDesktop());
		JScrollPane hScroll = new JScrollPane(hPane);
		mainPane.add(hScroll, BorderLayout.CENTER);
		
		FillSidePane();
	}
	
	private void LoadDocument(ClassAbility ab) {
		nameLbl.setText(ab.name);
		hPane.setDocument(ab.desc);
		mainPane.revalidate();
		mainPane.repaint();
	}
	
	private void FillSidePane() {
		listPane.removeAll();
		
		ArrayList<ClassAbility> keys;
		if(filter.getText().length() > 0) {
			keys = new ArrayList<ClassAbility>();
			for(String s : aMap.keySet()) {
				if(s.toLowerCase().contains(filter.getText().toLowerCase()))
					keys.add(aMap.get(s));
			}
		}else {
			keys = new ArrayList<ClassAbility>(aMap.values());
		}
		Collections.sort(keys);
		
		for(ClassAbility ab : keys) {
			JLabel lbl = CompFactory.createNewLabel(ab.name, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {LoadDocument(ab);}
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
}