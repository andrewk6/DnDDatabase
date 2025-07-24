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
}