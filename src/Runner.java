import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import data.DataContainer;
import gui.DnD_Database_Tool;
import gui.gui_helpers.LoadFrame;
import utils.ErrorLogger;

/*
 * Classes Icon
 * Feats Icon
 * Dungeon Builder
 * Dungeon Viewer
 * Notes Editor
 * Class Builder
 * Feat Builder
 * 
 */
//TODO: Re-namable players
public class Runner
{
	private static DataContainer data;
	private static LoadFrame load;
	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(()->{
			load = new LoadFrame("Loading");
			load.setVisible(true);
		});
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					data = new DataContainer();
					data.init();
					
					DnD_Database_Tool frame = new DnD_Database_Tool(data);
					frame.registerLoadListener(load);
					frame.init();
					frame.setVisible(true);
					data.buildExportDialog(frame);
				} catch (Exception e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			}
		});
	}
}