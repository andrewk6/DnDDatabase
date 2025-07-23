package gui.item_panels;

import data.DataContainer;
import data.items.Armor;
import data.items.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ArmorPanel extends JPanel {

    private static final String[] COLUMN_NAMES = {
        "Name", "AC", "Dex Bonus", "Stealth", "STR", "Weight", "Cost"
    };

    public ArmorPanel(DataContainer dataContainer) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Fetch sorted armor keys and item map once
        List<String> armorKeys = dataContainer.getArmorKeysSorted();
        Map<String, Item> allItems = dataContainer.getItems();

        add(buildSection("Light Armor (Don: 1 min, Doff: 1 min)", Armor.ArmorType.LIGHT, armorKeys, allItems));
        add(Box.createVerticalStrut(20));
        add(buildSection("Medium Armor (Don: 5 min, Doff: 1 min)", Armor.ArmorType.MEDIUM, armorKeys, allItems));
        add(Box.createVerticalStrut(20));
        add(buildSection("Heavy Armor (Don: 10 min, Doff: 5 min)", Armor.ArmorType.HEAVY, armorKeys, allItems));
        add(Box.createVerticalStrut(20));
        add(buildSection("Shields", Armor.ArmorType.SHIELD, armorKeys, allItems));
    }

    private JPanel buildSection(String title, Armor.ArmorType type, List<String> armorKeys, Map<String, Item> allItems) {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JLabel header = new JLabel(title);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBorder(new EmptyBorder(0, 0, 5, 0));

        JTable table = new JTable(buildModel(type, armorKeys, allItems));
        table.setEnabled(false);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPane.setPreferredSize(new Dimension(600, table.getRowHeight() * Math.max(1, table.getRowCount()) + 24));

        section.add(header, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);
        return section;
    }

    private DefaultTableModel buildModel(Armor.ArmorType type, List<String> armorKeys, Map<String, Item> allItems) {
        DefaultTableModel model = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String key : armorKeys) {
            Item item = allItems.get(key);
            if (!(item instanceof Armor armor)) continue;
            if (armor.type != type) continue;

            String dexBonus;
            if (armor.addDexFull) {
                dexBonus = "Yes";
            } else if (armor.addDexCap) {
                dexBonus = "Yes (max 2)";
            } else {
                dexBonus = "No";
            }

            String stealth = armor.stealthDisadv ? "Disadv." : "—";
            String strReq = armor.minSTR > 0 ? String.valueOf(armor.minSTR) : "—";

            model.addRow(new Object[]{
                armor.name,
                armor.ac,
                dexBonus,
                stealth,
                strReq,
                item.weight > 0 ? item.weight + " lb." : "—",
                formatCost(item.costs)
            });
        }

        return model;
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
    
    
}
