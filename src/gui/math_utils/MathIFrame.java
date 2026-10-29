package gui.math_utils;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import gui.gui_helpers.structures.StyleContainer;

@SuppressWarnings("serial")
public class MathIFrame extends JInternalFrame
{
	public MathIFrame() {
		ConfigFrame();
		BuildContent(getContentPane());
		pack();
	}
	
	private void ConfigFrame() {
		getContentPane().setLayout(new BorderLayout());
		StyleContainer.ConfigIFrame(this, "Math Tools");
		StyleContainer.SetIcon(this, StyleContainer.MATH_TOOLS_ICON_FILE);
		
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
		
		JTabbedPane tabs = new JTabbedPane();
		cPane.add(tabs, BorderLayout.CENTER);
		
		tabs.addTab("Hypotenuse Calculator", new HypoPane());
	}
}