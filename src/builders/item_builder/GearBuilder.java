package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Source;
import data.items.Gear;
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

public class GearBuilder extends JPanel {

    private final JTextField nameField = new JTextField(15);
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final ReminderField weightField = new ReminderField();
    private final ReminderField[] costFields = {
        new ReminderField(3), // CP
        new ReminderField(3), // SP
        new ReminderField(3), // EP
        new ReminderField(3), // GP
        new ReminderField(3)  // PP
    };
    private final JCheckBox customBox = new JCheckBox("Custom");
    private final JComboBox<Source> sourceBox = new JComboBox<>(Source.values());


    private final Map<String, Gear> gearMap = new LinkedHashMap<>();
    private final DataContainer data;
    private final JPanel listPanel = new JPanel();
    
    public static void main(String[]args) {
    	SwingUtilities.invokeLater(()->{
    		JFrame frm = new JFrame();
    		GearBuilder aBuild = new GearBuilder(new DataContainer());
    		frm.setContentPane(aBuild);
    		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frm.pack();
    		frm.setVisible(true);
    	});
    }

    public GearBuilder(DataContainer data) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.data = data;

        for (ReminderField field : costFields) field.setNumbersOnly();
        weightField.setNumbersOnly();

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
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(descScroll, gbc);
        gbc.anchor = GridBagConstraints.CENTER;

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
        JButton addButton = new JButton("Add Gear");
        addButton.addActionListener(this::handleAddGear);
        panel.add(addButton, gbc);

        return panel;
    }

    private void handleAddGear(ActionEvent e) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gear name cannot be empty.");
            return;
        }

        Gear gear = new Gear(name);
        gear.description = descriptionArea.getText().trim();

        try {
            gear.weight = Integer.parseInt(weightField.getText().trim());
        } catch (NumberFormatException ignored) {
            gear.weight = 0;
        }

        for (int i = 0; i < 5; i++) {
            try {
                gear.costs[i] = Integer.parseInt(costFields[i].getText().trim());
            } catch (NumberFormatException ignored) {
                gear.costs[i] = 0;
            }
        }
        
        gear.custom = customBox.isSelected();
        gear.source = (Source) sourceBox.getSelectedItem();


        gearMap.put(name, gear);
        updateGearList();
        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        nameField.setEditable(true);
        nameField.requestFocus();
        descriptionArea.setText("");
        weightField.setText("");
        customBox.setSelected(false);
        sourceBox.setSelectedIndex(0);

        for (ReminderField field : costFields) field.setText("");
    }

    private void updateGearList() {
        listPanel.removeAll();
        for (String name : gearMap.keySet()) {
            JPanel entry = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
            nameLabel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(GearBuilder.this, "Would you like to load " + 
							nameLabel.getText() + " you will lose any unadded progress.", "Load Confirm", 
							JOptionPane.YES_NO_OPTION);
					if(opt == JOptionPane.YES_OPTION) {
						LoadEdit(gearMap.get(nameLabel.getText()));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));}
            });
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(e -> {
                gearMap.remove(name);
                updateGearList();
            });
            entry.add(nameLabel, BorderLayout.CENTER);
            entry.add(deleteBtn, BorderLayout.EAST);
            entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            listPanel.add(entry);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    private void LoadEdit(Gear g) {
    	nameField.setText(g.name);
    	nameField.setEditable(false);
        descriptionArea.setText(g.description);
        weightField.setText("" + g.weight);
        costFields[Weapon.CP].setText("" + g.costs[Weapon.CP]);
        costFields[Weapon.SP].setText("" + g.costs[Weapon.SP]);
        costFields[Weapon.EP].setText("" + g.costs[Weapon.EP]);
        costFields[Weapon.GP].setText("" + g.costs[Weapon.GP]);
        costFields[Weapon.PP].setText("" + g.costs[Weapon.PP]);
        customBox.setSelected(g.custom);
        sourceBox.setSelectedItem(g.source);
    }
    
    public void LoadItems() {
		for(String s : data.getGearKeysSorted())
			gearMap.put(s, (Gear) data.getItems().get(s));
		updateGearList();
	}

    public Map<String, Gear> getGearMap() {
        return gearMap;
    }
}
