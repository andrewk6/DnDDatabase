package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Abilities;
import data.DataContainer.Source;
import data.items.ToolSet;
import gui.gui_helpers.ReminderField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolSetBuilder extends JPanel {

	private final JTextField nameField = new JTextField(15);
	private final JComboBox<Abilities> abilBox = new JComboBox<>(Abilities.values());
	private final JTextArea utilizeArea = new JTextArea(4, 20);
	private final JTextArea craftArea = new JTextArea(4, 20);

	private final ReminderField weightField = new ReminderField(5);
	private final ReminderField[] costFields = {
		new ReminderField(3), new ReminderField(3),
		new ReminderField(3), new ReminderField(3),
		new ReminderField(3)
	};
	private final JCheckBox customBox = new JCheckBox("Custom");
	private final JComboBox<Source> sourceBox = new JComboBox<>(Source.values());


	private final Map<String, ToolSet> toolMap = new LinkedHashMap<>();
	private final JPanel listPanel = new JPanel();
	private final DataContainer data;

	public ToolSetBuilder(DataContainer data) {
		this.data = data;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		setNumbersOnly();

		// Left panel: item list
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setPreferredSize(new Dimension(250, 0));
		add(scrollPane, BorderLayout.WEST);

		// Center: form
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
		wrapper.add(buildFormPanel(), BorderLayout.NORTH);
		add(new JScrollPane(wrapper), BorderLayout.CENTER);
	}

	private void setNumbersOnly() {
		weightField.setNumbersOnly();
		for (ReminderField f : costFields) f.setNumbersOnly();
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
		panel.add(new JLabel("Ability:"), gbc);
		gbc.gridx = 1;
		panel.add(abilBox, gbc);

		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(new JLabel("Weight:"), gbc);
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
		gbc.gridwidth = 2;
		panel.add(new JLabel("Utilize:"), gbc);

		row++;
		gbc.gridy = row;
		utilizeArea.setLineWrap(true);
		panel.add(new JScrollPane(utilizeArea), gbc);

		row++;
		gbc.gridy = row;
		panel.add(new JLabel("Craft:"), gbc);

		row++;
		gbc.gridy = row;
		craftArea.setLineWrap(true);
		panel.add(new JScrollPane(craftArea), gbc);

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
		gbc.gridy = row;
		JButton addButton = new JButton("Add Tool Set");
		addButton.addActionListener(this::handleAddTool);
		panel.add(addButton, gbc);

		return panel;
	}

	private void handleAddTool(ActionEvent e) {
		String name = nameField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tool set name cannot be empty.");
			return;
		}

		ToolSet tool = new ToolSet(name);
		tool.abil = (Abilities) abilBox.getSelectedItem();
		tool.utilize = utilizeArea.getText().trim();
		tool.craft = craftArea.getText().trim();

		try { tool.weight = Integer.parseInt(weightField.getText().trim()); }
		catch (NumberFormatException ignored) { tool.weight = 0; }

		for (int i = 0; i < 5; i++) {
			try {
				tool.costs[i] = Integer.parseInt(costFields[i].getText().trim());
			} catch (NumberFormatException ignored) {
				tool.costs[i] = 0;
			}
		}
		tool.custom = customBox.isSelected();
		tool.source = (Source) sourceBox.getSelectedItem();


		toolMap.put(name, tool);
		updateToolList();
		clearForm();
	}

	private void updateToolList() {
		listPanel.removeAll();
		for (String name : toolMap.keySet()) {
			JPanel entry = new JPanel(new BorderLayout());
			JLabel nameLabel = new JLabel(name);
			nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
			nameLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(ToolSetBuilder.this,
							"Load " + name + "? You will lose any unsaved changes.",
							"Load Confirm", JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						loadEdit(toolMap.get(name));
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
				}
				@Override
				public void mouseExited(MouseEvent e) {
					nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
				}
			});
			JButton deleteBtn = new JButton("Delete");
			deleteBtn.addActionListener(e -> {
				toolMap.remove(name);
				updateToolList();
			});
			entry.add(nameLabel, BorderLayout.CENTER);
			entry.add(deleteBtn, BorderLayout.EAST);
			entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			listPanel.add(entry);
		}
		listPanel.revalidate();
		listPanel.repaint();
	}

	private void loadEdit(ToolSet tool) {
		nameField.setText(tool.name);
		nameField.setEditable(false);
		abilBox.setSelectedItem(tool.abil);
		utilizeArea.setText(tool.utilize);
		craftArea.setText(tool.craft);
		weightField.setText(String.valueOf(tool.weight));
		customBox.setSelected(tool.custom);
		sourceBox.setSelectedItem(tool.source);

		for (int i = 0; i < 5; i++) {
			costFields[i].setText(String.valueOf(tool.costs[i]));
		}
	}

	private void clearForm() {
		nameField.setText("");
		nameField.setEditable(true);
		abilBox.setSelectedIndex(0);
		utilizeArea.setText("");
		craftArea.setText("");
		weightField.setText("");
		customBox.setSelected(false);
		sourceBox.setSelectedIndex(0);
		for (ReminderField f : costFields) f.setText("");
		nameField.requestFocus();
	}

	public void LoadItems() {
		for (String s : data.getToolKeysSorted()) {
			toolMap.put(s, (ToolSet) data.getItems().get(s));
		}
		updateToolList();
	}

	public Map<String, ToolSet> getToolSetMap() {
		return toolMap;
	}
}
