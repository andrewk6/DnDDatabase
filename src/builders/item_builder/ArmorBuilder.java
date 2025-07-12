package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Source;
import data.items.Armor;
import data.items.Item;
import data.items.MagicItem;
import data.items.Weapon;
import gui.gui_helpers.ReminderField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArmorBuilder extends JPanel {

    private final JTextField nameField = new JTextField(15);
    private final ReminderField acField = new ReminderField(5);
    private final JComboBox<Armor.ArmorType> typeBox = new JComboBox<>(Armor.ArmorType.values());
    private final ReminderField minStrField = new ReminderField(5);
    private final JCheckBox stealthDisadvBox = new JCheckBox("Stealth Disadvantage");
    private final JCheckBox addDexBox = new JCheckBox("Add Dex to AC");
    private final JCheckBox addDexCappedBox = new JCheckBox("Cap Dex Bonus");
    private final JCheckBox customBox = new JCheckBox("Custom");
    private final JComboBox<Source> sourceBox = new JComboBox<>(Source.values());

    private final ReminderField weightField = new ReminderField(5);
    private final ReminderField[] costFields = {
            new ReminderField(3), // CP
            new ReminderField(3), // SP
            new ReminderField(3), // EP
            new ReminderField(3), // GP
            new ReminderField(3)  // PP
    };
    private final DataContainer data;

    private final Map<String, Armor> armorMap = new LinkedHashMap<>();
    private final JPanel listPanel = new JPanel();
    
    public static void main(String[]args) {
    	SwingUtilities.invokeLater(()->{
    		JFrame frm = new JFrame();
    		ArmorBuilder aBuild = new ArmorBuilder(new DataContainer());
    		frm.setContentPane(aBuild);
    		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frm.pack();
    		frm.setVisible(true);
    	});
    }

    public ArmorBuilder(DataContainer data) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.data = data;

        // Left panel
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        add(scrollPane, BorderLayout.WEST);

        // Center form panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
        JPanel formPanel = buildFormPanel();
        wrapper.add(formPanel, BorderLayout.NORTH);
        JScrollPane formScroll = new JScrollPane(wrapper);
        add(formScroll, BorderLayout.CENTER);
        
        SetNumbersOnly();
    }
    
    private void SetNumbersOnly() {
    	minStrField.setNumbersOnly();
    	weightField.setNumbersOnly();
    	acField.setNumbersOnly();
    	for(ReminderField f : costFields) {
    		f.setNumbersOnly();
    	}
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("AC:"), gbc);
        gbc.gridx = 1;
        panel.add(acField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeBox, gbc);

        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Min STR:"), gbc);
        gbc.gridx = 1;
        panel.add(minStrField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(stealthDisadvBox, gbc);
        gbc.gridwidth = 1;
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(addDexBox, gbc);

        row++;
        gbc.gridy = row;
        panel.add(addDexCappedBox, gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Weight (lb):"), gbc);
        gbc.gridx = 1;
        panel.add(weightField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        String[] labels = {"CP", "SP", "EP", "GP", "PP"};
        for (int i = 0; i < 5; i++) {
            costPanel.add(new JLabel(labels[i]));
            costPanel.add(costFields[i]);
        }
        panel.add(costPanel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Source:"), gbc);
        gbc.gridx = 1;
        panel.add(sourceBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(customBox, gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Armor");
        addButton.addActionListener(this::handleAddArmor);
        panel.add(addButton, gbc);

        return panel;
    }

    private void handleAddArmor(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Armor name cannot be empty.");
            return;
        }

        Armor armor = new Armor(name);
        try { armor.ac = Integer.parseInt(acField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { armor.minSTR = Integer.parseInt(minStrField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { armor.weight = Integer.parseInt(weightField.getText().trim()); } catch (NumberFormatException ignored) {}

        armor.stealthDisadv = stealthDisadvBox.isSelected();
        armor.addDexFull = addDexBox.isSelected();
        armor.addDexCap = addDexCappedBox.isSelected();

        armor.type = (Armor.ArmorType) typeBox.getSelectedItem();

        for (int i = 0; i < 5; i++) {
            try {
                armor.costs[i] = Integer.parseInt(costFields[i].getText().trim());
            } catch (NumberFormatException ignored) {
                armor.costs[i] = 0;
            }
        }
        armor.custom = customBox.isSelected();
        armor.source = (Source) sourceBox.getSelectedItem();

        armorMap.put(name, armor);
        updateArmorList();
        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        nameField.setEditable(true);
        nameField.requestFocus();
        acField.setText("");
        minStrField.setText("");
        weightField.setText("");
        stealthDisadvBox.setSelected(false);
        addDexBox.setSelected(false);
        addDexCappedBox.setSelected(false);
        typeBox.setSelectedIndex(0);
        customBox.setSelected(false);
        sourceBox.setSelectedIndex(0);

        for (JTextField field : costFields) field.setText("");
    }

    private void updateArmorList() {
        listPanel.removeAll();
        for (String name : armorMap.keySet()) {
            JPanel entry = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
            nameLabel.addMouseListener(new MouseListener() {
            	public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(ArmorBuilder.this, "Would you like to load " + 
							nameLabel.getText() + " you will lose any unadded progress.", "Load Confirm", 
							JOptionPane.YES_NO_OPTION);
					if(opt == JOptionPane.YES_OPTION) {
						LoadEdit(armorMap.get(nameLabel.getText()));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));}
            });
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(e -> {
                armorMap.remove(name);
                updateArmorList();
            });
            entry.add(nameLabel, BorderLayout.CENTER);
            entry.add(deleteBtn, BorderLayout.EAST);
            entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            listPanel.add(entry);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    private void LoadEdit(Armor a) {
    	nameField.setText(a.name);
    	nameField.setEditable(false);
        acField.setText("" + a.ac);
        minStrField.setText("" + a.minSTR);
        weightField.setText("" + a.weight);
        addDexBox.setSelected(a.addDexFull);
        addDexCappedBox.setSelected(a.addDexCap);
        stealthDisadvBox.setSelected(a.stealthDisadv);
        typeBox.setSelectedItem(a.type);
        costFields[Weapon.CP].setText("" + a.costs[Weapon.CP]);
        costFields[Weapon.SP].setText("" + a.costs[Weapon.SP]);
        costFields[Weapon.EP].setText("" + a.costs[Weapon.EP]);
        costFields[Weapon.GP].setText("" + a.costs[Weapon.GP]);
        costFields[Weapon.PP].setText("" + a.costs[Weapon.PP]);
        customBox.setSelected(a.custom);
        sourceBox.setSelectedItem(a.source);
    }
    
    public void LoadItems() {
		for(String s : data.getArmorKeysSorted())
			armorMap.put(s, (Armor) data.getItems().get(s));
		updateArmorList();
	}

    public Map<String, Armor> getArmorMap() {
        return armorMap;
    }
}
