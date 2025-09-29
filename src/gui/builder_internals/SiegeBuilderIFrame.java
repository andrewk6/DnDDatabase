package gui.builder_internals;

import javax.swing.JInternalFrame;

import builders.hazard_builder.HazardBuilderPane;
import builders.siege_builder.SiegeBuilderPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

public class SiegeBuilderIFrame extends JInternalFrame
{
	public SiegeBuilderIFrame(DataContainer data)
	{

		ConfigFrame();
		this.setContentPane(new SiegeBuilderPane(data));
		pack();
	}

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Siege Equipment Builder");
		StyleContainer.SetIcon(this, StyleContainer.SIEGE_BUILD_ICON_FILE);
	}	
}