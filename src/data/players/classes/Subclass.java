package data.players.classes;

import java.io.Serializable;
import java.util.HashMap;

import data.DataContainer.Source;

public class Subclass implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5018964977099133228L;
	
	public String name;
	public HashMap<String, ClassAbility> abilities;
	
	public Source src;
}