package gui.gui_helpers;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DesktopManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import gui.campaign.PartyIFrame;
import gui.gui_helpers.structures.ColorTabbedPaneUI;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.structures.TaskbarDesktopManager;
import utils.ErrorLogger;

public class CompFactory
{
	public enum ComponentType{
		HEADER, BODY, BUTTON
	}
	
	public enum ScrollPolicy{
		VERTICAL, HORIZONTAL, BOTH
	}
	
	private final static DefaultListCellRenderer seperateItems = new DefaultListCellRenderer() {
	    @Override
	    public Component getListCellRendererComponent(JList<?> list,
	                                                  Object value,
	                                                  int index,
	                                                  boolean isSelected,
	                                                  boolean cellHasFocus) {
	        String display = value.toString();
	        if (index < list.getModel().getSize() - 1) {
	            display += " | "; 
	        }
	        JLabel label = (JLabel) super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
	        
	        // Use the font from the list itself or apply a custom one
	        label.setFont(list.getFont());
	        return label;
	    }
	};
	
	private static <T> void setFont(Component c, ComponentType font) {
		switch(font) {
		case ComponentType.HEADER: StyleContainer.SetFontHeader(c); break;
		default: StyleContainer.SetFontMain(c);
		}
	}
	
	public static JButton createNewButton(String text) {
		JButton btn = new JButton(text);
		StyleContainer.SetFontBtn(btn);
		
		return btn;
	}
	
	public static JButton createNewButton(String text, ActionListener act) {
		JButton btn = new JButton(text);
		StyleContainer.SetFontBtn(btn);
		btn.addActionListener(act);
		
		return btn;
	}
	
	public static JButton createNewButton(String text, Runnable act) {
		JButton btn = new JButton(text);
		StyleContainer.SetFontBtn(btn);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {act.run();}
		});
		
		return btn;
	}
	
	public static <T> JButton createDeleteButton(Map<String, T> map, String key, Runnable action) {
		JButton btn = createNewButton("Delete", _->{
			int delConf = JOptionPane.showConfirmDialog(null, "Delete " + key, 
					"Delete Confirm", JOptionPane.YES_NO_OPTION);
			if(delConf == JOptionPane.YES_OPTION) {
				map.remove(key);
				action.run();
			}			
		});
		btn.setFocusable(false);
		return btn;
	}
	
	public static JLabel createNewLabel(String text, ComponentType type) {
		JLabel lbl = new JLabel(text);		
		setFont(lbl, type);		
		return lbl;
	}
	
	public static JLabel createNewLabel(String text, ComponentType type, float fontMod) {
		JLabel lbl = createNewLabel(text, type);
		lbl.setFont(lbl.getFont().deriveFont(lbl.getFont().getSize() + fontMod));
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
	
	public static ReminderField createReminderField(String tooltip, boolean numbersOnly,
			int columns, ComponentType font) {
		ReminderField field = new ReminderField(tooltip);
		field.setColumns(columns);
		if(numbersOnly)
			field.setNumbersOnly();
		setFont(field, font);
		return field;
	}
	
	public static ReminderField createReminderField(String tooltip, boolean numbersOnly, ComponentType font) {
		ReminderField field = new ReminderField(tooltip);
		if(numbersOnly)
			field.setNumbersOnly();
		setFont(field, font);
		return field;
	}
	
	public static ReminderField createReminderField(String tooltip, int columns, ComponentType font) {
		ReminderField field = new ReminderField(tooltip);
		field.setColumns(columns);
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
	
	public static <T> JList<T> createJList(ListModel<T> model, ComponentType font){
		JList<T> list = new JList<T>(model);
		setFont(list, font);
		list.setCellRenderer(seperateItems);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		return list;
	}
	
	public static HoverTextPane createHoverTextPane(DataContainer data, GuiDirector gd, 
			StyledDocument doc, ComponentType font) {
		HoverTextPane hPane;
		if(gd == null)
			hPane = new HoverTextPane(data, gd, null);
		else
			hPane = new HoverTextPane(data, gd, gd.getDesktop());
		setFont(hPane, font);
		hPane.setDocument(doc);
		return hPane;
		
	}
	
	/**
	 * Creates a {@link JPanel} containing the given buttons arranged using a {@link FlowLayout}.
	 *
	 * @param layout  the alignment constant for the {@code FlowLayout}
	 *                (for example, {@link FlowLayout#LEFT}, {@link FlowLayout#CENTER},
	 *                or {@link FlowLayout#RIGHT})
	 * @param buttons the array of buttons to add to the panel; if {@code null} or empty,
	 *                the panel will be created with no buttons
	 * @return a {@code JPanel} with the specified layout containing the given buttons
	 */
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
			public void internalFrameClosing(InternalFrameEvent e) {
				iFrame.setVisible(false);
				((TaskbarDesktopManager)iFrame.getDesktopPane()
						.getDesktopManager()).repositionIcons(iFrame.getDesktopPane());
				iFrame.getDesktopPane().revalidate();
				iFrame.getDesktopPane().repaint();
			}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		};
	}
}