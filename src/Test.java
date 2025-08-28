import java.awt.BorderLayout;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
import data.DataContainer.Abilities;
import data.DataContainer.Proficiency;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.Monster;
import data.Rule;
import data.items.Item;
import data.items.MagicItem;
import data.items.Armor.ArmorType;
import data.players.classes.ClassAbility;
import data.players.classes.DnDClass;
import data.players.classes.Subclass;
import data.players.classes.DnDClass.HitDiceType;
import data.players.classes.DnDClass.WeaponProficiency;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;
//import javafx.css.Rule;
import utils.ErrorLogger;

public class Test extends JFrame {	
	public static void main(String[] args) throws BadLocationException {
		DataContainer data = new DataContainer();
		data.init();
		
		HashMap<String, DnDClass> classMap = new HashMap<String, DnDClass>();
		
		/*
		 * public enum HitDiceType {d12, d10, d8, d6};
	public enum WeaponProficiency {Simple, All, SimpleMartialFinesseLight};
	
	public HashMap<String, ClassAbility> abilities; 
	public HashMap<String, Subclass> subclasses;
	
	public String name;
	public StyledDocument desc;
	
	public HitDiceType hd;
	public WeaponProficiency weaponProf;
	public Abilities primaryAbility;
	public Abilities[] saveingThrows = new Abilities[2];
	
	
	public ArrayList<Skills> startProf;
	public ArrayList<ArmorType> armorProf;
	
	public ArrayList<Item> startingEquip;
	public int startingGoldNoEquip, startingGoldEquip;
	public int numStartSkills;
		 */
//		for(PlayerClass p : data.getClasses().values()) {
//			DnDClass c = new DnDClass();
//			c.abilities = p.abilities;
//			HashMap<String, Subclass> subs = new HashMap<String, Subclass>();
//			for(String s : p.subAbilities.keySet()) {
//				Subclass sub = new Subclass();
//				sub.name = s;
//				sub.src = Source.PlayersHandbook2024;
//				sub.abilities = p.subAbilities.get(s);
//				subs.put(s, sub);
//			}
//			c.subclasses = subs;
//			c.name = p.name;
//			c.desc = p.desc;
//			c.hd = DnDClass.HitDiceType.valueOf(p.hd.toString());
//			c.weaponProf = DnDClass.WeaponProficiency.valueOf(p.weaponProf.toString());
//			c.primaryAbility = p.primaryAbility;
//			c.savingThrows = p.saveingThrows;
//			c.startProf = p.startProf;
//			c.armorProf = p.armorProf;
//			c.startingEquip = p.startingEquip;
//			c.startingGoldNoEquip = p.startingGoldNoEquip;
//			c.startingGoldEquip = p.startingGoldEquip;
//			c.numStartSkills = p.numStartSkills;
//			c.src = Source.PlayersHandbook2024;
//			classMap.put(c.name, c);
//		}
//		
//		for(DnDClass c : classMap.values())
//			System.out.println(c.name);
//		SaveStuff(classMap);
		data.Exit();
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