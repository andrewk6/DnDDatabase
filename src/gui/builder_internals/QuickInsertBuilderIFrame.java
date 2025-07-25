package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class QuickInsertBuilderIFrame extends JInternalFrame
{
	private DataContainer data;
	private HashMap<String, StyledDocument> inMap;
	
	private RichEditor richEdit;
	private JPanel sideGPane;
	private JPanel mPane;
	
	private ReminderField title;

	public QuickInsertBuilderIFrame(DataContainer d) {
		data = d;
		
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setTitle("Quick Insert Builder");
		this.setSize(800, 800);
		StyleContainer.SetIcon(this, StyleContainer.ITEM_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		
		LoadInserts();
		BuildMainPane(this.getContentPane());
	}
	
	private void BuildMainPane(Container cPane)
	{
		cPane.setLayout(new BorderLayout());
		
		mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		
		title = new ReminderField("", "Enter the insert name");
		StyleContainer.SetFontHeader(title);
		mPane.add(title, BorderLayout.NORTH);
		
		richEdit = new RichEditor(data);
		richEdit.disableTables();
		mPane.add(richEdit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton addBtn = new JButton("Add Insert");
		StyleContainer.SetFontBtn(addBtn);
		addBtn.addActionListener(e ->{
			AddInsert(mPane);
		});
		btnPane.add(addBtn);
		
		JPanel sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		cPane.add(sidePane, BorderLayout.WEST);
		
		sideGPane = new JPanel();
		sideGPane.setLayout(new GridLayout(0,2));
		
		JScrollPane sideScroll = new JScrollPane(sideGPane);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sideScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sidePane.add(sideScroll, BorderLayout.CENTER);
		
		JButton saveBtn = new JButton("Save");
		StyleContainer.SetFontBtn(saveBtn);
		saveBtn.addActionListener(e -> {
			data.setInserts(inMap);
			data.SafeSaveData(DataContainer.INSERTS);
		});
		sidePane.add(saveBtn, BorderLayout.SOUTH);
		
		FillSidePane();
	}
	
	private void AddInsert(Container cPane) {
		if(title.getText().length() > 0 && richEdit.getStyledDocument().getLength() > 0) {
			inMap.put(title.getText(), richEdit.getStyledDocument());
			SwingUtilities.invokeLater(()->{
				cPane.remove(richEdit);
				richEdit = new RichEditor(data);
				cPane.add(richEdit, BorderLayout.CENTER);
				title.setText("");
				if(!title.isEditable())
					title.setEditable(true);
				
				FillSidePane();
				
				cPane.revalidate();
				cPane.repaint();
			});
		}		
	}
	
	private void FillSidePane() {
		if(inMap.size() > 0) {
			
			ArrayList<String> keySet = new ArrayList<String>();
			for(String k : inMap.keySet())
				keySet.add(k);
			Collections.sort(keySet);
			SwingUtilities.invokeLater(()->{
				sideGPane.removeAll();
				
				for(String s : keySet) {
					JTextField card = new JTextField(s);
					card.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(Font.PLAIN));
					card.setColumns(15);
					card.setEditable(false);
					
					card.addMouseListener(new MouseListener() {

						@Override
						public void mouseClicked(MouseEvent e) {SetDocument(s);}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {card.setFont(card.getFont().deriveFont(Font.BOLD));}
						public void mouseExited(MouseEvent e) {card.setFont(card.getFont().deriveFont(Font.PLAIN));}
					});
					
					JButton delBtn = new JButton("Delete");
					StyleContainer.SetFontBtn(delBtn);
					delBtn.addActionListener(e ->{
						inMap.remove(s);
						SwingUtilities.invokeLater(()->{
							sideGPane.remove(delBtn);
							sideGPane.remove(card);
							sideGPane.revalidate();
							sideGPane.repaint();
						});
					});
					
					sideGPane.add(card);
					sideGPane.add(delBtn);
				}
				sideGPane.revalidate();
				sideGPane.repaint();
			});
		}
	}
	
	private void SetDocument(String key) {
		int opt = JOptionPane.showConfirmDialog(richEdit, "Load: " + 
				key + "\nYou will lose any unfinished work", "Load Confirmation", JOptionPane.YES_NO_OPTION);
		if(opt == JOptionPane.YES_OPTION) {
			title.setText(key);
			title.setEditable(false);
			mPane.remove(richEdit);
			richEdit = new RichEditor(data);
			richEdit.disableTables();
			richEdit.LoadDocument(inMap.get(key));
			mPane.add(richEdit, BorderLayout.CENTER);			
		}
	}
	
//	private void Save() throws IOException {
//		File inFile = new File(DataContainer.INSERT_FILE_NAME);
//		
//		if(!inFile.exists()) {
//			inFile.createNewFile();
//		}
//		
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(inFile));
//		oos.writeObject(inMap);
//		oos.flush();
//		oos.close();
//	}

	private void LoadInserts() {
		inMap = new HashMap<String, StyledDocument>(data.getInserts());
	}
}