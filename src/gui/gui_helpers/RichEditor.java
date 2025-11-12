package gui.gui_helpers;

import builders.monster_builder.AttackInsertForm;
import data.DataContainer;
import data.DataContainer.MapType;
import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;
import data.campaign.Player;
import data.hazards.Hazard;
import data.interfaces.DataChangeListener;
import data.interfaces.InsertString;
import data.items.Item;
import data.players.Background;
import data.players.Species;
import data.players.classes.DnDClass;
import data.siege_equipment.SiegeEquipment;
import data.vehicles.Vehicle;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.hazard.HazardPane;
import gui.monsters.MonsterDispPane;
import utils.ErrorLogger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("serial")
public class RichEditor extends RichEditorBase implements DataChangeListener{
    private final JPopupMenu suggestionPopup = new JPopupMenu();
    private final JList<String> suggestionList = new JList<>();
    private final JScrollPane scrollPane = CompFactory.wrapPanelInScroll(suggestionList);
    private final JTextPane editor;
    
    private DataContainer data;
    private Map<String, Rule> ruleMap;
    private Map<String, Spell> spellMap;
    private Map<String, Monster> monstMap;
    private Map<String, Item> itemMap;
    private Map<String, Feat> featMap;
    private Map<String, StyledDocument> insertMap;
    private Map<String, Player> playerMap;
    private Map<String, DnDClass> classMap;
    private Map<String, Species> speciesMap;
    private Map<String, Background> backgroundMap;
    private Map<String, Hazard> hazardMap;
    private Map<String, Vehicle> vehicleMap;
    private Map<String, SiegeEquipment> siegeMap;
//    private Map<String, Species> 

    private final JWindow rulePreviewWindow = new JWindow();
    private final JTextPane rulePreviewPane = new JTextPane();
    private final JScrollPane ruleScrollPane = CompFactory.wrapPanelInScroll(rulePreviewPane);
    
    private final JWindow specialPreviewWindow = new JWindow();
    private final JPanel specialPreviewPane = new JPanel();
    private final JScrollPane specialScrollPane = 
    		CompFactory.wrapPanelInScroll(specialPreviewPane, ScrollPolicy.BOTH);
    
    private final Map<Integer, String> ruleOffsets = new HashMap<>();
    private final Map<Integer, String> spellOffsets = new HashMap<>();
    private final Map<Integer, String> monstOffsets = new HashMap<>();
    private final Map<Integer, String> featOffsets = new HashMap<>();
    private final Map<Integer, String> itemOffsets = new HashMap<>();
    private final Map<Integer, String> playerOffsets = new HashMap<>();
    private final Map<Integer, String> classOffsets = new HashMap<>();
    private final Map<Integer, String> speciesOffsets = new HashMap<>();
    private final Map<Integer, String> backgroundOffsets = new HashMap<>();
    private final Map<Integer, String> hazardOffsets = new HashMap<>();

    private int atPosition = -1;
    private String currentPartial = "";
    private boolean showMatches = false;

    private Style ruleStyle;
    private Style spellStyle;
    private Style monstStyle;
    private Style itemStyle;
    private Style playerStyle;
    private Style featStyle;
    private Style classStyle;
    private Style speciesStyle;
    private Style backgroundStyle;
    private Style hazardStyle;
    
    private int ampPosition = -1;
//	private final List<String> ampSuggestions = Arrays.asList(
//		    "<b>(Recharge 5–6).</b>", 
//		    
//		    "<b>Spider Climb.</b> The NAME can climb difficult surfaces, "
//		    + "including along ceilings, without needing to make an ability check.", 
//		    
//		    "<b>Demonic Restoration.</b> If the NAME dies outside the Abyss, "
//		    + "its body dissolves into ichor, and it gains a new body instantly, "
//		    + "reviving with all its HIT somewhere in the Abyss.", 
//		    
//		    "<b>Magic Resistance.</b> The NAME has ADV on saving throws against spells and other magical effects.", 
//		    
//		    "<b>Amphibious.</b> The NAME can breathe air and water."
//		);
	
	public HashMap<String, String> replacements;
	
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setSize(new Dimension(800, 800));
			Container c = frm.getContentPane();
			
			c.setLayout(new BorderLayout());
			c.add(new RichEditor(data), BorderLayout.CENTER);
			frm.setVisible(true);
		});
	}

    public RichEditor(DataContainer data) {
        super();
       this.data = data;
       this.data.registerListener(this);
        updateData();
        editor = getEditorTextPane();
        ConstructorShared(data);
    }
    
    public RichEditor(DataContainer data, HashMap<String, String> reps) {
    	super();
    	this.data = data;
    	this.data.registerListener(this);
        updateData();
        editor = getEditorTextPane();
        replacements = reps;
        ConstructorShared(data);
    }
    
    public void ConstructorShared(DataContainer data) {
        StyledDocument doc = editor.getStyledDocument();

        ruleStyle = doc.addStyle("RuleStyle", null);
        StyleConstants.setForeground(ruleStyle, Color.BLUE);
        StyleConstants.setItalic(ruleStyle, true);
        StyleConstants.setUnderline(ruleStyle, true);

        spellStyle = doc.addStyle("SpellStyle", null);
        StyleConstants.setForeground(spellStyle, Color.RED);
        StyleConstants.setItalic(spellStyle, true);
        StyleConstants.setUnderline(spellStyle, true);
        
        monstStyle = doc.addStyle("MonsterStyle", null);
        StyleConstants.setForeground(monstStyle, Color.ORANGE);
        StyleConstants.setItalic(monstStyle, true);
        StyleConstants.setUnderline(monstStyle, true);
        
        itemStyle = doc.addStyle("ItemStyle", null);
        StyleConstants.setForeground(itemStyle, Color.GREEN);
        StyleConstants.setItalic(itemStyle, true);
        StyleConstants.setUnderline(itemStyle, true);
        
        playerStyle = doc.addStyle("PlayerStyle", null);
        StyleConstants.setForeground(playerStyle, Color.BLACK);
        StyleConstants.setItalic(playerStyle, true);
        StyleConstants.setUnderline(playerStyle, true);
        
        featStyle = doc.addStyle("FeatStyle", null);
        StyleConstants.setForeground(featStyle, Color.PINK);
        StyleConstants.setItalic(featStyle, true);
        StyleConstants.setUnderline(featStyle, true);
        
        classStyle = doc.addStyle("ClassStyle", null);
        StyleConstants.setForeground(classStyle, Color.MAGENTA);
        StyleConstants.setItalic(classStyle, true);
        StyleConstants.setUnderline(classStyle, true);
        
        speciesStyle = doc.addStyle("SpeciesStyle", null);
        StyleConstants.setForeground(speciesStyle, Color.MAGENTA);
        StyleConstants.setItalic(speciesStyle, true);
        StyleConstants.setUnderline(speciesStyle, true);
        
        backgroundStyle = doc.addStyle("BackgroundStyle", null);
        StyleConstants.setForeground(backgroundStyle, Color.MAGENTA);
        StyleConstants.setItalic(backgroundStyle, true);
        StyleConstants.setUnderline(backgroundStyle, true);
        
        hazardStyle = doc.addStyle("HazardStyle", null);
        StyleConstants.setForeground(hazardStyle, Color.WHITE);
        StyleConstants.setBackground(hazardStyle, new Color(150, 0, 0, 100));
        StyleConstants.setItalic(hazardStyle, true);
        StyleConstants.setUnderline(hazardStyle, true);

        editor.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) return false;

                try {
                    String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    int caretPos = editor.getCaretPosition();
                    editor.getDocument().insertString(caretPos, data, null);
                    return true;
                } catch (Exception e) {
                	ErrorLogger.log(e);
                    e.printStackTrace();
                    return false;
                }
            }
        });
        
        

        addDocumentListeners();
        editor.addKeyListener(keyAdapter);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        suggestionList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    insertSuggestion(suggestionList.getSelectedValue());
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionPopup.setVisible(false);
                    showMatches = false;
                    System.out.println("Escape pressed: " + showMatches);
                    editor.requestFocusInWindow();
                }
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    insertSuggestion(suggestionList.getSelectedValue());
                }
            }
        });

        editor.registerKeyboardAction(_ -> triggerSuggestion(),
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_FOCUSED);
        
        editor.registerKeyboardAction(_ -> insertAttack(), 
        		KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), 
        		JComponent.WHEN_FOCUSED);
        
        editor.registerKeyboardAction(_ -> copyFunction(), 
        		KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), 
        		JComponent.WHEN_FOCUSED);
        
        editor.registerKeyboardAction(_->{
        	showMatches = false;
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
        		JComponent.WHEN_IN_FOCUSED_WINDOW);

        
        rulePreviewPane.setEditable(false);
        rulePreviewPane.setContentType("text/rtf");
        rulePreviewWindow.getContentPane().add(ruleScrollPane);
        ruleScrollPane.setPreferredSize(new Dimension(300, 200));
        rulePreviewWindow.pack();
        
        specialPreviewPane.setLayout(new BorderLayout());
        specialPreviewWindow.getContentPane().setLayout(new BorderLayout());
        specialPreviewWindow.add(specialScrollPane, BorderLayout.CENTER);
        specialScrollPane.setPreferredSize(new Dimension(600, 600));
        specialPreviewWindow.pack();
        
        editor.addMouseMotionListener(new MouseMotionAdapter() {
            @SuppressWarnings("deprecation")
			@Override
            public void mouseMoved(MouseEvent e) {
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.viewToModel(pt);
                String ruleName = ruleOffsets.get(pos);
                String spellName = spellOffsets.get(pos);
                String monstName = monstOffsets.get(pos);
                String featName = featOffsets.get(pos);
                String className = classOffsets.get(pos);
                String speciesName = speciesOffsets.get(pos);
                String backgroundName = backgroundOffsets.get(pos);
                String hazardName = hazardOffsets.get(pos);
//                System.out.println("Rule:" + ruleName +"\nSpell: " + spellName);
                if (ruleName != null) {
                	showRulePreview(ruleName, e.getLocationOnScreen());
                } else if(spellName != null) {
                	showSpellPreview(spellName, e.getLocationOnScreen());
                }else if(featName != null){
                	showFeatPreview(featName, e.getLocationOnScreen());
                }else if(monstName != null){
                	showMonstPreview(monstName, e.getLocationOnScreen());
                }else if(className != null){
                	showClassPreview(className, e.getLocationOnScreen());
                }else if(speciesName != null){
                	showSpeciesPreview(speciesName, e.getLocationOnScreen());
               }else if(backgroundName != null) {
                	showBackgroundPreview(backgroundName, e.getLocationOnScreen());
                }else if(hazardName != null) {
                	showHazardPreview(hazardName, e.getLocationOnScreen());
                }else { 
                    rulePreviewWindow.setVisible(false);
                    specialPreviewWindow.setVisible(false);
                }
            }
        });

        editor.addMouseWheelListener(e -> {
            if (rulePreviewWindow.isVisible()) {
                JScrollBar verticalScrollBar = ruleScrollPane.getVerticalScrollBar();
                int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
                verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
                e.consume();
            } else if(specialPreviewWindow.isVisible()) {
            	JScrollBar verticalScrollBar = specialScrollPane.getVerticalScrollBar();
                JScrollBar horizontalScrollBar = specialScrollPane.getHorizontalScrollBar();
                if (e.isShiftDown() && horizontalScrollBar != null) {
                	 int scrollAmount = e.getUnitsToScroll() * horizontalScrollBar.getUnitIncrement();
                     horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmount);
                }else if (verticalScrollBar != null) {
                    int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
                    verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
                }
                e.consume();
            } else {
                Component parent = editor.getParent();
                while (parent != null && !(parent instanceof JScrollPane)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JScrollPane) {
                    JScrollBar verticalScrollBar = ((JScrollPane) parent).getVerticalScrollBar();
                    int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
                    verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
                    e.consume();
                }
            }
        });
    }
    
    private void insertAttack() {
    	SwingUtilities.invokeLater(()->{
    		AttackInsertForm aForm = new AttackInsertForm();
    		aForm.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        	aForm.setVisible(true);
        	System.out.println("Exited Dialog");
        	if(aForm.finished) {
    			try {
					DocumentHelper.insertStyledDocument(editor.getStyledDocument(), 
							aForm.getAttackString(), editor.getCaretPosition());
					setTextStyle();
					aForm.dispose();
				} catch (BadLocationException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
    		}
   
    	});    	
    }

    private JTextPane getEditorTextPane() {
        try {
            java.lang.reflect.Field field = RichEditorBase.class.getDeclaredField("editorTextPane");
            field.setAccessible(true);
            return (JTextPane) field.get(this);
        } catch (Exception e) {
        	ErrorLogger.log(e);
            throw new RuntimeException("Failed to access editorTextPane", e);
        }
    }

    private final DocumentListener documentListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { updatePartial(); }
        public void removeUpdate(DocumentEvent e) {
            clearStaleRuleOffsets();
            clearStaleSpellOffsets();
            clearStaleMonstOffsets();
            clearStaleItemOffsets();
            clearStalePlayerOffsets();
            clearStaleFeatOffsets();
            clearStaleClassOffsets();
            clearStaleSpeciesOffsets();
            clearStaleBackgroundOffsets();
            clearStaleHazardOffsets();
            SwingUtilities.invokeLater(() -> resetStyleAfterDeletion());
        }
        public void changedUpdate(DocumentEvent e) {}
    };

    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (suggestionPopup.isVisible()) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    suggestionList.requestFocusInWindow();
                    suggestionList.setSelectedIndex(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionPopup.setVisible(false);
                    showMatches = false;
                    System.out.println("Escape pressed: " + showMatches);
                }
            }
        }
    };
    
    private void copyFunction() {
    	String selectedText = editor.getSelectedText();
    	if (selectedText != null && !selectedText.isEmpty()) {
    	    StringSelection selection = new StringSelection(selectedText);
    	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    	}
    }

    private void updatePartial() {
        if (atPosition < 0 || atPosition >= editor.getDocument().getLength()) return;
        int caretPos = editor.getCaretPosition();
        if (caretPos <= atPosition) return;
        try {
            currentPartial = editor.getText(atPosition + 1, caretPos - atPosition - 1);
            List<String> matches = new ArrayList<>();

            for (String key : ruleMap.keySet()) {
                if (key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                    matches.add(key);
                }
            }
            for (String key : spellMap.keySet()) {
                if (key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                    matches.add(key + " (spell)");
                }
            }
            
            for (String key : monstMap.keySet()) {
                if (key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                    matches.add(key + " (creature)");
                }
            }
            
            for(String key : itemMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (item)");
            	}
            }
            if(data.isCampaignLoaded()) {
            	for(String key : playerMap.keySet()) {
            		if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            			matches.add(key + " (player)");
            		}
            	}
            }
            for(String key : featMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (feat)");
            	}
            }
            
            for(String key : classMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (class)");
            	}
            }
            
            for(String key : speciesMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (species)");
            	}
            }
            
            for(String key : backgroundMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (background)");
            	}
            }
            
            for(String key : hazardMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (hazard)");
            	}
            }
            
            for(String key : vehicleMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (vehicle)");
            	}
            }
            
            for(String key : siegeMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (siege equipment)");
            	}
            }

            Collections.sort(matches);
            if (!matches.isEmpty()) {
                updateSuggestions(matches, caretPos);
            } else {
                suggestionPopup.setVisible(false);
            }
        } catch (BadLocationException e) {
        	ErrorLogger.log(e);
            e.printStackTrace();
        }
    }

    private void triggerSuggestion() {
        int caretPos = editor.getCaretPosition();
        try {
            String text = editor.getText(0, caretPos);
            int lastAt = text.lastIndexOf("@");
            if (lastAt >= 0 && (lastAt == 0 || !Character.isLetterOrDigit(text.charAt(lastAt - 1)))) {
                atPosition = lastAt;
                currentPartial = text.substring(lastAt + 1, caretPos);
                List<String> matches = new ArrayList<>();

                for (String key : ruleMap.keySet()) {
                    if (key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                        matches.add(key);
                    }
                }
                for (String key : spellMap.keySet()) {
                    if (key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                        matches.add(key + " (spell)");
                    }
                }
                
                for(String key : monstMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (creature)");
                	}
                }
                
                for(String key : itemMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (item)");
                	}
                }
                
                for(String key : featMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (feat)");
                	}
                }
                
                for(String key : classMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (class)");
                	}
                }
                
                for(String key : speciesMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (species)");
                	}
                }
                
                for(String key : backgroundMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (background)");
                	}
                }
                
                for(String key : hazardMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (hazard)");
                	}
                }
                
                for(String key : vehicleMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (vehicle)");
                	}
                }
                
                for(String key : siegeMap.keySet()) {
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                		matches.add(key + " (siege equipment)");
                	}
                }
                
                if(data.isCampaignLoaded())
                	for(String key : playerMap.keySet()) {
                		if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                			matches.add(key + " (player)");
                		}
                }	

                Collections.sort(matches);
                if (!matches.isEmpty()) {
                	if(matches.size() == 1) {
                		insertSuggestion(matches.getFirst());
                	}else {
                		showMatches = true;
                        updateSuggestions(matches, caretPos);
                	}
                }
            }
            
            // Handle & suggestions
            int lastAmp = text.lastIndexOf("&");
            if (lastAmp >= 0 && (lastAmp == 0 || !Character.isLetterOrDigit(text.charAt(lastAmp - 1)))) {
                ampPosition = lastAmp;
                currentPartial = text.substring(lastAmp + 1, caretPos);
//                List<String> matches = ampSuggestions.stream()
//                    .filter(opt -> opt.toLowerCase().startsWith(currentPartial.toLowerCase()))
//                    .collect(Collectors.toList());
                List<String> matches = new ArrayList<String>();
                for(String key : insertMap.keySet())
                	if(key.toLowerCase().startsWith(currentPartial.toLowerCase()))
                		matches.add(key);
                Collections.sort(matches);
                if (!matches.isEmpty()) {
                	showMatches = true;
                    updateSuggestions(matches, caretPos);
                }
            }
            
            
        } catch (BadLocationException e) {
        	ErrorLogger.log(e);
            e.printStackTrace();
        }
    }

    private void updateSuggestions(List<String> suggestions, int caretEnd) {
    	if(showMatches) {
            suggestionList.setListData(suggestions.toArray(new String[0]));
            suggestionList.setSelectedIndex(0);

            suggestionPopup.removeAll();
            scrollPane.setPreferredSize(new Dimension(200, 150));
            suggestionPopup.add(scrollPane);

            try {
                @SuppressWarnings("deprecation")
				Rectangle caretCoords = editor.modelToView(caretEnd);
                suggestionPopup.show(editor, caretCoords.x, caretCoords.y + 20);
                suggestionList.requestFocusInWindow();
            } catch (BadLocationException e) {
            	ErrorLogger.log(e);
                e.printStackTrace();
            }
    	}
    }

    private void insertSuggestion(String selected) {
        if (atPosition >= 0 && selected != null) {
            try {
                boolean isSpell = selected.endsWith(" (spell)");
                boolean isMonster = selected.endsWith(" (creature)");
                boolean isItem = selected.endsWith(" (item)");
                boolean isPlayer = selected.endsWith(" (player)");
                boolean isFeat = selected.endsWith(" (feat)");
                boolean isClass = selected.endsWith(" (class)");
                boolean isSpecies = selected.endsWith(" (species)");
                boolean isBackground = selected.endsWith(" (background)");
                boolean isHazard = selected.endsWith(" (hazard)");
                boolean isVehicle = selected.endsWith(" (vehicle)");
                boolean isSiege = selected.endsWith(" (siege equipment)");
                String plainName;
                if(isSpell)
                	plainName = selected.substring(0, selected.length() - 8);
                else if(isMonster)
                	 plainName = selected.substring(0, selected.length() -11);
                else if(isItem)
                	plainName = selected.substring(0, selected.length() - " (item)".length());
                else if(isFeat)
                	plainName = selected.substring(0, selected.length() - " (feat)".length());
                else if(isPlayer)
                	plainName = selected.substring(0, selected.length() - " (player)".length());
                else if(isClass)
                	plainName = selected.substring(0, selected.length() - " (class)".length());
                else if(isSpecies)
                	plainName = selected.substring(0, selected.length() - " (species)".length());
                else if(isBackground)
                	plainName = selected.substring(0, selected.length() - " (background)".length());
                else if(isHazard)
                	plainName= selected.substring(0, selected.length() - " (hazard)".length());
                else if(isVehicle)
                	plainName= selected.substring(0, selected.length() - " (vehicle)".length());
                else if(isSiege)
                	plainName= selected.substring(0, selected.length() - " (siege equipment)".length());
                else
                	plainName =  selected;
                int caretPos = editor.getCaretPosition();
                StyledDocument doc = editor.getStyledDocument();
                doc.remove(atPosition, caretPos - atPosition);

                SimpleAttributeSet styleWithAttr;
                InsertString in = null;
                if(isSpell) {
                	styleWithAttr = new SimpleAttributeSet(spellStyle);
                	styleWithAttr.addAttribute("spellLink", plainName);
                }else if(isMonster) {
                	styleWithAttr = new SimpleAttributeSet(monstStyle);
                	styleWithAttr.addAttribute("monstLink", plainName);
                }else if(isItem || isVehicle || isSiege) {
                	styleWithAttr = new SimpleAttributeSet(itemStyle);
                	styleWithAttr.addAttribute("itemLink", plainName);
                }else if (isFeat){
                	styleWithAttr = new SimpleAttributeSet(featStyle);
                	styleWithAttr.addAttribute("featLink", plainName);
                }else if(isPlayer) {
                	styleWithAttr = new SimpleAttributeSet(playerStyle);
                	styleWithAttr.addAttribute("playerLink", plainName);
                }else if (isClass){
                	styleWithAttr = new SimpleAttributeSet(classStyle);
                	styleWithAttr.addAttribute("classLink", plainName);
                }else if (isSpecies){
                	styleWithAttr = new SimpleAttributeSet(speciesStyle);
                	styleWithAttr.addAttribute("speciesLink", plainName);
                }else if (isBackground){
                	styleWithAttr = new SimpleAttributeSet(backgroundStyle);
                	styleWithAttr.addAttribute("backgroundLink", plainName);
                }else if (isHazard){
                	styleWithAttr = new SimpleAttributeSet(hazardStyle);
                	styleWithAttr.addAttribute("hazardLink", plainName);
                }else {
                	in = data.getRules().get(plainName);
                	styleWithAttr = new SimpleAttributeSet(ruleStyle);
                	styleWithAttr.addAttribute("ruleLink", plainName);
                }
//                styleWithAttr.addAttribute(isSpell? "spellLink" : "ruleLink", plainName);
//                styleWithAttr.addAttribute("ruleLink", plainName);
                if(in != null) {
                	doc.insertString(atPosition, in.getInsert(), styleWithAttr);
                	editor.setCaretPosition(atPosition + in.getInsert().length());
                }
                else {
                	doc.insertString(atPosition, plainName, styleWithAttr);
                	editor.setCaretPosition(atPosition + plainName.length());
                }
                int iterLen = (in != null)? in.getInsert().length() : plainName.length();

                for (int i = atPosition; i < atPosition + iterLen; i++) {
                	if(isSpell)
                		spellOffsets.put(i, plainName);
                	else if(isMonster) {
                		monstOffsets.put(i, plainName);
                	}else if(isItem || isVehicle || isSiege) {
                		itemOffsets.put(i, plainName);
                	}else if(isFeat){
                		featOffsets.put(i, plainName);
                	}else if(isPlayer){
                		playerOffsets.put(i, plainName);
                	}else if (isClass) {
                		classOffsets.put(i, plainName);
                	}else if(isSpecies){
                		speciesOffsets.put(i, plainName);
                	}else if (isBackground){
                		backgroundOffsets.put(i, plainName);
                	}else if (isHazard){
                		hazardOffsets.put(i, plainName);
                	}else {
                		ruleOffsets.put(i, plainName);
                	}
                }
            } catch (BadLocationException e) {
            	ErrorLogger.log(e);
                e.printStackTrace();
            }
        }else if(ampPosition >= 0 && selected != null)
        {
        	int caretPos = editor.getCaretPosition();
            StyledDocument doc = editor.getStyledDocument();
            try {
				doc.remove(ampPosition, caretPos - ampPosition);
//				doc.insertString(ampPosition, selected, null);
				if(replacements == null)
					DocumentHelper.insertStyledDocument(doc, insertMap.get(selected), ampPosition);
				else
					DocumentHelper.insertWithReplacements(doc, insertMap.get(selected), ampPosition, replacements);
			} catch (BadLocationException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
        }
        suggestionPopup.setVisible(false);
        atPosition = -1;
        ampPosition = -1;
        currentPartial = "";
        editor.requestFocusInWindow();
        super.setTextStyle();
    }
    
    private void offsetBuilder(String plainName) {
    	
    }


    private void showRulePreview(String ruleName, Point screenLocation) {
        Rule rule = ruleMap.get(ruleName);
        if (rule == null) return;

        rulePreviewPane.setStyledDocument(rule.ruleDoc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    
    private void showSpellPreview(String ruleName, Point screenLocation) {
        Spell spell = spellMap.get(ruleName);
        if (spell == null) return;
        
        rulePreviewPane.setStyledDocument(spell.spellDoc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    private void showFeatPreview(String featName, Point screenLocation) {
    	Feat feat = featMap.get(featName);
        if (feat == null) return;
        
        rulePreviewPane.setStyledDocument(feat.desc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    private void showClassPreview(String className, Point screenLocation) {
        DnDClass classs = classMap.get(className);
        if (classs == null) return;

        rulePreviewPane.setStyledDocument(classs.desc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    private void showSpeciesPreview(String speciesName, Point screenLocation) {
    	Species s = speciesMap.get(speciesName);
        if (s == null) return;

        rulePreviewPane.setStyledDocument(s.desc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    private void showMonstPreview(String ruleName, Point screenLocation) {
    	specialPreviewPane.removeAll();
    	specialPreviewPane.add(new MonsterDispPane(data, ruleName, null), BorderLayout.CENTER);
    	specialPreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
    	specialPreviewWindow.pack();
    	specialPreviewWindow.setVisible(true);
    }
    
    private void showBackgroundPreview(String backName, Point screenLocation) {
    	Background b = backgroundMap.get(backName);
    	if(b == null) return;
    	
    	rulePreviewPane.setStyledDocument(b.desc);
    	rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }
    
    private void showHazardPreview(String hazardName, Point screenLocation) {
    	specialPreviewPane.removeAll();
    	specialPreviewPane.add(new HazardPane(data.getHazards().get(hazardName), data, null), BorderLayout.CENTER);
    	specialPreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
    	specialPreviewWindow.pack();
    	specialPreviewWindow.setVisible(true);
    }

    private void resetStyleAfterDeletion() {
        int caretPos = editor.getCaretPosition();
        if (caretPos >= 0 && caretPos <= editor.getDocument().getLength()) {
            StyledDocument doc = editor.getStyledDocument();
            SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
            doc.setCharacterAttributes(caretPos, 1, defaultStyle, true);
        }
    }

    private void clearStaleRuleOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        ruleOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("ruleLink") == null;
        });
    }
    
    private void clearStaleSpellOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        spellOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("spellLink") == null;
        });
    }
    
    private void clearStaleMonstOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        monstOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("monstLink") == null;
        });
    }
    
    private void clearStaleItemOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        itemOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("itemLink") == null;
        });
    }
    
    private void clearStalePlayerOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        playerOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("playerLink") == null;
        });
    }
    private void clearStaleFeatOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        featOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("featLink") == null;
        });
    }
    
    private void clearStaleClassOffsets() {
        StyledDocument doc = editor.getStyledDocument();
        classOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("classLink") == null;
        });
    }
    
    private void clearStaleSpeciesOffsets() {
    	StyledDocument doc = editor.getStyledDocument();
        speciesOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("speciesLink") == null;
        });
    }
    
    private void clearStaleBackgroundOffsets() {
    	StyledDocument doc = editor.getStyledDocument();
        backgroundOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("backgroundLink") == null;
        });
    }   
    private void clearStaleHazardOffsets() {
    	StyledDocument doc = editor.getStyledDocument();
        hazardOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("hazardLink") == null;
        });
    } 
    
    private void checkForSlash() {
        int caretPos = editor.getCaretPosition();
        try {
            String text = editor.getText(0, caretPos);
            int commandPos = text.lastIndexOf("/m");
            boolean isMelee = true;

            if (commandPos == -1) {
                commandPos = text.lastIndexOf("/r");
                isMelee = false;
            }

            if (commandPos != -1) {
                final boolean finalIsMelee = isMelee;
                final int insertPos = commandPos;

                SwingUtilities.invokeLater(() -> {
                    try {
                        StyledDocument doc = editor.getStyledDocument();
                        doc.remove(insertPos, 2);
                        if (finalIsMelee) {
                            insertMeleeAttack(insertPos);
                        } else {
                            insertRangedAttack(insertPos);
                        }
                    } catch (BadLocationException ex) {
                    	ErrorLogger.log(ex);
                        ex.printStackTrace();
                    }
                });
            }

        } catch (BadLocationException e) {
        	ErrorLogger.log(e);
            e.printStackTrace();
        }
    }

    
    
    private void insertMeleeAttack(int insertPos) throws BadLocationException {
        StyledDocument doc = editor.getStyledDocument();

        insertStyledText(doc, insertPos, "MELEE NAME.", true, false);
        insertStyledText(doc, insertPos + "MELEE NAME.".length(), " ", false, false);
        insertStyledText(doc, insertPos + "MELEE NAME. ".length(), "Melee Attack Roll:", false, true);
        insertStyledText(doc, insertPos + "MELEE NAME. Melee Attack Roll:".length(), " +X, reach Xft. ", false, false);
        insertStyledText(doc, insertPos + "MELEE NAME. Melee Attack Roll: +X, reach Xft. ".length(), "Hit:", false, true);
        insertStyledText(doc, insertPos + "MELEE NAME. Melee Attack Roll: +X, reach Xft. Hit:".length(), " X (XdX + X) TYPE damage.", false, false);
    }

    private void insertRangedAttack(int insertPos) throws BadLocationException {
        StyledDocument doc = editor.getStyledDocument();

        insertStyledText(doc, insertPos, "RNG NAME.", true, false);
        insertStyledText(doc, insertPos + "RNG NAME.".length(), " ", false, false);
        insertStyledText(doc, insertPos + "RNG NAME. ".length(), "Ranged Attack Roll:", false, true);
        insertStyledText(doc, insertPos + "RNG NAME. Ranged Attack Roll:".length(), " +X, reach Xft. ", false, false);
        insertStyledText(doc, insertPos + "RNG NAME. Ranged Attack Roll: +X, reach Xft. ".length(), "Hit:", false, true);
        insertStyledText(doc, insertPos + "RNG NAME. Ranged Attack Roll: +X, reach Xft. Hit:".length(), " X (XdX + X) TYPE damage.", false, false);
    }
    
    private void insertStyledText(StyledDocument doc, int pos, String text, boolean bold, boolean italic) throws BadLocationException {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, bold);
        StyleConstants.setItalic(attr, italic);
        doc.insertString(pos, text, attr);
    }

    public String getText() {
        return editor.getText();
    }

    public StyledDocument getStyledDocument() {
        return editor.getStyledDocument();
    }
    
    public void LoadDocument(StyledDocument doc) {
    	this.editor.setStyledDocument(doc);
    	RebuildOffsets();
    	addDocumentListeners();
    }
    private void RebuildOffsets() {
    	StyledDocument doc = getStyledDocument();
    	int length = doc.getLength();
    	for (int i = 0; i < length; i++) {
    	    Element elem = doc.getCharacterElement(i);
    	    AttributeSet attr = elem.getAttributes();
    	    
            String ruleName = (String) attr.getAttribute("ruleLink");
            String spellName = (String) attr.getAttribute("spellLink");
            String monsterName = (String) attr.getAttribute("monstLink");
            String itemName = (String) attr.getAttribute("itemLink");
            String featName = (String) attr.getAttribute("featLink");
            String className = (String) attr.getAttribute("classLink");
            String speciesName = (String)attr.getAttribute("speciesLink");
            String backgroundName = (String)attr.getAttribute("backgroundLink");
            
            if(ruleName != null)
            	ruleOffsets.put(i, ruleName);
            else if(spellName != null)
            	spellOffsets.put(i, spellName);
            else if(monsterName != null)
            	monstOffsets.put(i, monsterName);
            else if(itemName != null)
            	itemOffsets.put(i, itemName);
            else if(featName != null)
            	featOffsets.put(i, featName);
            else if(className != null)
            	classOffsets.put(i, className);
            else if(speciesName != null)
            	speciesOffsets.put(i, speciesName);
            else if(backgroundName != null)
            	backgroundOffsets.put(i, backgroundName);
    	}
    }

    public void close() {
        rulePreviewWindow.dispose();
        specialPreviewWindow.dispose();
    }
    
    /*
     * editor.getDocument().addDocumentListener(documentListener);
     */
    
    private void addDocumentListeners() {
    	editor.getDocument().addDocumentListener(documentListener);
    	
    	editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> checkForSlash());
            }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
    }
    
    public void updateData() {
    	this.ruleMap = data.getRules();
        this.spellMap = data.getSpells();
        this.monstMap = data.getMonsters();
        this.insertMap = data.getInserts();
        this.itemMap = data.getItems();
        this.vehicleMap = data.getVehicles();
        this.siegeMap = data.getSiegeEquipment();
        this.featMap = data.getFeats();
        this.playerMap = data.getParty();
        this.classMap = data.getClasses();
        this.speciesMap = data.getSpecies();
        this.backgroundMap = data.getBackgrounds();
        this.hazardMap = data.getHazards();
    }

	@Override
	public void onMapUpdated() {
		updateData();
	}

	@Override
	public void onMapUpdated(MapType mapType) {
		switch(mapType) {
		case MapType.RULES: this.ruleMap = data.getRules(); break;
		case MapType.SPELLS: this.spellMap = data.getSpells(); break;
		case MapType.MONSTERS: this.monstMap = data.getMonsters(); break;
		case MapType.INSERTS: this.insertMap = data.getInserts(); break;
		case MapType.FEATS: this.featMap = data.getFeats(); break;
		case MapType.ITEMS: this.itemMap = data.getItems(); break;
		case MapType.VEHICLES: this.vehicleMap = data.getVehicles(); break;
		case MapType.SIEGEEQUIP: this.siegeMap = data.getSiegeEquipment(); break;
		case MapType.CAMPAIGN: this.playerMap = data.getParty(); break;
		case MapType.CLASSES: this.classMap = data.getClasses(); break;
		case MapType.SPECIES: this.speciesMap = data.getSpecies(); break;
		case MapType.BACKGROUNDS: this.backgroundMap = data.getBackgrounds(); break;
		case MapType.BASTION_ROOMS: break;
		case MapType.HAZARDS: this.hazardMap = data.getHazards(); break;
		default: System.out.println("Invalid map type: " + mapType);
		}
		
	}
	
	public void removeNotify() {
		super.removeNotify();
		data.deregisterListener(this);
		close();
	}
}

