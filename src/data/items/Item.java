package data.items;

import java.io.Serializable;

import data.DataContainer.Source;

public sealed class Item implements Serializable permits Weapon, Armor, ToolSet, Gear, MagicItem{
	private static final long serialVersionUID = 1L;
	
	public static final int CP = 0;
	public static final int SP = 1;
	public static final int EP = 2;
	public static final int GP = 3;
	public static final int PP = 4;
	
	public String name;
	public int weight;
	public int[] costs = new int[5];
	public boolean custom = false;
	public Source source = Source.PlayersHandbook2024;
	
	
	
	public Item(String name) {
		this.name = name;
	}
}