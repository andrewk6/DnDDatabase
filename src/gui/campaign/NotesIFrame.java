package gui.campaign;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.RichViewer;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class NotesIFrame extends JInternalFrame
{
	private DataContainer data;
	private GuiDirector gd;
	
	private RichEditor edit;
	private RichViewer view;
	private ReminderField noteTitle;
	private JPanel mPane, sidePane, nPane;
	private JCheckBox editChck;
	private JButton addBtn;
	
	private CardLayout cl;
	
	private final String EDIT_CARD = "EDIT";
	private final String VIEW_CARD = "VIEW";
	
	private boolean loaded = false;

	public NotesIFrame(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		
		ConfigureFrame(getContentPane());
		BuildSidePane(getContentPane());
		BuildMainPane(getContentPane());
		StyleContainer.SetIcon(this, StyleContainer.ITEM_ICON_FILE);
	}
	
	private void ConfigureFrame(Container cPane) {
		cPane.setLayout(new BorderLayout());
		setSize(800, 800);
		setTitle("Notes Editor");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
		addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			
			public void internalFrameIconified(InternalFrameEvent e) {}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {}
			
			public void internalFrameActivated(InternalFrameEvent e) {}
		});
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void BuildSidePane(Container cPane) {
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = new JScrollPane(sidePane);
		cPane.add(sideScroll, BorderLayout.WEST);
		FillSidePane();
	}
	
	private void BuildMainPane(Container cPane) {		
		mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		mPane.add(hPane, BorderLayout.NORTH);
		
		JLabel titleLabel = new JLabel("Title:");
		StyleContainer.SetFontHeader(titleLabel);
		hPane.add(titleLabel, BorderLayout.WEST);
		
		noteTitle = new ReminderField("Enter the notes title");
		StyleContainer.SetFontHeader(noteTitle);
		hPane.add(noteTitle, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mPane.add(btnPane, BorderLayout.SOUTH);
		
		editChck = new JCheckBox("Editing");
		editChck.setSelected(true);
		editChck.setFocusable(false);
		StyleContainer.SetFontHeader(editChck);
		editChck.addActionListener(e ->{
			SetEditing(editChck.isSelected());
		});
		btnPane.add(editChck);
		
		addBtn = new JButton("Add Note");
		addBtn.setFocusable(false);
		StyleContainer.SetFontBtn(addBtn);
		addBtn.addActionListener(e ->{
			if(noteTitle.getText().length() > 0) {
				data.AddNote(noteTitle.getText(), edit.getStyledDocument());
				ResetEditor();
				FillSidePane();
			}else {
				JOptionPane.showMessageDialog(edit, "Note Requires a Title", 
						"No Title Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
		btnPane.add(addBtn);
		
		JButton resetBtn = new JButton("Reset Editor");
		StyleContainer.SetFontBtn(resetBtn);
		resetBtn.setFocusable(false);
		resetBtn.addActionListener(e ->{
			ResetEditor();
		});
		btnPane.add(resetBtn);
		
		nPane = new JPanel();
		nPane.setLayout(new CardLayout());
		cl = (CardLayout) nPane.getLayout();
		mPane.add(nPane, BorderLayout.CENTER);
		
		edit = new RichEditor(data);
		edit.disableTables();
		nPane.add(edit, EDIT_CARD);
		
		view = new RichViewer(data, gd);
		view.LoadDocument(edit.getStyledDocument());
		nPane.add(view, VIEW_CARD);
		cl.show(nPane, EDIT_CARD);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		if(data.getNoteKeys().size() < 1) {
			getContentPane().revalidate();
			getContentPane().repaint();
		}else {
			for(String s : data.getNoteKeys()) {
				JPanel pane = new JPanel();
				pane.setLayout(new BorderLayout());
				pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
				sidePane.add(pane);
				
				JLabel lbl = new JLabel(s);
				lbl.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {LoadEditorConf(s);}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
					public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
				});
				StyleContainer.SetFontHeader(lbl);
				lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
				pane.add(lbl, BorderLayout.CENTER);
				
				JButton dBtn = new JButton("Delete");
				dBtn.setFocusable(false);
				StyleContainer.SetFontBtn(dBtn);
				dBtn.addActionListener(e -> {
					data.DeleteNote(s);
					FillSidePane();
				});
				pane.add(dBtn, BorderLayout.EAST);
				sidePane.revalidate();
				sidePane.repaint();
			}
		}
	}
	
	private void ResetEditor() {
		nPane.remove(edit);
		edit = new RichEditor(data);
		edit.disableTables();
		nPane.add(edit, EDIT_CARD);
		
		nPane.remove(view);
		view = new RichViewer(data, gd);
		nPane.add(view, VIEW_CARD);
		
		mPane.revalidate();
		mPane.repaint();
		noteTitle.setText("");
		
		editChck.setSelected(true);
		loaded = false;
		SetEditing(true);
		
		cl.show(nPane, EDIT_CARD);
	}
	
	private void LoadEditorConf(String key) {
		if(noteTitle.getText().length() > 0 || edit.getStyledDocument().getLength() > 0) {
			if(editChck.isSelected()) {
				int loadConf = JOptionPane.showConfirmDialog(edit, "Would you like to load, you will lose any unsaved work",
						"Load Confirmation", JOptionPane.YES_NO_OPTION);
				if(loadConf == JOptionPane.YES_OPTION) {
					LoadEditor(key);
				}
			}else{
				LoadEditor(key);
			}
		}else {
			LoadEditor(key);
		}
	}
	
	private void LoadEditor(String key) {
		ResetEditor();
		edit.LoadDocument(data.getNote(key));
		view.LoadDocument(data.getNote(key));
		noteTitle.setText(key);
		editChck.setSelected(false);
		loaded = true;
		SetEditing(false);
	}
	
	private void SetEditing(boolean b) {
		if(loaded) {
			noteTitle.setEditable(false);
			noteTitle.setFocusable(false);
		}else {
			noteTitle.setEditable(b);
			noteTitle.setFocusable(b);
		}
		
		addBtn.setEnabled(b);
		
		if(b)
			cl.show(nPane, EDIT_CARD);
		else
			cl.show(nPane, VIEW_CARD);
	}
}