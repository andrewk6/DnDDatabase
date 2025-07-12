package gui.gui_helpers.structures;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.JTextComponent;

import data.DataContainer;
import gui.gui_helpers.CustomDesktopIcon;

public class StyleContainer {
	public final static Font BTN_FONT_MAIN = new Font("Monospaced", Font.BOLD, 24);
	public final static Font FNT_BODY_PLAIN = new Font("Monospaced", Font.PLAIN, 16);
	public final static Font FNT_HEADER_BOLD = new Font("Monospaced", Font.BOLD, 18);
	
	public final static String RULE_ICON_FILE = "rule_ico.png";
	public final static String SPELL_ICON_FILE = "spell_ico.png";
	public final static String BACKGROUND_FILE = "background.jpg";
	public final static String PROGRAM_ICON_FILE = "main_icon.png";
	public final static String MONSTER_ICON_FILE = "monster_ico.png";
	public final static String ITEM_ICON_FILE = "items_ico.png";
	public final static String FULL_ICON_FILE = "full_ico.png";
	public final static String INIT_ICON_FILE = "init_ico.png";
	public final static String PARTY_ICON_FILE = "party_ico.png";
	public final static String DICE_CALC_ICON_FILE = "dice_calc_ico.png";
	public static final String RULE_BUILDER_ICON_FILE = "builder_rule_ico.png";
	public static final String SPELL_BUILDER_ICON_FILE = "builder_spell_ico.png";
	public static final String MONSTER_BUILDER_ICON_FILE = "builder_monster_ico.png";
	public static final String ITEM_BUILDER_ICON_FILE = "builder_item_ico.png";
	public final static int ICON_SIZE = 64;

	public static void SetLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("ComboBox.disabledForeground", UIManager.getColor("ComboBox.foreground"));
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public static void SetIcon(JInternalFrame iFrame, String file) {
		try {

			BufferedImage iconImage = ImageIO.read(iFrame.getClass().getResource("/" + file));
			iFrame.setDesktopIcon(new CustomDesktopIcon(iFrame, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(file));
			iFrame.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(file));
			iFrame.setFrameIcon(icon);
			System.out.println("IO Exception");
		}
	}

	public static void SetFontMain(Component comp) {
		if (comp instanceof JLabel || comp instanceof AbstractButton || // JButton, JToggleButton, JCheckBox,
																		// JRadioButton
				comp instanceof JTextComponent || comp instanceof JComboBox) {
			comp.setFont(FNT_BODY_PLAIN);
		}
	}

	public static void SetFontHeader(Component comp) {
		if (comp instanceof JLabel || comp instanceof AbstractButton || // JButton, JToggleButton, JCheckBox,
																		// JRadioButton
				comp instanceof JTextComponent || comp instanceof JComboBox) {
			comp.setFont(FNT_HEADER_BOLD);
		}
	}

	public static void SetFontBtn(Component comp) {
		if (comp instanceof AbstractButton) {
			comp.setFont(BTN_FONT_MAIN);
		}
	}

	public static void setAllNonFocusable(Container container) {
		for (Component comp : container.getComponents()) {
			comp.setFocusable(false);
			if (comp instanceof Container) {
				setAllNonFocusable((Container) comp);
			}
		}
	}

	public static WindowListener GetDefaultCloseListener(DataContainer data) {
		return new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				data.Exit();
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}
	
	public static int getStringWidth(Font font, String text) {
	    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = img.createGraphics();
	    g2.setFont(font);
	    FontMetrics fm = g2.getFontMetrics();
	    int width = fm.stringWidth(text);
	    g2.dispose(); // always dispose graphics context
	    return width;
	}
}