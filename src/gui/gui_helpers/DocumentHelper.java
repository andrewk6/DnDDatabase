package gui.gui_helpers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import data.DataContainer;

public class DocumentHelper {
	public static void insertStyledDocument(StyledDocument mainDoc, StyledDocument insertDoc, int caretPosition)
			throws BadLocationException {

		int length = insertDoc.getLength();

//		System.out.println(length + "/" + caretPosition);
//		// Insert blank space in mainDoc to make room for inserted content
//		mainDoc.insertString(caretPosition, "\0".repeat(length), null);

		// Copy text and attributes from insertDoc into mainDoc
		for (int i = 0; i < length; i++) {
			Element elem = insertDoc.getCharacterElement(i);
			AttributeSet attrs = elem.getAttributes();
			String ch = insertDoc.getText(i, 1);
			if (caretPosition < 0 || caretPosition > mainDoc.getLength()) {
			    throw new IllegalArgumentException("Caret position out of bounds: " + caretPosition);
			}

			// Replace the dummy character with the real character and attributes
//			mainDoc.remove(caretPosition + i, 1);
			mainDoc.insertString(caretPosition + i, ch, attrs);
		}
	}

	public static void insertWithReplacements(StyledDocument mainDoc, StyledDocument insertDoc, int caretPosition,
			HashMap<String, String> replacements) throws BadLocationException {
		System.out.println("Insert With Replacements");

		// Step 1: Create a temporary doc to hold modified insertDoc content with
		// replacements
		StyledDocument tempDoc = new DefaultStyledDocument();

		int length = insertDoc.getLength();
		int pos = 0;

		while (pos < length) {
			// Get element at pos to respect attribute boundaries
			Element elem = insertDoc.getCharacterElement(pos);
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();
			if (end > length)
				end = length;

			// Extract text and attributes from insertDoc in this range
			String text = insertDoc.getText(start, end - start);
			AttributeSet attrs = elem.getAttributes();

			// Replace placeholders in this text
			String replacedText = replacePlaceholders(text, replacements);

			// Insert replaced text with original attributes into tempDoc
			tempDoc.insertString(tempDoc.getLength(), replacedText, attrs);

			pos = end;
		}

		// Step 2: Insert tempDoc's content into mainDoc at caretPosition

		// Extract full content and attributes from tempDoc for insertion
		int tempLength = tempDoc.getLength();

		// Insert blank space in mainDoc to make room
		mainDoc.insertString(caretPosition, "\0".repeat(tempLength), null);

		// Now copy attributes and text from tempDoc into mainDoc at caretPosition
		for (int i = 0; i < tempLength; i++) {
			Element elem = tempDoc.getCharacterElement(i);
			AttributeSet attrs = elem.getAttributes();
			String ch = tempDoc.getText(i, 1);
			mainDoc.remove(caretPosition + i, 1);
			mainDoc.insertString(caretPosition + i, ch, attrs);
		}
	}

//	 Helper method to replace all placeholders in text with their values from
//	 replacements map
	private static String replacePlaceholders(String text, HashMap<String, String> replacements) {
		String result = text;
		for (Map.Entry<String, String> entry : replacements.entrySet()) {
			System.out.println(entry);
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static StyledDocument replacePlaceholders(
	        StyledDocument sourceDoc,
	        LinkedHashMap<String, String> replacements
	) throws BadLocationException {

	    StyledDocument resultDoc = new DefaultStyledDocument();
	    int length = sourceDoc.getLength();
	    int pos = 0;

	    while (pos < length) {
	        Element element = sourceDoc.getCharacterElement(pos);
	        AttributeSet attrs = element.getAttributes();

	        int start = element.getStartOffset();
	        int end = element.getEndOffset();
	        int chunkLen = end - start;

	        String chunk = sourceDoc.getText(start, chunkLen);

	        // Replace placeholders in this chunk
	        for (Map.Entry<String, String> entry : replacements.entrySet()) {
	            chunk = chunk.replace(entry.getKey(), entry.getValue());
	        }

	        // Insert the replaced chunk with attributes
	        resultDoc.insertString(resultDoc.getLength(), chunk, attrs);
	        pos = end;
	    }

	    return resultDoc;
	}
	
	public static void extendAndInsertStyledDocument(
	        StyledDocument mainDoc,
	        StyledDocument insertDoc,
	        int insertOffset,
	        LinkedHashMap<String, String> replacements
	) throws BadLocationException {

	    // Validate offset
	    if (insertOffset < 0 || insertOffset > mainDoc.getLength()) {
	        throw new BadLocationException("Invalid insert offset: " + insertOffset + 
	        		" Doc Length: " + mainDoc.getLength() + 
	        		" Doc text: " + mainDoc.getText(0, mainDoc.getLength()), insertOffset);
	    }

	    // Get total length of content to be inserted
	    int insertLength = insertDoc.getLength();

	    // Extract full text to determine true character count
	    String insertText = insertDoc.getText(0, insertLength);

	    // Do replacements if needed
	    if (replacements != null && !replacements.isEmpty()) {
	        StyledDocument replacedDoc = DocumentHelper.replacePlaceholders(insertDoc, replacements);
	        insertDoc = replacedDoc;
	        insertText = insertDoc.getText(0, insertDoc.getLength());
	        insertLength = insertText.length();
	    }

	    // Step 1: Make space in mainDoc for the insertion
	    mainDoc.insertString(insertOffset, " ".repeat(insertLength), null); // create space

	    // Step 2: Insert styled content into that space
	    DocumentHelper.insertStyledDocument(mainDoc, insertDoc, insertOffset);
	}

}
