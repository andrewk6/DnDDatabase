package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Source;
import data.items.Item;
import data.items.MagicItem;
import data.items.MagicItem.Rarity;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class MagicItemBuilder extends JPanel {

	private final JTextField nameField = new JTextField(15);
	private final ReminderField weightField = new ReminderField();
	private final ReminderField[] costFields = { new ReminderField(3), new ReminderField(3), new ReminderField(3),
			new ReminderField(3), new ReminderField(3) };
	private final JCheckBox attuneBox = new JCheckBox("Requires Attunement");
	private RichEditor descriptionEditor;
	
	private final JCheckBox customBox = new JCheckBox("Custom");
	private final JComboBox<Source> sourceBox = new JComboBox<>(Source.values());
	private final JTextField subtypeField = new JTextField(15);
	private final JComboBox<Rarity> rarityBox = new JComboBox<>(Rarity.values());
	private JScrollPane descScroll;



	private final Map<String, MagicItem> itemMap = new LinkedHashMap<>();
	private final JPanel listPanel = new JPanel();
	private final DataContainer data;
	private JPanel wrapper;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frm = new JFrame();
			DataContainer data = new DataContainer();
			MagicItemBuilder mBuild = new MagicItemBuilder(data);
			frm.setContentPane(mBuild);
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {data.Exit();}
				public void windowClosed(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
			});
			frm.pack();
			frm.setVisible(true);
		});
	}

	public MagicItemBuilder(DataContainer data) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		this.data = data;
		descriptionEditor = new RichEditor(this.data) {
			public Dimension getPreferredSize() {
				// Force preferred height to a reasonable value
				Dimension size = super.getPreferredSize();
				size.height = 300;  // or whatever height you want
				return size;
			}
		};
		for (ReminderField field : costFields)
			field.setNumbersOnly();
		weightField.setNumbersOnly();

		// Left: List of items
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setPreferredSize(new Dimension(250, 0));
		add(scrollPane, BorderLayout.WEST);

		// Center: Form
		wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(new EmptyBorder(0, 10, 0, 0));
		wrapper.add(buildFormPanel(), BorderLayout.NORTH);
		descScroll = new JScrollPane(descriptionEditor);
		descScroll.setPreferredSize(new Dimension(400, 300));// or any suitable size
		wrapper.add(descScroll, BorderLayout.CENTER);
		add(new JScrollPane(wrapper), BorderLayout.CENTER);
	}

	private JPanel buildFormPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		int row = 0;

		gbc.gridx = 0;
		gbc.gridy = row;
		panel.add(new JLabel("Name:"), gbc);
		gbc.gridx = 1;
		panel.add(nameField, gbc);

//		row++;
//		gbc.gridx = 0;
//		gbc.gridy = row;
//		panel.add(new JLabel("Weight:"), gbc);
//		gbc.gridx = 1;
//		panel.add(weightField, gbc);
//
//		row++;
//		gbc.gridx = 0;
//		gbc.gridy = row;
//		panel.add(new JLabel("Cost:"), gbc);
//		gbc.gridx = 1;
//		JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
//		String[] labels = { "CP", "SP", "EP", "GP", "PP" };
//		for (int i = 0; i < 5; i++) {
//			costPanel.add(new JLabel(labels[i]));
//			costPanel.add(costFields[i]);
//		}
//		panel.add(costPanel, gbc);
		
		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(new JLabel("Subtype:"), gbc);
		gbc.gridx = 1;
		panel.add(subtypeField, gbc);

		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(new JLabel("Rarity:"), gbc);
		gbc.gridx = 1;
		panel.add(rarityBox, gbc);


		row++;
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 2;
		panel.add(attuneBox, gbc);
		
		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(new JLabel("Source:"), gbc);
		gbc.gridx = 1;
		sourceBox.setSelectedItem(Source.DungeonMastersGuide2024);
		panel.add(sourceBox, gbc);

		row++;
		gbc.gridx = 0; gbc.gridy = row;
		gbc.gridwidth = 2;
		panel.add(customBox, gbc);

		row++;
		gbc.gridy = row;
		JButton addButton = new JButton("Add Magic Item");
		addButton.addActionListener(this::handleAddItem);
		panel.add(addButton, gbc);

		return panel;
	}

	private void handleAddItem(ActionEvent e) {
		String name = nameField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Item name cannot be empty.");
			return;
		}

		MagicItem item = new MagicItem(name);
		try {
			item.weight = Integer.parseInt(weightField.getText().trim());
		} catch (NumberFormatException ignored) {
			item.weight = 0;
		}

		for (int i = 0; i < 5; i++) {
			try {
				item.costs[i] = Integer.parseInt(costFields[i].getText().trim());
			} catch (NumberFormatException ignored) {
				item.costs[i] = 0;
			}
		}

		item.atttune = attuneBox.isSelected();
		item.desc = descriptionEditor.getStyledDocument();
		item.custom = customBox.isSelected();
		item.source = (Source) sourceBox.getSelectedItem();
		item.subtype = subtypeField.getText().trim();
		item.rare = (Rarity) rarityBox.getSelectedItem();


		itemMap.put(name, item);
		updateItemList();
		clearForm();
	}

	private void clearForm() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.requestFocus();
		weightField.setText("");
		for (ReminderField field : costFields)
			field.setText("");
		attuneBox.setSelected(false);
		customBox.setSelected(false);
		sourceBox.setSelectedItem(Source.DungeonMastersGuide2024);
		subtypeField.setText("");
		rarityBox.setSelectedItem(Rarity.Common);

		ResetEditor(null);
	}

	private void updateItemList() {
		listPanel.removeAll();
		for (String name : itemMap.keySet()) {
			JPanel entry = new JPanel(new BorderLayout());
			JLabel nameLabel = new JLabel(name);
			nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
			nameLabel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(MagicItemBuilder.this, "Would you like to load " + 
							nameLabel.getText() + " you will lose any unadded progress.", "Load Confirm", 
							JOptionPane.YES_NO_OPTION);
					if(opt == JOptionPane.YES_OPTION) {
						LoadEdit(itemMap.get(nameLabel.getText()));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));}
			});
			JButton deleteBtn = new JButton("Delete");
			deleteBtn.addActionListener(e -> {
				itemMap.remove(name);
				updateItemList();
			});
			entry.add(nameLabel, BorderLayout.CENTER);
			entry.add(deleteBtn, BorderLayout.EAST);
			entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			listPanel.add(entry);
		}
		listPanel.revalidate();
		listPanel.repaint();
	}
	
	private void LoadEdit(MagicItem i) {
		nameField.setText(i.name);
		nameField.setEditable(false);
		weightField.setText("" + i.weight);
		
		costFields[Item.CP].setText("" + i.costs[Item.CP]);
		costFields[Item.SP].setText("" + i.costs[Item.SP]);
		costFields[Item.EP].setText("" + i.costs[Item.EP]);
		costFields[Item.GP].setText("" + i.costs[Item.GP]);
		costFields[Item.PP].setText("" + i.costs[Item.PP]);
		
		attuneBox.setSelected(i.atttune);
		customBox.setSelected(i.custom);
		sourceBox.setSelectedItem(i.source);
		subtypeField.setText(i.subtype != null ? i.subtype : "");
		rarityBox.setSelectedItem(i.rare != null ? i.rare : Rarity.Common);
		
		ResetEditor(i.desc);
	}
	
	private void ResetEditor(StyledDocument doc) {
		SwingUtilities.invokeLater(()->{
			wrapper.remove(descScroll);
			descriptionEditor = new RichEditor(data) {
				public Dimension getPreferredSize() {
					// Force preferred height to a reasonable value
					Dimension size = super.getPreferredSize();
					size.height = 300;  // or whatever height you want
					return size;
				}
			};
			if(doc != null)
				descriptionEditor.LoadDocument(doc);
			descScroll = new JScrollPane(descriptionEditor);
			descScroll.setPreferredSize(new Dimension(400, 300));
			wrapper.add(descScroll, BorderLayout.CENTER);
			
			
//			descriptionEditor = new RichEditor(data);
//			descriptionEditor.LoadDocument(i.desc);
//			wrapper.add(descriptionEditor, BorderLayout.CENTER);
			wrapper.revalidate();
			wrapper.repaint();
		});
	}
	
	public void LoadItems() {
		for(String s : data.getMagicItemKeysSorted())
			itemMap.put(s, (MagicItem) data.getItems().get(s));
		updateItemList();
	}

	public Map<String, MagicItem> getMagicItemMap() {
		return itemMap;
	}
}
