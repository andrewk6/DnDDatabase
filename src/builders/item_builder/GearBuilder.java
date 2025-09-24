package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Source;
import data.items.Gear;
import data.items.Poison;
import data.items.Poison.Poison_Type;
import data.items.Weapon;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class GearBuilder extends JPanel {

//    private final JTextField nameField = new JTextField(15);
	private final ReminderField nameField = 
			CompFactory.createReminderField("Gear name...", 15, ComponentType.HEADER);
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final ReminderField weightField = CompFactory.createReminderField("Item weight...");
    private final ReminderField[] costFields = {
        CompFactory.createReminderField("CP", true, 3), // CP
        CompFactory.createReminderField("SP", true, 3), // SP
        CompFactory.createReminderField("EP", true, 3), // EP
        CompFactory.createReminderField("GP", true, 3), // GP
        CompFactory.createReminderField("PP", true, 3)  // PP
    };
    private final JCheckBox poisonBox = CompFactory.createNewCheckbox("Poison");
    private final JComboBox<Source> sourceBox = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
    private final JComboBox<Poison_Type> poisonCombo = 
    		CompFactory.createEnumCombo(Poison_Type.class, ComponentType.BODY);


    private final Map<String, Gear> gearMap = new LinkedHashMap<>();
    private final DataContainer data;
    private final JPanel listPanel = new JPanel();
    
    public static void main(String[]args) {
    	DataContainer data = new DataContainer();
    	data.init();
    	SwingUtilities.invokeLater(()->{
    		JFrame frm = new JFrame();
    		GearBuilder aBuild = new GearBuilder(data);
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
    	JPanel poisonPane = CompFactory.createSplitPane("Poison Type", poisonCombo);
        JPanel panel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
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
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(CompFactory.createNewLabel("Description:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(StyleContainer.FNT_BODY_PLAIN);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(descScroll, gbc);
        gbc.anchor = GridBagConstraints.CENTER;

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
        String[] labels = {"CP", "SP", "EP", "GP", "PP"};
        for (int i = 0; i < 5; i++) {
            costPanel.add(CompFactory.createNewLabel(labels[i], ComponentType.HEADER));
            costPanel.add(costFields[i]);
        }
        panel.add(costPanel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(CompFactory.createNewLabel("Source:", ComponentType.HEADER), gbc);
        gbc.gridx = 1;
        panel.add(sourceBox, gbc);

        poisonBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					poisonPane.setVisible(true);
				else
					poisonPane.setVisible(false);
			}
        });
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(poisonBox, gbc);
        
        poisonPane.setVisible(false);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(poisonPane, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton addButton = CompFactory.createNewButton("Add Gear", this::handleAddGear);
        panel.add(addButton, gbc);

        return panel;
    }

    private void handleAddGear() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gear name cannot be empty.");
            return;
        }

        Gear gear = ((poisonBox.isSelected()) ? new Poison(name) : new Gear(name));
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
        
        gear.source = (Source) sourceBox.getSelectedItem();

        if(gear instanceof Poison p)
        	p.poisonType = (Poison_Type) poisonCombo.getSelectedItem();

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
        poisonBox.setSelected(false);

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
            deleteBtn.addActionListener(_ -> {
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
    	if(g instanceof Poison p) {
    		poisonBox.setSelected(true);
    		poisonCombo.setSelectedItem(p.poisonType);
    	}
    	nameField.setText(g.name);
    	nameField.setEditable(false);
        descriptionArea.setText(g.description);
        weightField.setText("" + g.weight);
        costFields[Weapon.CP].setText("" + g.costs[Weapon.CP]);
        costFields[Weapon.SP].setText("" + g.costs[Weapon.SP]);
        costFields[Weapon.EP].setText("" + g.costs[Weapon.EP]);
        costFields[Weapon.GP].setText("" + g.costs[Weapon.GP]);
        costFields[Weapon.PP].setText("" + g.costs[Weapon.PP]);
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
