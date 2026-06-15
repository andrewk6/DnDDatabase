package gui.initative;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.GuiDirector;
import gui.initative.InitiativeIFrameHP.InitiativeEntry;

public class EntryPane extends JPanel
{
	private JLabel eLbl;
	private final InitiativeEntry entry;
	
	public EntryPane(InitiativeEntry entry, InitiativeIFrameHP owner) {
		this.entry = entry;
		
		this.setLayout(new BorderLayout());
		
		eLbl = CompFactory.createNewLabel(entry.name);
		eLbl.setOpaque(true);
		eLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isAltDown() || SwingUtilities.isRightMouseButton(e))
					owner.removeInitiativeEntry(EntryPane.this.entry);
				else
					if(entry.monster != null)
						owner.AddTab(entry.monster);
						
			}
		});
		this.add(eLbl, BorderLayout.CENTER);
		
		if(entry.monster != null) {
			ReminderField hpField = CompFactory.createReminderField("HP...", true, 6);
			hpField.setText(entry.hp + "");
			hpField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {processUpdate();}

				@Override
				public void removeUpdate(DocumentEvent e) {processUpdate();}

				@Override
				public void changedUpdate(DocumentEvent e) {processUpdate();}
				
				private void processUpdate() {
					int newHP;
					if(hpField.getText().length() <= 0)
						newHP = 0;
					else {
						try {
							newHP = Integer.parseInt(hpField.getText());
						}catch(NumberFormatException e) {
							newHP = 0;
						}
					}
					EntryPane.this.entry.setHP(newHP);
				}
			});
			this.add(hpField, BorderLayout.EAST);
		}
	}
	
	public InitiativeEntry getEntry() {
		return entry;
	}
	
	public void setSelected(boolean selected) {
		if(selected)
			eLbl.setBackground(Color.CYAN);
		else
			eLbl.setBackground(getBackground());
		repaint();
	}
}