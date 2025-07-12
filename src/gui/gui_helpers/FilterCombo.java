package gui.gui_helpers;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import gui.gui_helpers.structures.StyleContainer;

import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class FilterCombo extends JComboBox<String> {
    private final List<String> originalItems = new ArrayList<>();
    private boolean isAdjusting = false;
    private int highlightedIndex = -1;

    public FilterCombo(List<String> items) {
        super(new DefaultComboBoxModel<>());
        setEditable(true);
        originalItems.addAll(items);
        reloadModel(originalItems);
        setSelectedItem(null);
        StyleContainer.SetFontHeader(this);

        JTextComponent editor = (JTextComponent) getEditor().getEditorComponent();

        // Override key actions instead of using KeyListener
        InputMap im = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = editor.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "navigateDown");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "navigateUp");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmSelection");

        am.put("navigateDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int size = getModel().getSize();
                if (size == 0) return;
                highlightedIndex = Math.min(highlightedIndex + 1, size - 1);
                setSelectedIndex(highlightedIndex);
                setPopupVisible(true);
            }
        });

        am.put("navigateUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int size = getModel().getSize();
                if (size == 0) return;
                highlightedIndex = Math.max(highlightedIndex - 1, 0);
                setSelectedIndex(highlightedIndex);
                setPopupVisible(true);
            }
        });

        am.put("confirmSelection", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int size = getModel().getSize();
                if (highlightedIndex >= 0 && highlightedIndex < size) {
                    setSelectedIndex(highlightedIndex);
                    editor.setText(getItemAt(highlightedIndex));
                }
                hidePopup();
            }
        });

        // Filtering on normal key release
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (isNavigationKey(e)) return;
                if (isAdjusting) return;

                SwingUtilities.invokeLater(() -> {
                    String text = editor.getText();
                    List<String> filtered = originalItems.stream()
                            .filter(item -> item.toLowerCase().contains(text.toLowerCase()))
                            .collect(Collectors.toList());

                    isAdjusting = true;
                    reloadModel(filtered);

                    highlightedIndex = -1;
                    setPopupVisible(true);
                    editor.setText(text);
                    editor.setCaretPosition(text.length());
                    isAdjusting = false;
                });
            }
        });
    }

    private boolean isNavigationKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN;
    }

    private void reloadModel(List<String> items) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
        model.removeAllElements();
        for (String item : items) {
            model.addElement(item);
        }
    }
}

