//public class 
package builders.spell_builder;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.StyleContainer;

@SuppressWarnings("serial")
public class SpellBuilder extends JFrame {
	public static void main(String[] args) {
		DataContainer data = new DataContainer();
		data.init();
		EventQueue.invokeLater(() -> {
			SpellBuilder appFrame = new SpellBuilder(data);
			appFrame.setVisible(true);
		});
	}

	public SpellBuilder(DataContainer data) {
		StyleContainer.SetLookAndFeel();
		this.addWindowListener(CompFactory.createSafeExitWindowListener(this, data));
		this.setContentPane(new SpellBuilderPane(data));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setSize(new Dimension(800,800));
	}
}