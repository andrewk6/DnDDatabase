package gui.dungeon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.DataContainer;
import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;
import data.dungeon.Dungeon;
import data.dungeon.DungeonFloor;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.MonsterDispPane;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.AllTab;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import utils.ErrorLogger;

public class DungeonIViewer extends JInternalFrame implements AllTab
{
	private Dungeon d; 
	private DataContainer data;
	private GuiDirector gd;
	private ColorTabbedPaneUI tabsUI;
	
	private final JFileChooser fChoose = new JFileChooser();
	
	private JPanel sidePane;
	private JTabbedPane mainPane;
	private DungeonViewerPane dView;
	private JLabel dungeonLbl;
	
	private JButton editBtn;
	
	public DungeonIViewer(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dungeon Files (*.dol)", "dol");
		fChoose.setFileFilter(filter);
		
		this.setResizable(true);
		this.setSize(700,600);
		this.setResizable(true);
		this.setClosable(true);
		this.setMaximizable(true);
		this.setIconifiable(true);
		this.addInternalFrameListener(GuiDirector.getAllTabListener(gd, this));
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		this.gd.RegisterDungeonFrame(this);
		
		Initialize(this.getContentPane());
		AddButtonPane(this.getContentPane());
	}
	
	private void Initialize(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		mainPane = new JTabbedPane();
		tabsUI = new ColorTabbedPaneUI();
		mainPane.setUI(tabsUI);
		cPane.add(mainPane, BorderLayout.CENTER);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = new JScrollPane(sidePane);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cPane.add(sideScroll, BorderLayout.WEST);
		
		dungeonLbl = CompFactory.createNewLabel("", ComponentType.HEADER);
	}
	
	private void AddButtonPane(Container cPane) {
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton loadBtn = CompFactory.createNewButton("Load", _ -> {
			int result = fChoose.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fChoose.getSelectedFile();

				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile));
					d = (Dungeon) ois.readObject();	
					LoadDungeon(d);
					ois.close();
				} catch (IOException e1) {
					ErrorLogger.log(e1);
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			}
		});
		btnPane.add(loadBtn);
		
		editBtn = CompFactory.createNewButton("GoTo Edit", _->{
			gd.EditDungeon(d);
		});
		btnPane.add(editBtn);
		editBtn.setEnabled(false);
	}
	
	public void LoadDungeon(Dungeon d) {
		editBtn.setEnabled(true);
		if(this.d != d)
			this.d = d;
		if(d.floors.size() > 0) {
			FillSidePane();
		}
		SwingUtilities.invokeLater(()->{
//			addNewEditor(tiles);
			revalidate();
			repaint();
		});
	}
	
	private void FillSidePane() {
		for(String f : d.floors.keySet()) {
			JPanel sidePoint = new JPanel();
			sidePoint.setLayout(new BorderLayout());
			JLabel lbl = CompFactory.createNewLabel(f, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Clicked");
					if(dView != null) {
						System.out.println("Loading Floor");
						dView.loadFloor(d.floors.get(f).tiles);
						dungeonLbl.setText(f);
						resetTabs();
					} else {
						System.out.println("Adding Floor Tab and Floor");
						dView = new DungeonViewerPane(data, gd, mainPane, d.floors.get(f).tiles);
						mainPane.addTab("Dungeon View", dView);
						dungeonLbl.setText(f);
						mainPane.setTabComponentAt(0, dungeonLbl);
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			sidePoint.add(lbl, BorderLayout.CENTER);
			sidePane.add(sidePoint);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void resetTabs() {
		if(mainPane.getTabCount() >= 2) {
			for(int i = mainPane.getTabCount()-1; i > 0; i --) {
				mainPane.removeTabAt(i);
			}
		}
	}
	@Override
	public JTabbedPane GetTabs() {
		return mainPane;
	}
	
	public void AddTab(Monster m) {
		if(!hasTab(m.name))
		{
			JPanel monstDisp = new JPanel();
			monstDisp.setLayout(new BorderLayout());
			MonsterDispPane monstPane = new MonsterDispPane(data, gd, m.name, gd.getDesktop());
			monstDisp.add(monstPane, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			monstDisp.add(btnFlow, BorderLayout.SOUTH);

			JButton removeMonst = new JButton("Remove Monster");
			StyleContainer.SetFontBtn(removeMonst);
			removeMonst.addActionListener(e -> {
				int index = mainPane.indexOfComponent(monstDisp);
				if (index != -1) {
					mainPane.removeTabAt(index);
				}
			});
			btnFlow.add(removeMonst);
			mainPane.addTab(m.name, monstDisp);
			tabsUI.setTabColor(mainPane.indexOfTab(m.name), Color.ORANGE);
			mainPane.setSelectedComponent(monstDisp);
		}
	}

	public void AddTab(Rule r) {
		if(!hasTab(r.name)) {
			JPanel rPane = new JPanel();
			rPane.setLayout(new BorderLayout());
			mainPane.addTab(r.name, rPane);
			tabsUI.setTabColor(mainPane.indexOfTab(r.name), Color.CYAN);

			JTextField rTitle = new JTextField(r.name);
			rTitle.setEditable(false);
			rTitle.setFocusable(false);
			rTitle.setHorizontalAlignment(JTextField.CENTER);
			StyleContainer.SetFontHeader(rTitle);
			rPane.add(rTitle, BorderLayout.NORTH);

			HoverTextPane ruleDesc = new HoverTextPane(data, gd, gd.getDesktop());
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
				int index = mainPane.indexOfComponent(rPane);
				if (index != -1) {
					mainPane.removeTabAt(index);
				}
			});
			btnFlow.add(removeRule);
			mainPane.setSelectedComponent(rPane);
		}
	}
	
	public void AddTab(Feat f) {
		if(!hasTab(f.name)) {
			JPanel fPane = new JPanel();
			fPane.setLayout(new BorderLayout());
			mainPane.addTab(f.name, fPane);
			tabsUI.setTabColor(mainPane.indexOfTab(f.name), Color.YELLOW);

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
				int index = mainPane.indexOfComponent(fPane);
				if (index != -1) {
					mainPane.removeTabAt(index);
				}
			});
			btnFlow.add(removeFeat);
			mainPane.setSelectedComponent(fPane);
		}
	}

	public void AddTab(Spell s) {
		if(!hasTab(s.name)) {
			JPanel sPane = new JPanel();
			sPane.setLayout(new BorderLayout());
			mainPane.addTab(s.name, sPane);
			tabsUI.setTabColor(mainPane.indexOfTab(s.name), Color.PINK);

			JTextField sTitle = new JTextField(s.name);
			sTitle.setEditable(false);
			sTitle.setFocusable(false);
			sTitle.setHorizontalAlignment(JTextField.CENTER);
			StyleContainer.SetFontHeader(sTitle);
			sPane.add(sTitle, BorderLayout.NORTH);

			HoverTextPane sDesc = new HoverTextPane(data, gd, gd.getDesktop());
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
				int index = mainPane.indexOfComponent(sPane);
				if (index != -1) {
					mainPane.removeTabAt(index);
				}
			});
			btnFlow.add(removeSpell);
			mainPane.setSelectedComponent(sPane);
		}
	}
	
	private boolean hasTab(String n) {
		 for (int i = 0; i < mainPane.getTabCount(); i++) {
		        if (n.equals(mainPane.getTitleAt(i))) {
		        	mainPane.setSelectedIndex(i);
		            return true;
		        }
		    }
		    return false;
	}
}