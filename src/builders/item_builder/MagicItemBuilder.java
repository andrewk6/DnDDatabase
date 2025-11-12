package builders.item_builder;

import data.DataContainer;
import data.DataContainer.Source;
import data.items.Item;
import data.items.MagicItem;
import data.items.MagicItem.Rarity;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class MagicItemBuilder extends JPanel {

	private final ReminderField nameField = 
			CompFactory.createReminderField("Item name..", 15, ComponentType.HEADER);
	private final ReminderField weightField = CompFactory.createReminderField("Weight...", ComponentType.BODY);
	private final ReminderField[] costFields = { 
			CompFactory.createReminderField("", 3, ComponentType.BODY), 
			CompFactory.createReminderField("", 3, ComponentType.BODY),
			CompFactory.createReminderField("", 3, ComponentType.BODY),
			CompFactory.createReminderField("", 3, ComponentType.BODY), 
			CompFactory.createReminderField("", 3, ComponentType.BODY)
			};
	private final JCheckBox attuneBox = CompFactory.createNewCheckbox("Requires Attunement");
	private RichEditor descriptionEditor;
	
	private final JComboBox<Source> sourceBox = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
	private final ReminderField subtypeField = CompFactory.createReminderField("Subtype...", 15, ComponentType.BODY);
	private final JComboBox<Rarity> rarityBox = CompFactory.createEnumCombo(Rarity.class, ComponentType.BODY);
	private JScrollPane descScroll;



	private Map<String, MagicItem> itemMap;
	private final JPanel listPanel = new JPanel();
	private final DataContainer data;
	private JPanel wrapper;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frm = new JFrame();
			DataContainer data = new DataContainer();
			data.init();
			MagicItemBuilder mBuild = new MagicItemBuilder(data);
			mBuild.LoadItems();
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
		itemMap = new HashMap<String, MagicItem>();
		
		descriptionEditor = new RichEditor(this.data) {
			public Dimension getPreferredSize() {
				// Force preferred height to a reasonable value
				Dimension size = super.getPreferredSize();
				size.height = 300;  // or whatever height you want
				return size;
			}
		};
		for (ReminderField field : costFields) {
			field.setNumbersOnly();
			field.setColumns(3);
		}
		weightField.setNumbersOnly();

		// Left: List of items
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = CompFactory.wrapPanelInScroll(listPanel, ScrollPolicy.VERTICAL);
//		scrollPane.setPreferredSize(new Dimension(250, 0));
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
		panel.add(CompFactory.createNewLabel("Name:", ComponentType.HEADER), gbc);
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
		panel.add(CompFactory.createNewLabel("Subtype:", ComponentType.HEADER), gbc);
		gbc.gridx = 1;
		panel.add(subtypeField, gbc);

		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(CompFactory.createNewLabel("Rarity:", ComponentType.HEADER), gbc);
		gbc.gridx = 1;
		panel.add(rarityBox, gbc);


		row++;
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 2;
		panel.add(attuneBox, gbc);
		
		row++;
		gbc.gridx = 0; gbc.gridy = row;
		panel.add(CompFactory.createNewLabel("Source:", ComponentType.HEADER), gbc);
		gbc.gridx = 1;
		sourceBox.setSelectedItem(Source.DungeonMastersGuide2024);
		panel.add(sourceBox, gbc);

		row++;
		gbc.gridy = row;
		JButton addButton = CompFactory.createNewButton("Add Magic Item", this::handleAddItem);
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
		subtypeField.setText("");
		rarityBox.setSelectedItem(Rarity.Common);

		ResetEditor(null);
	}

	private void updateItemList() {
		listPanel.removeAll();
		ArrayList<String> keys = new ArrayList<String>(itemMap.keySet());
		Collections.sort(keys);
		
		for (String name : keys) {
			JPanel entry = new JPanel(new BorderLayout());
			JLabel nameLabel;
			if(name.length() > StyleContainer.SIDE_STRING_LIMIT)
				nameLabel = CompFactory.createNewLabel(name.substring(0,
						StyleContainer.SIDE_STRING_LIMIT), ComponentType.BODY);
			else
				nameLabel = CompFactory.createNewLabel(name, ComponentType.BODY);
			nameLabel.setToolTipText(name);
			nameLabel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int opt = JOptionPane.showConfirmDialog(MagicItemBuilder.this, "Would you like to load " + 
							name + " you will lose any unadded progress.", "Load Confirm", 
							JOptionPane.YES_NO_OPTION);
					if(opt == JOptionPane.YES_OPTION) {
						LoadEdit(itemMap.get(name));
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));}
			});
			JButton deleteBtn = new JButton("Delete");
			deleteBtn.addActionListener(_ -> {
				itemMap.remove(name);
				updateItemList();
			});
			entry.add(nameLabel, BorderLayout.CENTER);
			entry.add(deleteBtn, BorderLayout.EAST);
//			entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
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
		return new HashMap<String, MagicItem>(itemMap);
	}
}
