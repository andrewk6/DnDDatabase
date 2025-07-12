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
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class SpellIFrame extends JInternalFrame implements ContentTab{
	private GuiDirector gd;
	private DataContainer data;
	private JDesktopPane dPane;
	private HoverTextPane hPane;
	private JTextField spellTitle;
	private JPanel spellGridPane, cardPane;
	private JTextField spellFilter;
	
	private JTabbedPane spellTab;

	public SpellIFrame(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		gd = guiD;

		BuildFrame();
		BuildContent(getContentPane());
		BuildSidePane(getContentPane());
		
		toFront();
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				gd.NotifyFocus(SpellIFrame.this);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegister(SpellIFrame.this);
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.NotifyFocus(SpellIFrame.this);
			}
		});
//		setVisible(true);
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		if(gd.getsFrame() == null)
			gd.NotifyFocus(this);
	}

	private void BuildFrame() {
		getContentPane().setLayout(new BorderLayout());
		setSize(800, 800);
		setTitle("Spell Database");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
//		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		
		try {
			
			BufferedImage iconImage = ImageIO.read(
				    getClass().getResource("/"+ StyleContainer.SPELL_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.SPELL_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.SPELL_ICON_FILE));
			this.setFrameIcon(icon);
		}
	}
	
	public void BuildSidePane(Container cPane){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		spellFilter = new JTextField();
		spellFilter.setToolTipText("Enter a spell filter");
		spellFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(spellFilter);
		sPane.add(spellFilter, BorderLayout.NORTH);
		
		spellGridPane = new JPanel();
		spellGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane spellGridScroll = new JScrollPane(spellGridPane);
		spellGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		sPane.add(spellGridScroll, BorderLayout.CENTER);
		cPane.add(sPane, BorderLayout.WEST);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			spellGridPane.removeAll();
			List<String> keys = data.getSpellKeysSorted();
			for(String s : keys) {
				if(s.toLowerCase().startsWith(spellFilter.getText().toLowerCase()) || spellFilter.getText().length() == 0) {
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
								gd.getComboFrame().AddTab(data.getSpells().get(s));
							}else {
								AddSpellTab(s);
							}
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						
					});
					spellGridPane.add(sField);
				}
			}
			spellGridPane.revalidate();
			spellGridPane.repaint();
		});
	}
	
	public void CheckTabs() {
		if(spellTab.getTabCount() == 1) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "spelltabs");
		}else if(spellTab.getTabCount() <= 0) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "noload");
		}
	}
	
	public void AddSpellTab(String key) {
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		spellTab.addTab(key, sPane);
		
		JTextField spellTitle = new JTextField(key);
		spellTitle.setEditable(false);
		spellTitle.setFocusable(false);
		spellTitle.setHorizontalAlignment(JTextField.CENTER);
		StyleContainer.SetFontHeader(spellTitle);
		sPane.add(spellTitle, BorderLayout.NORTH);
		
		HoverTextPane spellDesc = new HoverTextPane(data, gd, dPane);
		spellDesc.SetSpellTabbedPane(this);
		spellDesc.setDocument(data.getSpells().get(key).spellDoc);
		JScrollPane spellScroll = new JScrollPane(spellDesc);
		spellScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sPane.add(spellScroll, BorderLayout.CENTER);
		
		JPanel btnFlow = new JPanel();
		btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
		sPane.add(btnFlow, BorderLayout.SOUTH);
		
		JButton removeBtn = new JButton("Remove " + key);
		removeBtn.addActionListener(e ->{
			int index = spellTab.indexOfComponent(sPane);
		    if (index != -1) {
		    	spellTab.removeTabAt(index);
		    }
			CheckTabs();
		});
		btnFlow.add(removeBtn);
		CheckTabs();
		spellTab.setSelectedComponent(sPane);
	}
	
	private void BuildContent(Container cPane) {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		cPane.add(cardPane, BorderLayout.CENTER);
		
		JLabel noLoad = new JLabel("Np Spells Selected");
		StyleContainer.SetFontHeader(noLoad);
		cardPane.add(noLoad, "noload");
		
		spellTab = new JTabbedPane();
		cardPane.add(spellTab, "spelltabs");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "noload");
	}
	
	public JTabbedPane GetTabs() {
		return spellTab;
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