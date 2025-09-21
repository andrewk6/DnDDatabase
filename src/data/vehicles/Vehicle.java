package data.vehicles;

import java.io.Serializable;

import data.DataContainer.Source;
import data.interfaces.SourceProvider;

public sealed class Vehicle implements Serializable, SourceProvider permits Mount, LargeVehicle
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6674525387661399259L;
	public String name;
	public int cost;
	
	public Source src;

	public Source getSource() {
		return src;
	}
}