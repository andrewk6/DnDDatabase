package data;

public interface DataChangeListener{
	public void onMapUpdated();
	
	public void onMapUpdated(int mapType);
}