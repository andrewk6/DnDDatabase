package builders.rule_builder;

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
        	if(table instanceof SerializableTable)
        		((SerializableTable) table).stopEditing();
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

//    public void wr
}
