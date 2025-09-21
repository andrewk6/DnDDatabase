package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.DataContainer.Source;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;

@SuppressWarnings("serial")
public class SourceToggleDialog extends JDialog
{
	public static void main(String[]args) {
		ArrayList<Source> srcs = new ArrayList<Source>(Arrays.asList(Source.values()));
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			Container cPane = frm.getContentPane();
			cPane.setLayout(new BorderLayout());
			cPane.add(CompFactory.createNewButton("Add Source", _->{
				SourceToggleDialog toggle = new SourceToggleDialog(frm, srcs);
				if(toggle.setSources) {
					ArrayList<Source> togSrc = new ArrayList<Source>(toggle.getSources());
					cPane.add(CompFactory.createNewLabel(togSrc.toString(), 
							ComponentType.BODY), BorderLayout.CENTER);
					srcs.clear();
					srcs.addAll(togSrc);
					toggle.dispose();
					
					cPane.revalidate();
					cPane.repaint();
				}
			}), BorderLayout.SOUTH);
			frm.pack();
			frm.setSize(800,800);
			
			
			frm.setVisible(true);
		});
	}
	private ArrayList<Source> sources;
	public boolean setSources = false;
	
	public SourceToggleDialog(JFrame frm, ArrayList<Source> src) {
		super(frm, "Export Database Selector", ModalityType.APPLICATION_MODAL);
		sources = new ArrayList<Source>(src);
		init(this.getContentPane());
		this.pack();
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		this.setLocation(p.x - this.getWidth() / 2, p.y - this.getHeight() / 2);
		this.setVisible(true);
	}
	
	private void init(Container cPane)
	{
		cPane.setLayout(new BorderLayout());
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new GridLayout(0,1));
		cPane.add(srcPane, BorderLayout.CENTER);
		
		for(Source s : Source.values()) {
			JCheckBox cBox = CompFactory.createNewCheckbox(DataContainer.sourceToString(s));
			if(sources.contains(s))
				cBox.setSelected(true);
			cBox.addActionListener(_ -> {
				if(cBox.isSelected())
					sources.add(s);
				else
					sources.remove(s);
			});
			srcPane.add(cBox);
		}
		
		JPanel btnPane = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Set Sources", _->{
					setSources = true;
					this.setVisible(false);
				}),
				CompFactory.createNewButton("Reset Sources", _->{
					resetSources(srcPane);
				}),
				CompFactory.createNewButton("Cancel", _->{
					setSources = false;
					this.setVisible(false);
				})
		});
		cPane.add(btnPane, BorderLayout.SOUTH);
	}
	
	private void resetSources(JPanel srcPane) {
		sources.clear();
		sources.addAll(Arrays.asList(Source.values()));
		
		for(Component c : srcPane.getComponents())
			if(c instanceof JCheckBox)
				((JCheckBox) c).setSelected(true);
	}
	
	public List<Source> getSources() {
		return Collections.unmodifiableList(sources);
	}
}