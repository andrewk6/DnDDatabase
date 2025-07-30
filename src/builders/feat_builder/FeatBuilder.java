package builders.feat_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import data.DataChangeListener;
import data.DataContainer;
import data.Feat;
import data.Feat.FeatType;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.InfoLabel;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.StyleContainer;

public class FeatBuilder extends JFrame implements DataChangeListener
{
	private static final long serialVersionUID = 7767913160428645959L;
	
	private DataContainer data;
	private final HashMap<String, Feat> featMap;
	
	private JPanel featList, mPane;
	private ReminderField titleField;
	private JComboBox<FeatType> featTypeComb;
	private RichEditor edit;

	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(()->{
			FeatBuilder build = new FeatBuilder(data);
			build.setVisible(true);
		});
	}
	public FeatBuilder(DataContainer data) 
	{
		this.data = data;
		if(data.getFeats() == null)
			featMap = new HashMap<String, Feat>();
		else
			featMap = new HashMap<String, Feat>(data.getFeats());
		this.data.registerListener(this);
		this.addWindowListener(CompFactory.createSafeExitWindowListener(this, data));
		Initialize(this.getContentPane());
		pack();
	}
	
	private void Initialize(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		cPane.add(mPane, BorderLayout.CENTER);
		
		JPanel tPane = new JPanel();
		tPane.setLayout(new GridLayout(0,1));
		mPane.add(tPane, BorderLayout.NORTH);
		
		titleField = new ReminderField("What is the feats title...");
		titleField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(22f));
		tPane.add(titleField);
		
		featTypeComb = new JComboBox<FeatType>(FeatType.values());
		featTypeComb.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(20f));
		tPane.add(featTypeComb);
		
		edit = new RichEditor(data);
		edit.disableTables();
		mPane.add(edit, BorderLayout.CENTER);
		
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		mPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton clearBtn = CompFactory.createNewButton("Clear Editor", _->{
			int conf = JOptionPane.showConfirmDialog(FeatBuilder.this, "Are you sure you want to clear", 
					"Confirmation", JOptionPane.YES_NO_OPTION);
			if(conf == JOptionPane.YES_OPTION)
				resetEditor();
		});
		btnPane.add(clearBtn);
		
		JButton addFeat = CompFactory.createNewButton("Add Feat", _->{
			if(titleField.getText().length() > 0 && edit.getText().length() > 0)
			{
				Feat f = new Feat();
				f.name = titleField.getText();
				f.desc = edit.getStyledDocument();
				f.type = (FeatType) featTypeComb.getSelectedItem();
				
				featMap.put(f.name, f);
				
				resetEditor();
				FillSidePane();
			}else {
				JOptionPane.showMessageDialog(FeatBuilder.this, 
						"Please finish entering the feat name and desc", 
						"Feat Edit Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
		btnPane.add(addFeat);
		
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		cPane.add(sPane, BorderLayout.WEST);
		
		JButton saveBtn = CompFactory.createNewButton("Save", _->{
			data.setFeatMap(featMap);
			data.SafeSaveData(DataContainer.FEATS);
		});
		sPane.add(saveBtn, BorderLayout.SOUTH);
		
		featList = new JPanel();
		featList.setLayout(new GridLayout(0,1));
		JScrollPane listScroll = new JScrollPane(featList);
		sPane.add(listScroll, BorderLayout.CENTER);
		
		FillSidePane();
	}
	
	public void FillSidePane() {
		featList.removeAll();
		ArrayList<String> keys = new ArrayList<String>(featMap.keySet());
		Collections.sort(keys);
		for(String f : keys) 
		{
			JPanel pane = new JPanel();
			pane.setLayout(new GridLayout(1,0));
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			featList.add(pane);
			JLabel lbl;
			if(f.length() > 10)
				lbl = CompFactory.createNewLabel(f.substring(0, 10) + "...", ComponentType.BODY);
			else
				lbl = CompFactory.createNewLabel(f, ComponentType.BODY);
			lbl.setFont(lbl.getFont().deriveFont(18f));
			lbl.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {loadFeatCheck(f);}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			pane.add(lbl);
			
			JButton btn = CompFactory.createNewButton("Delete", _->{
				featMap.remove(f);
				FillSidePane();
			});
			pane.add(btn);
		}
		featList.revalidate();
		featList.repaint();
	}
	
	private void loadFeatCheck(String f) {
		if(titleField.getText().length() == 0 && edit.getText().length() == 0)
			loadFeat(f);
		else {
			int opt = JOptionPane.showConfirmDialog(this, "Load " + f + ", you will lose unsaved progress", 
					"Load Confirm", JOptionPane.YES_NO_OPTION);
			if(opt == JOptionPane.YES_OPTION)
				loadFeat(f);
		}
	}
	
	private void loadFeat(String f) {
		resetEditor();
		titleField.setEditable(false);
		titleField.setFocusable(false);
		titleField.setText(f);
		
		featTypeComb.setSelectedItem(featMap.get(f).type);
		edit.LoadDocument(featMap.get(f).desc);
	}
	
	public void resetEditor() {
		titleField.setText("");
		titleField.setEditable(true);
		titleField.setFocusable(true);
		titleField.requestFocus();
		mPane.remove(edit);
		edit = new RichEditor(data);
		edit.disableTables();
		mPane.add(edit, BorderLayout.CENTER);
		mPane.revalidate();
		mPane.repaint();
	}
	
	public void onMapUpdated() {
		//TODO: Implement Map Update if necessary
	}
	
	public void onMapUpdated(int mapType) {
		
	}
}