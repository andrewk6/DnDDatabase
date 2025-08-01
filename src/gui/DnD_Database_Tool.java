package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.DataContainer;
import data.campaign.Campaign;
import gui.builder_internals.FeatBuilderIFrame;
import gui.builder_internals.ItemBuilderIFrame;
import gui.builder_internals.MonsterBuilderIFrame;
import gui.builder_internals.QuickInsertBuilderIFrame;
import gui.builder_internals.RuleBuilderIFrame;
import gui.builder_internals.SpellBuilderIFrame;
import gui.campaign.NotesIFrame;
import gui.campaign.PartyIFrame;
import gui.dungeon.DungeonIBuilder;
import gui.dungeon.DungeonIViewer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.LoadListener;
import gui.gui_helpers.structures.StyleContainer;
import gui.initative.InitiativeIFrame;

public class DnD_Database_Tool extends JFrame {
	
	private JToolBar tools;
	private DataContainer data;
	private JDesktopPane dPane;
	private GuiDirector gd;
	
	//Database Frames
	private ComboIFrame comboFrame;
	private RuleIFrame rFrame;
	private SpellIFrame sFrame;
	private MonsterIFrame mFrame;
	private ItemIFrame iFrame;
	private FeatIFrame fFrame;
	
	//Campaign Label
	private JLabel camp;
	
	//Dungeon Frames
	DungeonIBuilder dBuildFrame;
	DungeonIViewer dViewFrame;
	
	//Builder Frames
	RuleBuilderIFrame rBuildFrame;
	SpellBuilderIFrame sBuildFrame;
	MonsterBuilderIFrame mBuildFrame;
	QuickInsertBuilderIFrame qBuildFrame;
	ItemBuilderIFrame iBuildFrame;
	FeatBuilderIFrame fBuildFrame;

	private final List<LoadListener> loadListeners = new ArrayList<LoadListener>();

	private static final long serialVersionUID = 1L;
	
	private final JFileChooser fChoose;
	private boolean campBtnsLoaded = false;
	private JMenu quickLoad;
	/**
	 * Create the frame.
	 */
	public DnD_Database_Tool(DataContainer data) {
		this.data = data;
		StyleContainer.SetLookAndFeel();
		fChoose = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Campaign Files (*.col)", "col");
		fChoose.setFileFilter(filter);
		if(data.getLastCampPath() != null) {
			fChoose.setCurrentDirectory(new File(data.getLastCampPath()));
		}
	}
	
	public void registerLoadListener(LoadListener loader) {
		loadListeners.add(loader);
	}
	
	public void loadFinsihed() {
		for(LoadListener l : loadListeners) {
			l.onDataLoaded();
		}
		
		loadListeners.clear();
	}
	
	public void init() {
		try {
			this.setIconImage(ImageIO.read(this.getClass().
					getResourceAsStream("/" + StyleContainer.PROGRAM_ICON_FILE)));
		} catch (IOException e) {
			System.out.println("Failed load icon");
		}
		
		ConfigFrame();
		gd = new GuiDirector(dPane);
		PreloadFrames();
//		BuildControlBar(getContentPane());
		BuildMenuBar();
		loadFinsihed();
	}
	
	private void PreloadFrames() {
		comboFrame = new ComboIFrame(data, gd, dPane);
		dPane.add(comboFrame);
		comboFrame.setVisible(false);
		
		rFrame = new RuleIFrame(data, gd, dPane);
		dPane.add(rFrame);
		rFrame.setVisible(false);
		
		sFrame = new SpellIFrame(data, gd, dPane);
		dPane.add(sFrame);
		sFrame.setVisible(false);
		
		mFrame = new MonsterIFrame(data, gd, dPane);
		dPane.add(mFrame);
		mFrame.setVisible(false);
		
		iFrame = new ItemIFrame(data, gd, dPane);
		dPane.add(iFrame);
		iFrame.setVisible(false);
		
		fFrame = new FeatIFrame(data, gd);
		dPane.add(fFrame);
		fFrame.setVisible(false);
		
		rBuildFrame = new RuleBuilderIFrame(data);
		dPane.add(rBuildFrame);
		rBuildFrame.setVisible(false);
		
		sBuildFrame = new SpellBuilderIFrame(data);
		dPane.add(sBuildFrame);
		sBuildFrame.setVisible(false);
		
		mBuildFrame = new MonsterBuilderIFrame(data);
		dPane.add(mBuildFrame);
		mBuildFrame.setVisible(false);
		
		qBuildFrame = new QuickInsertBuilderIFrame(data);
		dPane.add(qBuildFrame);
		qBuildFrame.setVisible(false);
		
		iBuildFrame = new ItemBuilderIFrame(data);
		dPane.add(iBuildFrame);
		iBuildFrame.setVisible(false);
		
		dBuildFrame = new DungeonIBuilder(data, gd);
		dPane.add(dBuildFrame);
		dBuildFrame.setVisible(false);
		
		fBuildFrame = new FeatBuilderIFrame(data, gd);
		dPane.add(fBuildFrame);
		fBuildFrame.setVisible(false);
		
		dViewFrame = new DungeonIViewer(data, gd);
		dPane.add(dViewFrame);
		dViewFrame.setVisible(false);
	}
	
	private void BuildMenuBar() {
		JMenuBar menu = new JMenuBar();
		this.setJMenuBar(menu);
		
		/*
		 * DATABASE MENU
		 */
		
		JMenu dataMenu = new JMenu("Databases");
		StyleContainer.SetFontHeader(dataMenu);
		menu.add(dataMenu);
		dataMenu.add(CompFactory.createNewJMenuItem("Full Database", comboFrame));
		dataMenu.add(CompFactory.createNewJMenuItem("Rules", rFrame));
		dataMenu.add(CompFactory.createNewJMenuItem("Spells", sFrame));
		dataMenu.add(CompFactory.createNewJMenuItem("Monsters", mFrame));
		dataMenu.add(CompFactory.createNewJMenuItem("Items", iFrame));
		dataMenu.add(CompFactory.createNewJMenuItem("Feats", fFrame));
		
		/*
		 * TOOLS MENU
		 */
		JMenu toolsMenu = new JMenu("Tools");
		StyleContainer.SetFontHeader(toolsMenu);
		menu.add(toolsMenu);
		
		toolsMenu.add(CompFactory.createNewJMenuItem(
				"Initiative Tracker", ()->new InitiativeIFrame(data, gd, dPane), dPane));
		toolsMenu.add(CompFactory.createNewJMenuItem(
				"Dice Calculator", ()->new DiceCalcIFrame(), dPane));
		
		/*
		 * DUNGEON MENU
		 */
		
		JMenu dungeonMenu = new JMenu("Dungeons");
		StyleContainer.SetFontHeader(dungeonMenu);
		menu.add(dungeonMenu);
		
		dungeonMenu.add(CompFactory.createNewJMenuItem("Dungeon Builder", dBuildFrame));
		dungeonMenu.add(CompFactory.createNewJMenuItem("Dungeon Viewer", dViewFrame));
		
		/*
		 * CAMPAIGN MENU
		 */
		
		JMenu campMenu = new JMenu("Campaign Options");
		StyleContainer.SetFontHeader(campMenu);
		menu.add(campMenu);
		
		JMenu cMenu = new JMenu("Campaign");
		StyleContainer.SetFontMain(cMenu);
		campMenu.add(cMenu);
		
		JMenuItem newCamp = CompFactory.createNewJMenuItem("New Campaign");
		newCamp.addActionListener(e ->{
			int result = fChoose.showSaveDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				 File selectedFile = fChoose.getSelectedFile();
				 
				 if (!selectedFile.getName().toLowerCase().endsWith(".col")) {
		                selectedFile = new File(selectedFile.getAbsolutePath() + ".col");
		         }
				 Campaign c = new Campaign(selectedFile);
				 data.LoadCampaign(c);
				 if(!campBtnsLoaded)
					 AddCampaignButtons(campMenu, cMenu);
			}
		});
		cMenu.add(newCamp);
		
		JMenuItem loadCamp = CompFactory.createNewJMenuItem("Load Campaign");
		loadCamp.addActionListener(e -> {
	        // Show open dialog
	        int result = fChoose.showOpenDialog(null);
	        if (result == JFileChooser.APPROVE_OPTION) {
	            File selectedFile = fChoose.getSelectedFile();
	            data.LoadCampaign(selectedFile);
	            if(!campBtnsLoaded)
	            	AddCampaignButtons(campMenu, cMenu);
	            updateQuickLoad(campMenu, cMenu);
	            camp.setText(data.getCampaignName());
	        }
		});
		cMenu.add(loadCamp);
		
		quickLoad = new JMenu("Quick Loads");
		StyleContainer.SetFontMain(quickLoad);
		cMenu.add(quickLoad);
		
		updateQuickLoad(campMenu, cMenu);
		/*
		 * Builder Menu
		 */
		JMenu buildMenu = new JMenu("Builder Tools");
		StyleContainer.SetFontHeader(buildMenu);
		menu.add(buildMenu);
		buildMenu.add(CompFactory.createNewJMenuItem("Rule Builder", rBuildFrame));
		buildMenu.add(CompFactory.createNewJMenuItem("Spell Builder", sBuildFrame));
		buildMenu.add(CompFactory.createNewJMenuItem("Monster Builder", mBuildFrame));
		buildMenu.add(CompFactory.createNewJMenuItem("Quick Insert Builder", qBuildFrame));
		buildMenu.add(CompFactory.createNewJMenuItem("Item Buider", iBuildFrame));
		buildMenu.add(CompFactory.createNewJMenuItem("Feat Builder", fBuildFrame));
		
		/*
		 * LOADED CAMPAIGN
		 */
		menu.add(Box.createHorizontalGlue());
		
		camp = CompFactory.createNewLabel("", ComponentType.HEADER);
		menu.add(camp);
		/*
		 * EXIT BUTTON
		 */		
		JButton exitButton = new JButton("Exit");
		exitButton.setFocusable(false);
		StyleContainer.SetFontBtn(exitButton);
		exitButton.addActionListener(e ->{
			data.Exit();
			dispose();
		});
		menu.add(exitButton);
	}
	
	private void updateQuickLoad(JMenu campMenu, JMenu cMenu) {
		quickLoad.removeAll();
		
		if(data.getRecentFiles().size() > 0) {
			Queue<File> recentFiles = data.getRecentFiles();
			while(!recentFiles.isEmpty()) {
				File f = recentFiles.poll();
				String fileNameWithExt = f.getName();  // campaign1.dnd
		        String fileName = fileNameWithExt.contains(".")
		            ? fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf('.'))
		            : fileNameWithExt;
				JMenuItem load = CompFactory.createNewJMenuItem(fileName);
				load.addActionListener(e -> {
					data.LoadCampaign(f);
					if(!campBtnsLoaded)
						AddCampaignButtons(campMenu, cMenu);
				});
				quickLoad.add(load);
			}
		}
	}
	
	private void AddCampaignButtons(JMenu campMenu, JMenu cMenu) {
		/*
		 * Add Save Button
		 */
		JMenuItem saveCamp = CompFactory.createNewJMenuItem("Save Campaign");
		saveCamp.addActionListener(e -> {
			data.SafeSaveData(DataContainer.CAMPAIGN);
		});
		cMenu.add(saveCamp);
		
		campMenu.add(CompFactory.createNewJMenuItem("Party", ()->new PartyIFrame(data, gd), dPane));
		campMenu.add(CompFactory.createNewJMenuItem("Notes", ()-> new NotesIFrame(data, gd), dPane));
		campBtnsLoaded = true;
	}
	
	private void BuildControlBar(Container cPane) {
		tools = new JToolBar();
		tools.setFloatable(false);
		tools.setRollover(true);
		cPane.add(tools, BorderLayout.NORTH);
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.setFocusable(false);
		exitBtn.setFont(StyleContainer.BTN_FONT_MAIN);
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				data.Exit();
			}
		});
		tools.add(exitBtn);
		tools.addSeparator();
		
		JButton rulesBtn = new JButton("Rules");
		rulesBtn.setFont(StyleContainer.BTN_FONT_MAIN);
		rulesBtn.setFocusable(false);
		rulesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(()->{
					dPane.add(new RuleIFrame(data, gd, dPane));
					dPane.revalidate();
					dPane.repaint();
				});
				
			}
		});
		tools.add(rulesBtn);
		tools.addSeparator();
		
		JButton spellsBtn = new JButton("Spells");
		spellsBtn.setFocusable(false);
		StyleContainer.SetFontBtn(spellsBtn);
		spellsBtn.addActionListener(e->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new SpellIFrame(data, gd, dPane));
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(spellsBtn);
		tools.addSeparator();
		
		JButton monstBtn = new JButton("Monsters");
		monstBtn.setFocusable(false);
		StyleContainer.SetFontBtn(monstBtn);
		monstBtn.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new MonsterIFrame(data, gd, dPane));
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(monstBtn);
		tools.addSeparator();
		
		JButton itemBtn = new JButton("Items");
		itemBtn.setFocusable(false);
		StyleContainer.SetFontBtn(itemBtn);
		itemBtn.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new ItemIFrame(data, gd, dPane));
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(itemBtn);
		tools.addSeparator();
		
		JButton initBtn = new JButton("Initiative Tool");
		initBtn.setFocusable(false);
		StyleContainer.SetFontBtn(initBtn);
		initBtn.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new InitiativeIFrame(data, gd, dPane));
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(initBtn);
		tools.addSeparator();
		
		JButton comboBtn = new JButton("Full Database");
		comboBtn.setFocusable(false);
		StyleContainer.SetFontBtn(comboBtn);
		comboBtn.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new ComboIFrame(data, gd, dPane));
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(comboBtn);
		tools.addSeparator();
		
		JButton diceCalc = new JButton("Dice Calc");
		diceCalc.setFocusable(false);
		StyleContainer.SetFontBtn(diceCalc);
		diceCalc.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				dPane.add(new DiceCalcIFrame());
				dPane.revalidate();
				dPane.repaint();
			});
		});
		tools.add(diceCalc);
		tools.addSeparator();
	}
	
	private void ConfigFrame() {		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//		setUndecorated(true);
//		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		
		try {
			dPane = new JDesktopPane() {
				final Image bgImage = ImageIO.read(getClass().getResource("/" 
						+ StyleContainer.BACKGROUND_FILE));
				
				public void paintComponent(Graphics g) {
					g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
					g.setColor(new Color(60, 60, 60, 155));
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			};
		} catch (IOException e) {
			dPane = new JDesktopPane();
		}
		getContentPane().add(dPane);
		
//		GraphicsDevice gd = GraphicsEnvironment
//				.getLocalGraphicsEnvironment()
//				.getDefaultScreenDevice();
//		
//		if(gd.isFullScreenSupported()) {
//			gd.setFullScreenWindow(this);
//		}else {
//			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		}
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

}
