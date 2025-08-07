import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import data.DataContainer;
import gui.DnD_Database_Tool;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.LoadFrame;
import utils.ErrorLogger;

public class Runner
{
	//TODO: Re-add Rule Breaking Objects/Carrying Capacity/Condition/Damage Types/Dehydration
	//TODO: Continue Fixing Rules
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
				} catch (Exception e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			}
		});
	}
}