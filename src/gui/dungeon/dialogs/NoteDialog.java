package gui.dungeon.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.dungeon.DungeonNote;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class NoteDialog extends JDialog{
	private static final long serialVersionUID = 725990709923540407L;
	private final DungeonNote note;
	
	private ReminderField title;
	private RichEditor noteEdit;
	
	public NoteDialog(DataContainer data, DungeonNote note) {
		super(null, "Note Dialog", Dialog.ModalityType.APPLICATION_MODAL);
		this.note = note;
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) { exit(); }
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});
		
		noteEdit = new RichEditor(data);
		if(note.note != null)
			noteEdit.LoadDocument(note.note);
		else
			note.note = noteEdit.getStyledDocument();
		noteEdit.disableTables();
		add(noteEdit, BorderLayout.CENTER);
		
		title = new ReminderField("Notes title...");
		StyleContainer.SetFontHeader(title);
		if(note.title.length() > 0)
			title.setText(note.title);
		add(title, BorderLayout.NORTH);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(btnPane, BorderLayout.SOUTH);
		
		JButton finBtn = CompFactory.createNewButton("Finish", _ -> { exit(); });
		btnPane.add(finBtn);
		
		this.pack();
	}
	
	private void exit() {
		note.title = title.getText();
		this.dispose();
	}
}