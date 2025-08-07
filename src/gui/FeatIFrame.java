package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataChangeListener;
import data.DataContainer;
import data.Feat;
import data.Feat.FeatType;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class FeatIFrame extends JInternalFrame implements ContentTab, DataChangeListener{
	private GuiDirector gd;
	private DataContainer data;
	private HoverTextPane hPane;
	private JTextField featTitle;
	private JPanel featGridPane, cardPane;
	
	private JTextField featFilter;
	private JComboBox<String> featTypeFilt;
	
	private JTabbedPane featTab;

	public FeatIFrame(DataContainer data, GuiDirector guiD) {
		this.data = data;
		gd = guiD;

		data.registerListener(this);
		BuildFrame();
		BuildContent(getContentPane());
		BuildSidePane(getContentPane());
		
		toFront();
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				gd.NotifyFocus(FeatIFrame.this);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegister(FeatIFrame.this);
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.NotifyFocus(FeatIFrame.this);
			}
		});
//		setVisible(true);
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		if(gd.getsFrame() == null)
			gd.NotifyFocus(this);
	}

	private void BuildFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Feats Database");
//		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		
		//TODO: Add in an icon for the feat frames
//		try {
//			
//			BufferedImage iconImage = ImageIO.read(
//				    getClass().getResource("/"+ StyleContainer.SPELL_ICON_FILE));
//			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
//			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.SPELL_ICON_FILE));
//			this.setFrameIcon(icon);
//		} catch (IOException e) {
//			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.SPELL_ICON_FILE));
//			this.setFrameIcon(icon);
//		}
	}
	
	public void BuildSidePane(Container cPane){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		JTabbedPane filterTab = new JTabbedPane();
		sPane.add(filterTab, BorderLayout.NORTH);
		
		featFilter = new JTextField();
		featFilter.setToolTipText("Enter a feat filter");
		featFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(featFilter);
		filterTab.addTab("Name", featFilter);
		
		ArrayList<String> vals = new ArrayList<String>();
		vals.add("All");
		for(FeatType f : FeatType.values())
			vals.add(f.name());
		
		featTypeFilt = new JComboBox<String>(new Vector<String>(vals));
		featTypeFilt.addActionListener(_->{
			FillSidePane();
		});
		featTypeFilt.setFont(StyleContainer.FNT_BODY_PLAIN);
		filterTab.addTab("Type", featTypeFilt);
		
		featGridPane = new JPanel();
		featGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane spellGridScroll = new JScrollPane(featGridPane);
		spellGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		sPane.add(spellGridScroll, BorderLayout.CENTER);
		cPane.add(sPane, BorderLayout.WEST);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			featGridPane.removeAll();
			List<String> keys = new ArrayList<String>(data.getFeatKeysSorted());
			ArrayList<String> filter = new ArrayList<String>();
			if(!featTypeFilt.getSelectedItem().equals("All")) {
				for(String k : keys) {
					Feat f = data.getFeats().get(k);
					if(f.type != FeatType.valueOf((String) featTypeFilt.getSelectedItem()))
						filter.add(k);
				}
				keys.removeAll(filter);
			}
			for(String s : keys) {
				if((s.toLowerCase().contains(featFilter.getText().toLowerCase())) 
						|| featFilter.getText().length() == 0) {
					JTextField sField = new JTextField(s);
					StyleContainer.SetFontHeader(sField);
					sField.setEditable(false);
					sField.setFocusable(false);
					sField.setColumns(25);
					sField.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent e) {
//							spellTitle.setText(s);
//							hPane.setDocument(data.getSpells().get(s).spellDoc);
							if(gd.getComboFrame() != null) {
								gd.getComboFrame().AddTab(data.getFeats().get(s));
							}else {
								AddFeatTab(s);
							}
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						
					});
					featGridPane.add(sField);
				}
			}
			featGridPane.revalidate();
			featGridPane.repaint();
		});
	}
	
	public void CheckTabs() {
		if(featTab.getTabCount() == 1) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "feattabs");
		}else if(featTab.getTabCount() <= 0) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "noload");
		}
	}
	
	public void AddFeatTab(String key) {
		JPanel fPane = new JPanel();
		fPane.setLayout(new BorderLayout());
		featTab.addTab(key, fPane);
		
		JTextField featTitle = new JTextField(key);
		featTitle.setEditable(false);
		featTitle.setFocusable(false);
		featTitle.setHorizontalAlignment(JTextField.CENTER);
		StyleContainer.SetFontHeader(featTitle);
		fPane.add(featTitle, BorderLayout.NORTH);
		
		HoverTextPane featDesc = new HoverTextPane(data, gd, gd.getDesktop());
		featDesc.setDocument(data.getFeats().get(key).desc);
		JScrollPane featScroll = new JScrollPane(featDesc);
		featScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fPane.add(featScroll, BorderLayout.CENTER);
		
		JPanel btnFlow = new JPanel();
		btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fPane.add(btnFlow, BorderLayout.SOUTH);
		
		JButton removeBtn = new JButton("Remove " + key);
		removeBtn.addActionListener(e ->{
			int index = featTab.indexOfComponent(fPane);
		    if (index != -1) {
		    	featTab.removeTabAt(index);
		    }
			CheckTabs();
		});
		StyleContainer.SetFontBtn(removeBtn);
		btnFlow.add(removeBtn);
		CheckTabs();
		featTab.setSelectedComponent(fPane);
	}
	
	private void BuildContent(Container cPane) {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		cPane.add(cardPane, BorderLayout.CENTER);
		
		JLabel noLoad = new JLabel("No Feats Selected");
		StyleContainer.SetFontHeader(noLoad);
		cardPane.add(noLoad, "noload");
		
		featTab = new JTabbedPane();
		cardPane.add(featTab, "feattabs");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "noload");
	}
	
	public JTabbedPane GetTabs() {
		return featTab;
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
	}

	@Override
	public void onMapUpdated(int mapType) {
		FillSidePane();
	}

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