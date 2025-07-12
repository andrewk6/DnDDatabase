package gui.gui_helpers.structures;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorTabbedPaneUI extends BasicTabbedPaneUI {

    private final Map<Integer, Color> tabColors = new HashMap<>();

    public void setTabColor(int index, Color color) {
        tabColors.put(index, color);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g;
        Color color = tabColors.getOrDefault(tabIndex, tabPane.getBackground());

        if (isSelected) {
            color = color.darker(); // Optional: darker when selected
        }

        g2.setColor(color);
        g2.fillRect(x, y, w, h);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                       Rectangle[] rects, int tabIndex,
                                       Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Optional: Disable focus indicator if undesired
    }
}