package builders.hazard_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.PartyTier;
import data.DataContainer.Source;
import data.hazards.Hazard;
import data.hazards.Hazard.HazardDanger;
import data.hazards.Trap;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.CompFactory.ScrollPolicy;
import gui.gui_helpers.DocumentHelper;

@SuppressWarnings("serial")
public class HazardBuilderPane extends JPanel
{
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setContentPane(new HazardBuilderPane(data));
			frm.pack();
			frm.setVisible(true);
		});
	}
	private DataContainer data;
	private HashMap<String, Hazard> hMap;	
	static final List<String> DANGER_OPTIONS = Stream.concat(
            Stream.of("None"),
            Stream.of(HazardDanger.values()).map(Enum::name)
    ).collect(Collectors.toUnmodifiableList());
	
	private ReminderField nameField, triggerField, durField;
	private JComboBox<Source> srcCombo;
	private RichEditor edit;
	
	@SuppressWarnings("unchecked")
	private JComboBox<String>[] dangerCombos = (JComboBox<String>[]) 
	        new JComboBox<?>[]{
		CompFactory.createCombo(String.class, DANGER_OPTIONS, ComponentType.BODY),
		CompFactory.createCombo(String.class, DANGER_OPTIONS, ComponentType.BODY),
		CompFactory.createCombo(String.class, DANGER_OPTIONS, ComponentType.BODY),
		CompFactory.createCombo(String.class, DANGER_OPTIONS, ComponentType.BODY)
	};
	
	private JCheckBox trapCheck;
	
	private JPanel sidePane, trapPane;
	
	
	
	public HazardBuilderPane(DataContainer data) {
		this.data = data;
		if(data.getHazards() != null) {
			hMap = new HashMap<String, Hazard>(data.getHazards());
		}else {
			hMap = new HashMap<String, Hazard>();
		}
		
		BuildContent();
	}
	
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		JPanel mPane = new JPanel();
		mPane.setLayout(new BorderLayout());
		this.add(mPane, BorderLayout.CENTER);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		mPane.add(hPane, BorderLayout.NORTH);
		
		BuildHeader(hPane);
		
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		mPane.add(descPane, BorderLayout.CENTER);
		
		trapPane = new JPanel();
		trapPane.setLayout(new GridLayout(0,1));
		descPane.add(trapPane, BorderLayout.NORTH);
		trapPane.setVisible(false);
		
		BuildTrapPane(trapPane);
		BuildDescPane(descPane);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		this.add(sideWrapper, BorderLayout.WEST);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		JScrollPane sideScroll = CompFactory.wrapPanelInScroll(sidePane, ScrollPolicy.VERTICAL);
		sideScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sideWrapper.add(sideScroll, BorderLayout.CENTER);
		
		JButton saveBtn = CompFactory.createNewButton("Save", this::Save);
		sideWrapper.add(saveBtn, BorderLayout.SOUTH);
		
		FillSidePane();
		
		JPanel btnPane = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
			CompFactory.createNewButton("Reset", this::Reset),
			CompFactory.createNewButton("Add Hazard/Trap", this::AddHazard)
		});
		mPane.add(btnPane, BorderLayout.SOUTH);
	}
	
	private void BuildHeader(JPanel hPane) {
		JPanel namePane = new JPanel();
		namePane.setLayout(new BorderLayout());
		hPane.add(namePane, BorderLayout.CENTER);
		
		nameField = CompFactory.createReminderField("Name...", ComponentType.HEADER);
		nameField.setColumns(20);
		namePane.add(nameField, BorderLayout.CENTER);
		
		JPanel srcPane = new JPanel();
		srcPane.setLayout(new BorderLayout());
		namePane.add(srcPane, BorderLayout.EAST);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source:", ComponentType.HEADER);
		srcPane.add(srcLbl, BorderLayout.WEST);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		srcCombo.setSelectedItem(Source.DungeonMastersGuide2024);
		srcPane.add(srcCombo, BorderLayout.CENTER);
		
		JPanel trapPane = new JPanel();
		trapPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		hPane.add(trapPane, BorderLayout.SOUTH);
		
		trapCheck = CompFactory.createNewCheckbox("Trap", null);
		trapCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				HazardBuilderPane.this.trapPane.setVisible(trapCheck.isSelected());
			}
		});
		trapPane.add(trapCheck);
	}
	
	private void BuildTrapPane(JPanel tPane) {
		JPanel triggerPane = new JPanel();
		triggerPane.setLayout(new BorderLayout());
		tPane.add(triggerPane);
		
		JLabel trigLbl = CompFactory.createNewLabel("Trigger:", ComponentType.HEADER);
		triggerPane.add(trigLbl, BorderLayout.WEST);
		
		triggerField = CompFactory.createReminderField("Trigger condition...", ComponentType.BODY);
		triggerPane.add(triggerField, BorderLayout.CENTER);
		
		JPanel durPane = new JPanel();
		durPane.setLayout(new BorderLayout());
		tPane.add(durPane);
		
		JLabel durLbl = CompFactory.createNewLabel("Duration:", ComponentType.HEADER);
		durPane.add(durLbl, BorderLayout.WEST);
		
		durField = CompFactory.createReminderField("Trap duration...", ComponentType.BODY);
		durPane.add(durField, BorderLayout.CENTER);
	}
	
	private void BuildDescPane(JPanel dPane) {
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		dPane.add(descPane, BorderLayout.CENTER);
		
		JPanel hazardPane = new JPanel();
		hazardPane.setLayout(new GridLayout(0,1));
		descPane.add(hazardPane, BorderLayout.NORTH);
		
		JPanel tierPane = new JPanel();
		tierPane.setLayout(new BorderLayout());
		hazardPane.add(tierPane);
		
		hazardPane.add(BuildTierPane(PartyTier.Tier1, dangerCombos[0]));
		hazardPane.add(BuildTierPane(PartyTier.Tier2, dangerCombos[1]));
		hazardPane.add(BuildTierPane(PartyTier.Tier3, dangerCombos[2]));
		hazardPane.add(BuildTierPane(PartyTier.Tier4, dangerCombos[3]));
		
		edit = new RichEditor(data);
		descPane.add(edit, BorderLayout.CENTER);
	}
	
	private JPanel BuildTierPane(PartyTier tier, JComboBox<String> combo) {
		JPanel tierPane = new JPanel();
		String desc = tier.getDescription() +":";
		if(desc.length() < (PartyTier.Tier4.getDescription() + ":").length()) {
			desc = String.format("%-" + (PartyTier.Tier4.getDescription() + ":").length() + "s", desc);
		}
		tierPane.setLayout(new BorderLayout());
		tierPane.add(CompFactory.createNewLabel(desc, ComponentType.HEADER), BorderLayout.WEST);
		tierPane.add(combo, BorderLayout.CENTER);
		return tierPane;
	}
	
	private void FillSidePane() {
		sidePane.removeAll();
		ArrayList<String> unsortedKeys = new ArrayList<String>(hMap.keySet());
		ArrayList<String> hazKeys = new ArrayList<String>();
		ArrayList<String> trapKeys = new ArrayList<String>();
		
		for(String key : unsortedKeys) {
			if(hMap.get(key) instanceof Trap)
				trapKeys.add(key);
			else
				hazKeys.add(key);
		}
		
		JLabel hazLbl = CompFactory.createNewLabel("Hazards", ComponentType.HEADER);
		hazLbl.setFont(hazLbl.getFont().deriveFont(hazLbl.getFont().getSize() + 3f));
		sidePane.add(hazLbl);
		FillList(hazKeys);
		
		JLabel trapLbl = CompFactory.createNewLabel("Traps", ComponentType.HEADER);
		trapLbl.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, Color.BLACK));
		trapLbl.setFont(trapLbl.getFont());
		sidePane.add(trapLbl);
		FillList(trapKeys);
		
		sidePane.revalidate();
		sidePane.repaint();
	}
	
	private void FillList(ArrayList<String> keys) {
		Collections.sort(keys);
		
		for(String key : keys) {
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());
			pane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
			sidePane.add(pane);
			
			JLabel lbl = CompFactory.createNewLabel(key, ComponentType.BODY);
			lbl.addMouseListener(CompFactory.createSideMouseListener(lbl, ()->{
				Load(key);
			}));
			pane.add(lbl, BorderLayout.CENTER);
			
			JButton delBtn = CompFactory.createDeleteButton(hMap, key, this::FillSidePane);
			pane.add(delBtn, BorderLayout.EAST);
		}
	}
	
	private void Reset() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.setFocusable(true);
		
		srcCombo.setEnabled(true);
		trapCheck.setEnabled(true);
		
		triggerField.setText("");
		durField.setText("");
		
		for(JComboBox<String> combo : dangerCombos)
			combo.setSelectedIndex(0);
		
		Container c = edit.getParent();
		c.remove(edit);
		edit = new RichEditor(data);
		c.add(edit, BorderLayout.CENTER);
		c.revalidate();
		c.repaint();
		
		nameField.requestFocus();
	}
	
	private boolean ResetConfirm(String load) {
		String msg = (load != null) ? 
				"Load " + load + ", you will lose all unsaved work" : 
				"Reset the editor, you will lose any unsaved work";
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, 
				msg, "Reset Confirm", JOptionPane.YES_NO_OPTION);
	}
	
	private void AddHazard() {
		if(trapCheck.isSelected()) {
			Trap t = new Trap();
			BuildHazard(t);
			t.duration = durField.getText();
			t.trigger = triggerField.getText();
			hMap.put(t.name, t);
		}else {
			Hazard h = new Hazard();
			BuildHazard(h);
			hMap.put(h.name, h);
		}
		
		Reset();
		FillSidePane();
	}
	
	private void BuildHazard(Hazard h) {
		h.name = nameField.getText();
		h.desc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
		h.src = (Source) srcCombo.getSelectedItem();
		
		h.dangerMap = new EnumMap<PartyTier, HazardDanger>(PartyTier.class);
		for(int i = 0; i < dangerCombos.length; i ++)
			if(!dangerCombos[i].getSelectedItem().equals("None"))
				h.dangerMap.put(intToTier(i), 
						HazardDanger.valueOf((String) dangerCombos[i].getSelectedItem()));
	}
	
	private void Load(String key) {
		ResetConfirm(key);
		Hazard h = hMap.get(key);
		if(h instanceof Trap) {
			Trap t = (Trap) h;
			trapCheck.setSelected(true);
			triggerField.setText(t.trigger);
			durField.setText(t.duration);
			LoadHazard(t);
		}else {
			trapCheck.setSelected(false);
			LoadHazard(h);
		}
		edit.requestFocus();
	}
	
	private void LoadHazard(Hazard h) {
		nameField.setText(h.name);
		nameField.setEditable(false);
		nameField.setFocusable(false);
		
		srcCombo.setSelectedItem(h.src);
		srcCombo.setEnabled(false);
		trapCheck.setEnabled(false);
		
		edit.LoadDocument(DocumentHelper.deepCopyDocument(h.desc));
		
		for(PartyTier p : h.dangerMap.keySet())
			dangerCombos[partyTierToInt(p)].setSelectedItem(h.dangerMap.get(p).toString());
	}
	
	private void Save() {
		data.SetHazardMap(hMap);
		data.SafeSaveData(MapType.HAZARDS);
	}
	
	private PartyTier intToTier(int val) {
		switch(val) {
		case 0: return PartyTier.Tier1;
		case 1: return PartyTier.Tier2;
		case 2: return PartyTier.Tier3;
		case 3: return PartyTier.Tier4;
		default: throw new IllegalArgumentException("Invalid Party Tier value");
		}
	}
	
	private int partyTierToInt(PartyTier val) {
		switch(val) {
		case PartyTier.Tier1: return 0;
		case PartyTier.Tier2: return 1;
		case PartyTier.Tier3: return 2;
		case PartyTier.Tier4: return 3;
		default: throw new IllegalArgumentException("Invalid Party Tier value");
		}
	}
}