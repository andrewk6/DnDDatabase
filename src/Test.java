import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.Feat;
import data.DataContainer.Abilities;
import data.DataContainer.MapType;
import data.DataContainer.Proficiency;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.Monster;
import data.Rule;
import data.Spell;
import data.items.Item;
import data.items.MagicItem;
import data.items.Armor.ArmorType;
import data.players.classes.ClassAbility;
import data.players.classes.DnDClass;
import data.players.classes.Subclass;
import data.vehicles.Vehicle;
import data.players.classes.DnDClass.HitDiceType;
import data.players.classes.DnDClass.WeaponProficiency;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.RichEditorBase;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
//import javafx.css.Rule;
import gui.hazard.HazardPane;
import utils.ErrorLogger;

public class Test extends JFrame {	
	private static HashMap<String, StyledDocument> bastionDocs = new HashMap<String, StyledDocument>();
	
	public static void main(String[] args) throws BadLocationException {
		DataContainer data = new DataContainer();
		data.init();
		boolean gui = true;
		
		for(Vehicle v : data.getVehicles().values())
			System.out.println(v.src);
//		data.SafeSaveData(MapType.VEHICLES);
		if(gui)
			guiStuff(data);
		else
			data.Exit();
	}
	
	private static void guiStuff(DataContainer data) {
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
//			initFrame(frm.getContentPane(), data);
			frm.setContentPane(new HazardPane(
					data.getHazards().get("Spike Pit"), 
					data, 
					new GuiDirector(new JDesktopPane())));
			frm.setSize(800, 800);
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setVisible(true);
		});
	}
	
	private static void initFrame(Container cPane, DataContainer data) {

	}
	
	private static void SaveStuff(HashMap<String, DnDClass> out) {
		JFileChooser fChoose = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Class Database (*.clol)", "clol");
		fChoose.setFileFilter(filter);
		int save = fChoose.showSaveDialog(null);
		if(save == JFileChooser.APPROVE_OPTION) {
			File file = fChoose.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".txt")) {
				file = new File(file.getParentFile(), file.getName() + ".clol");
            }
			
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
				for (String s : out.keySet()) {
					oos.writeObject(out.get(s));
				}
				oos.flush();
				oos.close();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
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