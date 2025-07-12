package gui.gui_helpers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import gui.gui_helpers.structures.LoadListener;

public class LoadFrame extends JFrame implements LoadListener
{
	public static void main(String[]args) {
		SwingUtilities.invokeLater(() -> {
			LoadFrame f = new LoadFrame("Test");
			f.setVisible(true);
		});
	}
	public LoadFrame(String title) {
		this.setTitle(title);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setResizable(true);
		this.setUndecorated(true);
		this.setSize(400, 200);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point screenCenter = new Point(screenSize.width / 2, screenSize.height / 2);
        int x = screenCenter.x - getWidth() / 2;
        int y = screenCenter.y - getHeight() / 2;
        setLocation(x, y);
        
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		JLabel loadLbl = CompFactory.createNewLabel("Loading", CompFactory.ComponentType.HEADER);
		loadLbl.setFont(loadLbl.getFont().deriveFont(60f));
		loadLbl.setHorizontalAlignment(SwingConstants.CENTER);
		this.getContentPane().add(loadLbl, BorderLayout.CENTER);
		this.revalidate();
		this.pack();
		
	}

	@Override
	public void onDataLoaded() {
		this.dispose();
	}
}