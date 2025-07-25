package gui.dungeon;

import gui.dungeon.dialogs.FloorDialog;
import gui.dungeon.tile.*;
import gui.dungeon.tile.Tile.TILE_TYPE;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.DataContainer;
import data.dungeon.Dungeon;
import data.dungeon.DungeonFloor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class DungeonIBuilder extends JInternalFrame {

	private JScrollPane editScroll;
	private HashMap<String, DungeonEditorPane> editor;
	private String curEditKey;
	private JMenuBar menuBar;
	private JMenu toolMenu, iconMenu, floorMenu; 
	private JScrollPane sideScroll;
	private JPanel editPane;

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
		
		editPane = new JPanel();
		add(editPane, BorderLayout.CENTER);
		
		configMenu();
		addBtnPane();
	}
	
//	private void addNewEditor(Tile[][] tiles) {
//		if(editScroll != null)
//			remove(editScroll);
//		if(tiles == null)
//			editor = new DungeonEditorPane(data);
//		else
//			editor = new DungeonEditorPane(data, tiles);
//		editScroll = new JScrollPane(editor);
//		editScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		editScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		add(editScroll, BorderLayout.CENTER);
//	}
	
	private void addEditor(String key) {
		curEditKey = key;
		editPane.removeAll();
		editPane.add(editor.get(curEditKey));
		toolMenu.setEnabled(true);
		iconMenu.setEnabled(true);
		revalidate();
		repaint();
	}

	private void addBtnPane() {
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(btnPane, BorderLayout.SOUTH);
		
		JButton newDungeonBtn = CompFactory.createNewButton("New Dungeonr", e->{
			String dungeonName = JOptionPane.showInputDialog("What is the dungeon's name:");
			d = new Dungeon();
			d.dungeonName = dungeonName;
			curEditKey = null;
			if(editor != null)
				editor.clear();
			else
				editor = new HashMap<String, DungeonEditorPane>();
			toolMenu.setEnabled(false);
			iconMenu.setEnabled(false);
			floorMenu.setEnabled(true);
		});
		btnPane.add(newDungeonBtn);

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
					oos.writeObject(editor.get(curEditKey).getTiles());
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
					d = (Dungeon) ois.readObject();
					floorMenu.setEnabled(true);
					if(d.floors.size() > 0) {
//						for(Stin)
					}
					SwingUtilities.invokeLater(()->{
//						addNewEditor(tiles);
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
		toolMenu = new JMenu("Tools");
		StyleContainer.SetFontHeader(toolMenu);
		toolMenu.setEnabled(false);
		
		ButtonGroup toolGroup = new ButtonGroup();
		for (Tool t : Tool.values()) {
			String toolName = t.toString().replace("_FILL", " Fill").replace("_", " ");
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(toolName);
			StyleContainer.SetFontMain(item);
			item.addActionListener(_ -> editor.get(curEditKey).setTool(t));
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
			editor.get(curEditKey).setCurrentColor(Color.WHITE);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(whiteSelect);
		
		JMenuItem greenSelect = CompFactory.createNewJMenuItem("Green", _->{
			editor.get(curEditKey).setCurrentColor(Color.GREEN);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(greenSelect);
		
		JMenuItem redSelect = CompFactory.createNewJMenuItem("Red", _->{
			editor.get(curEditKey).setCurrentColor(Color.RED);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(redSelect);
		
		JMenuItem blueSelect = CompFactory.createNewJMenuItem("Blue", _->{
			editor.get(curEditKey).setCurrentColor(Color.BLUE);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(blueSelect);
		
		JMenuItem orangeSelect = CompFactory.createNewJMenuItem("Orange", _->{
			editor.get(curEditKey).setCurrentColor(Color.ORANGE);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(orangeSelect);
		
		JMenuItem cyanSelect = CompFactory.createNewJMenuItem("Cyan", _->{
			editor.get(curEditKey).setCurrentColor(Color.CYAN);
			editor.get(curEditKey).setTool(Tool.BRUSH);
		});
		colorMenu.add(cyanSelect);
		
		JMenuItem pinkSelect = CompFactory.createNewJMenuItem("Pink", _->{
			editor.get(curEditKey).setCurrentColor(Color.GREEN);
			editor.get(curEditKey).setTool(Tool.BRUSH);
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
		iconMenu = new JMenu("Icons");
		StyleContainer.SetFontHeader(iconMenu);
		iconMenu.setEnabled(false);

//		JMenuItem doorItem = new JMenuItem("Place Door");
		JMenuItem doorItem = CompFactory.createNewJMenuItem("Place Door");
		doorItem.addActionListener(e -> {
			editor.get(curEditKey).selectIcon("door");
			toolGroup.clearSelection();
		});
		iconMenu.add(doorItem);

		
		JMenuItem lockedDoorItem = CompFactory.createNewJMenuItem("Place Locked Door");
		lockedDoorItem.addActionListener(e -> {
			editor.get(curEditKey).selectIcon("locked_door");
			toolGroup.clearSelection();
		});
		iconMenu.add(lockedDoorItem);

//		JMenuItem trapItem = new JMenuItem("Place Trap");
		JMenuItem trapItem = CompFactory.createNewJMenuItem("Place Trap");
		trapItem.addActionListener(e -> {
			editor.get(curEditKey).selectIcon("trap");
			toolGroup.clearSelection();
		});
		iconMenu.add(trapItem);

//		JMenuItem monsterItem = new JMenuItem("Place Encounter");
		JMenuItem monsterItem = CompFactory.createNewJMenuItem("Place Encounter");
		monsterItem.addActionListener(e -> {
			editor.get(curEditKey).selectIcon("encounter");
			toolGroup.clearSelection();
		});
		iconMenu.add(monsterItem);

//		JMenuItem noteItem = new JMenuItem("Place Note");
		JMenuItem noteItem = CompFactory.createNewJMenuItem("Place Note");
		noteItem.addActionListener(e -> {
			editor.get(curEditKey).selectIcon("note");
			toolGroup.clearSelection();
		});
		iconMenu.add(noteItem);
		menuBar.add(iconMenu);
		
		floorMenu = new JMenu("Floors");
		StyleContainer.SetFontHeader(floorMenu);
		menuBar.add(floorMenu);
		
		floorMenu.add(CompFactory.createNewJMenuItem("Add Floor", _->{
			FloorDialog fd = new FloorDialog();
			fd.setVisible(true);
			if(fd.floor != null) {
				d.floors.put(fd.floorName, fd.floor);
				editor.put(fd.floorName, new DungeonEditorPane(data, fd.floor.tiles));
				fillSidePane();
			}
		}));
		floorMenu.setEnabled(false);
		

		setJMenuBar(menuBar);
	}
	
	public void fillSidePane() {
		SwingUtilities.invokeLater(()->{
			if(sideScroll != null)
				remove(sideScroll);
			JPanel sidePane = new JPanel();
			sidePane.setLayout(new GridLayout(0,1));
			sideScroll = new JScrollPane(sidePane);
			sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			add(sideScroll, BorderLayout.WEST);
			if(d != null) {
				if(d.floors.size() > 0)
					for(String s : d.floors.keySet()) {
//						System.out.println(s);
						JLabel lbl = CompFactory.createNewLabel(s, ComponentType.BODY);
						lbl.addMouseListener(new MouseListener() {
							public void mouseClicked(MouseEvent e) {
								addEditor(s);
							}
							public void mousePressed(MouseEvent e) {}
							public void mouseReleased(MouseEvent e) {}
							public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
							public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
						});
						sidePane.add(lbl);
					}
				revalidate();
				repaint();
			}
		});
		
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
	
	public static void setMenuBarEnabled(JMenuBar menuBar, boolean enabled) {
	    for (int i = 0; i < menuBar.getMenuCount(); i++) {
	        JMenu menu = menuBar.getMenu(i);
	        if (menu != null) {
	            menu.setEnabled(enabled);
	            for (int j = 0; j < menu.getItemCount(); j++) {
	                JMenuItem item = menu.getItem(j);
	                if (item != null) {
	                    item.setEnabled(enabled);
	                }
	            }
	        }
	    }
	}

	// Simple square color icon for menu
//	static class ColorIcon implements Icon {
//		private final Color color;
//		private final int size = 12;
//
//		ColorIcon(Color color) {
//			this.color = color;
//		}
//
//		@Override
//		public int getIconWidth() {
//			return size;
//		}
//
//		@Override
//		public int getIconHeight() {
//			return size;
//		}
//
//		@Override
//		public void paintIcon(Component c, Graphics g, int x, int y) {
//			g.setColor(color);
//			g.fillRect(x, y, size, size);
//			g.setColor(Color.BLACK);
//			g.drawRect(x, y, size, size);
//		}
//	}
}
