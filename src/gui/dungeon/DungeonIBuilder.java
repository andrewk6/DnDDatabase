package gui.dungeon;

import gui.dungeon.tile.*;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DungeonIBuilder extends JInternalFrame {

	private DungeonEditorPane editor;
	private JMenuBar menuBar;

	private final JFileChooser fChoose = new JFileChooser();

	public DungeonIBuilder() {
		super("Dungeon Builder", true, true, true, true);
		setSize(800, 600);
		setLayout(new BorderLayout());

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dungeon Files (*.dol)", "dol");
		fChoose.setFileFilter(filter);

		editor = new DungeonEditorPane();
		add(editor, BorderLayout.CENTER);

		configMenu();
		addIOPane();
	}

	private void addIOPane() {
		JPanel ioPane = new JPanel();
		ioPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(ioPane, BorderLayout.SOUTH);

		JButton saveBtn = CompFactory.createNewButton("Save", _ -> {
			int result = fChoose.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fChoose.getSelectedFile();

				if (!selectedFile.getName().toLowerCase().endsWith(".dol")) {
					selectedFile = new File(selectedFile.getAbsolutePath() + ".dol");
				}

				try {
					if (!selectedFile.exists())
						selectedFile.createNewFile();

					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile));
					oos.writeObject(editor.getTiles());
					oos.flush();
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		ioPane.add(saveBtn);

		JButton loadBtn = CompFactory.createNewButton("Load", _ -> {
			int result = fChoose.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fChoose.getSelectedFile();

				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile));
					Tile[][] tiles = (Tile[][]) ois.readObject();
					SwingUtilities.invokeLater(()->{
						remove(editor);
						editor = new DungeonEditorPane(tiles);
						add(editor, BorderLayout.CENTER);
						revalidate();
						repaint();
					});
					
					ois.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		ioPane.add(loadBtn);
	}
	
	private void configToolbar() {
		
	}

	private void configMenu() {
		menuBar = new JMenuBar();
		// Tool Menu
		JMenu toolMenu = new JMenu("Tools");
		StyleContainer.SetFontHeader(toolMenu);
		ButtonGroup toolGroup = new ButtonGroup();
		for (Tool t : Tool.values()) {
			String toolName = t.toString().replace("_FILL", " Fill").replace("_", " ");
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(toolName);
			StyleContainer.SetFontMain(item);
			item.addActionListener(_ -> editor.setTool(t));
			if (t != Tool.ICON) {
				toolGroup.add(item);
				toolMenu.add(item);
			}
			if (t == Tool.SELECT) {
				item.setSelected(true);
				editor.setTool(t);
			}
		}

		toolMenu.addSeparator();

		JMenuItem colorPickerItem = CompFactory.createNewJMenuItem("Color Picker");
		final ColorIcon colorIcon = new ColorIcon(editor.getCurrentColor());
		colorPickerItem.setIcon(colorIcon);

		colorPickerItem.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(this, "Choose Tile Color", editor.getCurrentColor());
			if (newColor != null) {
				editor.setCurrentColor(newColor);
				colorPickerItem.setIcon(new ColorIcon(newColor));
			}
		});

		toolMenu.add(colorPickerItem);
		menuBar.add(toolMenu);

		// Icon Menu
		JMenu iconMenu = new JMenu("Icons");
		StyleContainer.SetFontHeader(iconMenu);

//		JMenuItem doorItem = new JMenuItem("Place Door");
		JMenuItem doorItem = CompFactory.createNewJMenuItem("Place Door");
		doorItem.addActionListener(e -> {
			editor.selectIcon("door");
			toolGroup.clearSelection();
		});
		iconMenu.add(doorItem);

		
		JMenuItem lockedDoorItem = CompFactory.createNewJMenuItem("Place Locked Door");
		lockedDoorItem.addActionListener(e -> {
			editor.selectIcon("locked_door");
			toolGroup.clearSelection();
		});
		iconMenu.add(lockedDoorItem);

//		JMenuItem trapItem = new JMenuItem("Place Trap");
		JMenuItem trapItem = CompFactory.createNewJMenuItem("Place Trap");
		trapItem.addActionListener(e -> {
			editor.selectIcon("trap");
			toolGroup.clearSelection();
		});
		iconMenu.add(trapItem);

//		JMenuItem monsterItem = new JMenuItem("Place Encounter");
		JMenuItem monsterItem = CompFactory.createNewJMenuItem("Place Encounter");
		monsterItem.addActionListener(e -> {
			editor.selectIcon("encounter");
			toolGroup.clearSelection();
		});
		iconMenu.add(monsterItem);

//		JMenuItem noteItem = new JMenuItem("Place Note");
		JMenuItem noteItem = CompFactory.createNewJMenuItem("Place Note");
		noteItem.addActionListener(e -> {
			editor.selectIcon("note");
			toolGroup.clearSelection();
		});
		iconMenu.add(noteItem);
		menuBar.add(iconMenu);

		setJMenuBar(menuBar);
	}

	public static void main(String[] args) {
		StyleContainer.SetLookAndFeel();
		SwingUtilities.invokeLater(() -> {

			JFrame frame = new JFrame("Dungeon Editor Test");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1000, 800);

			JDesktopPane desktop = new JDesktopPane();
			DungeonIBuilder dungeon = new DungeonIBuilder();
			desktop.add(dungeon);
			dungeon.setVisible(true);

			frame.add(desktop);
			frame.setVisible(true);
		});
	}

	// Simple square color icon for menu
	static class ColorIcon implements Icon {
		private final Color color;
		private final int size = 12;

		ColorIcon(Color color) {
			this.color = color;
		}

		@Override
		public int getIconWidth() {
			return size;
		}

		@Override
		public int getIconHeight() {
			return size;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x, y, size, size);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, size, size);
		}
	}
}
