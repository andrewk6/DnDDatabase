package gui.initative;

import data.DataContainer;
import data.Monster;
import data.Rule;
import data.Spell;
import data.campaign.Player;
import gui.campaign.PlayerPane;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.MonsterDispPane;
import gui.gui_helpers.structures.AllTab;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class InitiativeIFrame extends JInternalFrame implements AllTab {
    private final DataContainer data;
    private final GuiDirector gd;
    private JDesktopPane dPane;
    private ColorTabbedPaneUI tabsUI;

    private final DefaultListModel<InitiativeEntry> initiativeListModel = new DefaultListModel<>();
    private final JList<InitiativeEntry> initiativeList = new JList<>(initiativeListModel);
    private final JPanel rightPane = new JPanel(new BorderLayout());
    
    private JTabbedPane tabs;
    private final List<InitiativeEntry> entries = new ArrayList<>();
    private int currentIndex = -1;
    
    private ProgrammaticSelectionModel selectionModel;
    
    public static final UUID PLAYER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataContainer data = new DataContainer();
            JFrame frame = new JFrame("Initiative Tracker Test");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(StyleContainer.GetDefaultCloseListener(data));
            frame.setSize(1000, 700);

            JDesktopPane desktopPane = new JDesktopPane();
            frame.setContentPane(desktopPane);

            GuiDirector gd = new GuiDirector(desktopPane);
            InitiativeIFrame tracker = new InitiativeIFrame(data, gd, desktopPane);
            desktopPane.add(tracker);
            tracker.setVisible(true);
            frame.setVisible(true);
        });
    }

    public InitiativeIFrame(DataContainer data, GuiDirector gd, JDesktopPane dPane) {
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
        System.out.println("Tabs UI class: " + tabs.getUI().getClass().getName());
        tabs.addTab("Initiative Tracker", buildTrackerPanel());
        this.add(tabs, BorderLayout.CENTER);
        
        try {

			BufferedImage iconImage = ImageIO.read(getClass().getResource("/" + StyleContainer.INIT_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.INIT_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.INIT_ICON_FILE));
			this.setFrameIcon(icon);
		}

        this.addInternalFrameListener(GuiDirector.getAllTabListener(gd, this));
        this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {gd.lockPlayerEdits(true);}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {
				gd.lockPlayerEdits(false);
				gd.DegisterInitiativeFrame(InitiativeIFrame.this);
				}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
		});
        setVisible(true);
    }

    private JPanel buildTrackerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        initiativeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initiativeList.setFocusable(false); // Prevent tab/arrow focus
        initiativeList.setFont(StyleContainer.FNT_HEADER_BOLD);
        selectionModel = new ProgrammaticSelectionModel(initiativeList);
        initiativeList.setSelectionModel(selectionModel);
        initiativeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	int index = initiativeList.locationToIndex(e.getPoint());
//            	if(index >= 0 && index <= initiativeList.)
            	if(e.isAltDown()) {
//            		System.out.println("Alt Down Click: " + initiativeList.getSelectedValue());
//            		removeInitiativeEntry(initiativeList.getSelectedValue().split(" ")[0]);
            		
            		Rectangle cellBounds = initiativeList.getCellBounds(index, index);
                    if (cellBounds.contains(e.getPoint())) {
//                        String clickedItem = initiativeList.getModel().getElementAt(index);
                        InitiativeEntry clickedItem = initiativeList.getModel().getElementAt(index);
                        System.out.println("Alt Down Clicked: " + clickedItem.toString());
                        removeInitiativeEntry(clickedItem);
                    }
            	}else {
//            		System.out.println(initiativeList.getSelectedValue());
//            		if(data.getMonsterKeysSorted().contains(initiativeList.getSelectedValue().split(" ")[0]))
//                		AddTab(data.getMonsters().get(initiativeList.getSelectedValue().split(" ")[0]));
//                    e.consume(); // Stop click from affecting selection
            		
            		Rectangle cellBounds = initiativeList.getCellBounds(index, index);
                    if (cellBounds.contains(e.getPoint())) {
//                        String clickedItem = initiativeList.getModel().getElementAt(index);
                        InitiativeEntry clickedItem = initiativeList.getModel().getElementAt(index);
                        System.out.println("Clicked: " + clickedItem);
                        if(data.getMonsterKeysSorted().contains(clickedItem.name)) {
                        	AddTab(data.getMonsters().get(clickedItem.name));
                        }else if(data.isCampaignLoaded()) {
                        	if(data.getParty().containsKey(clickedItem.name)) {
                        		PlayerPane pPane = new PlayerPane(data.getParty().get(clickedItem), data, gd);
                        		tabs.addTab(clickedItem.name, pPane);
                        		tabs.setSelectedComponent(pPane);
                        	}
                        }     
                    }
            	}
            }
        });
        leftPanel.add(new JScrollPane(initiativeList), BorderLayout.CENTER);

        JButton nextTurn = new JButton("Next");
        nextTurn.addActionListener(e -> advanceTurn());
        StyleContainer.SetFontBtn(nextTurn);
        leftPanel.add(nextTurn, BorderLayout.SOUTH);

        JPanel topBar = new JPanel();
        JButton addPlayerBtn = new JButton("Add Player");
        JButton addMonsterBtn = new JButton("Add Monster");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");

        saveBtn.addActionListener(e -> saveInitiativeState());
        StyleContainer.SetFontBtn(saveBtn);
        loadBtn.addActionListener(e -> loadInitiativeState());
        StyleContainer.SetFontBtn(loadBtn);
        addPlayerBtn.addActionListener(e -> showAddPlayerDialog());
        StyleContainer.SetFontBtn(addPlayerBtn);
        addMonsterBtn.addActionListener(e -> showAddMonsterDialog());
        StyleContainer.SetFontBtn(addMonsterBtn);

        topBar.add(addPlayerBtn);
        topBar.add(addMonsterBtn);
        topBar.add(saveBtn);
        topBar.add(loadBtn);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPane, BorderLayout.CENTER);

        return panel;
    }

//    private void showAddPlayerDialog() {
//        JTextField nameField = new JTextField(10);
//        JTextField initField = new JTextField(5);
//
//        JPanel panel = new JPanel();
//        panel.add(new JLabel("Name:"));
//        panel.add(nameField);
//        panel.add(new JLabel("Initiative:"));
//        panel.add(initField);
//
//        int result = JOptionPane.showConfirmDialog(this, panel, "Add Player", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            String name = nameField.getText().trim();
//            try {
//                int init = Integer.parseInt(initField.getText().trim());
//                addInitiativeEntry(new InitiativeEntry(name, init, null));
//            } catch (NumberFormatException ignored) {
//                JOptionPane.showMessageDialog(this, "Invalid initiative value.");
//            }
//        }
//    }
    
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
                addInitiativeEntry(new InitiativeEntry(PLAYER_ID, name, init, null));
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(this, "Invalid initiative value.");
            }
        }
    }
    
    private void showAddPlayerDialog() {
        Map<String, Player> players = data.getParty();
        
        if (players == null) {
           addSinglePlayer();
            return;
        }else if(players.isEmpty()) {
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

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(inputPanel),
                "Set Player Initiatives", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            for (Map.Entry<String, JTextField> entry : initFields.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue().getText().trim();
                if (!value.isEmpty()) {
                    try {
                        int init = Integer.parseInt(value);
                        addInitiativeEntry(new InitiativeEntry(PLAYER_ID, name, init, null));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid initiative for " + name + ": " + value);
                    }
                }
            }
        }
    }


    private void showAddMonsterDialog() {
        String[] names = data.getMonsterKeysSorted().toArray(new String[0]);
        Map<String, Monster> monsters = data.getMonsters();

        FilterCombo monsterSelector = new FilterCombo(data.getMonsterKeysSorted());
        JTextField overrideBonus = new JTextField(5);
        JLabel initBonusLabel = new JLabel("Init Bonus: +0");

        monsterSelector.addActionListener(e -> {
            String selected = (String) monsterSelector.getSelectedItem();
            Monster m = monsters.get(selected);
            if (m != null) {
                initBonusLabel.setText("Init Bonus: " + m.init);
                overrideBonus.setText(m.GetInitBonus() + "");
            }
        });

//        JPanel panel = new JPanel();
//        panel.add(new JLabel("Monster:"));
//        panel.add(monsterSelector);
//        panel.add(initBonusLabel);
//        panel.add(new JLabel("Bonus:"));
//        panel.add(overrideBonus);
        
        JPanel request = new JPanel();
        request.setLayout(new BorderLayout());
        
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        request.add(header, BorderLayout.NORTH);
        header.add(new JLabel("Monster:"), BorderLayout.WEST);
        header.add(monsterSelector, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        request.add(panel, BorderLayout.CENTER);
      panel.add(initBonusLabel);
      panel.add(new JLabel("Bonus:"));
      panel.add(overrideBonus);
        

        int result = JOptionPane.showConfirmDialog(this, request, "Add Monster", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) monsterSelector.getSelectedItem();
            Monster m = monsters.get(selected);
            if (m == null) return;

            try {
                int bonus = Integer.parseInt(overrideBonus.getText().trim());
                int roll = bonus + new Random().nextInt(20) + 1;
                UUID id = UUID.randomUUID();
                addInitiativeEntry(new InitiativeEntry(id, m.name, roll, m));
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(this, "Invalid bonus.");
            }
        }
    }
    
    public void importMonsterInit(ArrayList<Monster> monsts) {
    	for(Monster m : monsts) {
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
        initiativeListModel.clear();
        for (InitiativeEntry e : entries) {
            initiativeListModel.addElement(e);
        }
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
            initiativeList.setSelectedIndex(currentIndex);
            loadEntry(entries.get(currentIndex));
        } else {
            rightPane.removeAll();
            rightPane.revalidate();
            rightPane.repaint();
        }
    }
    
    public void removeInitiativeEntry(InitiativeEntry remove) {
        if (remove == null || remove.id.equals(PLAYER_ID)) return;

        InitiativeEntry tmpEntry = initiativeList.getSelectedValue();
        // Remove matching entry (case-sensitive)
        entries.removeIf(entry -> entry.equals(remove));

        // Re-sort and update the display
        entries.sort((a, b) -> Integer.compare(b.initiative, a.initiative));
        updateInitiativeList();

        SwingUtilities.invokeLater(()->{
        	if(currentIndex > -1) {
            	if(!remove.equals(tmpEntry))
            		for(int i = 0; i < initiativeListModel.size(); i++)
            			if(initiativeListModel.get(i).equals(tmpEntry))
            				currentIndex = i;
            	// Reset selection and right pane if current was removed
                if (currentIndex >= entries.size()) currentIndex = 0;

                if (!entries.isEmpty()) {
                    initiativeList.setSelectedIndex(currentIndex);
                    loadEntry(entries.get(currentIndex));
                    initiativeList.ensureIndexIsVisible(currentIndex);
                    selectionModel.setProgrammaticSelection(currentIndex);
                    initiativeList.repaint();
                } else {
                    rightPane.removeAll();
                    rightPane.revalidate();
                    rightPane.repaint();
                }
            }
        });
    }

    private void advanceTurn() {
//        if (entries.isEmpty()) return;
//        currentIndex = (currentIndex + 1) % entries.size();
//        initiativeList.setSelectedIndex(currentIndex);
//        initiativeList.getSelectionModel().
//        loadEntry(entries.get(currentIndex));
//        initiativeList.ensureIndexIsVisible(currentIndex);                // Scrolls to it, optional
//        initiativeList.repaint(); 
    	
    	if (entries.isEmpty()) return;
        currentIndex = (currentIndex + 1) % entries.size();

        // **use your helper instead of setSelectedIndex(…)**
        selectionModel.setProgrammaticSelection(currentIndex);

        loadEntry(entries.get(currentIndex));
    }

//    private void loadEntry(InitiativeEntry entry) {
//        rightPane.removeAll();
//        if (entry.monster != null) {
//            rightPane.add(new MonsterDispPane(entry.monster, data, gd), BorderLayout.CENTER);
//        } else {
//        	JLabel player = new JLabel("Player Loaded: " + entry.name, SwingConstants.CENTER);
//        	StyleContainer.SetFontHeader(player);
//            rightPane.add(player, BorderLayout.CENTER);
//        }
//        rightPane.revalidate();
//        rightPane.repaint();
//    }
    
    private void loadEntry(InitiativeEntry entry) {
        rightPane.removeAll();

        if (entry.monster != null) {
            rightPane.add(new MonsterDispPane(entry.monster, data, gd), BorderLayout.CENTER);
        } else {
        	if(data.isCampaignLoaded()) {
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
        	}else {
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
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                List<SavedEntry> saved = entries.stream()
                        .map(SavedEntry::new)
                        .collect(Collectors.toList());
                out.writeObject(saved);
                out.writeInt(currentIndex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
            }
        }
    }
    
    private void loadInitiativeState() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                List<SavedEntry> saved = (List<SavedEntry>) in.readObject();
                int index = in.readInt();

                entries.clear();
                for (SavedEntry s : saved) {
                    entries.add(new InitiativeEntry(s.id, s.name, s.initiative, s.monster));
                }

                currentIndex = index;
                updateInitiativeList();
                advanceTurn(); // Load active turn
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load: " + ex.getMessage());
            }
        }
    }

    
    // Stub AllTab methods
    public JTabbedPane GetTabs() { return null; }
    
    public void AddTab(Monster m) {
    	System.out.println("Adding: " + m.name);
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
			});
			btnFlow.add(removeMonst);
			tabs.addTab(m.name, monstDisp);
			tabsUI.setTabColor(tabs.indexOfTab(m.name), Color.ORANGE);
			tabs.setSelectedComponent(monstDisp);
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
			});
			btnFlow.add(removeSpell);
			tabs.setSelectedComponent(sPane);
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
			});
			btnFlow.add(removeRule);
			tabs.setSelectedComponent(rPane);
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
        UUID id;
    	String name;
        int initiative;
        Monster monster; // Serializable!
        

        public SavedEntry(InitiativeEntry entry) {
        	this.id = entry.id;
            this.name = entry.name;
            this.initiative = entry.initiative;
            this.monster = entry.monster;
        }
        
        public InitiativeEntry toEntry() {
            return new InitiativeEntry(id, name, initiative, monster);
        }
    }

    
    private static class InitiativeEntry {
    	final UUID id;
        String name;
        int initiative;
        Monster monster;

        InitiativeEntry(UUID id, String name, int initiative, Monster monster) {
            this.id = id;
        	this.name = name;
            this.initiative = initiative;
            this.monster = monster;
        }
        
        public String toString() {
        	return name + " (" + initiative + ")";
        }
        
        public boolean equals(InitiativeEntry e) {
        	return id.equals(e.id) && name.equals(e.name) &&
        			initiative == e.initiative && monster.equals(e.monster);
        }
    }
}


class ProgrammaticSelectionModel extends DefaultListSelectionModel {
    private boolean programmaticChange = false;
    private final JList<?> list;

    public ProgrammaticSelectionModel(JList<?> list) {
        this.list = list;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (programmaticChange) {
            super.setSelectionInterval(index0, index1);
            list.ensureIndexIsVisible(index0);
            list.repaint();
        }
    }

    /** Call this instead of setSelectedIndex(...) */
    public void setProgrammaticSelection(int index) {
        programmaticChange = true;
        setSelectionInterval(index, index);
        programmaticChange = false;
    }
}

