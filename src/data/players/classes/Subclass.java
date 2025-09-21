package data.players.classes;

import java.io.Serializable;
import java.util.HashMap;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public class Subclass implements Serializable, SourceProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5018964977099133228L;
	
	public String name;
	public HashMap<String, ClassAbility> abilities;
	
	public Source src;

	@Override
	public Source getSource() {
		return src;
	}
	
	
}