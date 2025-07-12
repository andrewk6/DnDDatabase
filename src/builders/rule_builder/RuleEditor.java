package builders.rule_builder;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import builders.utlities.*;

public class RuleEditor extends JPanel {
	private final JTextPane editorTextPane;
	private JButton boldButton, itBtn;
	
	private final StyledDocument doc;
	private boolean boldMode = false;
	private boolean italicMode = false;
	
	private final Font DEFAULT_FONT_LARGE = new Font("Monospaced", Font.BOLD, 19);
	private final Font DEFAULT_FONT_REG = new Font("Monospaced", Font.PLAIN, 16);

	public RuleEditor() {
		this.setLayout(new BorderLayout());

		editorTextPane = new JTextPane() 
		{			
			public boolean getScrollableTracksViewportWidth() {
				return true;
		    }
		};
		editorTextPane.setPreferredSize(new Dimension(300, 300));
		editorTextPane.setFont(DEFAULT_FONT_REG);
//		rulesTextPane.setEditorKit(new StyledEditorKit());
		editorTextPane.setEditorKit(new WrapEditorKit());
//		rulesTextPane.setStyledDocument(new CustomStyledDocument());
		// Bind Ctrl+B to BoldAction
		KeyStroke boldStroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK);
		editorTextPane.getInputMap().put(boldStroke, "bold");
		editorTextPane.getActionMap().put("bold", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boldMode = !boldMode;
				setTextStyle();
			}
		});
		KeyStroke itStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
		editorTextPane.getInputMap().put(itStroke, "italic");
		editorTextPane.getActionMap().put("italic", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				italicMode = !italicMode;
				setTextStyle();
			}
		});
		doc = editorTextPane.getStyledDocument();

		JScrollPane scrollPane = new JScrollPane(editorTextPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// Toolbar
		JToolBar toolBar = new JToolBar();

		boldButton = new JButton("Bold");
		boldButton.setFont(DEFAULT_FONT_LARGE);
		boldButton.addActionListener(e -> {
			boldMode = !boldMode;
			setTextStyle();
		});
		boldButton.setFocusable(false);
		toolBar.add(boldButton);
		toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

		itBtn = new JButton("Iitalics");
		itBtn.setFont(DEFAULT_FONT_LARGE);
		itBtn.addActionListener(e -> {
			italicMode = !italicMode;
			setTextStyle();
			
		});
		itBtn.setFocusable(false);
		toolBar.add(itBtn);
		toolBar.add(Box.createRigidArea(new Dimension(10, 0)));
		
		// Set default style
		setTextStyle();

		JButton tableButton = new JButton("Insert Table");
		tableButton.setFont(DEFAULT_FONT_LARGE);
		tableButton.addActionListener(this::insertTable);
		tableButton.setFocusable(false);
		toolBar.add(tableButton);

		add(toolBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void setTextStyle() {
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setItalic(attr, italicMode);
		StyleConstants.setBold(attr, boldMode);
		editorTextPane.setCharacterAttributes(attr, true);
		
		boldButton.setText(boldMode ? "Bold (ON)" : "Bold");
		itBtn.setText(italicMode ? "Italics (ON)" : "Italics");

	}

	private void insertTable(ActionEvent e) {
		showTableDialog();
	}

	private void showTableDialog() {
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Insert Table",
				Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		JTextField rowField = new JTextField("3");
		JTextField colField = new JTextField("3");

		inputPanel.add(new JLabel("Number of Rows:"));
		inputPanel.add(rowField);
		inputPanel.add(new JLabel("Number of Columns:"));
		inputPanel.add(colField);

		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(e -> {
			int rows, cols, prefSize;
			try {

				rows = Integer.parseInt(rowField.getText());
				cols = Integer.parseInt(colField.getText());
				if (rows <= 0 || cols <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Please enter valid positive numbers.", "Invalid Input",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			dialog.getContentPane().removeAll();
			showHeaderInputPanel(dialog, rows, cols);
		});
		inputPanel.add(new JLabel());
		inputPanel.add(nextButton);

		dialog.add(inputPanel, BorderLayout.CENTER);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showHeaderInputPanel(JDialog dialog, int rows, int cols) {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JPanel headerPanel = new JPanel(new GridLayout(cols, 2, 5, 5));
		JTextField[] headers = new JTextField[cols];

		for (int i = 0; i < cols; i++) {
			headerPanel.add(new JLabel("Header " + (i + 1) + ":"));
			headers[i] = new JTextField("Col " + (i + 1));
			headerPanel.add(headers[i]);
		}

		JButton insertButton = new JButton("Insert Table");
		insertButton.addActionListener(e -> {
			String[] colNames = new String[cols];
			for (int i = 0; i < cols; i++) {
				colNames[i] = headers[i].getText().trim();
				if (colNames[i].isEmpty())
					colNames[i] = "Column " + (i + 1);
			}

			String[][] data = new String[rows][cols];
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					data[r][c] = "";
				}
			}

			SerialTable table = new SerialTable(data, colNames);
			table.setFont(DEFAULT_FONT_REG);
			table.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 16));
//			table.setPreferredScrollableViewportSize(new Dimension(cols*prefSize, 80));
//			new Dimension(cols * prefSize, rows * prefSize);
//			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			int rowHeight = 20;
			int rowCount = table.getRowCount();
			table.setRowHeight(rowHeight);

			int visibleHeight = rowHeight * rowCount;
			int visibleWidth = cols * 80;  // assuming 80px per column

			table.setPreferredScrollableViewportSize(new Dimension(visibleWidth, visibleHeight));
			
			JScrollPane tableScroll = new JScrollPane(table);
			tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			editorTextPane.insertComponent(tableScroll);
			dialog.dispose();
		});

		panel.add(new JLabel("Enter column headers:"), BorderLayout.NORTH);
		panel.add(headerPanel, BorderLayout.CENTER);
		panel.add(insertButton, BorderLayout.SOUTH);

		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
	}
	
    public String convertDocumentToHTML() {
	    StringBuilder html = new StringBuilder("<html><body>");

	    StyledDocument doc = editorTextPane.getStyledDocument();
	    int length = doc.getLength();
	    int i = 0;

	    try {
	        while (i < length) {
	            Element elem = doc.getCharacterElement(i);
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
	            String text = doc.getText(i, elem.getEndOffset() - i)
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

	public String getText() {
		return editorTextPane.getText();
	}

	public JTextPane getTextPane() {
		return editorTextPane;
	}
}

class SerializableTable extends JTable implements Serializable {
	

	public SerializableTable(Object[][] data, Object[] headers) {
        super(data, headers);
        // Avoid storing cell editors which might not be serializable
//        this.setDefaultEditor(Object.class, null);
    }
    
    public void stopEditing() {
    	clearSelection();  // Removes row selection
	    getSelectionModel().clearSelection();  // Clears the selection model
	    setCellSelectionEnabled(false); 
    	// If editing is still in progress, stop the editing
        if (isEditing()) {
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
                editor.stopCellEditing(); // Stop editing if active
            }
        }

        // Remove the default editor to avoid serialization issues with non-serializable editors
        setDefaultEditor(Object.class, null);
        setDefaultRenderer(Object.class, null);

        // Clear any selection in the table
	}
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	stopEditing();
    	
        // Remove the default editor for all columns before serialization
        setDefaultEditor(Object.class, null);  // Clears the editor
        setDefaultRenderer(Object.class, null);
        
        // Proceed with the default serialization of the table
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        // Deserialize the table object
        ois.defaultReadObject(); // This will deserialize the basic fields of the table (including the model)

        // Make the table uneditable after deserialization
        setDefaultEditor(Object.class, null);  // Disable editing on all cells

        // Optionally, you can also disable the table entirely (make it unselectable and uneditable)
        setEnabled(false); // Disable the table interaction
    }
    
    
}