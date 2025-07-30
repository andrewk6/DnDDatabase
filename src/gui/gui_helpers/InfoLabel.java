package gui.gui_helpers;

import javax.swing.JLabel;

public class InfoLabel extends JLabel
{
	public String info;
	
	public InfoLabel(String fullText, int textLength) {
		info = fullText;
		
		if(fullText.length() + 3 > textLength)
			this.setText(fullText.substring(0, textLength) + "...");
		else
			this.setText(fullText);
	}
}