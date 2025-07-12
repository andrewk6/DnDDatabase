package data.items;

import javax.swing.text.StyledDocument;

public final class MagicItem extends Item
{
	private static final long serialVersionUID = -293733487913646814L;

	public enum Rarity {
		Common, Uncommon, Rare, VeryRare, Legendary, Artifact, Special
	}
	
	public String subtype = "";
	public Rarity rare = Rarity.Common;
	public StyledDocument desc;
	public boolean atttune;
	
	public MagicItem(String name) {
		super(name);
	}
	
	public String getTypeString() {
		if(atttune) return subtype + ", " + prettifyRarity() + " (Requires Attunement)";
		else return subtype + ", " + prettifyRarity();
	}
	
	private String prettifyRarity() {
        return switch (rare) {
            case VeryRare -> "Very Rare";
            case Legendary -> "Legendary";
            case Uncommon -> "Uncommon";
            case Common -> "Common";
            case Rare -> "Rare";
            case Artifact -> "Artifact";
            case Special -> "Special";
        };
    }
}