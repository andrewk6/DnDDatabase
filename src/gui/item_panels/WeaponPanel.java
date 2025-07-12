package gui.item_panels;

import data.DataContainer;
import data.items.Item;
import data.items.Weapon;
import data.items.Weapon.WeaponMastery;
import data.items.Weapon.WeaponProperty;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WeaponPanel extends JPanel {

    private final DataContainer data;

    public WeaponPanel(DataContainer data) {
        this.data = data;
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Weapon Tables", createWeaponTables());
        tabs.addTab("Weapon Mastery", createMasteryPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane createWeaponTables() {
        List<Weapon> simpleMelee = new ArrayList<>();
        List<Weapon> simpleRanged = new ArrayList<>();
        List<Weapon> martialMelee = new ArrayList<>();
        List<Weapon> martialRanged = new ArrayList<>();

        for (String key : data.getWeaponKeysSorted()) {
            Item item = data.getItems().get(key);
            if (item instanceof Weapon w) {
                boolean melee = !w.ranged;

                if (w.martial) {
                    if (melee) martialMelee.add(w);
                    else martialRanged.add(w);
                } else {
                    if (melee) simpleMelee.add(w);
                    else simpleRanged.add(w);
                }
            }
        }

        JPanel fullPanel = new JPanel();
        fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.Y_AXIS));
        fullPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        fullPanel.add(createTableSection("Simple Melee Weapons", simpleMelee, false));
        fullPanel.add(createTableSection("Simple Ranged Weapons", simpleRanged, true));
        fullPanel.add(createTableSection("Martial Melee Weapons", martialMelee, false));
        fullPanel.add(createTableSection("Martial Ranged Weapons", martialRanged, true));

        JScrollPane scroll = new JScrollPane(fullPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private JPanel createTableSection(String title, List<Weapon> weapons, boolean showRange) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));

        JTable table = new JTable(new WeaponTableModel(weapons, showRange));
        table.setRowHeight(25);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);
        table.setFont(table.getFont().deriveFont(12f));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(12f));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        // Add extra padding at bottom of Ranged weapon tables
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(table.getTableHeader(), BorderLayout.NORTH);
        tableWrapper.add(table, BorderLayout.CENTER);
        if (showRange) {
            tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        }

        JScrollPane scrollPane = new JScrollPane(tableWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(table.getPreferredSize());

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createMasteryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (Map.Entry<WeaponMastery, String> entry : Weapon.WEAPON_MASTERY_DESCR.entrySet()) {
            JLabel title = new JLabel(entry.getKey().name());
            title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));

            JTextArea desc = new JTextArea(entry.getValue());
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);
            desc.setEditable(false);
            desc.setOpaque(false);
            desc.setFont(desc.getFont().deriveFont(12f));

            JPanel box = new JPanel(new BorderLayout());
            box.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            box.add(title, BorderLayout.NORTH);
            box.add(desc, BorderLayout.CENTER);

            panel.add(box);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        return scroll;
    }

    private static class WeaponTableModel extends AbstractTableModel {
        private final String[] baseColumns = { "Name", "Damage", "Properties", "Mastery", "Range", "Cost", "Weight" };
        private final boolean showRange;
        private final List<Weapon> data;

        public WeaponTableModel(List<Weapon> data, boolean showRange) {
            this.data = data;
            this.showRange = showRange;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return showRange ? baseColumns.length : baseColumns.length - 1;
        }

        @Override
        public String getColumnName(int col) {
            if (!showRange && col >= 4) return baseColumns[col + 1]; // Skip "Range"
            return baseColumns[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            Weapon w = data.get(row);
            boolean ranged = w.ranged;
            boolean hasThrown = w.properties != null && w.properties.contains(WeaponProperty.THROWN);
            int adjustedCol = (!showRange && col >= 4) ? col + 1 : col;

            return switch (adjustedCol) {
                case 0 -> w.name;
                case 1 -> (w.versDmg != null && !w.versDmg.isEmpty()) ? w.damage + " / " + w.versDmg : w.damage;
                case 2 -> formatProperties(w);
                case 3 -> w.mastery != null ? w.mastery.name() : "";
                case 4 -> (!ranged && !hasThrown) ? "-" : ranged ? w.rangeLow + "/" + w.rangeHigh : "-";
                case 5 -> formatCost(w.costs);
                case 6 -> w.weight + " lb.";
                default -> "";
            };
        }

        private String formatProperties(Weapon w) {
            if (w.properties == null) return "";
            List<String> props = new ArrayList<>();
            for (WeaponProperty p : w.properties) {
                if (p == WeaponProperty.THROWN && !w.ranged) {
                    props.add("THROWN (" + w.rangeLow + "/" + w.rangeHigh + ")");
                } else {
                    props.add(p.name());
                }
            }
            return String.join(", ", props);
        }

        private String formatCost(int[] costs) {
            if (costs[Item.PP] > 0) return costs[Item.PP] + " pp";
            if (costs[Item.GP] > 0) return costs[Item.GP] + " gp";
            if (costs[Item.EP] > 0) return costs[Item.EP] + " ep";
            if (costs[Item.SP] > 0) return costs[Item.SP] + " sp";
            if (costs[Item.CP] > 0) return costs[Item.CP] + " cp";
            return "-";
        }
    }
}
