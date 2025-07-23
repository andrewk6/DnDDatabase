//public class 
package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import data.Rule;
import data.Spell;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.RichEditorBase;
import gui.gui_helpers.structures.StyleContainer;

public class SpellBuilderIFrame extends JInternalFrame {

	private HashMap<String, Spell> spellMap;
//	private Map<String, Rule> rMap;
	private DataContainer data;

	private JTextField spellNameField;
	private RichEditor editor;
	private JPanel spellListPane;
	
	private JPanel centerPane;
	
	public SpellBuilderIFrame(DataContainer d) {
		data = d;
		this.setSize(800, 800);
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setTitle("Spell Builder");
		StyleContainer.SetIcon(this, StyleContainer.SPELL_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		
		ReadSpellList();
		centerPane = new JPanel();
		centerPane.setLayout(new BorderLayout());
		this.getContentPane().add(centerPane, BorderLayout.CENTER);
		ResetEditor();
		BuildHeader(centerPane);
		BuildSpellListPane(getContentPane());
	}

	private void BuildSpellListPane(Container cPane) {
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		cPane.add(sidePane, BorderLayout.WEST);

		spellListPane = new JPanel();
		spellListPane.setLayout(new GridLayout(0, 1));
		JScrollPane spellListScroll = new JScrollPane(spellListPane);
		spellListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		FillSpellList();
		sidePane.add(spellListScroll, BorderLayout.CENTER);

		JButton saveBtn = new JButton("Save");
		saveBtn.setPreferredSize(new Dimension(150, 50));
		saveBtn.setFocusable(false);
		StyleContainer.SetFontHeader(saveBtn);
		saveBtn.addActionListener(e -> {
			Save();
		});
		sidePane.add(saveBtn, BorderLayout.SOUTH);
	}

	private void BuildHeader(Container cPane) {
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		cPane.add(hPane, BorderLayout.NORTH);

		spellNameField = new JTextField();
		spellNameField.setToolTipText("Enter the name of the spell.");
		StyleContainer.SetFontHeader(spellNameField);
		hPane.add(spellNameField, BorderLayout.CENTER);

		JButton addBtn = new JButton("Add Spell");
		StyleContainer.SetFontHeader(addBtn);
		addBtn.setFocusable(false);
		addBtn.addActionListener(e -> {
			if (spellNameField.getText().length() > 0 && editor.getText().length() > 0) {
				Spell s = new Spell();
				s.name = spellNameField.getText();
				s.descrBasic = editor.getText();
				s.spellDoc = editor.getStyledDocument();
				spellMap.put(s.name, s);
				spellNameField.setText("");
				ResetEditor();
				FillSpellList();
			}
		});
		hPane.add(addBtn, BorderLayout.EAST);
	}

	private void ResetEditor() {
		SwingUtilities.invokeLater(() -> {
			if (editor != null) {
				editor.close();
				centerPane.remove(editor);
				centerPane.revalidate();
				centerPane.repaint();
			}
			editor = new RichEditor(data);
			centerPane.add(editor, BorderLayout.CENTER);
			centerPane.revalidate();
			centerPane.repaint();
		});

	}

	private void FillSpellList() {
		SwingUtilities.invokeLater(() -> {
			spellListPane.removeAll();
			ArrayList<String> keys = new ArrayList<String>();
			for(String s : spellMap.keySet())
				keys.add(s);
			Collections.sort(keys);
			for (String s : keys) {
				JPanel pane = new JPanel();
				pane.setLayout(new BorderLayout());
				spellListPane.add(pane);
				
				JTextField spellDispField = new JTextField(s);
				spellDispField.setColumns(15);
				spellDispField.setEditable(false);
				spellDispField.setFocusable(false);
				StyleContainer.SetFontMain(spellDispField);
				spellDispField.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						SwingUtilities.invokeLater(()->{
							LoadSpell(s);
						});						
					}
					@Override
					public void mousePressed(MouseEvent e) {}
					@Override
					public void mouseReleased(MouseEvent e) {}
					@Override
					public void mouseEntered(MouseEvent e) {}
					@Override
					public void mouseExited(MouseEvent e) {}
				});
				pane.add(spellDispField, BorderLayout.CENTER);
				
				JButton exit = CompFactory.createNewButton("Delete", e->{
					spellMap.remove(s);
					FillSpellList();
				});
				pane.add(exit, BorderLayout.EAST);
			}
			spellListPane.revalidate();
			spellListPane.repaint();
		});
	}

	public void ReadSpellList() {
		spellMap = new HashMap<String, Spell>(data.getSpells());
<<<<<<< HEAD
		
=======
>>>>>>> 9a45e0e (Minor Fixes Builder - Initiative Fix)
	}
	
	private void LoadSpell(String key) {
		Consumer<String> resetEditor = (keyVal) ->{
			editor.close();
			centerPane.remove(editor);
			editor = new RichEditor(data);
			editor.LoadDocument(data.getSpells().get(keyVal).spellDoc);
			centerPane.add(editor, BorderLayout.CENTER);
			spellNameField.setText(keyVal);
			
			System.out.println(editor.getStyledDocument().toString());
			centerPane.revalidate();
			centerPane.repaint();
		};
		
		SwingUtilities.invokeLater(()->{
			if(editor.getStyledDocument().getLength() == 0 && spellNameField.getText().length() == 0) {
				resetEditor.accept(key);
			}else {
				int conf = JOptionPane.showConfirmDialog(this, "Load " + key + ", you will lose any unsaved progress.", 
						"Load Confirm", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					resetEditor.accept(key);
				}
			}
		});		
	}
	
	public void Save() {
		data.setSpellMap(spellMap);
		data.SafeSaveData(DataContainer.SPELLS);
		
	}
}