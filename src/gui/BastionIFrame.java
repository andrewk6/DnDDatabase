package gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import gui.bastions.BastionPane;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class BastionIFrame extends JInternalFrame
{
	private DataContainer data;
	private GuiDirector gd;
	
	public BastionIFrame(DataContainer data, GuiDirector guiD) {
		this.data = data;
		gd = guiD;

		ConfigFrame();
		BuildContent(this.getContentPane());
	}

	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Bastion Database");
		StyleContainer.SetIcon(this, StyleContainer.BASTION_ICON_FILE);
		
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {}
		});
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void BuildContent(Container cPane) {
		cPane.setLayout(new BorderLayout());
		cPane.add(new BastionPane(data, gd), BorderLayout.CENTER);
	}
}