package gui.gui_helpers;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LoadingFrame extends JFrame
{
	private int stepVal, target;
	JPanel loadPane;
	
	public LoadingFrame(int s, int t) {
		this.stepVal = s;
		this.target = t;
		
		loadPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				int stepAmount = getWidth() / t;
				g.setColor(Color.BLUE);
				g.fillRect(0, 0, stepAmount * stepVal, getHeight());
			}
		};
		
		this.setContentPane(loadPane);
		this.setSize(150, 800);
	}
	
	public void Step() {
		stepVal ++;
		
		loadPane.repaint();
	}
}