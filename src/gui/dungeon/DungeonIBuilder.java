package gui.dungeon;

import gui.dungeon.tile.*;
import gui.dungeon.tile.Tile.TILE_TYPE;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.DataContainer;
import data.dungeon.Dungeon;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DungeonIBuilder extends JInternalFrame {

	private JScrollPane editScroll;
	private DungeonEditorPane editor;
	private JMenuBar menuBar;

	private final JFileChooser fChoose = new JFileChooser();
	private final DataContainer data;
	
	private Dungeon d;

	public DungeonIBuilder(DataContainer data) {
		super("Dungeon Builder", true, true, true, true);
		setSize(800, 600);
		setLayout(new BorderLayout());
		this.data = data;

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dungeon Files (*.dol)", "dol");
		fChoose.setFileFilter(filter);

		configMenu();
		addBtnPane();
	}
	
	private void addNewEditor(Tile[][] tiles) {
		if(editScroll != null)
			remove(editScroll);
		if(tiles == null)
			editor = new DungeonEditorPane(data);
		else
			editor = new DungeonEditorPane(data, tiles);
		editScroll = new JScrollPane(editor);
		editScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		editScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(editScroll, BorderLayout.CENTER);
	}

	private void addBtnPane() {
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(btnPane, BorderLayout.SOUTH);
		
		JButton addFloorBtn = CompFactory.createNewButton("Add Floor", e->{
			String floorName = JOptionPane.showInputDialog("What is the floor's name:");
			
		});

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
		btnPane.add(saveBtn);

		JButton loadBtn = CompFactory.createNewButton("Load", _ -> {
			int result = fChoose.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fChoose.getSelectedFile();

				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile));
					Tile[][] tiles = (Tile[][]) ois.readObject();
					SwingUtilities.invokeLater(()->{
						addNewEditor(tiles);
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
		btnPane.add(loadBtn);
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
			}
		}

		toolMenu.addSeparator();

		JMenu colorMenu = new JMenu("Color Selection");
		StyleContainer.SetFontMain(colorMenu);
		toolMenu.add(colorMenu);
		
		JMenuItem whiteSelect = CompFactory.createNewJMenuItem("White", _->{
			editor.setCurrentColor(Color.WHITE);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(whiteSelect);
		
		JMenuItem greenSelect = CompFactory.createNewJMenuItem("Green", _->{
			editor.setCurrentColor(Color.GREEN);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(greenSelect);
		
		JMenuItem redSelect = CompFactory.createNewJMenuItem("Red", _->{
			editor.setCurrentColor(Color.RED);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(redSelect);
		
		JMenuItem blueSelect = CompFactory.createNewJMenuItem("Blue", _->{
			editor.setCurrentColor(Color.BLUE);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(blueSelect);
		
		JMenuItem orangeSelect = CompFactory.createNewJMenuItem("Orange", _->{
			editor.setCurrentColor(Color.ORANGE);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(orangeSelect);
		
		JMenuItem cyanSelect = CompFactory.createNewJMenuItem("Cyan", _->{
			editor.setCurrentColor(Color.CYAN);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(cyanSelect);
		
		JMenuItem pinkSelect = CompFactory.createNewJMenuItem("Pink", _->{
			editor.setCurrentColor(Color.GREEN);
			editor.setTool(Tool.BRUSH);
		});
		colorMenu.add(pinkSelect);
		
//		JMenuItem colorPickerItem = CompFactory.createNewJMenuItem("Color Picker");
//		final ColorIcon colorIcon = new ColorIcon(editor.getCurrentColor());
//		colorPickerItem.setIcon(colorIcon);
//
//		colorPickerItem.addActionListener(e -> {
//			Color newColor = JColorChooser.showDialog(this, "Choose Tile Color", editor.getCurrentColor());
//			if (newColor != null) {
//				editor.setCurrentColor(newColor);
//				colorPickerItem.setIcon(new ColorIcon(newColor));
//			}
//		});
//
//		toolMenu.add(colorPickerItem);
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
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(() -> {

			JFrame frame = new JFrame("Dungeon Editor Test");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setSize(1000, 800);
			frame.addWindowListener(CompFactory.createSafeExitWindowListener(frame, data));

			JDesktopPane desktop = new JDesktopPane();
			DungeonIBuilder dungeon = new DungeonIBuilder(data);
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
