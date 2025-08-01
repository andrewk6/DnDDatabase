package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataChangeListener;
import data.DataContainer;
import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.MonsterDispPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.AllTab;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class ComboIFrame extends JInternalFrame implements AllTab, DataChangeListener{
	private static final long serialVersionUID = 1L;
	private ColorTabbedPaneUI tabsUI;
	private JTabbedPane tabs;
	private DataContainer data;
	private GuiDirector gd;
	private JDesktopPane dPane;

//	private HoverTextPane hPane;
//	private JTextField monstTitle;

	private JPanel gridPane, cardPane;
	private ReminderField filterName;

	public ComboIFrame(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		gd = guiD;

		this.data.registerListener(this);
		BuildFrame();
		BuildContent(getContentPane());
		BuildSidePane(getContentPane());

		toFront();
//		addInternalFrameListener(new InternalFrameListener() {
//			public void internalFrameOpened(InternalFrameEvent e) {
//				gd.RegisterCombo(ComboIFrame.this);
//			}
//
//			public void internalFrameIconified(InternalFrameEvent e) {
//				gd.DeRegisterCombo(ComboIFrame.this);
//			}
//
//			public void internalFrameDeiconified(InternalFrameEvent e) {
//				gd.RegisterCombo(ComboIFrame.this);
//			}
//
//			public void internalFrameDeactivated(InternalFrameEvent e) {}
//
//			public void internalFrameClosing(InternalFrameEvent e) {
//				gd.RegisterCombo(ComboIFrame.this);
//			}
//
//			public void internalFrameClosed(InternalFrameEvent e) {
//			}
//
//			public void internalFrameActivated(InternalFrameEvent e) {
//				gd.RegisterCombo(ComboIFrame.this);
//			}
//		});
		
		addInternalFrameListener(GuiDirector.getAllTabListener(guiD, this));
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
		});
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
//		setVisible(true);
	}

	private void BuildFrame() {
		getContentPane().setLayout(new BorderLayout());
		setSize(800, 800);
		setTitle("Full Database");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
//		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		try {
			BufferedImage iconImage = ImageIO.read(getClass().getResource("/" + StyleContainer.FULL_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.FULL_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.FULL_ICON_FILE));
			this.setFrameIcon(icon);
		}
	}

	public void BuildSidePane(Container cPane) {
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());

		BuildFilterPane(sPane);

		gridPane = new JPanel();
		gridPane.setLayout(new GridLayout(0, 1));
		FillSidePane();

		JScrollPane gridScroll = new JScrollPane(gridPane);
		gridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		sPane.add(gridScroll, BorderLayout.CENTER);
		cPane.add(sPane, BorderLayout.WEST);
	}

	public void BuildFilterPane(JPanel sPane) {
		JTabbedPane fPane = new JTabbedPane();

		JPanel nFPane = new JPanel();
		nFPane.setLayout(new BorderLayout());
		filterName = new ReminderField("", "Enter a monster name filter");
		filterName.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				FillSidePane();
			};

			public void insertUpdate(DocumentEvent e) {
				FillSidePane();
			}

			public void changedUpdate(DocumentEvent e) {
				FillSidePane();
			}
		});
		StyleContainer.SetFontHeader(filterName);
		nFPane.add(filterName, BorderLayout.CENTER);
		fPane.addTab("Name Filter", nFPane);

		sPane.add(fPane, BorderLayout.NORTH);

		JButton clearFilter = new JButton("Clear Filter");
		StyleContainer.SetFontBtn(clearFilter);
		clearFilter.addActionListener(e -> {
			filterName.setText("");
			FillSidePane();
		});
		sPane.add(clearFilter, BorderLayout.SOUTH);
	}

	public void FillSidePane() {
		SwingUtilities.invokeLater(() -> {
			gridPane.removeAll();

			Set<String> combinedKeysSet = new LinkedHashSet<>();
			combinedKeysSet.addAll(data.getRuleKeysSorted());
			combinedKeysSet.addAll(data.getSpellKeysSorted());
			combinedKeysSet.addAll(data.getMonsterKeysSorted());
			List<String> keys = new ArrayList<>(combinedKeysSet);
			Collections.sort(keys);

			String nameFilt = filterName.getText().toLowerCase();
			List<String> goodKeys;
			if (nameFilt.length() > 0) {
				goodKeys = new ArrayList<String>();
				ArrayList<String> nKeys;
				nKeys = new ArrayList<String>();
				if (nameFilt.length() > 0) {
					for (String s : keys) {
						if (s.toLowerCase().startsWith(nameFilt)) {
							nKeys.add(s);
						}
					}
				}
				goodKeys.addAll(nKeys);
			} else {
				goodKeys = new ArrayList<String>(keys);
			}

			for (String s : goodKeys) {
//				if(s.toLowerCase().startsWith(nameFilt) || monstFilterName.getText().length() == 0) {
				JTextField sField = new JTextField(s);
				StyleContainer.SetFontHeader(sField);
				sField.setEditable(false);
				sField.setFocusable(false);
				sField.setColumns(25);
				sField.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						AddTabDirector(s);

					}

					public void mousePressed(MouseEvent e) {
					}

					public void mouseReleased(MouseEvent e) {
					}

					public void mouseEntered(MouseEvent e) {
					}

					public void mouseExited(MouseEvent e) {
					}

				});
				gridPane.add(sField);
//				}
			}
			gridPane.revalidate();
			gridPane.repaint();
		});
	}

	public void AddTabDirector(String s) {
		if (data.getRuleKeysSorted().contains(s) && !data.getSpellKeysSorted().contains(s)
				&& !data.getMonsterKeysSorted().contains(s)) {
			AddTab(data.getRules().get(s));
		} else if (!data.getRuleKeysSorted().contains(s) && data.getSpellKeysSorted().contains(s)
				&& !data.getMonsterKeysSorted().contains(s)) {
			AddTab(data.getSpells().get(s));
		} else if (!data.getRuleKeysSorted().contains(s) && !data.getSpellKeysSorted().contains(s)
				&& data.getMonsterKeysSorted().contains(s)) {
			AddTab(data.getMonsters().get(s));
		} else {
			ArrayList<String> opts = new ArrayList<String>();
			if (data.getRuleKeysSorted().contains(s))
				opts.add("Rule");
			if (data.getSpellKeysSorted().contains(s))
				opts.add("Spell");
			if (data.getMonsterKeysSorted().contains(s))
				opts.add("Monster");

			if (opts.size() > 0) {
				String type = gd.showTypeSelectionDialog(SwingUtilities.getWindowAncestor(this), s, opts);
				if (type != null) {
					if (type.equals("Rule"))
						AddTab(data.getRules().get(s));
					else if (type.equals("Spell"))
						AddTab(data.getSpells().get(s));
					else if (type.equals("Monster"))
						AddTab(data.getMonsters().get(s));
				}
			}
		}
	}

	private void BuildContent(Container cPane) {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		cPane.add(cardPane, BorderLayout.CENTER);

		tabs = new JTabbedPane();
//		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabsUI = new ColorTabbedPaneUI();
		tabs.setUI(tabsUI);
		cardPane.add(tabs, "tabs");

		JLabel noContent = new JLabel("  No Content Loaded");
		StyleContainer.SetFontHeader(noContent);
		cardPane.add(noContent, "none");

		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "none");
	}

	public void AddTab(Monster m) {
		if(!hasTab(m.name))
		{
			JPanel monstDisp = new JPanel();
			monstDisp.setLayout(new BorderLayout());
			MonsterDispPane monstPane = new MonsterDispPane(data, gd, m.name, dPane);
			monstDisp.add(monstPane, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			monstDisp.add(btnFlow, BorderLayout.SOUTH);

			JButton removeMonst = new JButton("Remove Monster");
			StyleContainer.SetFontBtn(removeMonst);
			removeMonst.addActionListener(e -> {
				int index = tabs.indexOfComponent(monstDisp);
				if (index != -1) {
					tabs.removeTabAt(index);
				}

				CheckTabs();
			});
			btnFlow.add(removeMonst);
			tabs.addTab(m.name, monstDisp);
			tabsUI.setTabColor(tabs.indexOfTab(m.name), Color.ORANGE);
			tabs.setSelectedComponent(monstDisp);
			CheckTabs();
		}
	}

	public void AddTab(Rule r) {
		if(!hasTab(r.name)) {
			JPanel rPane = new JPanel();
			rPane.setLayout(new BorderLayout());
			tabs.addTab(r.name, rPane);
			tabsUI.setTabColor(tabs.indexOfTab(r.name), Color.CYAN);

			JTextField rTitle = new JTextField(r.name);
			rTitle.setEditable(false);
			rTitle.setFocusable(false);
			rTitle.setHorizontalAlignment(JTextField.CENTER);
			StyleContainer.SetFontHeader(rTitle);
			rPane.add(rTitle, BorderLayout.NORTH);

			HoverTextPane ruleDesc = new HoverTextPane(data, gd, dPane);
			ruleDesc.setDocument(data.getRules().get(r.name).ruleDoc);
			JScrollPane rScroll = new JScrollPane(ruleDesc);
			rScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			rPane.add(rScroll, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			rPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeRule = new JButton("Remove " + r.name);
			StyleContainer.SetFontBtn(removeRule);
			removeRule.addActionListener(e -> {
				int index = tabs.indexOfComponent(rPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
				CheckTabs();
			});
			btnFlow.add(removeRule);
			tabs.setSelectedComponent(rPane);
			CheckTabs();
		}
	}

	public void AddTab(Spell s) {
		if(!hasTab(s.name)) {
			JPanel sPane = new JPanel();
			sPane.setLayout(new BorderLayout());
			tabs.addTab(s.name, sPane);
			tabsUI.setTabColor(tabs.indexOfTab(s.name), Color.PINK);

			JTextField sTitle = new JTextField(s.name);
			sTitle.setEditable(false);
			sTitle.setFocusable(false);
			sTitle.setHorizontalAlignment(JTextField.CENTER);
			StyleContainer.SetFontHeader(sTitle);
			sPane.add(sTitle, BorderLayout.NORTH);

			HoverTextPane sDesc = new HoverTextPane(data, gd, dPane);
			sDesc.setDocument(data.getSpells().get(s.name).spellDoc);
			JScrollPane sScroll = new JScrollPane(sDesc);
			sScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			sPane.add(sScroll, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			sPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeSpell = new JButton("Remove " + s.name);
			StyleContainer.SetFontBtn(removeSpell);
			removeSpell.addActionListener(e -> {
				int index = tabs.indexOfComponent(sPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}

				CheckTabs();
			});
			btnFlow.add(removeSpell);
			tabs.setSelectedComponent(sPane);
			CheckTabs();
		}
	}
	
	@Override
	public void AddTab(Feat f) {
		if(!hasTab(f.name)) {
			JPanel fPane = new JPanel();
			fPane.setLayout(new BorderLayout());
			tabs.addTab(f.name, fPane);
			tabsUI.setTabColor(tabs.indexOfTab(f.name), Color.YELLOW);

			JTextField fTitle = new JTextField(f.name);
			fTitle.setEditable(false);
			fTitle.setFocusable(false);
			fTitle.setHorizontalAlignment(JTextField.CENTER);
			StyleContainer.SetFontHeader(fTitle);
			fPane.add(fTitle, BorderLayout.NORTH);

			HoverTextPane fDesc = new HoverTextPane(data, gd, gd.getDesktop());
			fDesc.setDocument(data.getFeats().get(f.name).desc);
			JScrollPane fScroll = new JScrollPane(fDesc);
			fScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			fPane.add(fScroll, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			fPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeFeat = new JButton("Remove " + f.name);
			StyleContainer.SetFontBtn(removeFeat);
			removeFeat.addActionListener(e -> {
				int index = tabs.indexOfComponent(fPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeFeat);
			tabs.setSelectedComponent(fPane);
		}
		
	}

	public void CheckTabs() {
		if (tabs.getTabCount() == 1) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "tabs");
		} else if (tabs.getTabCount() <= 0) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "none");
		}
	}
	
	private boolean hasTab(String n) {
		 for (int i = 0; i < tabs.getTabCount(); i++) {
		        if (n.equals(tabs.getTitleAt(i))) {
		        	tabs.setSelectedIndex(i);
		            return true;
		        }
		    }
		    return false;
	}

	@Override
	public JTabbedPane GetTabs() {
		return null;
	}

	@Override
	public void onMapUpdated() {
		FillSidePane();
	}

	@Override
	public void onMapUpdated(int mapType) {
		FillSidePane();
	}
}