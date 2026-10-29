package gui.math_utils;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

@SuppressWarnings("serial")
public class HypoPane extends JPanel
{
	public static void main(String[]args) {
		JFrame test = new JFrame();
		test.setContentPane(new HypoPane());
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.pack();
		test.setVisible(true);
	}
	private enum CalcType {
		A("Horizontal"),
		B("Vertical"),
		C("Hypotenuse");
		
		private String desc;
		CalcType(String desc) {
			this.desc = desc;
		}
		
		public String toString() {
			return desc;
		}
	}
	
	private JComboBox<CalcType> calcCombo;
	private ReminderField aField, bField, cField;
	private JPanel triPane;
	
	public HypoPane() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		hPane.add(CompFactory.createNewLabel("Hypotenuse Calculator  ", ComponentType.HEADER));
		
		calcCombo = CompFactory.createEnumCombo(CalcType.class, ComponentType.BODY);
		calcCombo.setSelectedIndex(2);
		calcCombo.addItemListener(_->{
			setSearchTarget((CalcType) calcCombo.getSelectedItem());
		});
		
		hPane.add(CompFactory.createSplitPane("Calculate: ", calcCombo), BorderLayout.EAST);
		
		JPanel bPane = new JPanel(new GridLayout(1,0));
		this.add(bPane, BorderLayout.CENTER);
		
		JPanel eGrid = new JPanel(new GridLayout(0,1));
		bPane.add(eGrid);
	
		aField = CompFactory.createReminderField("", true);
		aField.setText("3");
		aField.setToolTipText(null);
		aField.getDocument().addDocumentListener(getDocListener(aField));
		
		bField  = CompFactory.createReminderField("", true);
		bField.setText("4");
		bField.setToolTipText(null);
		bField.getDocument().addDocumentListener(getDocListener(bField));
		
		cField = CompFactory.createReminderField("", true);
		cField.setToolTipText(null);
		cField.getDocument().addDocumentListener(getDocListener(cField));
		
		eGrid.add(CompFactory.createSplitPane("Horizontal: ", aField));
		eGrid.add(CompFactory.createSplitPane("Vertical:   ", bField));
		eGrid.add(CompFactory.createSplitPane("Hypotenuse: ", cField));
		
		setSearchTarget((CalcType)calcCombo.getSelectedItem());
		calculate();
		
		triPane = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				
				int sizeOff = 10;
				int textOff = 25;
				
				float strokeWidth = 5f;
				float fontSize = 24f;

				int[] xPoints = { sizeOff, sizeOff, triPane.getWidth() - sizeOff };
				int[] yPoints = { sizeOff, triPane.getHeight() - sizeOff, triPane.getHeight() - sizeOff };

				Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);

				g2.setStroke(new BasicStroke(strokeWidth)); // 5-pixel outline
				g2.drawPolygon(polygon);

				g2.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont(fontSize));

				g2.drawChars(bField.getText().toCharArray(), 0, bField.getText().length(), 
						textOff, triPane.getHeight() / 2);
				
				g2.drawChars(aField.getText().toCharArray(), 0, 
						aField.getText().length(), triPane.getWidth() / 2, triPane.getHeight() - textOff);
				
				g2.drawChars(cField.getText().toCharArray(), 0, cField.getText().length(), 
						triPane.getWidth() / 2 - textOff, triPane.getHeight() / 2 + textOff);
			}
		};
		
		triPane.setPreferredSize(new Dimension(400,400));
		triPane.setMinimumSize(triPane.getPreferredSize());
		triPane.setMaximumSize(triPane.getPreferredSize());
		bPane.add(triPane);
	}
	
	private void setSearchTarget(CalcType calcing) {
		aField.setEditable(true);
		bField.setEditable(true);
		cField.setEditable(true);
		
		switch(calcing) {
		case A: aField.setEditable(false); return;
		case B: bField.setEditable(false); return;
		case C: 
		default: cField.setEditable(false);
		}
	}
	
	private void calculate() {
		int a = 0, b= 0, c = 0;
		try {
			a = Integer.parseInt(aField.getText());
			b = Integer.parseInt(bField.getText());
			c = Integer.parseInt(cField.getText());
		}catch(NumberFormatException e) {}
		
		
		switch ((CalcType) calcCombo.getSelectedItem()) {
		case A: aField.setText("" + Math.round(Math.sqrt(c*c - b*b))); break;
		case B: bField.setText("" + Math.round(Math.sqrt(c*c - a*a))); break;
		case C: cField.setText("" + Math.round(Math.sqrt(a*a + b*b))); break;
		}
	}
	
	private DocumentListener getDocListener(ReminderField bound) {
		DocumentListener list = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {processEdit();}
			
			@Override
			public void insertUpdate(DocumentEvent e) {processEdit();}
			
			@Override
			public void changedUpdate(DocumentEvent e) {processEdit();}
			
			private void processEdit() {
				if(bound.isEditable()) {
					
					SwingUtilities.invokeLater(()->{
						calculate();
						triPane.repaint();
					});
				}
			}
		};
		return list;
	}
}