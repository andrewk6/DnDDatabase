package gui.builder_internals;

import javax.swing.JInternalFrame;
import builders.rule_builder.RuleBuildPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

public class RuleBuilderIFrame extends JInternalFrame{
	/**
	 * Create the application.
	 */
	public RuleBuilderIFrame(DataContainer d) {
		ConfigFrame();
		this.setContentPane(new RuleBuildPane(d));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void ConfigFrame() {
		StyleContainer.ConfigIFrame(this, "Rule Builder");
		StyleContainer.SetIcon(this, StyleContainer.RULE_BUILDER_ICON_FILE);
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
	}
}
