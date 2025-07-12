package gui.initative;

import java.awt.event.MouseEvent;

import javax.swing.JDesktopPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class InitHoverArea extends HoverTextPane {

	public InitHoverArea(DataContainer d, GuiDirector gD, JDesktopPane desktop) {
		super(d, gD, desktop);
	}

	@SuppressWarnings("unused")
	private void handleMouseClick(MouseEvent e) {
		int pos = viewToModel2D(e.getPoint());
		if (pos >= 0) {
			StyledDocument doc = getStyledDocument();
			Element elem = doc.getCharacterElement(pos);
			AttributeSet attr = elem.getAttributes();

			String ruleName = (String) attr.getAttribute("ruleLink");
			String spellName = (String) attr.getAttribute("spellLink");
			String monsterName = (String) attr.getAttribute("monstLink");
		}
	}
}