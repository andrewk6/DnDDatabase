package data.items;

import data.DataContainer.Abilities;

public final class ToolSet extends Item
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -207745338610981496L;

	public Abilities abil;
	
	public String utilize, craft;

	public ToolSet(String name) {
		super(name);
	}
}