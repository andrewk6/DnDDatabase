package gui.gui_helpers;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import gui.campaign.PartyIFrame;
import gui.gui_helpers.structures.StyleContainer;

public class CompFactory
{
	public enum ComponentType{
		HEADER, BODY, BUTTON
	}
	public static JButton createNewButton(String text, ActionListener act) {
		JButton btn = new JButton(text);
		StyleContainer.SetFontBtn(btn);
		btn.addActionListener(act);
		
		return btn;
	}
	
	public static JLabel createNewLabel(String text, ComponentType type) {
		JLabel lbl = new JLabel(text);
		
		switch(type) {
		case ComponentType.HEADER: StyleContainer.SetFontHeader(lbl);
		default: StyleContainer.SetFontMain(lbl);
		}
		
		return lbl;
	}
	
	public static JMenuItem createNewJMenuItem(String text) {
		JMenuItem out = new JMenuItem(text);
		StyleContainer.SetFontMain(out);
		return out;
	}
	
	public static JMenuItem createNewJMenuItem(String text, Supplier<JInternalFrame> fSup, JDesktopPane dPane) {
		JMenuItem out = new JMenuItem(text);
		StyleContainer.SetFontMain(out);
		out.addActionListener(e ->{
			JInternalFrame frm = fSup.get();
			dPane.add(frm);
			frm.setVisible(true);
		});
		return out;
	}
	
	public static JMenuItem createNewJMenuItem(String text, JInternalFrame frm) {
		JMenuItem out = new JMenuItem(text);
		StyleContainer.SetFontMain(out);
		out.addActionListener(e ->{
			try {
				frm.setVisible(true);
				frm.setIcon(false);   // Restore if minimized/iconified
				frm.toFront();         // Bring to front visually
				frm.setSelected(true);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			} // Make it active/focused
		});
		return out;
	}
	
	public static InternalFrameListener createNonCloseListener(JInternalFrame iFrame) {
		if(iFrame.getDefaultCloseOperation() != JInternalFrame.DO_NOTHING_ON_CLOSE)
			iFrame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		return new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {iFrame.setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		};
	}
	
	public static WindowListener createSafeExitWindowListener(JFrame frame, DataContainer d) {
		return new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				d.Exit();
				frame.dispose();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}
	
	public static WindowListener createSafeExitWindowListener(JFrame frame, DataContainer d, Runnable r) {
		return new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				r.run();
				d.Exit();
				frame.dispose();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}

	public static JCheckBox createNewCheckbox(String string, ActionListener act) {
		JCheckBox cBox = new JCheckBox(string);
		StyleContainer.SetFontMain(cBox);
		cBox.addActionListener(act);
		return cBox;
	}
	
	public static JCheckBox createNewCheckbox(String string) {
		JCheckBox cBox = new JCheckBox(string);
		StyleContainer.SetFontMain(cBox);
		return cBox;
	}
}