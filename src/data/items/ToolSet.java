package data.items;

import data.DataContainer.Abilities;

public final class ToolSet extends Item
{
	public Abilities abil;
	
	public String utilize, craft;

	public ToolSet(String name) {
		super(name);
	}
}