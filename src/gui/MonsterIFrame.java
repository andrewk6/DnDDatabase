package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import javax.swing.JCheckBox;

import data.DataContainer;
import data.Monster;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.MonsterDispPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class MonsterIFrame extends JInternalFrame implements ContentTab{
	private DataContainer data;
	private GuiDirector gd;
	private JDesktopPane dPane;
	private HoverTextPane hPane;
	private JTextField monstTitle;
	private JPanel monstGridPane, cardPane;
	private JTabbedPane mPane;
	private ReminderField monstFilterName, monstFilterTag;
	private JCheckBox allowCustom;

	public MonsterIFrame(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
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
				gd.NotifyFocus(MonsterIFrame.this);
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.DeRegister(MonsterIFrame.this);
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {
				gd.NotifyFocus(MonsterIFrame.this);
			}
		});
//		setVisible(true);
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		if(gd.getmFrame() == null)
			gd.NotifyFocus(this);
	}

	private void BuildFrame() {
		getContentPane().setLayout(new BorderLayout());
		setSize(800, 800);
		setTitle("Monster Database");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
//		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		
		try {
			
			BufferedImage iconImage = ImageIO.read(
				    getClass().getResource("/"+ StyleContainer.MONSTER_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.MONSTER_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.MONSTER_ICON_FILE));
			this.setFrameIcon(icon);
		}
	}
	
	public void BuildSidePane(Container cPane){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		BuildFilterPane(sPane);
		
		
		monstGridPane = new JPanel();
		monstGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane spellGridScroll = new JScrollPane(monstGridPane);
		spellGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		sPane.add(spellGridScroll, BorderLayout.CENTER);
		cPane.add(sPane, BorderLayout.WEST);
	}
	
	public void BuildFilterPane(JPanel sPane) {
		JTabbedPane fPane = new JTabbedPane();
		
		JPanel nFPane = new JPanel();
		nFPane.setLayout(new BorderLayout());
		monstFilterName = new ReminderField("", "Enter a monster name filter");
		monstFilterName.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(monstFilterName);
		nFPane.add(monstFilterName, BorderLayout.CENTER);
		fPane.addTab("Name Filter", nFPane);
		
		JPanel tFPane = new JPanel();
		tFPane.setLayout(new BorderLayout());
		monstFilterTag = new ReminderField("", "Enter a monster tag filter");
		monstFilterTag.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(monstFilterTag);
		tFPane.add(monstFilterTag, BorderLayout.CENTER);
		fPane.addTab("Tag Filter", tFPane);
		
		JPanel cFPane = new JPanel();
		allowCustom = new JCheckBox("Allow Custom Monsters");
		allowCustom.setSelected(false);
		allowCustom.addActionListener(e ->{
			FillSidePane();
		});
		cFPane.add(allowCustom);
		fPane.addTab("Config", cFPane);
		
		sPane.add(fPane, BorderLayout.NORTH);
		
		JButton clearFilter = new JButton("Clear Filter");
		StyleContainer.SetFontBtn(clearFilter);
		clearFilter.addActionListener(e ->{
			monstFilterName.setText("");
			monstFilterTag.setText("");
			FillSidePane();
		});
		sPane.add(clearFilter, BorderLayout.SOUTH);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			monstGridPane.removeAll();
			List<String> keys = data.getMonsterKeysSorted();
			String nameFilt = monstFilterName.getText().toLowerCase();
			String tagFilt = monstFilterTag.getText().toLowerCase();
			int filterLen = monstFilterName.getText().length() + monstFilterTag.getText().length();
			List<String> goodKeys;
			if(filterLen > 0) {
				goodKeys = new ArrayList<String>();
				ArrayList<String> nKeys, tKeys;
				nKeys = new ArrayList<String>();
				tKeys = new ArrayList<String>();
				if(nameFilt.length() > 0) {
					for(String s : keys) {
						if(s.toLowerCase().startsWith(nameFilt)) {
							nKeys.add(s);
						}
					}
				}
				
				if(tagFilt.length() > 0)
					tKeys.addAll(data.matchMonsterTag(tagFilt));
//				goodKeys = new ArrayList<>(new LinkedHashSet<>(goodKeys));
				if(nKeys.size() == 0)
					goodKeys.addAll(tKeys);
				else if(tKeys.size() == 0)
					goodKeys.addAll(nKeys);
				else {
					goodKeys = nKeys;
					goodKeys.retainAll(tKeys);
				}
				Collections.sort(goodKeys);
				
				if(!allowCustom.isSelected())
					customFilter(goodKeys);
			}else {
				goodKeys = new ArrayList<String>(keys);
				if(!allowCustom.isSelected())
				customFilter(goodKeys);
			}
			for(String s : goodKeys) {
//				if(s.toLowerCase().startsWith(nameFilt) || monstFilterName.getText().length() == 0) {
					JTextField sField = new JTextField(s);
					StyleContainer.SetFontHeader(sField);
					sField.setEditable(false);
					sField.setFocusable(false);
					sField.setColumns(25);
					sField.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent e) {
							if(gd.getComboFrame() != null)
								gd.getComboFrame().AddTab(data.getMonsters().get(s));
							else
								AddMonsterPane(s);
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						
					});
					monstGridPane.add(sField);
//				}
			}
			monstGridPane.revalidate();
			monstGridPane.repaint();
		});
	}
	
	private void customFilter(List<String> goodKeys) {
		if(goodKeys.size() > 0) {
			Iterator<String> iter = goodKeys.iterator();
		    while (iter.hasNext()) {
		        String k = iter.next();
		        if (data.getMonsters().get(k).custom) {
		            iter.remove();
		        }
		    }
		}

	}

	
//	private void SetMonsterPane(String key) {
//		SwingUtilities.invokeLater(()->{
//			mPane.removeAll();
//			mPane.add(new MonsterDispPane(data, key, dPane));
//			
//			mPane.repaint();
//			mPane.revalidate();
//		});		
//	}

	private void BuildContent(Container cPane) {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		cPane.add(cardPane, BorderLayout.CENTER);
		
		mPane = new JTabbedPane();
		cardPane.add(mPane, "mtabs");
		
		JLabel noContent = new JLabel("No Monster Loaded");
		StyleContainer.SetFontHeader(noContent);
		cardPane.add(noContent, "nomlabel");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "nomlabel");
	}
	
	public void AddMonsterPane(String key) {
		SwingUtilities.invokeLater(()->{
			JPanel monstDisp = new JPanel();
			monstDisp.setLayout(new BorderLayout());
			MonsterDispPane monstPane = new MonsterDispPane(data, gd, key, dPane);
			monstPane.SetMonstIFrame(this);
			monstDisp.add(monstPane, BorderLayout.CENTER);
			
			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			monstDisp.add(btnFlow, BorderLayout.SOUTH);
			
			JButton removeMonst = new JButton("Remove Monster");
			StyleContainer.SetFontBtn(removeMonst);
			removeMonst.addActionListener(e ->{
				int index = mPane.indexOfComponent(monstDisp);
			    if (index != -1) {
			        mPane.removeTabAt(index);
			    }
			    
			   CheckTabs();
			});
			btnFlow.add(removeMonst);
			mPane.addTab(key, monstDisp);
			mPane.setSelectedComponent(monstDisp);
			
			CheckTabs();
		});	
	}
	
	public void CheckTabs() {
		if(mPane.getTabCount() == 1) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "mtabs");
		}else if(mPane.getTabCount() <= 0) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "nomlabel");
		}
	}
	
	public JTabbedPane GetTabs() {
		return mPane;
	}
}