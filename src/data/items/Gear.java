package data.items;

public sealed class Gear extends Item permits Poison
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1461112189909826131L;
	public String description;

	public Gear(String name) {
		super(name);
	}
	
}