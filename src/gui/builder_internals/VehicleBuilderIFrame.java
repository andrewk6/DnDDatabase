package gui.builder_internals;

import javax.swing.JInternalFrame;
import builders.vehicle_builder.VehicleBuilderPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

public class VehicleBuilderIFrame extends JInternalFrame
{
	public VehicleBuilderIFrame(DataContainer data)
	{

		ConfigFrame();
		this.setContentPane(new VehicleBuilderPane(data));
		pack();
	}

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Species Room Builder");
		StyleContainer.SetIcon(this, StyleContainer.SPECIES_BUILDER_ICON_FILE);
	}	
}