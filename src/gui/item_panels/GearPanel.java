package gui.item_panels;

import data.DataContainer;
import data.items.Item;
import data.items.Poison;
import data.items.Gear;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class GearPanel extends JPanel {

    public final JPanel contentPanel = new JPanel();
    private final List<Gear> allGearList = new ArrayList<>();
    private final Map<String, JPanel> lblMap = new HashMap<String, JPanel>(); 

    public GearPanel(DataContainer dataContainer) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Search bar
        JTextField searchField = new JTextField();
        StyleContainer.SetFontMain(searchField);
        searchField.setToolTipText("Filter by gear name...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        searchField.setPreferredSize(new Dimension(200, 28));

        // Filter logic on typing
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterGear(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterGear(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterGear(searchField.getText()); }
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
//        searchPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK));
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        add(searchPanel, BorderLayout.NORTH);

        // Gear display panel
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<String> gearKeys = dataContainer.getGearKeysSorted();
        Map<String, Item> allItems = dataContainer.getItems();

        for (String key : gearKeys) {
            Item item = allItems.get(key);
            if (item instanceof Gear gear) {
                allGearList.add(gear);
            }
        }

        populateContentPanel(allGearList);

        JScrollPane scrollPane = CompFactory.wrapPanelInScroll(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private void filterGear(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<Gear> filtered = allGearList.stream()
            .filter(g -> g.name.toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());

        populateContentPanel(filtered);
    }

    private void populateContentPanel(List<Gear> gearList) {
        contentPanel.removeAll();
        for (Gear gear : gearList) {
            contentPanel.add(createGearPanel(gear));
            contentPanel.add(Box.createVerticalStrut(15));
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createGearPanel(Gear gear) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(8, 8, 8, 8)
        ));

        // Name with cost and weight
        JPanel headPane = new JPanel();
        headPane.setLayout(new BorderLayout());
        
        String fullName = gear.name + " (" + formatCost(gear.costs) + ", " + formatWeight(gear.weight) + ")";
        JLabel nameLabel = new JLabel(fullName);
        StyleContainer.SetFontHeader(nameLabel);
        headPane.add(nameLabel, BorderLayout.CENTER);
        lblMap.put(gear.name, panel);
        
        if(gear instanceof Poison p) {
        	JPanel pPane = CompFactory.createDescriptionPane("Poison Type:", p.poisonType.toString());
        	headPane.add(pPane, BorderLayout.SOUTH);
        }

        JTextArea descArea = new JTextArea();
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        StyleContainer.SetFontMain(descArea);

        descArea.setText(gear.description != null && !gear.description.isBlank()
            ? gear.description.trim()
            : "—");

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(descArea);

        panel.add(headPane, BorderLayout.NORTH);
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
    
    public void scrollToGear(String gearName) {
        JPanel gearPanel = lblMap.get(gearName);
        if (gearPanel == null) return;

        SwingUtilities.invokeLater(() -> {
            JViewport viewport = (JViewport) contentPanel.getParent();
            Rectangle rect = SwingUtilities.convertRectangle(gearPanel.getParent(), gearPanel.getBounds(), contentPanel);

            int viewHeight = viewport.getExtentSize().height;
            int y = rect.y + (rect.height / 2) - (viewHeight / 2);
            y = Math.max(0, Math.min(y, contentPanel.getHeight() - viewHeight));

            viewport.setViewPosition(new Point(0, y));
        });
    }
}

