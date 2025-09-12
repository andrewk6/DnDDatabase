package gui.gui_helpers;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.gui_helpers.CompFactory.ComponentType;

public class ExportDialog extends JDialog
{
	private  JCheckBox rules, spells, monsters, items, vehicles, 
	feats, classes, species, background, bastionRooms;
	private JLabel fileLbl;
	public boolean export;
	public File expoTarget;
	
	
	public ExportDialog(JFrame frm) {
		super(frm, "Create Subclass", ModalityType.APPLICATION_MODAL);
		expoTarget = null;
		init(this.getContentPane());
		this.pack();
	}


	private void init(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JPanel config = new JPanel();
		config.setLayout(new GridLayout(0,1));
		cPane.add(config, BorderLayout.CENTER);
		
		rules = CompFactory.createNewCheckbox("Export Rules");
		config.add(rules);
		
		spells = CompFactory.createNewCheckbox("Export Spells");
		config.add(spells);
		
		monsters = CompFactory.createNewCheckbox("Export Monsters");
		config.add(monsters);
		
		items = CompFactory.createNewCheckbox("Export Items");
		config.add(items);
		
		vehicles = CompFactory.createNewCheckbox("Export Vehicles");
		config.add(vehicles);
		
		classes = CompFactory.createNewCheckbox("Export Classes");
		config.add(classes);
		
		feats = CompFactory.createNewCheckbox("Export Feats");
		config.add(feats);
		
		background = CompFactory.createNewCheckbox("Export Background");
		config.add(background);
		
		species = CompFactory.createNewCheckbox("Export Species");
		config.add(species);
		
		bastionRooms = CompFactory.createNewCheckbox("Export Bastion Rooms");
		config.add(bastionRooms);
		
		fileLbl = CompFactory.createNewLabel("No File Loaded", ComponentType.BODY);
		config.add(fileLbl);
		
		Reset();
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout());
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton fileBtn = CompFactory.createNewButton("Export Location", _->{
			JFileChooser fChoose = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Export Files (*.exol)", "exol");
			fChoose.setFileFilter(filter);
			
			int approve = fChoose.showSaveDialog(this);
			if(approve == JFileChooser.APPROVE_OPTION) {
				File file = fChoose.getSelectedFile();
				
			    if (!file.getName().toLowerCase().endsWith(".exol")) {
			        file = new File(file.getAbsolutePath() + ".exol");
			    }
			    
			    expoTarget = file;
			    fileLbl.setText("File: " + expoTarget.getName());
			}
		});
		btnPane.add(fileBtn);
		
		JButton expoBtn = CompFactory.createNewButton("Export", _->{
			if(expoTarget != null && minSelected()) {
				export = true;
				setVisible(false);
			}else {
				Reset();
				setVisible(false);
			}
		});
		btnPane.add(expoBtn);
		
		JButton cancelBtn = CompFactory.createNewButton("Cancel", _->{
			Reset();
			setVisible(false);
		});
		btnPane.add(cancelBtn);
	}
	
	private boolean minSelected() {
		return rules.isSelected() || spells.isSelected() || monsters.isSelected()
				|| items.isSelected() || feats.isSelected() || classes.isSelected();
	}
	
	public void Reset() {
		rules.setSelected(false);
		spells.setSelected(false);
		monsters.setSelected(false);
		items.setSelected(false);
		feats.setSelected(false);
		classes.setSelected(false);
		fileLbl.setText("No File Loaded");
		export = false;
		expoTarget = null;
	}
	
	public void openDialog() {
		Reset();
		this.setVisible(true);
	}


	public boolean getRules() {
		return rules.isSelected();
	}


	public boolean getSpells() {
		return spells.isSelected();
	}


	public boolean getMonsters() {
		return monsters.isSelected();
	}


	public boolean getItems() {
		return items.isSelected();
	}

	public boolean getVehicles() {
		return vehicles.isSelected();
	}

	public boolean getFeats() {
		return feats.isSelected();
	}


	public boolean getClasses() {
		return classes.isSelected();
	}
	
	public boolean getBackgrounds() {
		return background.isSelected();
	}
	
	public boolean getSpecies() {
		return species.isSelected();
	}
	
	public boolean getBastionRooms() {
		return bastionRooms.isSelected();
	}
}