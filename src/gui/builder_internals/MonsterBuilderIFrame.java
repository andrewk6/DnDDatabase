package gui.builder_internals;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import builders.monster_builder.MonsterBuilderPane;
import data.*;
import gui.gui_helpers.structures.StyleContainer;

public class MonsterBuilderIFrame extends JInternalFrame {
	public MonsterBuilderIFrame(DataContainer data) {
		ConfigureFrame();
		this.setContentPane(new MonsterBuilderPane(data));
	}

	private void ConfigureFrame() {
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setSize(800, 800);
		this.setTitle("Monster Builder");
		StyleContainer.SetIcon(this, StyleContainer.MONSTER_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
	}
}