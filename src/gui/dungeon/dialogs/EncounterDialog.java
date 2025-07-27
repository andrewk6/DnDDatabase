package gui.dungeon.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.DataContainer;
import data.Monster;
import data.dungeon.EncounterNote;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.FilterCombo;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class EncounterDialog extends JDialog
{
	private RichEditor edit;
	private JPanel listPane;
	
	private ArrayList<Monster> enemies;
	private DataContainer data;
	
//	public EncounterNote enc = null;
	
	public EncounterDialog(DataContainer data, EncounterNote eNote) {
		super(null, "Encounter Dialog", Dialog.ModalityType.APPLICATION_MODAL);
		this.data = data;
		enemies = eNote.enemies;
		this.setLayout(new BorderLayout());
		
		JPanel cPane = new JPanel();
		cPane.setLayout(new BorderLayout());
		this.add(cPane, BorderLayout.CENTER);
		
		ReminderField titleField = new ReminderField("Note Title...");
		StyleContainer.SetFontHeader(titleField);
		if(eNote.title != null)
			if(eNote.title.length() > 0)
				titleField.setText(eNote.title);
		cPane.add(titleField, BorderLayout.NORTH);
		
		edit = new RichEditor(data);
		edit.disableTables();
		if(eNote.note != null)
			edit.LoadDocument(eNote.note);
		else
			eNote.note = edit.getStyledDocument();
		cPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton finishBtn = CompFactory.createNewButton("Finish", _->{
			eNote.title = titleField.getText();
			dispose();
		});
		btnPane.add(finishBtn);
		
//		JButton cancelBtn = CompFactory.createNewButton("Cancel", _->{
//			dispose();
//		});
//		btnPane.add(cancelBtn);
		
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		this.add(sidePane, BorderLayout.WEST);
		
		listPane = new JPanel();
		listPane.setLayout(new GridLayout(0,1));
		JScrollPane listScroll = new JScrollPane(listPane);
		sidePane.add(listScroll, BorderLayout.CENTER);
		
		JButton addEnemy = CompFactory.createNewButton("Add Enemy", _->{
			showAddMonsterDialog();
			FillSidePane();
		});
		sidePane.add(addEnemy, BorderLayout.NORTH);
		FillSidePane();
		this.pack();
	}

	private void FillSidePane() {
		System.out.println("Fill Side");
		listPane.removeAll();
		if(enemies.size() > 0) {
			for(Monster m : enemies) {
				System.out.println("Adding: " + m.name);
				JPanel p = new JPanel();
				p.setLayout(new BorderLayout());
				listPane.add(p);
				
				JLabel mLbl = CompFactory.createNewLabel(m.name, ComponentType.HEADER);
				mLbl.setFont(mLbl.getFont().deriveFont(Font.PLAIN));
				mLbl.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {mLbl.setFont(mLbl.getFont().deriveFont(Font.BOLD));}
					public void mouseExited(MouseEvent e) {mLbl.setFont(mLbl.getFont().deriveFont(Font.PLAIN));}
				});
				p.add(mLbl, BorderLayout.CENTER);
				
				JButton delete = CompFactory.createNewButton("Delete", _->{
					enemies.remove(m);
					FillSidePane();
				});
				p.add(delete, BorderLayout.EAST);
			}
		}
		listPane.revalidate();
		listPane.repaint();
	}
	
	private void showAddMonsterDialog() {
        String[] names = data.getMonsterKeysSorted().toArray(new String[0]);
        Map<String, Monster> monsters = data.getMonsters();

        FilterCombo monsterSelector = new FilterCombo(data.getMonsterKeysSorted());
        JTextField overrideBonus = new JTextField(4);
        JLabel initBonusLabel = new JLabel("Init Bonus: +0");

        monsterSelector.addActionListener(e -> {
            String selected = (String) monsterSelector.getSelectedItem();
            Monster m = monsters.get(selected);
            if (m != null) {
                initBonusLabel.setText("Init Bonus: " + m.init);
                overrideBonus.setText(m.GetInitBonus() + "");
            }
        });
        
        JPanel request = new JPanel();
        request.setLayout(new BorderLayout());
        
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        request.add(header, BorderLayout.NORTH);
        header.add(new JLabel("Monster:"), BorderLayout.WEST);
        header.add(monsterSelector, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        request.add(panel, BorderLayout.CENTER);
      panel.add(initBonusLabel);
      panel.add(new JLabel("Bonus:"));
      panel.add(overrideBonus);
      panel.add(new JLabel("Number of This Enemy:"));
      ReminderField numField = new ReminderField("");
      numField.setNumbersOnly();
      numField.setColumns(3);
      panel.add(numField);
        

        int result = JOptionPane.showConfirmDialog(this, request, "Add Monster", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	for(int i = 0; i < Integer.parseInt(numField.getText()); i++) {
        		String selected = (String) monsterSelector.getSelectedItem();
                enemies.add(monsters.get(selected));
        	}
            
        }
    }
}