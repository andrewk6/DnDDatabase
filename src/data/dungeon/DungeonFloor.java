package data.dungeon;

import java.io.Serializable;

import javax.swing.text.StyledDocument;

import gui.dungeon.tile.Tile;

public class DungeonFloor implements Serializable
{
	private static final long serialVersionUID = -6674574119482233926L;
	public Tile[][] tiles;
	public String floorName;
	
	public DungeonFloor(int rows, int cols) {
		tiles = new Tile[rows][cols];
	}
	
	public DungeonFloor(Tile[][]tiles) {
		this.tiles = tiles;
	}
	
	public void fillEmpty() {
		for(int x = 0; x < tiles.length; x ++)
			for(int y = 0; y < tiles[x].length; y ++)
				tiles[x][y] = new Tile();
	}
}