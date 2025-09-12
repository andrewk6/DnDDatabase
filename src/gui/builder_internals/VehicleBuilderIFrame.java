package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import builders.vehicle_builder.VehicleBuilderPane;
import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import data.players.Species;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.StyleContainer;

public class VehicleBuilderIFrame extends JInternalFrame
{
	public VehicleBuilderIFrame(DataContainer data)
	{

		ConfigFrame();
		this.setContentPane(new VehicleBuilderPane(data));
		pack();
	}

	private void ConfigFrame() {
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		StyleContainer.ConfigIFrame(this, "Species Room Builder");
		StyleContainer.SetIcon(this, StyleContainer.SPECIES_BUILDER_ICON_FILE);
	}	
}