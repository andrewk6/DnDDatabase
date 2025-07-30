package gui.gui_helpers;

import builders.monster_builder.AttackInsertForm;
import data.DataChangeListener;
import data.DataContainer;
import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;
import data.campaign.Player;
import data.items.Armor;
import data.items.Gear;
import data.items.Item;
import data.items.MagicItem;
import data.items.Weapon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.util.stream.Collectors;

public class RichEditor extends RichEditorBase implements DataChangeListener{
    private final JPopupMenu suggestionPopup = new JPopupMenu();
    private final JList<String> suggestionList = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane(suggestionList);
    private final JTextPane editor;
    
    private DataContainer data;
    private Map<String, Rule> ruleMap;
    private Map<String, Spell> spellMap;
    private Map<String, Monster> monstMap;
    private Map<String, Item> itemMap;
    private Map<String, Feat> featMap;
    private Map<String, StyledDocument> insertMap;
    private Map<String, Player> playerMap;

    private final JWindow rulePreviewWindow = new JWindow();
    private final JTextPane rulePreviewPane = new JTextPane();
    private final JScrollPane ruleScrollPane = new JScrollPane(rulePreviewPane);
    
    private final Map<Integer, String> ruleOffsets = new HashMap<>();
    private final Map<Integer, String> spellOffsets = new HashMap<>();
    private final Map<Integer, String> monstOffsets = new HashMap<>();
    private final Map<Integer, String> featOffsets = new HashMap<>();
    private final Map<Integer, String> itemOffsets = new HashMap<>();
    private final Map<Integer, String> playerOffsets = new HashMap<>();

    private int atPosition = -1;
    private String currentPartial = "";

    private Style ruleStyle;
    private Style spellStyle;
    private Style monstStyle;
    private Style itemStyle;
    private Style playerStyle;
    
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
        StyleConstants.setForeground(playerStyle, Color.PINK);
        StyleConstants.setItalic(playerStyle, true);
        StyleConstants.setUnderline(playerStyle, true);

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
                    e.printStackTrace();
                    return false;
                }
            }
        });
        
        

        editor.getDocument().addDocumentListener(documentListener);
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

        editor.registerKeyboardAction(e -> triggerSuggestion(),
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_FOCUSED);
        
        editor.registerKeyboardAction(e -> insertAttack(), 
        		KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), 
        		JComponent.WHEN_FOCUSED);
        
        editor.registerKeyboardAction(e -> copyFunction(), 
        		KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), 
        		JComponent.WHEN_FOCUSED);

        
        rulePreviewPane.setEditable(false);
        rulePreviewPane.setContentType("text/rtf");
        rulePreviewWindow.getContentPane().add(ruleScrollPane);
        ruleScrollPane.setPreferredSize(new Dimension(300, 200));
        rulePreviewWindow.pack();

        editor.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.viewToModel(pt);
                String ruleName = ruleOffsets.get(pos);
                String spellName = spellOffsets.get(pos);
                String monstName = monstOffsets.get(pos);
//                System.out.println("Rule:" + ruleName +"\nSpell: " + spellName);
                if (ruleName != null) {
                	showRulePreview(ruleName, e.getLocationOnScreen());
                } else if(spellName != null) {
                	showSpellPreview(spellName, e.getLocationOnScreen());
                }else if(monstName != null){
                	showMonstPreview(monstName, e.getLocationOnScreen());
                }else {
                    rulePreviewWindow.setVisible(false);
                }
            }
        });

        editor.addMouseWheelListener(e -> {
            if (rulePreviewWindow.isVisible()) {
                JScrollBar verticalScrollBar = ruleScrollPane.getVerticalScrollBar();
                int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
                verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
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
            
            for(String key : playerMap.keySet()) {
            	if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
            		matches.add(key + " (player)");
            	}
            }

            Collections.sort(matches);
            if (!matches.isEmpty()) {
                updateSuggestions(matches, caretPos);
            } else {
                suggestionPopup.setVisible(false);
            }
        } catch (BadLocationException e) {
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
                
                if(data.isCampaignLoaded())
                	for(String key : playerMap.keySet()) {
                		if(key.toLowerCase().startsWith(currentPartial.toLowerCase())) {
                			matches.add(key + " (player)");
                		}
                }	

                Collections.sort(matches);
                if (!matches.isEmpty()) {
                    updateSuggestions(matches, caretPos);
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
                    updateSuggestions(matches, caretPos);
                }
            }
            
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    	
//    	int caretPos = editor.getCaretPosition();
//        try {
//            String text = editor.getText(0, caretPos);
//            
//            // Handle @ mentions
//            int lastAt = text.lastIndexOf("@");
//            if (lastAt >= 0 && (lastAt == 0 || !Character.isLetterOrDigit(text.charAt(lastAt - 1)))) {
//                atPosition = lastAt;
//                currentPartial = text.substring(lastAt + 1, caretPos);
//                List<String> matches = getMatches(currentPartial);
//                if (!matches.isEmpty()) {
//                    updateSuggestions(matches, caretPos);
//                    return;
//                }
//            }

    }

    private void updateSuggestions(List<String> suggestions, int caretEnd) {
        suggestionList.setListData(suggestions.toArray(new String[0]));
        suggestionList.setSelectedIndex(0);

        suggestionPopup.removeAll();
        scrollPane.setPreferredSize(new Dimension(200, 150));
        suggestionPopup.add(scrollPane);

        try {
            Rectangle caretCoords = editor.modelToView(caretEnd);
            suggestionPopup.show(editor, caretCoords.x, caretCoords.y + 20);
            suggestionList.requestFocusInWindow();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertSuggestion(String selected) {
    	System.out.println(replacements == null);
    	
        if (atPosition >= 0 && selected != null) {
            try {
                boolean isSpell = selected.endsWith(" (spell)");
                boolean isMonster = selected.endsWith(" (creature)");
                boolean isItem = selected.endsWith(" (item)");
                boolean isPlayer = selected.endsWith(" (player)");
                String plainName;
                if(isSpell)
                	plainName = selected.substring(0, selected.length() - 8);
                else if(isMonster)
                	 plainName = selected.substring(0, selected.length() -11);
                else if(isItem)
                	plainName = selected.substring(0, selected.length() - " (item)".length());
                else if(isPlayer)
                	plainName = selected.substring(0, selected.length() - " (player)".length());
                else
                	plainName =  selected;
                int caretPos = editor.getCaretPosition();
                StyledDocument doc = editor.getStyledDocument();
                doc.remove(atPosition, caretPos - atPosition);

                SimpleAttributeSet styleWithAttr;
                if(isSpell) {
                	styleWithAttr = new SimpleAttributeSet(spellStyle);
                	styleWithAttr.addAttribute("spellLink", plainName);
                }else if(isMonster) {
                	styleWithAttr = new SimpleAttributeSet(monstStyle);
                	styleWithAttr.addAttribute("monstLink", plainName);
                }else if(isItem) {
                	styleWithAttr = new SimpleAttributeSet(itemStyle);
                	styleWithAttr.addAttribute("itemLink", plainName);
                }else if(isPlayer) {
                	styleWithAttr = new SimpleAttributeSet(playerStyle);
                	styleWithAttr.addAttribute("playerLink", plainName);
                }else {
                	styleWithAttr = new SimpleAttributeSet(ruleStyle);
                	styleWithAttr.addAttribute("ruleLink", plainName);
                }
//                styleWithAttr.addAttribute(isSpell? "spellLink" : "ruleLink", plainName);
//                styleWithAttr.addAttribute("ruleLink", plainName);

                doc.insertString(atPosition, plainName, styleWithAttr);
                editor.setCaretPosition(atPosition + plainName.length());

                for (int i = atPosition; i < atPosition + plainName.length(); i++) {
                	if(isSpell)
                		spellOffsets.put(i, plainName);
                	else if(isMonster) {
                		monstOffsets.put(i, plainName);
                	}else if(isItem) {
                		itemOffsets.put(i, plainName);
                	}else if(isPlayer){
                		playerOffsets.put(i, plainName);
                	}else {
                		ruleOffsets.put(i, plainName);
                	}
                }
            } catch (BadLocationException e) {
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
    
    private void insertStyledHTMLLikeText(String htmlLikeText, StyledDocument doc) {
        int pos = doc.getLength();

        // Create base styles
        SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setBold(bold, true);

        SimpleAttributeSet italic = new SimpleAttributeSet();
        StyleConstants.setItalic(italic, true);

        SimpleAttributeSet boldItalic = new SimpleAttributeSet();
        StyleConstants.setBold(boldItalic, true);
        StyleConstants.setItalic(boldItalic, true);

        SimpleAttributeSet normal = new SimpleAttributeSet();

        // Pattern to match <b>, <i>, </b>, </i>, and text
        Pattern tagPattern = Pattern.compile("(<(/?)(b|i)>)|([^<>]+)");
        Matcher matcher = tagPattern.matcher(htmlLikeText);

        boolean inBold = false;
        boolean inItalic = false;

        try {
            while (matcher.find()) {
                if (matcher.group(4) != null) {
                    // Plain text content
                    String text = matcher.group(4);
                    AttributeSet attr;
                    if (inBold && inItalic) {
                        attr = boldItalic;
                    } else if (inBold) {
                        attr = bold;
                    } else if (inItalic) {
                        attr = italic;
                    } else {
                        attr = normal;
                    }
                    doc.insertString(pos, text, attr);
                    pos += text.length();
                } else {
                    // Tag
                    String tag = matcher.group(3);
                    boolean closing = "/".equals(matcher.group(2));
                    if ("b".equals(tag)) {
                        inBold = !closing;
                    } else if ("i".equals(tag)) {
                        inItalic = !closing;
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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
    
    private void showMonstPreview(String ruleName, Point screenLocation) {
//        Monster m = monstMap.get(ruleName);
//        if (m == null) return;
//        
//        rulePreviewPane.setText(m.name);
//        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
//        rulePreviewWindow.pack();
//        rulePreviewWindow.setVisible(true);
    	
    	System.out.println("Maybe display monster one day");
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
                        ex.printStackTrace();
                    }
                });
            }

        } catch (BadLocationException e) {
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
    }

    public void close() {
        rulePreviewWindow.dispose();
    }
    
    public void updateData() {
    	this.ruleMap = data.getRules();
        this.spellMap = data.getSpells();
        this.monstMap = data.getMonsters();
        this.insertMap = data.getInserts();
        this.itemMap = data.getItems();
        this.featMap = data.getFeats();
        this.playerMap = data.getParty();
    }

	@Override
	public void onMapUpdated() {
		updateData();
	}

	@Override
	public void onMapUpdated(int mapType) {
		switch(mapType) {
		case DataContainer.RULES: this.ruleMap = data.getRules(); break;
		case DataContainer.SPELLS: this.spellMap = data.getSpells(); break;
		case DataContainer.MONSTERS: this.monstMap = data.getMonsters(); break;
		case DataContainer.INSERTS: this.insertMap = data.getInserts(); break;
		case DataContainer.FEATS: this.featMap = data.getFeats(); break;
		case DataContainer.ITEMS: this.itemMap = data.getItems(); break;
		case DataContainer.CAMPAIGN: this.playerMap = data.getParty(); break;
		default: System.out.println("Invalid map type: " + mapType);
		}
		
	}
}

