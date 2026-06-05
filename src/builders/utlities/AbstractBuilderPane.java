package builders.utlities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.DataContainer;
import data.siege_equipment.SiegeEquipment;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.RichEditor;

public abstract class AbstractBuilderPane<T> extends JPanel
{
	protected final DataContainer data;
	protected HashMap<String, T> map;
	
	protected JPanel sidePane, mainPane;
	
	public AbstractBuilderPane(DataContainer data) {
		super();
		this.data = data;
		
		BuildPane();
	}
	
	protected void FillSidePane() {
		sidePane.removeAll();
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		SortKeys(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createSideLabel(key);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				Load(key);
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createDeleteButton(map, key, null);
			pane.add(delBtn, BorderLayout.EAST);
		}
		
		if(map.size() == 1) {
			this.revalidate();
			this.repaint();
		}
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	protected void Reset(String load) {
		int conf = JOptionPane.YES_OPTION;
		if(load != null) {
			conf = JOptionPane.showConfirmDialog(this, 
					"Reset the editor to load: " + load + ", you will lose unsaved work?", 
					"Reset Confirm", JOptionPane.YES_NO_OPTION);
		}
		if(conf == JOptionPane.YES_OPTION) {
			ResetFields();
		}
	}
	
	protected void ResetConfirm() {
		int conf = JOptionPane.showConfirmDialog(this, 
				"Reset the editor, you will lose unsaved work?", "Reset Confirm", JOptionPane.YES_NO_OPTION);
		if(conf == JOptionPane.YES_OPTION) {
			Reset(null);
		}
	}
	
	protected abstract void SortKeys(ArrayList<String> keys);
	
	protected abstract void BuildPane();
	
	protected abstract void ResetFields();
	
	protected abstract void AddObj();
	
	protected abstract void Load(String s);
	
	protected abstract void Save();
}