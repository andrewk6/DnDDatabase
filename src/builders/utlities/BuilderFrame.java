package builders.utlities;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import builders.spell_builder.SpellBuilderPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

public class BuilderFrame<K, T extends AbstractBuilderPane<K>> extends JFrame
{
	public BuilderFrame(DataContainer data, T pane, Class<K> type) {
		StyleContainer.SetLookAndFeel();
		this.addWindowListener(CompFactory.createSafeExitWindowListener(this, data));
		this.setContentPane(pane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setTitle(type.getSimpleName() + " Builder");
		this.pack();
		this.setSize(new Dimension(800,800));
	}
}