package gui.dungeon.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.dungeon.DungeonFloor;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.ReminderField;

public class FloorDialog extends JDialog
{
	private static final long serialVersionUID = -2525060850451924323L;

	public DungeonFloor floor = null;
	
	private ReminderField nameField, rowField, colField;
	
	public FloorDialog() {
		super(null, "Floor Dialog", Dialog.ModalityType.APPLICATION_MODAL);
		this.setLayout(new BorderLayout());
		
		ConfigOptions();
		ConfigButtons();
	}
	
	private void ConfigOptions() {
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(0,2));
		add(mainPane);
		
		JLabel floorLabel = CompFactory.createNewLabel("Floor Name:", ComponentType.HEADER);
		mainPane.add(floorLabel);
		
		nameField = new ReminderField("Floor Name...");
		mainPane.add(nameField);
		
		JLabel rowLabel = CompFactory.createNewLabel("Row Num:", ComponentType.HEADER);
		mainPane.add(rowLabel);
		
		rowField = new ReminderField("Num. Rows...");
		rowField.setNumbersOnly();
		mainPane.add(rowField);
		
		JLabel colLabel = CompFactory.createNewLabel("Col Num:", ComponentType.HEADER);
		mainPane.add(colLabel);
		
		colField = new ReminderField("Num. Cols...");
		colField.setNumbersOnly();
		mainPane.add(colField);
	}
	
	private void ConfigButtons() {
		
	}
}