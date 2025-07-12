package gui.item_panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import data.items.MagicItem;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class MagicItemPanel extends JPanel{
	private GuiDirector gd;
	private DataContainer data;
	private JDesktopPane dPane;
	private HoverTextPane hPane;
	private JTextField miTitle;
	private JTextField miType;
	private JPanel miGridPane, cardPane;
	private JTextField miFilter;
	
//	private JTabbedPane miTab;
	private JPanel miPane;
	private final int subTypeSizeAdjust = 4;

	public MagicItemPanel(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		gd = guiD;
		
		setLayout(new BorderLayout());
		BuildContent();
		BuildSidePane();
	}

	public void BuildSidePane(){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		miFilter = new JTextField();
		miFilter.setToolTipText("Enter a spell filter");
		miFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(miFilter);
		sPane.add(miFilter, BorderLayout.NORTH);
		
		miGridPane = new JPanel();
		miGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane spellGridScroll = new JScrollPane(miGridPane);
		spellGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		sPane.add(spellGridScroll, BorderLayout.CENTER);
		add(sPane, BorderLayout.WEST);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			miGridPane.removeAll();
			List<String> keys = data.getMagicItemKeysSorted();
			for(String s : keys) {
				if(s.toLowerCase().startsWith(miFilter.getText().toLowerCase()) || miFilter.getText().length() == 0) {
					JTextField sField = new JTextField(s);
					StyleContainer.SetFontHeader(sField);
					sField.setEditable(false);
					sField.setFocusable(false);
					sField.setColumns(25);
					sField.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent e) {
//							spellTitle.setText(s);
//							hPane.setDocument(data.getSpells().get(s).spellDoc);
//							AddSpellTab(s);
							CardLayout cl = (CardLayout) cardPane.getLayout();
							cl.show(cardPane, "mipane");
							SetMIPane((MagicItem) data.getItems().get(s));
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						
					});
					miGridPane.add(sField);
				}
			}
			miGridPane.revalidate();
			miGridPane.repaint();
		});
	}
	
//	public void CheckTabs() {
//		if(miTab.getTabCount() == 1) {
//			CardLayout cl = (CardLayout) cardPane.getLayout();
//			cl.show(cardPane, "spelltabs");
//		}else if(miTab.getTabCount() <= 0) {
//			CardLayout cl = (CardLayout) cardPane.getLayout();
//			cl.show(cardPane, "noload");
//		}
//	}
	
//	public void AddSpellTab(String key) {
//		JPanel sPane = new JPanel();
//		sPane.setLayout(new BorderLayout());
//		miTab.addTab(key, sPane);
//		
//		JTextField spellTitle = new JTextField(key);
//		spellTitle.setEditable(false);
//		spellTitle.setFocusable(false);
//		spellTitle.setHorizontalAlignment(JTextField.CENTER);
//		StyleContainer.SetFontHeader(spellTitle);
//		sPane.add(spellTitle, BorderLayout.NORTH);
//		
//		HoverTextPane spellDesc = new HoverTextPane(data, gd, dPane);
//		spellDesc.SetSpellTabbedPane(this);
//		spellDesc.setDocument(data.getSpells().get(key).spellDoc);
//		JScrollPane spellScroll = new JScrollPane(spellDesc);
//		spellScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		sPane.add(spellScroll, BorderLayout.CENTER);
//		
//		JPanel btnFlow = new JPanel();
//		btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		sPane.add(btnFlow, BorderLayout.SOUTH);
//		
//		JButton removeBtn = new JButton("Remove " + key);
//		removeBtn.addActionListener(e ->{
//			int index = miTab.indexOfComponent(sPane);
//		    if (index != -1) {
//		    	miTab.removeTabAt(index);
//		    }
//			CheckTabs();
//		});
//		btnFlow.add(removeBtn);
//		CheckTabs();
//		miTab.setSelectedComponent(sPane);
//	}

	
	private void BuildContent() {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		add(cardPane, BorderLayout.CENTER);
		
		JLabel noLoad = new JLabel("No Magic Item Selected");
		StyleContainer.SetFontHeader(noLoad);
		cardPane.add(noLoad, "noload");
		
//		miTab = new JTabbedPane();
//		cardPane.add(miTab, "spelltabs");
		
		miPane = new JPanel();
		miPane.setLayout(new BorderLayout());
		cardPane.add(miPane, "mipane");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "noload");
	}
	
	private void SetMIPane(MagicItem i) {
		SwingUtilities.invokeLater(()->{
			miPane.removeAll();
			System.out.println("Adding Panel: " + i.name);
			JPanel miTPane = new JPanel();
			miTPane.setLayout(new GridLayout(0,1));
			miPane.add(miTPane, BorderLayout.NORTH);
			
			miTitle = new JTextField(i.name);
			miTitle.setEditable(false);
			miTitle.setFocusable(false);
			StyleContainer.SetFontHeader(miTitle);
			miTPane.add(miTitle);
			
			miType = new JTextField(i.getTypeString());
			miType.setEditable(false);
			miType.setFocusable(false);
			miType.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(Font.ITALIC).
					deriveFont(StyleContainer.FNT_HEADER_BOLD.getSize() - subTypeSizeAdjust ));
			miTPane.add(miType);
			
			hPane = new HoverTextPane(data, gd, dPane);
			hPane.setDocument(i.desc);
			JScrollPane hScroll = new JScrollPane(hPane);
			miPane.add(hScroll, BorderLayout.CENTER);
			
			revalidate();
			repaint();
		});		
	}
	
	public void LoadItem(String i) {
		SetMIPane((MagicItem) data.getItems().get(i));
		SwingUtilities.invokeLater(() -> {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "mipane");
		});
				
	}
	
//	public JTabbedPane GetTabs() {
//		return miTab;
//	}

//	private void BuildContent(Container cPane) {
//		hPane = new HoverTextPane(data, dPane);
//		JScrollPane spellScroller = new JScrollPane(hPane);
//		spellScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		cPane.add(spellScroller, BorderLayout.CENTER);
//		
//		spellTitle = new JTextField("NO SPELL SELECTED");
//		spellTitle.setToolTipText("Name of the spell");
//		spellTitle.setEditable(false);
//		spellTitle.setFocusable(false);
//		spellTitle.setHorizontalAlignment(JTextField.CENTER);
//		StyleContainer.SetFontHeader(spellTitle);
//		cPane.add(spellTitle, BorderLayout.NORTH);
//	}
}