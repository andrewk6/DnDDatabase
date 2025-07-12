package gui.gui_helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import gui.gui_helpers.structures.StyleContainer;

public class ReminderField extends JTextField {
	public ReminderField() {
		super();
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				repaint();
			}

			public void focusLost(FocusEvent e) {
				repaint();
			}
		});
	}

	public ReminderField(String tip) {
		super();
		this.setToolTipText(tip);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				repaint();
			}

			public void focusLost(FocusEvent e) {
				repaint();
			}
		});
	}
	
	public ReminderField(int cols) {
		super(cols);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				repaint();
			}

			public void focusLost(FocusEvent e) {
				repaint();
			}
		});
	}

	public ReminderField(String text, String tip) {
		super(text);
		this.setToolTipText(tip);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				repaint();
			}

			public void focusLost(FocusEvent e) {
				repaint();
			}
		});
	}

	public void setNumbersOnly() {
		((AbstractDocument) getDocument()).setDocumentFilter(new NumericFilter());
	}

	public void setDecimalsOnly() {
		((AbstractDocument) getDocument()).setDocumentFilter(new DecimalFilter());
	}

	public void setPlainDocument() {
		this.setDocument(new PlainDocument());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getText().isEmpty() && !isFocusOwner()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.GRAY);
			g2.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont(Font.ITALIC));
			Insets insets = getInsets();
			FontMetrics fm = g2.getFontMetrics();
			int x = insets.left;
			int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
			if(getToolTipText() != null)
				if(getToolTipText().length() > 0)
					g2.drawString(getToolTipText(), x, y);
			g2.dispose();
		}
	}
}

class NumericFilter extends DocumentFilter {	
	 @Override
	    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
	            throws BadLocationException {
	        if (string != null && string.matches("\\d*")) { // allows empty string and digits
	            super.insertString(fb, offset, string, attr);
	        }
	    }

	    @Override
	    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	            throws BadLocationException {
	        if (text != null && text.matches("\\d*")) { // allows empty string and digits
	            super.replace(fb, offset, length, text, attrs);
	        }
	    }
}

class DecimalFilter extends DocumentFilter {
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		if (string != null) {
			StringBuilder newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
			newText.insert(offset, string);
			if (isValidDecimal(newText.toString())) {
				super.insertString(fb, offset, string, attr);
			}
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if (text != null) {
			StringBuilder newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
			newText.replace(offset, offset + length, text);
			if (isValidDecimal(newText.toString())) {
				super.replace(fb, offset, length, text, attrs);
			}
		}
	}

	private boolean isValidDecimal(String text) {
		return text.matches("\\d*(\\.\\d*)?");
	}
}
