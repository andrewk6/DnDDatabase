package gui.gui_helpers;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import gui.campaign.PartyIFrame;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import utils.ErrorLogger;

public class CompFactory
{
	public enum ComponentType{
		HEADER, BODY, BUTTON
	}
	
	public enum ScrollPolicy{
		VERTICAL, HORIZONTAL, BOTH
	}
	
	private static <T> void setFont(Component c, ComponentType font) {
		switch(font) {
		case ComponentType.HEADER: StyleContainer.SetFontHeader(c); break;
		default: StyleContainer.SetFontMain(c);
		}
	}
	
	public static JButton createNewButton(String text, ActionListener act) {
		JButton btn = new JButton(text);
		StyleContainer.SetFontBtn(btn);
		btn.addActionListener(act);
		
		return btn;
	}
	
	public static JLabel createNewLabel(String text, ComponentType type) {
		JLabel lbl = new JLabel(text);		
		setFont(lbl, type);		
		return lbl;
	}
	
	public static InfoLabel createNewInfoLabel(String text, int textLength, ComponentType type) {
		InfoLabel lbl = new InfoLabel(text, textLength);
		setFont(lbl, type);
		return lbl;
	}
	
	
	public static JMenuItem createNewJMenuItem(String text) {
		JMenuItem out = new JMenuItem(text);
		StyleContainer.SetFontMain(out);
		return out;
	}
	
	public static JMenuItem createNewJMenuItem(String text, ActionListener act) {
		JMenuItem out = new JMenuItem(text);
		StyleContainer.SetFontMain(out);
		out.addActionListener(act);
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
		out.addActionListener(_ ->{
			try {
				frm.setVisible(true);
				frm.setIcon(false);   // Restore if minimized/iconified
				frm.toFront();         // Bring to front visually
				frm.setSelected(true);
			} catch (PropertyVetoException e1) {
				ErrorLogger.log(e1);
				e1.printStackTrace();
			} // Make it active/focused
		});
		return out;
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
	
	public static ReminderField createReminderField(String tooltip, boolean numbersOnly, ComponentType font) {
		ReminderField field = new ReminderField(tooltip);
		if(numbersOnly)
			field.setNumbersOnly();
		setFont(field, font);
		return field;
	}
	
	public static ReminderField createReminderField(String tooltip, ComponentType font) {
		ReminderField field = new ReminderField(tooltip);
		setFont(field, font);
		return field;
	}
	
	public static JTabbedPane createTabbedPane() {
		JTabbedPane tabs = new JTabbedPane();
		tabs = new JTabbedPane();
		ColorTabbedPaneUI tabsUI = new ColorTabbedPaneUI();
		tabs.setUI(tabsUI);
		
		return tabs;
	}
	
	public static <T> JComboBox<T> createCombo(Class<T> type, List<T> items, ComponentType font) {
	    JComboBox<T> combo = new JComboBox<>(new 
	    		DefaultComboBoxModel<>(items.toArray((T[]) java.lang.reflect.Array.newInstance(type, 0))));
	    setFont(combo, font);
	    return combo;
	}
	
	public static <T extends Enum<T>> JComboBox<T> createEnumCombo(Class<T> enumType, ComponentType font) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("Class must be an enum type");
        }

        JComboBox<T> combo = new JComboBox<>(enumType.getEnumConstants());
        setFont(combo, font);
        return combo;
    }
	
	public static HoverTextPane createHoverTextPane(DataContainer data, GuiDirector gd, 
			StyledDocument doc, ComponentType font) {
		HoverTextPane hPane = new HoverTextPane(data, gd, gd.getDesktop());
		setFont(hPane, font);
		hPane.setDocument(doc);
		return hPane;
		
	}
	
	public static JPanel createButtonFlowPane(int layout, JButton[] buttons) {
		JPanel out = new JPanel();
		out.setLayout(new FlowLayout(layout));
		for(JButton b : buttons) {
			out.add(b);
		}
		return out;
	}
	
	public static JScrollPane wrapPanelInScroll(JPanel pane, ScrollPolicy pol) {
		JScrollPane out = new JScrollPane(pane);
		if(pol == ScrollPolicy.VERTICAL) {
//			out.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			out.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}else if(pol == ScrollPolicy.HORIZONTAL) {
			out.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			out.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}else {
			out.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			out.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return out;
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
	
	public static MouseListener createSideMouseListener(JLabel lbl, Runnable r) {
		return new MouseListener() {
			public void mouseClicked(MouseEvent e) {r.run();}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
			public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
		};
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
}