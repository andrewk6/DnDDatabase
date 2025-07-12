/*
package builders.spell_builder;

import builders.RichEditorBase;
import data.Rule;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RuleRichEditor extends RichEditorBase {
    private final JPopupMenu suggestionPopup = new JPopupMenu();
    private final JList<String> suggestionList = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane(suggestionList);
    private final JTextPane editor;
    private final Map<String, Rule> ruleMap;
    
    private final JWindow rulePreviewWindow = new JWindow();
    private final JTextPane rulePreviewPane = new JTextPane();
    private final JScrollPane ruleScrollPane = new JScrollPane(rulePreviewPane);
    private final Map<Integer, String> ruleOffsets = new HashMap<>();
    
    
    private int atPosition = -1;
    private String currentPartial = "";
    
    private Style ruleStyle;

    public RuleRichEditor(Map<String, Rule> ruleMap) {
        super();
        this.ruleMap = ruleMap;

        editor = getEditorTextPane();
        StyledDocument doc = editor.getStyledDocument();
        
        ruleStyle = doc.addStyle("RuleStyle", null);
        StyleConstants.setForeground(ruleStyle, Color.BLUE);
        StyleConstants.setItalic(ruleStyle, true);
        StyleConstants.setUnderline(ruleStyle, true);
        
        
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
        
        rulePreviewPane.setEditable(false);
        rulePreviewPane.setContentType("text/rtf"); // Optional: if you store RTF
        rulePreviewWindow.getContentPane().add(ruleScrollPane);
        ruleScrollPane.setPreferredSize(new Dimension(300, 200));
        rulePreviewWindow.pack();
//        rulePreviewWindow.addMouseWheelListener(e -> {
//            JScrollBar vertical = ruleScrollPane.getVerticalScrollBar();
//            int amount = e.getUnitsToScroll() * vertical.getUnitIncrement();
//            vertical.setValue(vertical.getValue() + amount);
//            e.consume(); // prevent event from going to parent
//        });
        
        editor.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.viewToModel(pt);
                String ruleName = ruleOffsets.get(pos);
                if (ruleName != null) {
                    showRulePreview(ruleName, e.getLocationOnScreen());
                } else {
                    rulePreviewWindow.setVisible(false);
                }
            }
        });
        
//        editor.addMouseWheelListener(e -> {
//            if (rulePreviewWindow.isVisible() && rulePreviewWindow.getBounds().contains(MouseInfo.getPointerInfo().getLocation())) {
//                ruleScrollPane.dispatchEvent(SwingUtilities.convertMouseEvent(editor, e, ruleScrollPane));
//                e.consume(); // prevent scroll from affecting the main editor
//                System.out.println("Consume");
//            }
//        });
        
        editor.addMouseWheelListener(e -> {
            if (rulePreviewWindow.isVisible()) {
                JScrollBar verticalScrollBar = ruleScrollPane.getVerticalScrollBar();
                int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
                verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
                e.consume();
            } else {
                // Scroll the editor pane's scroll pane normally
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
        public void removeUpdate(DocumentEvent e) { updatePartial(); }
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

    private void updatePartial() {
        if (atPosition < 0 || atPosition >= editor.getDocument().getLength()) return;
        int caretPos = editor.getCaretPosition();
        if (caretPos <= atPosition) return;
        try {
            currentPartial = editor.getText(atPosition + 1, caretPos - atPosition - 1);
            List<String> matches = ruleMap.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith(currentPartial.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
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
                List<String> matches = ruleMap.keySet().stream()
                        .filter(k -> k.toLowerCase().startsWith(currentPartial.toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
                if (!matches.isEmpty()) {
                    updateSuggestions(matches, caretPos);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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

    private void insertSuggestion(String selectedRule) {
//    	if (atPosition >= 0 && selectedRule != null) {
//            try {
//                int caretPos = editor.getCaretPosition();
//                StyledDocument doc = editor.getStyledDocument();
//
//                // Remove the @mention text
//                doc.remove(atPosition, caretPos - atPosition);
//
//                // Define style
//
//                // Insert styled rule name
//                doc.insertString(atPosition, selectedRule, ruleStyle);
//
//                // Move caret after inserted text
//                editor.setCaretPosition(atPosition + selectedRule.length());
//
//                // Track offset range for hover detection
//                for (int i = atPosition; i < atPosition + selectedRule.length(); i++) {
//                    ruleOffsets.put(i, selectedRule);
//                }
//
//            } catch (BadLocationException e) {
//                e.printStackTrace();
//            }
//        }
//
//        suggestionPopup.setVisible(false);
//        atPosition = -1;
//        currentPartial = "";
//        editor.requestFocusInWindow();
//    
//    	    super.setTextStyle();
    	
    	if (atPosition >= 0 && selectedRule != null) {
    	    try {
    	        int caretPos = editor.getCaretPosition();
    	        StyledDocument doc = editor.getStyledDocument();

    	        // Remove the @mention text
    	        doc.remove(atPosition, caretPos - atPosition);

    	        // Create new style for the rule link
    	        SimpleAttributeSet ruleStyleWithAttr = new SimpleAttributeSet(ruleStyle);
    	        ruleStyleWithAttr.addAttribute("ruleLink", selectedRule);  // <-- Custom attribute added here

    	        // Insert styled rule name with attribute
    	        doc.insertString(atPosition, selectedRule, ruleStyleWithAttr);

    	        // Move caret after inserted text
    	        editor.setCaretPosition(atPosition + selectedRule.length());

    	        // Track offset range for hover detection
    	        for (int i = atPosition; i < atPosition + selectedRule.length(); i++) {
    	            ruleOffsets.put(i, selectedRule);
    	        }

    	    } catch (BadLocationException e) {
    	        e.printStackTrace();
    	    }
    	}

    	suggestionPopup.setVisible(false);
    	atPosition = -1;
    	currentPartial = "";
    	editor.requestFocusInWindow();

    	super.setTextStyle();

    }
    	
    private void showRulePreview(String ruleName, Point screenLocation) {
        Rule rule = ruleMap.get(ruleName);
        if (rule == null) return;

        rulePreviewPane.setStyledDocument(rule.ruleDoc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
    }    	
    
    private void resetStyleAfterDeletion() {
        int caretPos = editor.getCaretPosition();
        if (caretPos >= 0 && caretPos < editor.getDocument().getLength()) {
            StyledDocument doc = editor.getStyledDocument();
            SimpleAttributeSet defaultStyle = new SimpleAttributeSet(); // empty = normal style
            doc.setCharacterAttributes(caretPos, 1, defaultStyle, true);
        }
    }
}
*/


package builders.spell_builder;

import data.Rule;
import gui.gui_helpers.RichEditorBase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RuleRichEditor extends RichEditorBase {
    private final JPopupMenu suggestionPopup = new JPopupMenu();
    private final JList<String> suggestionList = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane(suggestionList);
    private final JTextPane editor;
    private final Map<String, Rule> ruleMap;

    private final JWindow rulePreviewWindow = new JWindow();
    private final JTextPane rulePreviewPane = new JTextPane();
    private final JScrollPane ruleScrollPane = new JScrollPane(rulePreviewPane);
    private final Map<Integer, String> ruleOffsets = new HashMap<>();

    private int atPosition = -1;
    private String currentPartial = "";

    private Style ruleStyle;

    public RuleRichEditor(Map<String, Rule> ruleMap) {
        super();
        this.ruleMap = ruleMap;

        editor = getEditorTextPane();
        StyledDocument doc = editor.getStyledDocument();

        ruleStyle = doc.addStyle("RuleStyle", null);
        StyleConstants.setForeground(ruleStyle, Color.BLUE);
        StyleConstants.setItalic(ruleStyle, true);
        StyleConstants.setUnderline(ruleStyle, true);

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
                if (ruleName != null) {
                    showRulePreview(ruleName, e.getLocationOnScreen());
                } else {
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

    private void updatePartial() {
        if (atPosition < 0 || atPosition >= editor.getDocument().getLength()) return;
        int caretPos = editor.getCaretPosition();
        if (caretPos <= atPosition) return;
        try {
            currentPartial = editor.getText(atPosition + 1, caretPos - atPosition - 1);
            List<String> matches = ruleMap.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith(currentPartial.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
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
                List<String> matches = ruleMap.keySet().stream()
                        .filter(k -> k.toLowerCase().startsWith(currentPartial.toLowerCase()))
                        .sorted()
                        .collect(Collectors.toList());
                if (!matches.isEmpty()) {
                    updateSuggestions(matches, caretPos);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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

    private void insertSuggestion(String selectedRule) {
        if (atPosition >= 0 && selectedRule != null) {
            try {
                int caretPos = editor.getCaretPosition();
                StyledDocument doc = editor.getStyledDocument();

                doc.remove(atPosition, caretPos - atPosition);

                SimpleAttributeSet ruleStyleWithAttr = new SimpleAttributeSet(ruleStyle);
                ruleStyleWithAttr.addAttribute("ruleLink", selectedRule);

                doc.insertString(atPosition, selectedRule, ruleStyleWithAttr);
                editor.setCaretPosition(atPosition + selectedRule.length());

                for (int i = atPosition; i < atPosition + selectedRule.length(); i++) {
                    ruleOffsets.put(i, selectedRule);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        suggestionPopup.setVisible(false);
        atPosition = -1;
        currentPartial = "";
        editor.requestFocusInWindow();
        super.setTextStyle();
    }

    private void showRulePreview(String ruleName, Point screenLocation) {
        Rule rule = ruleMap.get(ruleName);
        if (rule == null) return;

        rulePreviewPane.setStyledDocument(rule.ruleDoc);
        rulePreviewWindow.setLocation(screenLocation.x + 15, screenLocation.y + 15);
        rulePreviewWindow.pack();
        rulePreviewWindow.setVisible(true);
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
        // Remove ruleOffsets that no longer point to matching styled text
        StyledDocument doc = editor.getStyledDocument();
        ruleOffsets.entrySet().removeIf(entry -> {
            int pos = entry.getKey();
            if (pos >= doc.getLength()) return true;
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attrs = elem.getAttributes();
            return attrs.getAttribute("ruleLink") == null;
        });
    }
    
    public String getText() {
    	return editor.getText();
    }
    
    public StyledDocument getStyledDocument() {
    	return editor.getStyledDocument();
    }

	public void close() {
		rulePreviewWindow.dispose();
	}	
}
