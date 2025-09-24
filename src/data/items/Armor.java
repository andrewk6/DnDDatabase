package data.items;

public final class Armor extends Item
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1711249134494290094L;

	public enum ArmorType{
		LIGHT("Light"),
		MEDIUM("Medium"),
		HEAVY("Heavy"),
		SHIELD("Shield");
		
		private final String desc;
		
		ArmorType(String desc){
			this.desc = desc;
		}
		
		public String toString() {
			return desc;
		}
	}

	public int ac;
	public boolean stealthDisadv, addDexFull, addDexCap;
	public int minSTR;
	public ArmorType type;
	
	public Armor(String name) {
		super(name);
	}
	
}