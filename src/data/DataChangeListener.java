package data;

import data.DataContainer.MapType;

public interface DataChangeListener{
	public void onMapUpdated();
	
	public void onMapUpdated(MapType mapType);
}