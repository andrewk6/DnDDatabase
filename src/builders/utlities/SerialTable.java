package builders.utlities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class SerialTable extends JTable implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerialTable(Object[][] data, Object[] headers) {
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
//    	System.out.println("Attempt to write Table");
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
    // Optional: writeObject / readObject if needed