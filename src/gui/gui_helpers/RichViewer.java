package gui.gui_helpers;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import gui.gui_helpers.structures.GuiDirector;

@SuppressWarnings("serial")
public class RichViewer extends JPanel
{
	private HoverTextPane hPane;
	
	public RichViewer(DataContainer data, GuiDirector gd, StyledDocument doc) {
		this.setLayout(new BorderLayout());
		
		hPane = new HoverTextPane(data, gd, gd.getDesktop());
		hPane.setDocument(doc);
		JScrollPane scroll = CompFactory.wrapPanelInScroll(hPane);
		this.add(scroll, BorderLayout.CENTER);
	}
	
	public RichViewer(DataContainer data, GuiDirector gd) {
		this.setLayout(new BorderLayout());
		
		hPane = new HoverTextPane(data, gd, gd.getDesktop());
		JScrollPane scroll = CompFactory.wrapPanelInScroll(hPane);
		this.add(scroll, BorderLayout.CENTER);
	}
	
	public HoverTextPane getHPane() {
		return hPane;
	}
	
	public void LoadDocument(StyledDocument doc) {
		hPane.setDocument(doc);
	}
}