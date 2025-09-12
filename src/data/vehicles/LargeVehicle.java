package data.vehicles;

public final class LargeVehicle extends Vehicle
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2993530211613978455L;

	public final static String CARGO_UNIT = "Tons";
	
	public double speed, cargo;
	public int crew, passengers, ac, hp, damageThreshold;
}