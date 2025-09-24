package data.items;

public final class Poison extends Gear
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5660867857355449250L;	
	public enum Poison_Type{
		INHALED_POISON("Inhaled Poison"),
		CONTACT_POISON("Contact Poison"),
		INGESTED_POISON("Ingested Poison"),
		INJURY_POISON("Injury Poison");
		
		private final String desc;
		Poison_Type(String desc) {
			this.desc = desc;
		}
		
		public String toString() {
			return desc;
		}
	}
	
	public Poison_Type poisonType;

	public Poison(String name) {
		super(name);
	}
	
}