package builders.vehicle_builder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import data.vehicles.LargeVehicle;
import data.vehicles.Mount;
import data.vehicles.Vehicle;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.StyleContainer;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.FilterCombo;

public class VehicleBuilderPane extends JPanel
{
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.setContentPane(new VehicleBuilderPane(data));
			frm.pack();
			frm.setVisible(true);
		});
	}
	private DataContainer data;
	private HashMap<String, Vehicle> vMap;
	
	@SuppressWarnings("unchecked")
	private String[] vehicleTypes = new String[]{
            Mount.class.getSimpleName(), 
            LargeVehicle.class.getSimpleName()
        };
	private JComboBox<String> classCombo;
	private JComboBox<Source> srcCombo;
	private JPanel cardPane;
	private JPanel sidePane;
	
	//Vehivle Fields
	private ReminderField nameField;
	private ReminderField costField;
	
	//Mount Fields
	private ReminderField carryField;
	private FilterCombo monstCombo;

	//Large Vehicles Fields
	private ReminderField speedField, cargoField, crewField, 
		passengersField, acField, hpField, damageThreshField;
	
	public VehicleBuilderPane(DataContainer data) {
		this.data = data;
		if(this.data.getVehicles() != null)
			vMap = new HashMap<String, Vehicle>(data.getVehicles());
		else
			vMap = new HashMap<String, Vehicle>();
		ConfigPane();
		BuildContent();
	}
	
	private void ConfigPane() {
		this.setLayout(new BorderLayout());
	}
	
	@SuppressWarnings("unchecked")
	private void BuildContent() {
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		JPanel titlePane = new JPanel();
		titlePane.setLayout(new BorderLayout());
		hPane.add(titlePane, BorderLayout.CENTER);
		
		nameField = CompFactory.createReminderField("Vehicle name...", ComponentType.HEADER);
		titlePane.add(nameField, BorderLayout.CENTER);
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new BorderLayout());
		titlePane.add(srcPane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source:", ComponentType.HEADER);
		srcPane.add(srcLbl, BorderLayout.WEST);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		srcPane.add(srcCombo, BorderLayout.CENTER);
		
		JPanel headConfig = new JPanel();
		headConfig.setLayout(new FlowLayout(FlowLayout.LEFT));
		hPane.add(headConfig, BorderLayout.SOUTH);
		
		JLabel costLbl = CompFactory.createNewLabel("Cost:", ComponentType.HEADER);
		headConfig.add(costLbl);
		
		costField = CompFactory.createReminderField("Cost...", true, ComponentType.BODY);
		costField.setColumns(10);
		headConfig.add(costField);
		
		JLabel typeLbl = CompFactory.createNewLabel("Vehicle Type:", ComponentType.HEADER);
		headConfig.add(typeLbl);
		
		classCombo = new JComboBox<String>(vehicleTypes);
		classCombo.setFont(StyleContainer.FNT_BODY_PLAIN);
		classCombo.addActionListener(_->{
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, (String) classCombo.getSelectedItem());
		});
		classCombo.setFocusable(false);
		headConfig.add(classCombo);
		
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		this.add(cardPane, BorderLayout.CENTER);
		
		this.add(CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Add Vehicle", this::AddVehicle),
				CompFactory.createNewButton("Reset Editor", _->{
					if(Confirmation(null) == JOptionPane.YES_OPTION)
						Reset();
				})
		}), BorderLayout.SOUTH);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		this.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = new JScrollPane(sidePane);
		sideWrapper.add(sideScroll, BorderLayout.CENTER);
		
		JButton saveButton = CompFactory.createNewButton("Save", this::Save);
		saveButton.setFocusable(false);
		sideWrapper.add(saveButton, BorderLayout.SOUTH);
		
		BuildMountPane();
		BuildLargeVehiclePane();
		FillSidePane();
	}
	
	private void BuildMountPane() {
		JPanel mountPane = new JPanel();
		mountPane.setLayout(new GridLayout(0,1));
		cardPane.add(mountPane, Mount.class.getSimpleName());		
		
		JPanel carryPane = new JPanel();
		carryPane.setLayout(new BorderLayout());
		mountPane.add(carryPane);
		
		JLabel carryLbl = CompFactory.createNewLabel("Carrying Capacity:", ComponentType.HEADER);
		carryPane.add(carryLbl, BorderLayout.WEST);
		
		carryField = CompFactory.createReminderField("Carry Capacity Value...", true, ComponentType.BODY);
		carryPane.add(carryField, BorderLayout.CENTER);
		
		JPanel statPane = new JPanel();
		statPane.setLayout(new BorderLayout());
		mountPane.add(statPane);
		
		JLabel statLbl = CompFactory.createNewLabel("Monster Stat Block:", ComponentType.HEADER);
		statPane.add(statLbl, BorderLayout.WEST);
		
		monstCombo = new FilterCombo(data.getMonsterKeysSorted(), 20);
		statPane.add(monstCombo, BorderLayout.CENTER);
	}
	
	private void BuildLargeVehiclePane() {
		JPanel lvPane = new JPanel();
		lvPane.setLayout(new GridLayout(0,1));
		cardPane.add(lvPane, LargeVehicle.class.getSimpleName());
		
		JPanel speedPane = new JPanel();
		speedPane.setLayout(new BorderLayout());
		lvPane.add(speedPane);
		
		JLabel speedLbl = CompFactory.createNewLabel("Vehicle Speed:", ComponentType.HEADER);
		speedPane.add(speedLbl, BorderLayout.WEST);
		
		speedField = CompFactory.createReminderField("Speed...", false, ComponentType.BODY);
		speedField.setDecimalsOnly();
		speedPane.add(speedField, BorderLayout.CENTER);
		
		JPanel cargoPane = new JPanel();
		cargoPane.setLayout(new BorderLayout());
		lvPane.add(cargoPane);
		
		JLabel cargoLbl = CompFactory.createNewLabel("Cargo Capacity:", ComponentType.HEADER);
		cargoPane.add(cargoLbl, BorderLayout.WEST);
		
		cargoField = CompFactory.createReminderField("Cargo capacity...", false, ComponentType.BODY);
		cargoField.setDecimalsOnly();
		cargoPane.add(cargoField, BorderLayout.CENTER);
		
		JPanel crewPane = new JPanel();
		crewPane.setLayout(new BorderLayout());
		lvPane.add(crewPane);
		
		JLabel crewLbl = CompFactory.createNewLabel("Crew Number:", ComponentType.HEADER);
		crewPane.add(crewLbl, BorderLayout.WEST);
		
		crewField = CompFactory.createReminderField("Crew...", true, ComponentType.BODY);
		crewPane.add(crewField);
		
		JPanel passPane = new JPanel();
		passPane.setLayout(new BorderLayout());
		lvPane.add(passPane);
		
		JLabel passLbl = CompFactory.createNewLabel("Passenger Number:", ComponentType.HEADER);
		passPane.add(passLbl, BorderLayout.WEST);
		
		passengersField = CompFactory.createReminderField("Passengers...", true, ComponentType.BODY);
		passPane.add(passengersField, BorderLayout.CENTER);
		
		JPanel acPane = new JPanel();
		acPane.setLayout(new BorderLayout());
		lvPane.add(acPane);
		
		JLabel acLbl = CompFactory.createNewLabel("AC:", ComponentType.HEADER);
		acPane.add(acLbl, BorderLayout.WEST);
		
		acField = CompFactory.createReminderField("Ac...", true, ComponentType.BODY);
		acPane.add(acField, BorderLayout.CENTER);
		
		JPanel hpPane = new JPanel();
		hpPane.setLayout(new BorderLayout());
		lvPane.add(hpPane);
		
		JLabel hpLbl = CompFactory.createNewLabel("Hp:", ComponentType.HEADER);
		hpPane.add(hpLbl, BorderLayout.WEST);
		
		hpField = CompFactory.createReminderField("Hp...", true, ComponentType.BODY);
		hpPane.add(hpField, BorderLayout.CENTER);
		
		JPanel dmgThreshPane = new JPanel();
		dmgThreshPane.setLayout(new BorderLayout());
		lvPane.add(dmgThreshPane);
		
		JLabel threshLbl = CompFactory.createNewLabel("Damage Threshold:", ComponentType.HEADER);
		dmgThreshPane.add(threshLbl, BorderLayout.WEST);
		
		damageThreshField = CompFactory.createReminderField("Threshold...", true, ComponentType.BODY);
		dmgThreshPane.add(damageThreshField);
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		
		ArrayList<String> keys = new ArrayList<String>(vMap.keySet());
		Collections.sort(keys);
		
		ArrayList<String> mKeys = new ArrayList<String>();
		ArrayList<String> vlKeys = new ArrayList<String>();
		
		for(String key : keys)
			if(vMap.get(key) instanceof Mount)
				mKeys.add(key);
			else
				vlKeys.add(key);
		
		JLabel mLbl = CompFactory.createNewLabel("Mounts", ComponentType.HEADER);
		sidePane.add(mLbl);
		for(String key : mKeys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				Load(key);
			}));
			pane.add(lbl, BorderLayout.CENTER);
			JButton delBtn = CompFactory.createDeleteButton(vMap, key, this::FillSidePane);
			pane.add(delBtn, BorderLayout.EAST);
		}
		
		JLabel vlLbl = CompFactory.createNewLabel("Large Vehicles", ComponentType.HEADER);
		vlLbl.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLACK));
		sidePane.add(vlLbl);
		for(String key : vlKeys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				Load(key);
			}));
			pane.add(lbl, BorderLayout.CENTER);
			JButton delBtn = CompFactory.createDeleteButton(vMap, key, this::FillSidePane);
			pane.add(delBtn, BorderLayout.EAST);
		}
		if(keys.size() == 1) {
			this.revalidate();
			this.repaint();
		}
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private int Confirmation(String key) {
		String msg;
		if(key == null)
			msg = "Reset editor you will lose unsaved progress?";
		else
			msg = "Load " + key + ", you will lose any unadded progress.";
		
		return JOptionPane.showConfirmDialog(this, msg, "Reset Confirm", JOptionPane.YES_NO_OPTION);
	}
	
	private void Reset() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.setFocusable(true);
		classCombo.setEnabled(true);
		costField.setText("");
		
		carryField.setText("");
		monstCombo.reset();
		
		speedField.setText("");
		cargoField.setText("");
		crewField.setText("");
		passengersField.setText("");
		acField.setText("");
		hpField.setText("");
		damageThreshField.setText("");

		nameField.requestFocus();
	}
	
	private void Load(String key) {
		if(Confirmation(key) == JOptionPane.YES_OPTION) {
			if(vMap.get(key) instanceof Mount) {
				Mount m = (Mount) vMap.get(key);
				((CardLayout)cardPane.getLayout()).show(cardPane, Mount.class.getSimpleName());
				nameField.setText(key);
				srcCombo.setSelectedItem(m.src);
				costField.setText("" + m.cost);
				carryField.setText("" + m.carryCapacity);
				monstCombo.setSelectedItem(m.stats.name);
				classCombo.setSelectedItem(Mount.class.getSimpleName());
			}else {
				LargeVehicle lv = (LargeVehicle) vMap.get(key);
				((CardLayout)cardPane.getLayout()).show(cardPane, Mount.class.getSimpleName());
				nameField.setText(key);
				srcCombo.setSelectedItem(lv.src);
				costField.setText("" + lv.cost);
				speedField.setText("" + lv.speed);
				cargoField.setText("" + lv.cargo);
				crewField.setText("" + lv.crew);
				passengersField.setText("" + lv.passengers);
				acField.setText("" + lv.ac);
				hpField.setText("" + lv.hp);
				damageThreshField.setText("" + lv.damageThreshold);
				classCombo.setSelectedItem(LargeVehicle.class.getSimpleName());
			}
			
			classCombo.setEnabled(false);
			nameField.setEditable(false);
			nameField.setFocusable(false);
		}
	}
	
	private void AddVehicle() {
		if(classCombo.getSelectedItem().equals("Mount") && canAddMount()) {
			Mount m = new Mount();
			m.name = nameField.getText();
			m.src = (Source) srcCombo.getSelectedItem();
			m.cost = Integer.parseInt(costField.getText());
			m.carryCapacity = Integer.parseInt(carryField.getText());
			m.stats = data.getMonsters().get(monstCombo.getSelectedItem());
			vMap.put(m.name, m);
		}else if(canAddLargeVehicle()){
			LargeVehicle lv = new LargeVehicle();
			lv.name = nameField.getText();
			lv.src = (Source) srcCombo.getSelectedItem();
			lv.cost = Integer.parseInt(costField.getText());
			lv.speed = Double.parseDouble(speedField.getText());
			lv.cargo = Double.parseDouble(cargoField.getText());
			lv.crew = Integer.parseInt(crewField.getText());
			lv.passengers = Integer.parseInt(passengersField.getText());
			lv.ac = Integer.parseInt(acField.getText());
			lv.hp = Integer.parseInt(hpField.getText());
			lv.damageThreshold = Integer.parseInt(damageThreshField.getText());
			vMap.put(lv.name, lv);
		}else {
			JOptionPane.showMessageDialog(this, "Please fill out all fields before adding.", 
					"Add Fail Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		Reset();
		FillSidePane();
	}
	
	private boolean canAddMount() {
		try {
			if(nameField.getText().length() <= 0) return false;
			Integer.parseInt(costField.getText());
			Integer.parseInt(carryField.getText());
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean canAddLargeVehicle() {
		try {
			if(nameField.getText().length() <= 0) return false;
			Double.parseDouble(speedField.getText());
			Double.parseDouble(cargoField.getText());
			Integer.parseInt(crewField.getText());
			Integer.parseInt(passengersField.getText());
			Integer.parseInt(acField.getText());
			Integer.parseInt(hpField.getText());
			Integer.parseInt(damageThreshField.getText());
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	private void Save() {

		System.out.println(Arrays.toString(vMap.keySet().toArray()));
		data.SetVehiclesMap(vMap);
		data.SafeSaveData(MapType.VEHICLES);
	}
}