package gui.gui_helpers;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import data.DataContainer.Source;
import gui.gui_helpers.structures.StyleContainer;

public class SourceButton extends JButton
{
	
	private final Set<Source> selectedSources = EnumSet.allOf(Source.class);
	
	public SourceButton() {
		StyleContainer.SetFontBtn(this);

        // Build popup menu with checkbox items
        JPopupMenu sourcePopup = new JPopupMenu();

        Map<Source, JCheckBoxMenuItem> sourceMenuItems = new EnumMap<>(Source.class);
        for (Source src : Source.values()) {
            JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(src.toString(), true);
            sourceMenuItems.put(src, cbItem);
            sourcePopup.add(cbItem);
            cbItem.addItemListener(e -> {
                boolean selected = cbItem.isSelected();
                if (selected) selectedSources.add(src);
                else selectedSources.remove(src);
                updateSourceButtonText();
            });
        }

        addActionListener(e -> {
            sourcePopup.show(this, 0, getHeight());
        });

        updateSourceButtonText();
	}
	
	private void updateSourceButtonText() {
        if (selectedSources.size() == Source.values().length) {
            setText("Select Sources (All)");
        } else if (selectedSources.isEmpty()) {
            setText("Select Sources (None)");
        } else {
            setText("Select Sources (" + selectedSources.size() + ")");
        }
    }
	
	public Set<Source> getSelectedSources(){
		return new HashSet<Source>(selectedSources);
	}
}