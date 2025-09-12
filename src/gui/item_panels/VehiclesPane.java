package gui.item_panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import data.DataChangeListener;
import data.DataContainer;
import data.DataContainer.MapType;
import data.vehicles.LargeVehicle;
import data.vehicles.Mount;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class VehiclesPane extends JPanel implements DataChangeListener
{
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setContentPane(new VehiclesPane(data, new GuiDirector(new JDesktopPane())));
			frm.pack();
			frm.setVisible(true);
		});
	}
	private DataContainer data;
	private GuiDirector gd;
	
	public VehiclesPane(DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		
		BuildContent();
	}
	
	private void BuildContent() {
		List<String> mKeys = data.getMountKeysSorted();
		List<String> vKeys = data.getLargeVehicleKeysSorted();
		
		this.setLayout(new GridLayout(0,1));
		
		BuildMountPane(mKeys);
		BuildVehiclePane(vKeys);
	}
	
	private void BuildMountPane(List<String> mKeys) {
		JPanel mountPane = new JPanel();
		mountPane.setLayout(new BorderLayout());
		this.add(mountPane);
		
		JLabel mountLbl = CompFactory.createNewLabel("Mounts:", ComponentType.HEADER);
		mountLbl.setFont(mountLbl.getFont().deriveFont(mountLbl.getFont().getSize() + 2f));
		mountPane.add(mountLbl, BorderLayout.NORTH);
		
		String[] headers = new String[] {"Mount", "Carrying Capacity", "Cost",};
		Object[][] vals = new Object[mKeys.size()][3];
		for(int i = 0; i < vals.length; i ++) {
			Mount m = (Mount) data.getVehicles().get(mKeys.get(i));
			vals[i][0] = m.name;
			vals[i][1] = m.carryCapacity + Mount.CARRY_UNIT;
			vals[i][2] = m.cost + " GP";
		}
		UneditableTableModel tableModel = new UneditableTableModel(vals, headers);
		JTable mountTable = new JTable(tableModel);
		mountTable.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int row = mountTable.rowAtPoint(e.getPoint());
		        int col = mountTable.columnAtPoint(e.getPoint());

		        if (row >= 0 && col == 0) {
		        	String key = (String) mountTable.getValueAt(row, col);
		        	String monstKey = ((Mount)data.getVehicles().get(key)).stats.name;
		        	
		        	if(gd.getComboFrame() != null)
		        		gd.getComboFrame().AddTab(data.getMonsters().get(monstKey));
		        	else
		        		if(gd.getmFrame() != null)
		        			gd.getmFrame().AddMonsterPane(monstKey);
		        		else
		        			System.out.println(data.getMonsters().get(monstKey).name);
		        }
		    }
		});
		mountTable.setFont(StyleContainer.FNT_BODY_PLAIN);
		mountTable.setRowHeight(25);
		mountTable.setIntercellSpacing(new Dimension(0, 0));
		mountTable.setShowVerticalLines(false); 
		mountTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		mountTable.setPreferredScrollableViewportSize(mountTable.getPreferredSize());
		mountTable.getTableHeader().setReorderingAllowed(false);

		
		 JPanel tableWrapper = new JPanel(new BorderLayout());
	        tableWrapper.add(mountTable.getTableHeader(), BorderLayout.NORTH);
	        tableWrapper.add(mountTable, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(mountTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(mountTable.getPreferredSize());
		mountPane.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void BuildVehiclePane(List<String> vKeys) {
		JPanel vehiclePane = new JPanel();
		vehiclePane.setLayout(new BorderLayout());
		this.add(vehiclePane);
		
		JLabel vehicleLbl = CompFactory.createNewLabel("Vehicle:", ComponentType.HEADER);
		vehicleLbl.setFont(vehicleLbl.getFont().deriveFont(vehicleLbl.getFont().getSize() + 2f));
		vehiclePane.add(vehicleLbl, BorderLayout.NORTH);
		
		String[] headers = new String[] {"Ship", "Speed", "Crew", "Passengers", "Cargo (Tons)", 
				"Ac", "HP", "Damage Threshold", "Cost"};
		Object[][] vals = new Object[vKeys.size()][headers.length];
		for(int i = 0; i < vals.length; i ++) {
			LargeVehicle v = (LargeVehicle) data.getVehicles().get(vKeys.get(i));
			vals[i][0] = v.name;
			vals[i][1] = v.speed + " mph";
			vals[i][2] = v.crew;
			vals[i][3] = (v.passengers > 0) ? v.passengers : "-";
			vals[i][4] = (v.cargo > 0) ? v.cargo : "-";
			vals[i][5] = v.ac;
			vals[i][6] = v.hp;
			vals[i][7] = (v.damageThreshold > 0) ? v.damageThreshold : "-";;
			vals[i][8] = v.cost + " GP";
		}
		UneditableTableModel tableModel = new UneditableTableModel(vals, headers);
		
		JTable vehicleTable = new JTable(tableModel);
		vehicleTable.setFont(StyleContainer.FNT_BODY_PLAIN);
		vehicleTable.setRowHeight(25);
		vehicleTable.setIntercellSpacing(new Dimension(0, 0));
		vehicleTable.setShowVerticalLines(false); 
		vehicleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		vehicleTable.setPreferredScrollableViewportSize(vehicleTable.getPreferredSize());
		vehicleTable.getTableHeader().setReorderingAllowed(false);

		
		 JPanel tableWrapper = new JPanel(new BorderLayout());
	        tableWrapper.add(vehicleTable.getTableHeader(), BorderLayout.NORTH);
	        tableWrapper.add(vehicleTable, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(vehicleTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(vehicleTable.getPreferredSize());
		vehiclePane.add(scrollPane, BorderLayout.CENTER);
	}

	public void onMapUpdated() {
		this.removeAll();
		BuildContent();
	}
	public void onMapUpdated(MapType mapType) {
		if(mapType == MapType.VEHICLES)
			onMapUpdated();
	}
	
}

class UneditableTableModel extends DefaultTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UneditableTableModel(Object[][] data, String[] headers) {
		super(data, headers);
	}
	
	public boolean isCellEditable(int row, int column) {
        return false;
    }
}