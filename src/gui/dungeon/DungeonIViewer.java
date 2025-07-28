package gui.dungeon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.DataContainer;
import data.dungeon.Dungeon;
import data.dungeon.DungeonFloor;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class DungeonIViewer extends JInternalFrame
{
	private Dungeon d; 
	private DataContainer data;
	private GuiDirector gd;
	
	private final JFileChooser fChoose = new JFileChooser();
	
	private JPanel sidePane;
	private JTabbedPane mainPane;
	private DungeonViewerPane dView;
	private JLabel dungeonLbl;
	
	public static void main(String[] args) {
		StyleContainer.SetLookAndFeel();
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(() -> {

			JFrame frame = new JFrame("Dungeon Viewer Test");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setSize(1000, 800);
			frame.addWindowListener(CompFactory.createSafeExitWindowListener(frame, data));

			JDesktopPane desktop = new JDesktopPane();
			GuiDirector gd = new GuiDirector(desktop);
			DungeonIViewer dungeon = new DungeonIViewer(data, gd);
			desktop.add(dungeon);
			dungeon.setVisible(true);

			frame.add(desktop);
			frame.setVisible(true);
		});
	}
	public DungeonIViewer(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Dungeon Files (*.dol)", "dol");
		fChoose.setFileFilter(filter);
		this.setResizable(true);
		this.setSize(900,900);
		
		Initialize(this.getContentPane());
		AddButtonPane(this.getContentPane());
	}
	
	private void Initialize(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		mainPane = new JTabbedPane();
		cPane.add(mainPane, BorderLayout.CENTER);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = new JScrollPane(sidePane);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cPane.add(sideScroll, BorderLayout.WEST);
		
		dungeonLbl = CompFactory.createNewLabel("", ComponentType.HEADER);
	}
	
	private void AddButtonPane(Container cPane) {
		// TODO Auto-generated method stub
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(btnPane, BorderLayout.SOUTH);
		
		JButton loadBtn = CompFactory.createNewButton("Load", _ -> {
			int result = fChoose.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fChoose.getSelectedFile();

				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile));
					d = (Dungeon) ois.readObject();
					if(d.floors.size() > 0) {
						FillSidePane();
					}
					SwingUtilities.invokeLater(()->{
//						addNewEditor(tiles);
						revalidate();
						repaint();
					});
					
					ois.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		btnPane.add(loadBtn);
		
	}
	
	private void FillSidePane() {
		for(String f : d.floors.keySet()) {
			JPanel sidePoint = new JPanel();
			sidePoint.setLayout(new BorderLayout());
			JLabel lbl = CompFactory.createNewLabel(f, ComponentType.BODY);
			lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			lbl.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(dView != null) {
						dView.loadFloor(d.floors.get(f).tiles);
						dungeonLbl.setText(f);
						resetTabs();
					} else {
						dView = new DungeonViewerPane(data, gd, mainPane, d.floors.get(f).tiles);
						mainPane.addTab("", dView);
						dungeonLbl.setText(f);
						mainPane.setTabComponentAt(0, dungeonLbl);
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));}
				public void mouseExited(MouseEvent e) {lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));}
			});
			sidePoint.add(lbl, BorderLayout.CENTER);
			sidePane.add(sidePoint);
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void resetTabs() {
		if(mainPane.getTabCount() >= 2) {
			for(int i = mainPane.getTabCount()-1; i > 0; i --) {
				mainPane.removeTabAt(i);
			}
		}
	}
}