package data.items;

public final class Armor extends Item
{
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