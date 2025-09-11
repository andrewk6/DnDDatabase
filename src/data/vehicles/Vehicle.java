package data.vehicles;

import java.io.Serializable;

public sealed class Vehicle implements Serializable permits Mount, LargeVehicle
{
	public String name;
	public int cost;
}