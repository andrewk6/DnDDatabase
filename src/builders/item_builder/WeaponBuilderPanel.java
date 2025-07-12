package builders.item_builder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import data.DataContainer;
import data.DataContainer.DamageTypes;
import data.DataContainer.Source;
import data.items.MagicItem;
import data.items.Weapon;
import data.items.Weapon.WeaponMastery;
import data.items.Weapon.WeaponProperty;
import gui.gui_helpers.ReminderField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponBuilderPanel extends JPanel {
    private final JTextField nameField = new JTextField(15);
    private final JTextField damageField = new JTextField(10);
    private final JTextField versDamageField = new JTextField(10);
    private final JComboBox<DamageTypes> dmgTypeBox = new JComboBox<>(DamageTypes.values());
    private final JComboBox<WeaponMastery> masteryBox = new JComboBox<>(WeaponMastery.values());

    private final JCheckBox rangedBox = new JCheckBox("Ranged");
    private final JCheckBox martialBox = new JCheckBox("Martial");

//    private final ReminderField reachField = new ReminderField(5);
    private final ReminderField rangeLowField = new ReminderField(5);
    private final ReminderField rangeHighField = new ReminderField(5);

    private final ReminderField weightField = new ReminderField(5);
    private final ReminderField cpField = new ReminderField(3);
    private final ReminderField spField = new ReminderField(3);
    private final ReminderField epField = new ReminderField(3);
    private final ReminderField gpField = new ReminderField(3);
    private final ReminderField ppField = new ReminderField(3);
    
    private final JCheckBox customBox = new JCheckBox("Custom");
    private final JComboBox<Source> sourceBox = new JComboBox<>(Source.values());

    private final List<JCheckBox> propertyCheckboxes = Arrays.stream(Weapon.WeaponProperty.values())
            .map(prop -> new JCheckBox(prop.name()))
            .collect(Collectors.toList());

    private final Map<String, Weapon> weaponMap = new LinkedHashMap<>();
    private final DataContainer data;
    private final JPanel listPanel = new JPanel();
    
  public static void main(String[]args) {
	SwingUtilities.invokeLater(()->{
		JFrame frm = new JFrame();
		WeaponBuilderPanel wpPane = new WeaponBuilderPanel(new DataContainer());
		frm.setContentPane(wpPane);
		frm.setSize(580, 540);
		frm.setResizable(true);
		frm.setVisible(true);
	});    	
}

    public WeaponBuilderPanel(DataContainer data) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.data = data;

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        add(scrollPane, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
        JPanel formPanel = buildFormPanel();
        wrapper.add(formPanel, BorderLayout.NORTH);
        JScrollPane formScroll = new JScrollPane(wrapper);
        add(formScroll, BorderLayout.CENTER);
        SetNumbersOnly();
    }
    
    private void SetNumbersOnly() {
//    	reachField.setNumbersOnly();
    	rangeLowField.setNumbersOnly();
    	rangeHighField.setNumbersOnly();
    	weightField.setNumbersOnly();
    	cpField.setNumbersOnly();
    	spField.setNumbersOnly();
    	epField.setNumbersOnly();
    	gpField.setNumbersOnly();
    	ppField.setNumbersOnly();
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
        panel.add(new JLabel("Damage:"), gbc);
        gbc.gridx = 1;
        panel.add(damageField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Versatile Dmg:"), gbc);
        gbc.gridx = 1;
        panel.add(versDamageField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Damage Type:"), gbc);
        gbc.gridx = 1;
        panel.add(dmgTypeBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Mastery:"), gbc);
        gbc.gridx = 1;
        panel.add(masteryBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(rangedBox, gbc);
        gbc.gridx = 1;
        panel.add(martialBox, gbc);

//        row++;
//        gbc.gridx = 0; gbc.gridy = row;
//        panel.add(new JLabel("Reach:"), gbc);
//        gbc.gridx = 1;
//        panel.add(reachField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Range (Low-High):"), gbc);
        gbc.gridx = 1;
        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        rangePanel.add(rangeLowField);
        rangePanel.add(new JLabel("-"));
        rangePanel.add(rangeHighField);
        panel.add(rangePanel, gbc);

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
        costPanel.add(new JLabel("CP")); costPanel.add(cpField);
        costPanel.add(new JLabel("SP")); costPanel.add(spField);
        costPanel.add(new JLabel("EP")); costPanel.add(epField);
        costPanel.add(new JLabel("GP")); costPanel.add(gpField);
        costPanel.add(new JLabel("PP")); costPanel.add(ppField);
        panel.add(costPanel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Properties:"), gbc);

        row++;
        gbc.gridy = row;
        JPanel propsPanel = new JPanel();
        propsPanel.setLayout(new BoxLayout(propsPanel, BoxLayout.Y_AXIS));
        for (JCheckBox cb : propertyCheckboxes) propsPanel.add(cb);
        JScrollPane propsScroll = new JScrollPane(propsPanel);
        propsScroll.setPreferredSize(new Dimension(200, 100));
        propsScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
        panel.add(propsScroll, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Source:"), gbc);
        gbc.gridx = 1;
        panel.add(sourceBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(customBox, gbc);


        row++;
        gbc.gridy = row;
        JButton addButton = new JButton("Add Weapon");
        addButton.addActionListener(this::handleAddWeapon);
        panel.add(addButton, gbc);

        return panel;
    }

    private void handleAddWeapon(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Weapon name cannot be empty.");
            return;
        }

        Weapon weapon = new Weapon(name);
        weapon.damage = damageField.getText().trim();
        weapon.versDmg = versDamageField.getText().trim();
        weapon.dmgType = (DamageTypes) dmgTypeBox.getSelectedItem();
        weapon.mastery = (Weapon.WeaponMastery) masteryBox.getSelectedItem();
        weapon.ranged = rangedBox.isSelected();
        weapon.martial = martialBox.isSelected();

//        try { weapon.reach = Integer.parseInt(reachField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.rangeLow = Integer.parseInt(rangeLowField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.rangeHigh = Integer.parseInt(rangeHighField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.weight = Integer.parseInt(weightField.getText().trim()); } catch (NumberFormatException ignored) {}

        try { weapon.costs[Weapon.CP] = Integer.parseInt(cpField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.costs[Weapon.SP] = Integer.parseInt(spField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.costs[Weapon.EP] = Integer.parseInt(epField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.costs[Weapon.GP] = Integer.parseInt(gpField.getText().trim()); } catch (NumberFormatException ignored) {}
        try { weapon.costs[Weapon.PP] = Integer.parseInt(ppField.getText().trim()); } catch (NumberFormatException ignored) {}

        weapon.properties = propertyCheckboxes.stream()
                .filter(JCheckBox::isSelected)
                .map(cb -> Weapon.WeaponProperty.valueOf(cb.getText()))
                .collect(Collectors.toCollection(ArrayList::new));

        weapon.custom = customBox.isSelected();
        weapon.source = (Source) sourceBox.getSelectedItem();

        weaponMap.put(name, weapon);
        updateWeaponList();
        clearForm();
    }
    
    private void LoadEdit(Weapon w) {
    	nameField.setText(w.name);
    	nameField.setEditable(false);
    	damageField.setText(w.damage);
    	versDamageField.setText(w.versDmg);
    	dmgTypeBox.setSelectedItem(w.dmgType);
    	masteryBox.setSelectedItem(w.mastery);
    	rangedBox.setSelected(w.ranged);
    	martialBox.setSelected(w.martial);
//    	reachField.setText("" + w.reach);
    	rangeLowField.setText("" + w.rangeLow);
    	rangeHighField.setText("" + w.rangeHigh);
    	weightField.setText("" + w.weight);
    	cpField.setText("" + w.costs[Weapon.CP]);
    	spField.setText("" + w.costs[Weapon.SP]);
    	epField.setText("" + w.costs[Weapon.EP]);
    	gpField.setText("" + w.costs[Weapon.GP]);
    	ppField.setText("" + w.costs[Weapon.PP]);
    	customBox.setSelected(w.custom);
    	sourceBox.setSelectedItem(w.source);

    	for(WeaponProperty prop : w.properties) {
    		for (JCheckBox cb : propertyCheckboxes)
    			if(cb.getText().equals(prop.name()))
    				cb.setSelected(true);
    	}
    	
    }

    private void clearForm() {
        nameField.setText("");
        nameField.setEditable(true);
        nameField.requestFocus();
        damageField.setText("");
        versDamageField.setText("");
        dmgTypeBox.setSelectedIndex(0);
        masteryBox.setSelectedIndex(0);
        rangedBox.setSelected(false);
        martialBox.setSelected(false);
//        reachField.setText("");
        rangeLowField.setText("");
        rangeHighField.setText("");
        weightField.setText("");
        cpField.setText(""); spField.setText("");
        epField.setText(""); gpField.setText(""); ppField.setText("");
        customBox.setSelected(false);
        sourceBox.setSelectedIndex(0);

        for (JCheckBox cb : propertyCheckboxes) cb.setSelected(false);
    }

    private void updateWeaponList() {
        listPanel.removeAll();
        for (String name : weaponMap.keySet()) {
            JPanel entry = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
            nameLabel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(WeaponBuilderPanel.this, "Would you like to load " + 
							nameLabel.getText() + " you will lose any unadded progress.", "Load Confirm", 
							JOptionPane.YES_NO_OPTION);
					if(opt == JOptionPane.YES_OPTION) {
						LoadEdit(weaponMap.get(nameLabel.getText()));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));}
            });
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(e -> {
                weaponMap.remove(name);
                updateWeaponList();
            });
            entry.add(nameLabel, BorderLayout.CENTER);
            entry.add(deleteBtn, BorderLayout.EAST);
            entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            listPanel.add(entry);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    public void LoadItems() {
		for(String s : data.getWeaponKeysSorted()) {
			weaponMap.put(s, (Weapon) data.getItems().get(s));
		}
		updateWeaponList();
	}

    public Map<String, Weapon> getWeaponMap() {
        return weaponMap;
    }
}
