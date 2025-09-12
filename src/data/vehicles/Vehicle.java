package data.vehicles;

import java.io.Serializable;

public sealed class Vehicle implements Serializable permits Mount, LargeVehicle
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6674525387661399259L;
	public String name;
	public int cost;
}