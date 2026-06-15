package gui.initative;

import data.DataContainer;
import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;
import data.campaign.Player;
import data.hazards.Hazard;
import data.players.Background;
import data.players.Species;
import data.players.classes.DnDClass;
import gui.background.BackgroundPane;
import gui.campaign.PlayerPane;
import gui.classes.ClassPane;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.AllTab;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import gui.hazard.HazardPane;
import gui.monsters.MonsterDispPane;
import gui.species.SpeciesPane;
import gui.spells.SpellPane;
import utils.DiceCalculator;
import utils.ErrorLogger;
import utils.IllegalDiceNotationException;

import javax.imageio.ImageIO;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InitiativeIFrameHP extends JInternalFrame implements AllTab {
	private static final String INIT_EXT = "initiative";

	private static final FileFilter INITIATIVE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith("." + INIT_EXT);
		}

		@Override
		public String getDescription() {
			return "Initiative Files (*." + INIT_EXT + ")";
		}
	};

	private final DataContainer data;
	private final GuiDirector gd;
	private JDesktopPane dPane;
	private ColorTabbedPaneUI tabsUI;

	private final JPanel initListPane = new JPanel(new GridLayout(0, 1));
	private final JPanel rightPane = new JPanel(new BorderLayout());
	private final JSplitPane initSplit = new JSplitPane();

	private JTabbedPane tabs;
	private final List<InitiativeEntry> entries = new ArrayList<>();
	private int currentIndex = 0;

	public static final UUID PLAYER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DataContainer data = new DataContainer();
			data.init();
			JFrame frame = new JFrame("Initiative Tracker Test");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(StyleContainer.GetDefaultCloseListener(data));
			frame.setSize(1000, 700);

			JDesktopPane desktopPane = new JDesktopPane();
			frame.setContentPane(desktopPane);

			GuiDirector gd = new GuiDirector(desktopPane);
			InitiativeIFrameHP tracker = new InitiativeIFrameHP(data, gd, desktopPane);
			desktopPane.add(tracker);
			tracker.setVisible(true);
			frame.setVisible(true);
		});
	}

	public InitiativeIFrameHP(DataContainer data, GuiDirector gd, JDesktopPane dPane) {
		this.data = data;
		this.gd = gd;
		this.dPane = dPane;
        this.gd.RegisterInitiativeFrame(this);
		tabsUI = new ColorTabbedPaneUI();
		this.setBounds(20, 20, 800, 600);
		this.setLayout(new BorderLayout());

		setTitle("Initiative Tracker");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		tabs = new JTabbedPane();
		tabs.setUI(tabsUI);
		tabs.addTab("Initiative Tracker", buildTrackerPanel());
		this.add(tabs, BorderLayout.CENTER);

		try {

			BufferedImage iconImage = ImageIO.read(getClass().getResource("/" + StyleContainer.INIT_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.INIT_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ErrorLogger.log(e);
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.INIT_ICON_FILE));
			this.setFrameIcon(icon);
		}

		this.addInternalFrameListener(GuiDirector.getAllTabListener(gd, this));
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {
				gd.lockPlayerEdits(true);
			}

			public void internalFrameIconified(InternalFrameEvent e) {
			}

			public void internalFrameDeiconified(InternalFrameEvent e) {
			}

			public void internalFrameDeactivated(InternalFrameEvent e) {
			}

			public void internalFrameClosing(InternalFrameEvent e) {
				gd.lockPlayerEdits(false);
				gd.DegisterInitiativeFrame(InitiativeIFrameHP.this);
			}

			public void internalFrameClosed(InternalFrameEvent e) {
			}

			public void internalFrameActivated(InternalFrameEvent e) {
			}
		});
		setVisible(true);
	}

	private JPanel buildTrackerPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel leftPanel = new JPanel(new BorderLayout());

		leftPanel.add(CompFactory.wrapPanelInScroll(initListPane), BorderLayout.CENTER);

		JButton nextTurn = new JButton("Next");
		nextTurn.addActionListener(_ -> advanceTurn());
		StyleContainer.SetFontBtn(nextTurn);
		leftPanel.add(nextTurn, BorderLayout.SOUTH);

		JPanel topBar = new JPanel();
		JButton addPlayerBtn = new JButton("Add Player");
		JButton addMonsterBtn = new JButton("Add Monster");
		JButton addOtherBtn = new JButton("Add Other");
		JButton reset = CompFactory.createNewButton("Reset Initiative", this::removeAllInitiatives);
		JButton saveBtn = new JButton("Save");
		JButton loadBtn = new JButton("Load");

		saveBtn.addActionListener(_ -> saveInitiativeState());
		StyleContainer.SetFontBtn(saveBtn);
		loadBtn.addActionListener(_ -> loadInitiativeState());
		StyleContainer.SetFontBtn(loadBtn);
		addPlayerBtn.addActionListener(_ -> showAddPlayerDialog());
		StyleContainer.SetFontBtn(addPlayerBtn);
		addMonsterBtn.addActionListener(_ -> showAddMonsterDialog());
		StyleContainer.SetFontBtn(addMonsterBtn);
		addOtherBtn.addActionListener(_ -> showAddOtherDialog());
		StyleContainer.SetFontBtn(addOtherBtn);

		topBar.add(addPlayerBtn);
		topBar.add(addMonsterBtn);
		topBar.add(addOtherBtn);
		topBar.add(reset);
		topBar.add(saveBtn);
		topBar.add(loadBtn);

		initSplit.setLeftComponent(leftPanel);
		initSplit.setRightComponent(rightPane);
		
		panel.add(topBar, BorderLayout.NORTH);
		panel.add(initSplit, BorderLayout.CENTER);
//		panel.add(leftPanel, BorderLayout.WEST);
//		panel.add(rightPane, BorderLayout.CENTER);

		return panel;
	}

	private void addSinglePlayer() {
		// Fallback: single-player add
		JTextField nameField = new JTextField(10);
		JTextField initField = new JTextField(5);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Name:"));
		panel.add(nameField);
		panel.add(new JLabel("Initiative:"));
		panel.add(initField);

		int result = JOptionPane.showConfirmDialog(this, panel, "Add Player", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String name = nameField.getText().trim();
			try {
				int init = Integer.parseInt(initField.getText().trim());
				addInitiativeEntry(new InitiativeEntry(UUID.randomUUID(), name, init, null));
			} catch (NumberFormatException ignored) {
				ErrorLogger.log(ignored);
				JOptionPane.showMessageDialog(this, "Invalid initiative value.");
			}
		}
	}

	private void showAddPlayerDialog() {
		Map<String, Player> players = data.getParty();

		if (players == null) {
			addSinglePlayer();
			return;
		} else if (players.isEmpty()) {
			addSinglePlayer();
			return;
		}

		// Multi-player dialog
		JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 4));
		Map<String, JTextField> initFields = new HashMap<>();

		for (String playerName : players.keySet()) {
			inputPanel.add(new JLabel(playerName + " Initiative:"));
			JTextField field = new JTextField(5);
			initFields.put(playerName, field);
			inputPanel.add(field);
		}

		int result = JOptionPane.showConfirmDialog(this, CompFactory.wrapPanelInScroll(inputPanel),
				"Set Player Initiatives", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			for (Map.Entry<String, JTextField> entry : initFields.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().getText().trim();
				if (!value.isEmpty()) {
					try {
						int init = Integer.parseInt(value);
						addInitiativeEntry(new InitiativeEntry(UUID.randomUUID(), name, init, null));
					} catch (NumberFormatException ex) {
						ErrorLogger.log(ex);
						JOptionPane.showMessageDialog(this, "Invalid initiative for " + name + ": " + value);
					}
				}
			}
		}
	}

	private void showAddMonsterDialog() {
		String[] names = data.getMonsterKeysSorted().toArray(new String[0]);
		Map<String, Monster> monsters = data.getMonsters();

		FilterCombo monsterSelector = new FilterCombo(data.getMonsterKeysSorted(), 20);
		JTextField overrideBonus = new JTextField(5);
		JLabel initBonusLabel = new JLabel("Init Bonus: +0");
		ReminderField numEnemies = CompFactory.createReminderField("Num Enemies", true, ComponentType.BODY);
		numEnemies.setColumns(5);

		monsterSelector.addActionListener(_ -> {
			String selected = (String) monsterSelector.getSelectedItem();
			Monster m = monsters.get(selected);
			if (m != null) {
				initBonusLabel.setText("Init Bonus: " + m.init);
				overrideBonus.setText(m.GetInitBonus() + "");
			}
		});

		JPanel request = new JPanel();
		request.setLayout(new BorderLayout());

		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());
		request.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Monster:"), BorderLayout.WEST);
		header.add(monsterSelector, BorderLayout.CENTER);

		JPanel config = new JPanel();
		config.setLayout(new BorderLayout());
		request.add(config, BorderLayout.CENTER);

		JPanel bonusPane = new JPanel();
		config.add(bonusPane, BorderLayout.NORTH);

		JPanel numPane = new JPanel();
		config.add(numPane, BorderLayout.CENTER);

		JPanel specialPane = new JPanel();
		specialPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		config.add(specialPane, BorderLayout.SOUTH);

		JCheckBox advRoll = CompFactory.createNewCheckbox("Advantage");
		specialPane.add(advRoll);

		JLabel forceLbl = CompFactory.createNewLabel("   Set Init To:");
		specialPane.add(forceLbl);

		ReminderField forceField = CompFactory.createReminderField("Initiative Score", true);
		forceField.setColumns(5);
		specialPane.add(forceField);

		bonusPane.add(initBonusLabel);
		bonusPane.add(new JLabel("Bonus:"));
		bonusPane.add(overrideBonus);
		numPane.add(new JLabel("Number Enemies:"));
		numPane.add(numEnemies);

		int result = JOptionPane.showConfirmDialog(this, request, "Add Monster", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			int numEnemy;
			if (numEnemies.getText().length() > 0)
				numEnemy = Math.max(1, Integer.parseInt(numEnemies.getText()));
			else
				numEnemy = 1;
			String selected = (String) monsterSelector.getSelectedItem();
			Monster m = monsters.get(selected);
			if (m == null)
				return;

			for (int it = 0; it < numEnemy; it++) {
				try {
					int val;
					if (forceField.getText().length() > 0)
						val = Integer.parseInt(forceField.getText());
					else {
						int bonus = Integer.parseInt(overrideBonus.getText().trim());
						if (advRoll.isSelected()) {
							val = Math.max(DiceCalculator.parseDiceExpression("1d20 + " + bonus),
									DiceCalculator.parseDiceExpression("1d20 + " + bonus));
						} else {
							val = DiceCalculator.parseDiceExpression("1d20 + " + bonus);
						}

					}
					UUID id = UUID.randomUUID();
					addInitiativeEntry(new InitiativeEntry(id, m.name, val, m));
				} catch (NumberFormatException ignored) {
					ErrorLogger.log(ignored);
					JOptionPane.showMessageDialog(this, "Invalid bonus.");
				} catch (IllegalDiceNotationException e1) {
					e1.printStackTrace();
				} catch (ScriptException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void showAddOtherDialog() {
		ReminderField actorField = CompFactory.createReminderField("Person/Creature's Name");
		ReminderField overrideBonus = CompFactory.createReminderField("...", true);
		overrideBonus.setColumns(5);
		JLabel initBonusLabel = new JLabel("Init Bonus:");
		ReminderField numEnemies = CompFactory.createReminderField("Num Enemies", true, ComponentType.BODY);
		numEnemies.setColumns(5);

		JPanel request = new JPanel();
		request.setLayout(new BorderLayout());

		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());
		request.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Monster:"), BorderLayout.WEST);
		header.add(actorField, BorderLayout.CENTER);

		JPanel config = new JPanel();
		config.setLayout(new BorderLayout());
		request.add(config, BorderLayout.CENTER);

		JPanel bonusPane = new JPanel();
		config.add(bonusPane, BorderLayout.NORTH);

		JPanel numPane = new JPanel();
		config.add(numPane, BorderLayout.CENTER);

		JPanel specialPane = new JPanel();
		specialPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		config.add(specialPane, BorderLayout.SOUTH);

		JCheckBox advRoll = CompFactory.createNewCheckbox("Advantage");
		specialPane.add(advRoll);

		JLabel forceLbl = CompFactory.createNewLabel("   Set Init To:");
		specialPane.add(forceLbl);

		ReminderField forceField = CompFactory.createReminderField("Initiative Score", true);
		forceField.setColumns(5);
		specialPane.add(forceField);

		bonusPane.add(initBonusLabel);
		bonusPane.add(overrideBonus);
		numPane.add(new JLabel("Number Enemies:"));
		numPane.add(numEnemies);

		int result = JOptionPane.showConfirmDialog(this, request, "Add Other", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			int numEnemy;
			if (numEnemies.getText().length() > 0)
				numEnemy = Math.max(1, Integer.parseInt(numEnemies.getText()));
			else
				numEnemy = 1;

			for (int it = 0; it < numEnemy; it++) {
				try {
					int val;
					if (forceField.getText().length() > 0)
						val = Integer.parseInt(forceField.getText());
					else {
						int bonus = Integer.parseInt(overrideBonus.getText().trim());
						if (advRoll.isSelected()) {
							val = Math.max(DiceCalculator.parseDiceExpression("1d20 + " + bonus),
									DiceCalculator.parseDiceExpression("1d20 + " + bonus));
						} else {
							val = DiceCalculator.parseDiceExpression("1d20 + " + bonus);
						}

					}
					UUID id = UUID.randomUUID();
					addInitiativeEntry(new InitiativeEntry(id, actorField.getText(), val, null));
				} catch (NumberFormatException ignored) {
					ErrorLogger.log(ignored);
					JOptionPane.showMessageDialog(this, "Invalid bonus.");
				} catch (IllegalDiceNotationException e1) {
					e1.printStackTrace();
				} catch (ScriptException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void importMonsterInit(ArrayList<Monster> monsts) {
		for (Monster m : monsts) {
			int bonus = m.GetInitBonus();
			int roll = bonus + new Random().nextInt(20) + 1;
			UUID id = UUID.randomUUID();
			addInitiativeEntry(new InitiativeEntry(id, m.name, roll, m));
		}
	}

	private void addInitiativeEntry(InitiativeEntry entry) {
		entries.add(entry);
		entries.sort((a, b) -> Integer.compare(b.initiative, a.initiative));
		updateInitiativeList();
	}

	private void updateInitiativeList() {
		initListPane.removeAll();
		for (InitiativeEntry e : entries) {
			initListPane.add(new EntryPane(e, this));
		}
		
		initListPane.revalidate();
		initListPane.repaint();
		

		updateSelected();
		initSplit.setDividerLocation(initListPane.getPreferredSize().width);
		
		revalidate();
		repaint();
	}

	public void removeAllInitiatives() {
		entries.clear();
		updateInitiativeList();
		currentIndex = 0;
		rightPane.removeAll();
		rightPane.revalidate();
		rightPane.repaint();
	}

	public void removeInitiativeEntry(String name) {
        if (name == null || name.isBlank()) return;

        // Remove matching entry (case-sensitive)
        entries.removeIf(entry -> entry.name.equals(name));

        // Re-sort and update the display
        entries.sort((a, b) -> Integer.compare(b.initiative, a.initiative));
        updateInitiativeList();

        // Reset selection and right pane if current was removed
        if (currentIndex >= entries.size()) currentIndex = 0;

        if (!entries.isEmpty()) {
            loadEntry(entries.get(currentIndex));
        } else {
            rightPane.removeAll();
            rightPane.revalidate();
            rightPane.repaint();
        }
    }

	public EntryPane getEntryPaneByEntry(InitiativeEntry entry) {
		for(Component c : initListPane.getComponents())
			if(c instanceof EntryPane ePane)
				if(ePane.getEntry().id.equals(entry.id))
					return ePane;
		return null;
	}

	public void removeInitiativeEntry(InitiativeEntry remove) {
//        if (remove == null || remove.id.equals(PLAYER_ID)) return;
		if (remove == null)
			return;
		InitiativeEntry tmpEntry = entries.get(currentIndex);
		// Remove matching entry (case-sensitive)
		entries.removeIf(entry -> entry.id.equals(remove.id));

		// Re-sort and update the display
		entries.sort((a, b) -> Integer.compare(b.initiative, a.initiative));

		SwingUtilities.invokeLater(() -> {
			if (currentIndex > -1) {
				if (!remove.equals(tmpEntry))
					for (int i = 0; i < entries.size(); i++)
						if (entries.get(i).equals(tmpEntry))
							currentIndex = i;
				// Reset selection and right pane if current was removed
				if (currentIndex >= entries.size())
					currentIndex = 0;

				if (!entries.isEmpty()) {
					loadEntry(entries.get(currentIndex));
					updateSelected();
				} else {
					rightPane.removeAll();
					rightPane.revalidate();
					rightPane.repaint();
				}
			}
		});
		
		updateInitiativeList();
	}

	private void updateSelected() {
		for (int i = 0; i < entries.size(); i++) {
			if (i == currentIndex)
				getEntryPaneByEntry(entries.get(i)).setSelected(true);
			else
				getEntryPaneByEntry(entries.get(i)).setSelected(false);
		}
		 initListPane.repaint();
	}

	private void advanceTurn() {
		if (entries.isEmpty())
			return;
		currentIndex = (currentIndex + 1) % entries.size();
		loadEntry(entries.get(currentIndex));

		updateSelected();
	}

	private void loadEntry(InitiativeEntry entry) {
		rightPane.removeAll();

		if (entry.monster != null) {
			rightPane.add(new MonsterDispPane(entry.monster, data, gd), BorderLayout.CENTER);
		} else {
			if (data.isCampaignLoaded()) {
				// Load PlayerPane with noEdits = true
				Player p = data.getParty().get(entry.name);
				if (p != null) {
					PlayerPane playerPane = new PlayerPane(p, data, gd, true);
					rightPane.add(playerPane, BorderLayout.CENTER);
				} else {
					// Fallback if player not found
					JLabel fallback = new JLabel("Player not found: " + entry.name, SwingConstants.CENTER);
					StyleContainer.SetFontHeader(fallback);
					rightPane.add(fallback, BorderLayout.CENTER);
				}
			} else {
				JLabel player = new JLabel("Player Loaded: " + entry.name, SwingConstants.CENTER);
				StyleContainer.SetFontHeader(player);
				rightPane.add(player, BorderLayout.CENTER);
			}

		}

		rightPane.revalidate();
		rightPane.repaint();
	}

	private void saveInitiativeState() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(INITIATIVE_FILTER);

		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			// FORCE .initiative extension
			if (!file.getName().toLowerCase().endsWith("." + INIT_EXT)) {
				file = new File(file.getParentFile(), file.getName() + "." + INIT_EXT);
			}

			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {

				List<SavedEntry> saved = entries.stream().map(SavedEntry::new).collect(Collectors.toList());

				out.writeObject(saved);
				out.writeInt(currentIndex);

			} catch (IOException ex) {
				ErrorLogger.log(ex);
				JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadInitiativeState() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(INITIATIVE_FILTER);

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			// OPTIONAL: hard validation (prevents wrong file types even if filter is
			// bypassed)
			if (!file.getName().toLowerCase().endsWith("." + INIT_EXT)) {
				JOptionPane.showMessageDialog(this, "Invalid file type. Please select a *." + INIT_EXT + " file.");
				return;
			}

			try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

				List<SavedEntry> saved = (List<SavedEntry>) in.readObject();
				int index = in.readInt();

				entries.clear();
				for (SavedEntry s : saved) {
					entries.add(s.toEntry());
				}

				currentIndex = index;
				updateInitiativeList();
				advanceTurn();

			} catch (IOException | ClassNotFoundException ex) {
				ErrorLogger.log(ex);
				JOptionPane.showMessageDialog(this, "Failed to load: " + ex.getMessage());
			}
		}
	}

	// Stub AllTab methods
	public JTabbedPane GetTabs() {
		return null;
	}

	public void AddTab(Monster m) {
		System.out.println("Adding: " + m.name);
		if (!hasTab(m.name)) {
			JPanel monstDisp = new JPanel();
			monstDisp.setLayout(new BorderLayout());
			MonsterDispPane monstPane = new MonsterDispPane(data, gd, m.name, dPane);
			monstDisp.add(monstPane, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			monstDisp.add(btnFlow, BorderLayout.SOUTH);

			JButton removeMonst = new JButton("Remove Monster");
			StyleContainer.SetFontBtn(removeMonst);
			removeMonst.addActionListener(_ -> {
				int index = tabs.indexOfComponent(monstDisp);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeMonst);
			tabs.addTab(m.name, monstDisp);
			tabsUI.setTabColor(tabs.indexOfTab(m.name), Color.ORANGE);
			tabs.setSelectedComponent(monstDisp);
		}

	}

	public void AddTab(Spell s) {
		if (!hasTab(s.name)) {
			JPanel sPane = new JPanel();
			sPane.setLayout(new BorderLayout());
			tabs.addTab(s.name, sPane);
			tabsUI.setTabColor(tabs.indexOfTab(s.name), Color.PINK);

			sPane.add(new SpellPane(s, data, gd), BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			sPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeSpell = new JButton("Remove " + s.name);
			StyleContainer.SetFontBtn(removeSpell);
			removeSpell.addActionListener(_ -> {
				int index = tabs.indexOfComponent(sPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeSpell);
			tabs.setSelectedComponent(sPane);
		}
	}

	@Override
	public void AddTab(Feat f) {
		if (!hasTab(f.name)) {
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
			JScrollPane fScroll = CompFactory.wrapPanelInScroll(fDesc);
			fPane.add(fScroll, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			fPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeFeat = new JButton("Remove " + f.name);
			StyleContainer.SetFontBtn(removeFeat);
			removeFeat.addActionListener(_ -> {
				int index = tabs.indexOfComponent(fPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeFeat);
			tabs.setSelectedComponent(fPane);
		}
	}

	public void AddTab(DnDClass c) {
		if (!hasTab(c.name)) {
			JPanel cPane = new JPanel();
			cPane.setLayout(new BorderLayout());
			tabs.addTab(c.name, cPane);
			tabsUI.setTabColor(tabs.indexOfTab(c.name), Color.lightGray);

			ClassPane classDisp = new ClassPane(data, c, gd);
			cPane.add(classDisp, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			cPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeFeat = new JButton("Remove " + c.name);
			StyleContainer.SetFontBtn(removeFeat);
			removeFeat.addActionListener(_ -> {
				int index = tabs.indexOfComponent(cPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeFeat);
			tabs.setSelectedComponent(cPane);
		}
	}

	public void AddTab(Rule r) {
		if (!hasTab(r.name)) {
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
			JScrollPane rScroll = CompFactory.wrapPanelInScroll(ruleDesc);
			rPane.add(rScroll, BorderLayout.CENTER);

			JPanel btnFlow = new JPanel();
			btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
			rPane.add(btnFlow, BorderLayout.SOUTH);

			JButton removeRule = new JButton("Remove " + r.name);
			StyleContainer.SetFontBtn(removeRule);
			removeRule.addActionListener(_ -> {
				int index = tabs.indexOfComponent(rPane);
				if (index != -1) {
					tabs.removeTabAt(index);
				}
			});
			btnFlow.add(removeRule);
			tabs.setSelectedComponent(rPane);
		}
	}

	public void AddTab(Species s) {
		if (!hasTab(s.name)) {
			JPanel tab = new JPanel();
			tab.setLayout(new BorderLayout());
			tabs.addTab(s.name, tab);
			tabsUI.setTabColor(tabs.indexOfComponent(tab), Color.YELLOW);

			tab.add(new SpeciesPane(data, gd, s), BorderLayout.CENTER);
			tab.add(CompFactory.createButtonFlowPane(FlowLayout.RIGHT,
					new JButton[] { CompFactory.createNewButton("Remove " + s.name + " Tab", _ -> {
						tabs.removeTabAt(tabs.indexOfComponent(tab));
					}) }), BorderLayout.SOUTH);
			tabs.setSelectedComponent(tab);
		}
	}

	public void AddTab(Background b) {
		if (!hasTab(b.name)) {
			JPanel tab = new JPanel();
			tab.setLayout(new BorderLayout());
			tabs.addTab(b.name, tab);

			tab.add(new BackgroundPane(b, data, gd), BorderLayout.CENTER);
			tab.add(CompFactory.createButtonFlowPane(FlowLayout.RIGHT,
					new JButton[] { CompFactory.createNewButton("Remove " + b.name + " Tab", _ -> {
						tabs.removeTabAt(tabs.indexOfComponent(tab));
					}) }), BorderLayout.SOUTH);
			tabs.setSelectedComponent(tab);
		}
	}

	public void AddTab(Hazard h) {
		if (!hasTab(h.name)) {
			JPanel tab = new JPanel();
			tab.setLayout(new BorderLayout());
			tabs.addTab(h.name, tab);
			tabsUI.setTabColor(tabs.indexOfComponent(tab), Color.LIGHT_GRAY);

			tab.add(new HazardPane(h, data, gd), BorderLayout.CENTER);
			tab.add(CompFactory.createButtonFlowPane(FlowLayout.RIGHT,
					new JButton[] { CompFactory.createNewButton("Remove " + h.name + " Tab", _ -> {
						tabs.removeTabAt(tabs.indexOfComponent(tab));
					}) }), BorderLayout.SOUTH);
			tabs.setSelectedComponent(tab);
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

	private static class SavedEntry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3266233664109780689L;
		UUID id;
		String name;
		int initiative;
		Monster monster;
		int hp;

		public SavedEntry(InitiativeEntry entry) {
			this.id = entry.id;
			this.name = entry.name;
			this.initiative = entry.initiative;
			this.monster = entry.monster;
			this.hp = entry.hp;
		}

		public InitiativeEntry toEntry() {
			return new InitiativeEntry(id, name, initiative, monster, hp);
		}
	}

	public static class InitiativeEntry {
		final UUID id;
		String name;
		int initiative;
		Monster monster;
		int hp;

		InitiativeEntry(UUID id, String name, int initiative, Monster monster) {
			this.id = id;
			this.name = name;
			this.initiative = initiative;
			this.monster = monster;
			this.hp = (monster != null) ? monster.getAvgHPInt() : 0;
		}
		
		InitiativeEntry(UUID id, String name, int initiative, Monster monster, int hp) {
			this.id = id;
			this.name = name;
			this.initiative = initiative;
			this.monster = monster;
			this.hp = hp;
		}

		public void setHP(int hp) {
			this.hp = hp;
		}

		public String toString() {
			return name + " (" + initiative + ")";
		}

		public boolean equals(InitiativeEntry e) {
			if (e.monster != null)
				return id.equals(e.id) && name.equals(e.name) && initiative == e.initiative
						&& monster.equals(e.monster);
			else
				return id.equals(e.id) && name.equals(e.name) && initiative == e.initiative;
		}
	}
}
