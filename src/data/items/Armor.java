package data.items;

public final class Armor extends Item
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1711249134494290094L;

	public enum ArmorType{
		LIGHT,
		MEDIUM,
		HEAVY,
		SHIELD
	}

	public int ac;
	public boolean stealthDisadv, addDexFull, addDexCap;
	public int minSTR;
	public ArmorType type;
	
	public Armor(String name) {
		super(name);
	}
	
}