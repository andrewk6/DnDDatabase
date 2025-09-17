package gui.builder_internals;

import javax.swing.JInternalFrame;
import builders.hazard_builder.HazardBuilderPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

public class HazardBuilderIFrame extends JInternalFrame
{
	public HazardBuilderIFrame(DataContainer data)
	{

		ConfigFrame();
		this.setContentPane(new HazardBuilderPane(data));
		pack();
	}

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Hazard Builder");
		StyleContainer.SetIcon(this, StyleContainer.HAZARD_BUILD_ICON_FILE);
	}	
}