package data.hazards;

import java.io.Serializable;
import java.util.EnumMap;

import javax.swing.text.StyledDocument;

import data.DataContainer.PartyTier;
import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public class Hazard implements Serializable, SourceProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3430204560013587655L;
	public enum HazardDanger {Nuisance, Deadly};
	
	public String name;
	public StyledDocument desc;
	public EnumMap<PartyTier, HazardDanger> dangerMap;
	public Source src;
	
	public Source getSource() {
		return src;
	}
}