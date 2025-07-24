package gui.dungeon;

import gui.dungeon.tile.*;
import gui.dungeon.tile.Tile.TILE_TYPE;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;

public class DungeonEditorPane extends JPanel {
	private static final int TILE_SIZE = 32;
	private static final int IMG_SCALER = 5;
	private final int rows, cols;
	private final Tile[][] tiles;
	private Tool currentTool = Tool.BRUSH;
	private Point dragStart = null, dragEnd = null;
	private boolean mouseDown = false;
	private Color currentColor = Color.WHITE;

	private final HashMap<String, Image> iconMap = new HashMap<String, Image>();
	private String currentIcon;

	public DungeonEditorPane() {
		rows = 30;
		cols = 30;
		tiles = new Tile[rows][cols];
		Initialize();
		buildTiles();
	}
	public DungeonEditorPane(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		tiles = new Tile[this.rows][this.cols];
		Initialize();
		buildTiles();
	}

	public DungeonEditorPane(Tile[][] tiles) {
		this.tiles = tiles;
		for(int i = 0; i < this.tiles.length; i++)
			for(int i2 = 0; i2 < this.tiles[i].length; i2 ++)
					System.out.println(this.tiles[i][i2].color);
		rows = tiles.length;
		cols = tiles[0].length;
		Initialize();
	}

	public void Initialize() {
		setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));

		currentIcon = "NONE";
		try {
			loadIcons();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDown = true;
				int x = e.getX() / TILE_SIZE;
				int y = e.getY() / TILE_SIZE;
				dragStart = new Point(x, y);
				dragEnd = new Point(x, y);
				applyTool(x, y);
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDown = false;
				int x = e.getX() / TILE_SIZE;
				int y = e.getY() / TILE_SIZE;
				dragEnd = new Point(x, y);
				if (EnumSet.of(Tool.RECT_FILL, Tool.LINE_FILL, Tool.HEX_FILL, Tool.FLOOD_FILL).contains(currentTool)) {
					applyDragTool();
					dragStart = dragEnd = null;
				}
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX() / TILE_SIZE;
				int y = e.getY() / TILE_SIZE;
				if ((currentTool == Tool.BRUSH || currentTool == Tool.ERASER)) {
					applyTool(x, y);
				} else {
					dragEnd = new Point(x, y);
				}
				repaint();
			}
		};

		addMouseListener(ma);
		addMouseMotionListener(ma);
	}

	public void setTool(Tool tool) {
		this.currentTool = tool;
	}

	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color c) {
		currentColor = c;
	}

	public void selectIcon(String iconKey) {
		currentIcon = iconKey;
		currentTool = Tool.ICON;
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

	private void loadIcons() throws IOException {
		iconMap.put("note", ImageIO.read(this.getClass().getResource("/note_icon.png")).getScaledInstance(TILE_SIZE,
				TILE_SIZE, TILE_SIZE));
		iconMap.put("trap", ImageIO.read(this.getClass().getResource("/trap_icon.png")).getScaledInstance(TILE_SIZE,
				TILE_SIZE, TILE_SIZE));
		iconMap.put("door", ImageIO.read(this.getClass().getResource("/door_icon.png")).getScaledInstance(TILE_SIZE,
				TILE_SIZE, TILE_SIZE));
	}

	private void applyTool(int x, int y) {
		if (!isInBounds(x, y))
			return;
		switch (currentTool) {
		case BRUSH:
			tiles[y][x].color = currentColor;
			break;
		case ERASER:
			tiles[y][x] = null;
			break;
		case ICON:
			tiles[y][x].icon = iconMap.get(currentIcon);
			currentTool = Tool.SELECT;
			setTileType(currentIcon, tiles[y][x]);
			break;
		case SELECT:
			if(tiles[y][x].type == TILE_TYPE.NOTE) {
				
			}else if(tiles[y][x].type == TILE_TYPE.MONSTER) {
				
			}
		}
	}

	private void setTileType(String curIcon, Tile tile) {
		if (curIcon.equals("note") || curIcon.equals("trap") || curIcon.equals("door"))
			tile.type = TILE_TYPE.NOTE;
	}

	private void applyDragTool() {
		if (dragStart == null || dragEnd == null)
			return;
		int x1 = dragStart.x, y1 = dragStart.y, x2 = dragEnd.x, y2 = dragEnd.y;
		switch (currentTool) {
		case RECT_FILL -> fillRect(x1, y1, x2, y2);
		case LINE_FILL -> drawLine(x1, y1, x2, y2);
		case FLOOD_FILL -> floodFill(x1, y1, tiles[y1][x1].color, currentColor);
		case HEX_FILL -> fillHex(x1, y1, x2, y2);
		}
	}

	private void fillRect(int x1, int y1, int x2, int y2) {
		int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
		int minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				if (isInBounds(x, y))
					tiles[y][x].color = currentColor;
			}
		}
	}

	private void drawLine(int x1, int y1, int x2, int y2) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = x1 < x2 ? 1 : -1;
		int sy = y1 < y2 ? 1 : -1;
		int err = dx - dy;

		while (true) {
			if (isInBounds(x1, y1))
				tiles[y1][x1].color = currentColor;
			if (x1 == x2 && y1 == y2)
				break;
			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x1 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y1 += sy;
			}
		}
	}

	private void floodFill(int x, int y, Color target, Color replacement) {
		if (!isInBounds(x, y) || tiles[y][x].color == replacement || tiles[y][x].color != target)
			return;
		tiles[y][x].color = replacement;
		floodFill(x + 1, y, target, replacement);
		floodFill(x - 1, y, target, replacement);
		floodFill(x, y + 1, target, replacement);
		floodFill(x, y - 1, target, replacement);
	}

	private void fillHex(int x1, int y1, int x2, int y2) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int radius = Math.max(dx, dy);
		for (int y = y1 - radius; y <= y1 + radius; y++) {
			for (int x = x1 - radius; x <= x1 + radius; x++) {
				if (isInBounds(x, y) && (Math.abs(x - x1) + Math.abs(y - y1)) <= radius) {
					tiles[y][x].color = currentColor;
				}
			}
		}
	}

	private boolean isInBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < cols && y < rows;
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

		// Draw shape preview if dragging with shape tool
		if (mouseDown && dragStart != null && dragEnd != null
				&& EnumSet.of(Tool.RECT_FILL, Tool.LINE_FILL, Tool.HEX_FILL).contains(currentTool)) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(new Color(0, 0, 0, 80));
			g2.setStroke(new BasicStroke(2));
			int x1 = dragStart.x * TILE_SIZE, y1 = dragStart.y * TILE_SIZE;
			int x2 = dragEnd.x * TILE_SIZE, y2 = dragEnd.y * TILE_SIZE;
			switch (currentTool) {
			case RECT_FILL -> g2.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + TILE_SIZE,
					Math.abs(y2 - y1) + TILE_SIZE);
			case LINE_FILL ->
				g2.drawLine(x1 + TILE_SIZE / 2, y1 + TILE_SIZE / 2, x2 + TILE_SIZE / 2, y2 + TILE_SIZE / 2);
			case HEX_FILL -> {
				int radius = Math.max(Math.abs(dragEnd.x - dragStart.x), Math.abs(dragEnd.y - dragStart.y));
				Point center = new Point(dragStart.x * TILE_SIZE + TILE_SIZE / 2,
						dragStart.y * TILE_SIZE + TILE_SIZE / 2);
				Polygon hex = createHexPolygon(center.x, center.y, radius * TILE_SIZE);
				g2.drawPolygon(hex);
			}
			}
			g2.dispose();
		}
	}

	private Polygon createHexPolygon(int xCenter, int yCenter, int radius) {
		Polygon hex = new Polygon();
		for (int i = 0; i < 6; i++) {
			double angleRad = Math.toRadians(60 * i);
			int x = xCenter + (int) (radius * Math.cos(angleRad));
			int y = yCenter + (int) (radius * Math.sin(angleRad));
			hex.addPoint(x, y);
		}
		return hex;
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
