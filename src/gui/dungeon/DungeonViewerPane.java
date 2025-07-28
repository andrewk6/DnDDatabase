package gui.dungeon;

import gui.dungeon.dialogs.EncounterDialog;
import gui.dungeon.dialogs.NoteDialog;
import gui.dungeon.tile.*;
import gui.dungeon.tile.Tile.TILE_TYPE;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.GuiDirector;

import javax.imageio.ImageIO;
import javax.swing.*;

import data.DataContainer;
import data.Monster;
import data.dungeon.DungeonNote;
import data.dungeon.EncounterNote;
import data.dungeon.NoteViewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class DungeonViewerPane extends JPanel {
	private static final int TILE_SIZE = 32;
	private static final int IMG_SCALER = 5;
	private int rows, cols;
	private Tile[][] tiles;
	private Tool currentTool = Tool.BRUSH;
	private Point dragStart = null, dragEnd = null;
	private boolean mouseDown = false;
	private Color currentColor = Color.WHITE;

	private final HashMap<String, Image> iconMap = new HashMap<String, Image>();
	private String currentIcon;
	
	private DataContainer data;
	private GuiDirector gd;
	private JTabbedPane parentTab;

	public DungeonViewerPane(DataContainer data, GuiDirector gd, JTabbedPane parentTab) {
		rows = 30;
		cols = 30;
		tiles = new Tile[rows][cols];
		
		Initialize(data, gd, parentTab);
		buildTiles();
	}
	public DungeonViewerPane(DataContainer data, GuiDirector gd, JTabbedPane parentTab, int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		tiles = new Tile[this.rows][this.cols];
		Initialize(data, gd, parentTab);
		buildTiles();
	}

	public DungeonViewerPane(DataContainer data, GuiDirector gd, JTabbedPane parentTab, Tile[][] tiles) {
		this.tiles = tiles;
		for(int i = 0; i < this.tiles.length; i++)
			for(int i2 = 0; i2 < this.tiles[i].length; i2 ++)
					System.out.println(this.tiles[i][i2].color);
		rows = tiles.length;
		cols = tiles[0].length;
		Initialize(data, gd, parentTab);
	}

	public void Initialize(DataContainer data, GuiDirector gd, JTabbedPane parentTab) {
		setSize();
		this.data = data;
		this.gd = gd;
		this.parentTab = parentTab;
		currentIcon = "NONE";
//		try {
//			loadIcons();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDown = true;
				int x = e.getX() / TILE_SIZE;
				int y = e.getY() / TILE_SIZE;
				applyTool(x, y);
			}
		};

		addMouseListener(ma);
	}

//	public void selectIcon(String iconKey) {
//		currentIcon = iconKey;
//		currentTool = Tool.ICON;
//	}
	
	private void setSize() {
		setPreferredSize(new Dimension(cols * TILE_SIZE + 20, rows * TILE_SIZE + 20));
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	private void buildTiles() {
		for (int i = 0; i < tiles.length; i++) {
			for (int i2 = 0; i2 < tiles[i].length; i2++) {
				tiles[i][i2] = new Tile();
			}
		}

	}

//	private void loadIcons() throws IOException {
//		iconMap.put("note", ImageIO.read(this.getClass().getResource("/note_icon.png")).getScaledInstance(TILE_SIZE,
//				TILE_SIZE, TILE_SIZE));
//		iconMap.put("trap", ImageIO.read(this.getClass().getResource("/trap_icon.png")).getScaledInstance(TILE_SIZE,
//				TILE_SIZE, TILE_SIZE));
//		iconMap.put("door", ImageIO.read(this.getClass().getResource("/door_icon.png")).getScaledInstance(TILE_SIZE,
//				TILE_SIZE, TILE_SIZE));
//		iconMap.put("locked_door", ImageIO.read(this.getClass().getResource("/locked_door_icon.png")).getScaledInstance(TILE_SIZE,
//				TILE_SIZE, TILE_SIZE));
//		iconMap.put("encounter", ImageIO.read(this.getClass().getResource("/monster_icon.png")).getScaledInstance(TILE_SIZE,
//				TILE_SIZE, TILE_SIZE));
//	}

	private void applyTool(int x, int y) {
		if (!isInBounds(x, y))
			return;
		if(tiles[y][x].type == TILE_TYPE.NOTE) {
			AddTab(new NoteViewer(data, gd, tiles[y][x].note), tiles[y][x].note.title);
		}
	}
	
	public void AddTab(JPanel pane, String tabName) {
		JPanel notePane = new JPanel();
		notePane.setLayout(new BorderLayout());
		parentTab.addTab(tabName, notePane);
		
		notePane.add(pane, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		btnPane.add(CompFactory.createNewButton("Remove Tab", _->{
			parentTab.removeTabAt(parentTab.indexOfComponent(notePane));
		}));
		notePane.add(btnPane, BorderLayout.SOUTH);
		parentTab.setSelectedIndex(parentTab.indexOfComponent(notePane));
	}

	private boolean isInBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < cols && y < rows;
	}
	
	public void loadFloor(Tile[][] newFloor) {
		this.tiles = newFloor;
		rows = tiles.length;
		cols = tiles[0].length;
		System.out.println("Rows/Cols: " + rows + "/" + cols);
		setSize();
		revalidate();
		repaint();
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				int px = x * TILE_SIZE;
				int py = y * TILE_SIZE;
				g.setColor(tiles[y][x] != null ? tiles[y][x].color : Color.WHITE);
				g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
				g.drawImage(tiles[y][x].icon, px, py, null);
			}
		}
	}


	public Tile getTileAtMouse(Point mousePoint) {
		int x = mousePoint.x / TILE_SIZE;
		int y = mousePoint.y / TILE_SIZE;

		if (x < 0 || y < 0 || y >= tiles.length || x >= tiles[0].length) {
			return null; // Mouse is off the grid
		}

		return tiles[y][x];
	}
}
