package builders.utlities;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CustomStyledDocument implements StyledDocument, Serializable{
    private Map<Integer, JTable> embeddedTables;  // To store embedded tables at specific positions
    private DefaultStyledDocument baseDoc; // Delegate most methods to DefaultStyledDocument

    public CustomStyledDocument() {
        embeddedTables = new HashMap<>();
        baseDoc = new DefaultStyledDocument();
    }

    // Method to add a table at a specific position in the document
    public void addEmbeddedTable(int position, JTable table) {
        embeddedTables.put(position, table);
    }

    // Method to stop editing all tables if needed
    public void stopEditingAllTables() {
        for (JTable table : embeddedTables.values()) {
        	if(table instanceof SerialTable)
        		((SerialTable) table).stopEditing();
        	else if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            table.clearSelection();
        }
    }

    // Required method from StyledDocument (delegating to DefaultStyledDocument)
    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        baseDoc.insertString(offset, str, a);
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        baseDoc.remove(offset, length);
    }

    @Override
    public int getLength() {
        return baseDoc.getLength();
    }

    @Override
    public void getText(int offset, int length, Segment txt) throws BadLocationException {
        baseDoc.getText(offset, length, txt);
    }

    @Override
    public Position createPosition(int offs) throws BadLocationException {
        return baseDoc.createPosition(offs);
    }

    @Override
    public Element[] getRootElements() {
        return baseDoc.getRootElements();
    }

    @Override
    public Element getDefaultRootElement() {
        return baseDoc.getDefaultRootElement();
    }

    @Override
    public void render(Runnable r) {
        baseDoc.render(r);
    }

    @Override
    public Style addStyle(String nm, Style parent) {
        return baseDoc.addStyle(nm, parent);
    }

    @Override
    public void removeStyle(String nm) {
        baseDoc.removeStyle(nm);
    }

    @Override
    public Style getStyle(String nm) {
        return baseDoc.getStyle(nm);
    }

    @Override
    public void setLogicalStyle(int pos, Style s) {
        baseDoc.setLogicalStyle(pos, s);
    }

    @Override
    public Style getLogicalStyle(int p) {
        return baseDoc.getLogicalStyle(p);
    }

    @Override
    public Element getParagraphElement(int pos) {
        return baseDoc.getParagraphElement(pos);
    }

    @Override
    public Element getCharacterElement(int pos) {
        return baseDoc.getCharacterElement(pos);
    }

    @Override
    public Color getForeground(AttributeSet attr) {
        return baseDoc.getForeground(attr);
    }

    @Override
    public Color getBackground(AttributeSet attr) {
        return baseDoc.getBackground(attr);
    }

    @Override
    public Font getFont(AttributeSet attr) {
        return baseDoc.getFont(attr);
    }

    @Override
    public void addDocumentListener(DocumentListener listener) {
        baseDoc.addDocumentListener(listener);
    }

    @Override
    public void removeDocumentListener(DocumentListener listener) {
        baseDoc.removeDocumentListener(listener);
    }

    @Override
    public void addUndoableEditListener(UndoableEditListener listener) {
        baseDoc.addUndoableEditListener(listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        baseDoc.removeUndoableEditListener(listener);
    }

    @Override
    public Object getProperty(Object key) {
        return baseDoc.getProperty(key);
    }

    @Override
    public void putProperty(Object key, Object value) {
        baseDoc.putProperty(key, value);
    }

    @Override
    public String getText(int offset, int length) throws BadLocationException {
        return baseDoc.getText(offset, length);
    }

    @Override
    public Position getStartPosition() {
        return baseDoc.getStartPosition();
    }

    @Override
    public Position getEndPosition() {
        return baseDoc.getEndPosition();
    }

    @Override
    public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {
        baseDoc.setCharacterAttributes(offset, length, s, replace);
    }

    @Override
    public void setParagraphAttributes(int offset, int length, AttributeSet s, boolean replace) {
        baseDoc.setParagraphAttributes(offset, length, s, replace);
    }
    
    public String convertDocumentToHTML() {
	    StringBuilder html = new StringBuilder("<html><body>");

	    int length = getLength();
	    int i = 0;

	    try {
	        while (i < length) {
	            Element elem = getCharacterElement(i);
	            AttributeSet attr = elem.getAttributes();

	            Component comp = StyleConstants.getComponent(attr);
	            if (comp instanceof JScrollPane scroll) {
	                JTable table = findTableInScroll(scroll);
	                if (table != null) {
	                    html.append("<h3>[Table: ")
	                        .append(table.getRowCount())
	                        .append(" rows, ")
	                        .append(table.getColumnCount())
	                        .append(" columns]</h3>");
	                    html.append(convertTableToHTML(table));
	                } else {
	                    html.append("<p>[Unknown Component]</p>");
	                }
	                i = elem.getEndOffset();
	                continue;
	            }

	            boolean bold = StyleConstants.isBold(attr);
	            boolean italic = StyleConstants.isItalic(attr);

//	            String text = doc.getText(i, elem.getEndOffset() - i)
//	                .replace("&", "&amp;")
//	                .replace("<", "&lt;")
//	                .replace(">", "&gt;");
	            String text = getText(i, elem.getEndOffset() - i)
	            	    .replace("&", "&amp;")
	            	    .replace("<", "&lt;")
	            	    .replace(">", "&gt;")
	            	    .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
	            	    .replace("\n", "<br/>");

	            String openTags = "", closeTags = "";
	            if (bold) {
	                openTags += "<b>";
	                closeTags = "</b>" + closeTags;
	            }
	            if (italic) {
	                openTags += "<i>";
	                closeTags = "</i>" + closeTags;
	            }

	            html.append(openTags).append(text).append(closeTags);

	            i = elem.getEndOffset();
	        }
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }

	    html.append("</body></html>");
	    return html.toString();
	}


	private String convertTableToHTML(JTable table) {
	    StringBuilder tableHTML = new StringBuilder();
	    tableHTML.append("<table border='1' cellpadding='5' cellspacing='0'>");

	    // Table headers
	    tableHTML.append("<tr>");
	    for (int i = 0; i < table.getColumnCount(); i++) {
	        tableHTML.append("<th>").append(table.getColumnName(i)).append("</th>");
	    }
	    tableHTML.append("</tr>");

	    // Table rows
	    for (int r = 0; r < table.getRowCount(); r++) {
	        tableHTML.append("<tr>");
	        for (int c = 0; c < table.getColumnCount(); c++) {
	            tableHTML.append("<td>").append(table.getValueAt(r, c)).append("</td>");
	        }
	        tableHTML.append("</tr>");
	    }

	    tableHTML.append("</table>");
	    return tableHTML.toString();
	}

	private JTable findTableInScroll(JScrollPane scroll) {
	    Component view = scroll.getViewport().getView();
	    if (view instanceof JTable table) {
	        return table;
	    }
	    return null;
	}

//    public void wr
}
