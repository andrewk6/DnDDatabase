package gui.item_panels;

import data.DataContainer;
import data.items.Item;
import data.items.ToolSet;
import gui.gui_helpers.structures.StyleContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ToolsPanel extends JPanel {

    public ToolsPanel(DataContainer dataContainer) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Main content panel inside scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<String> toolKeys = dataContainer.getToolKeysSorted();
        Map<String, Item> allItems = dataContainer.getItems();

        for (String key : toolKeys) {
            Item item = allItems.get(key);
            if (!(item instanceof ToolSet tool)) continue;

            contentPanel.add(createToolPanel(tool));
            contentPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createToolPanel(ToolSet tool) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(8, 8, 8, 8)
        ));

        // Tool name
        JLabel nameLabel = new JLabel(tool.name);
        StyleContainer.SetFontHeader(nameLabel);

        // Ability line
        String abilityText = (tool.abil != null) ? "Ability: " + tool.abil.name() : "Ability: —";
        JLabel abilLabel = new JLabel(abilityText);
        StyleContainer.SetFontMain(abilLabel);
        abilLabel.setForeground(Color.DARK_GRAY);

        // Cost/weight
        JLabel costWeightLabel = new JLabel("Cost: " + formatCost(tool.costs) + "    Weight: " + formatWeight(tool.weight));
        StyleContainer.SetFontMain(costWeightLabel);
        costWeightLabel.setForeground(Color.DARK_GRAY);

        // Description area
        JTextArea descArea = new JTextArea();
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        StyleContainer.SetFontMain(descArea);
        descArea.setOpaque(false);

        StringBuilder descBuilder = new StringBuilder();
        if (tool.utilize != null && !tool.utilize.isBlank()) {
            descBuilder.append("Utilize: ").append(tool.utilize.trim()).append("\n\n");
        }
        if (tool.craft != null && !tool.craft.isBlank()) {
            descBuilder.append("Craft: ").append(tool.craft.trim());
        }
        descArea.setText(descBuilder.toString());

        // Vertical stack for label + ability + cost/weight + description
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(abilLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(costWeightLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(descArea);

        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }

    private String formatCost(int[] costs) {
        String[] unitNames = {" cp", " sp", " ep", " gp", " pp"};
        StringBuilder sb = new StringBuilder();

        for (int i = costs.length - 1; i >= 0; i--) {
            if (costs[i] > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(costs[i]).append(unitNames[i]);
            }
        }

        return sb.length() > 0 ? sb.toString() : "—";
    }

    private String formatWeight(int weight) {
        return weight > 0 ? weight + " lb." : "—";
    }
}
