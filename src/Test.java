import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import data.DataContainer;
import data.players.classes.DnDClass;
import data.vehicles.Vehicle;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.structures.GuiDirector;
//import javafx.css.Rule;
import gui.hazard.HazardPane;
import utils.ErrorLogger;

public class Test extends JFrame {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	@SuppressWarnings("unused")
	private static void initFrame(Container cPane, DataContainer data) {

	}
	
	@SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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