import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.Proficiency;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.Monster;
import data.Rule;
import data.items.Item;
import data.items.MagicItem;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.RichEditor;
//import javafx.css.Rule;

public class Test extends JFrame {	
	public static void main(String[] args) throws BadLocationException {
		DataContainer data = new DataContainer();
		data.init();
		System.out.println("R: " + data.getRuleKeysSorted().size());
		System.out.println("S: " + data.getSpellKeysSorted().size());
		System.out.println("M: " + data.getMonsterKeysSorted().size());
		data.Exit();
	}

    private static void printUID(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            ObjectStreamClass osc = ObjectStreamClass.lookup(clazz);
            long uid = osc.getSerialVersionUID();
            System.out.println(className + ": serialVersionUID = " + uid + "L;");
        } catch (Exception e) {
            System.err.println("Could not load class: " + className);
            e.printStackTrace();
        }
    }
}