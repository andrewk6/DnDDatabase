package builders.class_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.players.Ability.UseType;
import data.players.classes.ClassAbility;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;

public class AbilityPaneBuilder extends JPanel
{
	private DataContainer data;
	private HashMap<String, ClassAbility> abilities;
	
	private JPanel sidePane, mPane;
	private RichEditor edit;
	private ReminderField nameField, lvlField;
	private JComboBox<UseType> useTypeCombo;
	
	public AbilityPaneBuilder(DataContainer data, HashMap<String, ClassAbility> abilities) 
	{
		this.abilities = abilities;
		this.data = data;
		
		Initialize();
	}
	
	private void Initialize() {
		this.setLayout(new BorderLayout());
		BuildBaseAbilitiesPane();
		FillSidePane();
	}
	
	private void BuildBaseAbilitiesPane() {
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		FillSidePane();
		JScrollPane sideScroll = new JScrollPane(sidePane);
		this.add(sideScroll, BorderLayout.WEST);
		
		mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		this.add(mPane, BorderLayout.CENTER);
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		mPane.add(topPane, BorderLayout.NORTH);
		
		nameField = CompFactory.createReminderField("Ability name...", ComponentType.HEADER);
		topPane.add(nameField, BorderLayout.CENTER);
		
		JPanel configPane = new JPanel();
		configPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPane.add(configPane, BorderLayout.SOUTH);
		
		JLabel level = CompFactory.createNewLabel("Level: ", ComponentType.HEADER);
		configPane.add(level);
		
		lvlField = CompFactory.createReminderField("Level unlocked...", true, ComponentType.BODY);
		lvlField.setColumns(5);
		configPane.add(lvlField);
		
		JLabel useLbl = CompFactory.createNewLabel("Ability Use: ", ComponentType.HEADER);
		configPane.add(useLbl);
		
		useTypeCombo = CompFactory.createEnumCombo(UseType.class, ComponentType.BODY);
		configPane.add(useTypeCombo);
		
		edit = new RichEditor(data);
		data.registerListener(edit);
		mPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton addBtn = CompFactory.createNewButton("Add Ability", _->{
			if(nameField.getText().length() > 0) {
				AddAbility();
				ResetEditor();
				FillSidePane();
			}else {
				JOptionPane.showMessageDialog(this, "Please at least name the ability", 
						"No Name Warning", JOptionPane.WARNING_MESSAGE);
			}
			
		});
		btnPane.add(addBtn);
		
		JButton resetBtn = CompFactory.createNewButton("Reset Editor", _->{
			ResetEditor();
		});
		btnPane.add(resetBtn);
	}
	
	private void ResetEditor() {
		mPane.remove(edit);
		
		data.deregisterListener(edit);
		edit = new RichEditor(data);
		data.registerListener(edit);
		mPane.add(edit, BorderLayout.CENTER);
		
		nameField.setText("");
		lvlField.setText("");
		
		mPane.revalidate();
		mPane.repaint();
		
		nameField.setEditable(true);
		nameField.setFocusable(true);
		nameField.requestFocus();
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
//		ArrayList<String> keys = new ArrayList<String>(abilities.keySet());
		List<ClassAbility> keys = new ArrayList<ClassAbility>(abilities.values());
		Collections.sort(keys);
		for(ClassAbility s : keys) {
			JPanel pane = new JPanel();
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			pane.setLayout(new BorderLayout());
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(s.name, ComponentType.BODY);
			lbl.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {loadAbility(s.name);}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createNewButton("Delete", _->{
				int conf = JOptionPane.showConfirmDialog(this, "Delete: " + s.name, 
						"Delete Confirm", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					abilities.remove(s.name);
					FillSidePane();
				}
			});
			pane.add(delBtn, BorderLayout.EAST);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void loadAbility(String key) {
		int load = JOptionPane.YES_OPTION;
		if(nameField.getText().length() > 0 || edit.getText().length() > 0)
			load = JOptionPane.showConfirmDialog(edit, "Load " + key + " you will lose unadded work.", 
					"Load Confirm", JOptionPane.YES_NO_OPTION);
		if(load == JOptionPane.YES_OPTION) {
			ClassAbility a = abilities.get(key);
			ResetEditor();
			edit.LoadDocument(a.desc);
			nameField.setText(a.name);
			useTypeCombo.setSelectedItem(a.use);
			lvlField.setText(a.level + "");
			
			nameField.setEditable(false);
			nameField.setFocusable(false);
			edit.requestFocus();
		}
	}
	
	private void AddAbility() {
		ClassAbility a = new ClassAbility();
		a.desc = edit.getStyledDocument();
		a.name = nameField.getText();
		a.level = Integer.parseInt(lvlField.getText());
		a.use = (UseType) useTypeCombo.getSelectedItem();
		
		abilities.put(a.name, a);
	}
}