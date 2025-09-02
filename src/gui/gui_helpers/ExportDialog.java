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
	private  JCheckBox rules, spells, monsters, items, feats, classes;
	public boolean export;
	public File expoTarget;
	
	
	public ExportDialog(JFrame frm) {
		super(frm, "Create Subclass", ModalityType.APPLICATION_MODAL);
		expoTarget = null;
		init(this.getContentPane());
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
		
		feats = CompFactory.createNewCheckbox("Export Feats");
		config.add(feats);
		
		classes = CompFactory.createNewCheckbox("Export Classes");
		config.add(classes);
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
			}
		});
		btnPane.add(fileBtn);
		
		JButton expoBtn = CompFactory.createNewButton("Export", _->{
			if(expoTarget != null) {
				export = true;
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
	
	public void Reset() {
		rules.setSelected(false);
		spells.setSelected(false);
		monsters.setSelected(false);
		items.setSelected(false);
		feats.setSelected(false);
		classes.setSelected(false);
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


	public boolean getFeats() {
		return feats.isSelected();
	}


	public boolean getClasses() {
		return classes.isSelected();
	}
}