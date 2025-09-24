package builders.item_builder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import data.DataContainer;
import data.DataContainer.DamageTypes;
import data.DataContainer.Source;
import data.items.Weapon;
import data.items.Weapon.WeaponMastery;
import data.items.Weapon.WeaponProperty;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.EnumCheckbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class WeaponBuilderPanel extends JPanel {
    private final ReminderField nameField = 
    		CompFactory.createReminderField("Item name...", 15, ComponentType.HEADER);
    private final ReminderField damageField = 
    		CompFactory.createReminderField("Damage...", 10, ComponentType.BODY);
    private final ReminderField versDamageField = 
    		CompFactory.createReminderField("Vers. Damage...", 10, ComponentType.BODY);
    private final JComboBox<DamageTypes> dmgTypeBox = 
    		CompFactory.createEnumCombo(DamageTypes.class, ComponentType.BODY);
    private final JComboBox<WeaponMastery> masteryBox = 
    		CompFactory.createEnumCombo(WeaponMastery.class, ComponentType.BODY);

    private final JCheckBox rangedBox = CompFactory.createNewCheckbox("Ranged");
    private final JCheckBox martialBox = CompFactory.createNewCheckbox("Martial");
    private final JCheckBox modernBox = CompFactory.createNewCheckbox("Modern");
    private final JCheckBox futureBox = CompFactory.createNewCheckbox("Futuristic");

//    private final ReminderField reachField = new ReminderField(5);
    private final ReminderField rangeLowField = 
    		CompFactory.createReminderField("Low Range...", true, 5);
    private final ReminderField rangeHighField = 
    		CompFactory.createReminderField("High Range...", true, 5);
    private final ReminderField reloadField = 
    		CompFactory.createReminderField("Reload...", true, 5);
    private final ReminderField weightField = 
    		CompFactory.createReminderField("Weight...", true, 5);
    //3
    private final ReminderField cpField = 
    		CompFactory.createReminderField("CP", true, 3);
    private final ReminderField spField = 
    		CompFactory.createReminderField("SP", true, 3);
    private final ReminderField epField = 
    		CompFactory.createReminderField("EP", true, 3);
    private final ReminderField gpField = 
    		CompFactory.createReminderField("GP", true, 3);
    private final ReminderField ppField = 
    		CompFactory.createReminderField("PP", true, 3);
        
    private final JComboBox<Source> sourceBox = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);

    private final List<EnumCheckbox<WeaponProperty>> propertyCheckboxes = Arrays.stream(Weapon.WeaponProperty.values())
    		.map(prop -> CompFactory.createEnumCheckbox(prop))
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
        JScrollPane scrollPane = CompFactory.wrapPanelInScroll(listPanel, ScrollPolicy.VERTICAL);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        add(scrollPane, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
        JPanel formPanel = buildFormPanel();
        wrapper.add(formPanel, BorderLayout.NORTH);
        JScrollPane formScroll = new JScrollPane(wrapper);
        add(formScroll, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel reloadPane = new JPanel();
        JPanel rangePane = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        JLabel rangeLabel = CompFactory.createNewLabel("Range (Low-High):", ComponentType.HEADER);
        rangePane.setVisible(false);
        rangeLabel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        ItemListener showReload = new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		if(!modernBox.isSelected() && !futureBox.isSelected()) {
        			reloadPane.setVisible(false);
        		} else {
        			reloadPane.setVisible(true);
        			rangedBox.setSelected(true);
        		}
        	}
        };
        
        ItemListener showRange = new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		if(!rangedBox.isSelected()) {
        			rangePane.setVisible(false);
        			rangeLabel.setVisible(false);
        		}else {
        			rangePane.setVisible(true);
        			rangeLabel.setVisible(true);
        		}
        	}
        };
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Name:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Damage:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(damageField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Versatile Dmg:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(versDamageField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Damage Types:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(dmgTypeBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Mastery:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(masteryBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JPanel checkFlow = new JPanel();
        
        checkFlow.setLayout(new FlowLayout(FlowLayout.LEFT));
        checkFlow.add(martialBox);
        rangedBox.addItemListener(showRange);
        checkFlow.add(rangedBox);

        modernBox.addItemListener(showReload);
        checkFlow.add(modernBox);
        futureBox.addItemListener(showReload);
        checkFlow.add(futureBox);
        
        panel.add(checkFlow, gbc);
        
        reloadPane.setLayout(new BorderLayout());
        reloadPane.setVisible(false);
        gbc.gridx = 1;
        panel.add(reloadPane, gbc);
        
        reloadPane.add(CompFactory.createNewLabel("Reload(Ammo/Shots):", ComponentType.HEADER), BorderLayout.WEST);
        reloadPane.add(reloadField, BorderLayout.CENTER);
        
//        gbc.gridx = 1;
//        panel.add(martialBox, gbc);
//        gbc.gridx = 2;
//        panel.add(modernBox, gbc);
//        gbc.gridx = 3;
//        panel.add(futureBox, gbc);

//        row++;
//        gbc.gridx = 0; gbc.gridy = row;
//        panel.add(new JLabel("Reach:"), gbc);
//        gbc.gridx = 1;
//        panel.add(reachField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(rangeLabel, gbc);
        gbc.gridx = 1;
        rangePane.add(rangeLowField);
        rangePane.add(new JLabel("-"));
        rangePane.add(rangeHighField);
        panel.add(rangePane, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Weight (lb):", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(weightField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Cost:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        costPanel.add(CompFactory.createNewLabel("CP", ComponentType.HEADER)); 
        costPanel.add(cpField);
        costPanel.add(CompFactory.createNewLabel("SP", ComponentType.HEADER)); 
        costPanel.add(spField);
        costPanel.add(CompFactory.createNewLabel("EP", ComponentType.HEADER)); 
        costPanel.add(epField);
        costPanel.add(CompFactory.createNewLabel("GP", ComponentType.HEADER)); 
        costPanel.add(gpField);
        costPanel.add(CompFactory.createNewLabel("PP", ComponentType.HEADER)); 
        costPanel.add(ppField);
        panel.add(costPanel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(CompFactory.createNewLabel("Properties:", ComponentType.HEADER), gbc);

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
        panel.add(CompFactory.createNewLabel("Source:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(sourceBox, gbc);

        row++;
        gbc.gridy = row;
        JButton addButton = CompFactory.createNewButton("Add Weapon", this::handleAddWeapon);
        panel.add(addButton, gbc);

        return panel;
    }

    private void handleAddWeapon() {
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
        weapon.modern = modernBox.isSelected();
        weapon.future = futureBox.isSelected();
        if(reloadField.getText().length() > 0)
        	weapon.reload = Integer.parseInt(reloadField.getText());

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
                .map(EnumCheckbox::getValue)
                .collect(Collectors.toCollection(ArrayList::new));

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
    	modernBox.setSelected(w.modern);
    	futureBox.setSelected(w.future);
    	reloadField.setText(w.reload + "");
//    	reachField.setText("" + w.reach);
    	rangeLowField.setText("" + w.rangeLow);
    	rangeHighField.setText("" + w.rangeHigh);
    	weightField.setText("" + w.weight);
    	cpField.setText("" + w.costs[Weapon.CP]);
    	spField.setText("" + w.costs[Weapon.SP]);
    	epField.setText("" + w.costs[Weapon.EP]);
    	gpField.setText("" + w.costs[Weapon.GP]);
    	ppField.setText("" + w.costs[Weapon.PP]);
    	sourceBox.setSelectedItem(w.source);

    	for(WeaponProperty prop : w.properties) {
    		for (EnumCheckbox<WeaponProperty> cb : propertyCheckboxes)
    			if(cb.getValue().equals(prop))
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
        modernBox.setSelected(false);
        futureBox.setSelected(false);
        reloadField.setText("");
//        reachField.setText("");
        rangeLowField.setText("");
        rangeHighField.setText("");
        weightField.setText("");
        cpField.setText(""); spField.setText("");
        epField.setText(""); gpField.setText(""); ppField.setText("");
//        sourceBox.setSelectedIndex(0);

        for (EnumCheckbox<WeaponProperty> cb : propertyCheckboxes) cb.setSelected(false);
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
            deleteBtn.addActionListener(_ -> {
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
