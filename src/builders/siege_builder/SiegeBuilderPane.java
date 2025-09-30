package builders.siege_builder;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import builders.utlities.AbstractBuilderPane;
import builders.utlities.BuilderFrame;
import data.DataContainer;
import data.DataContainer.MapType;
import data.DataContainer.Source;
import data.siege_equipment.SiegeEquipment;
import data.siege_equipment.SiegeEquipment.SiegeSize;
import data.siege_equipment.SiegeEquipment.SiegeUse;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.DocumentHelper;

public class SiegeBuilderPane extends AbstractBuilderPane<SiegeEquipment>
{		
	public static void main(String[]args) {
		DataContainer data = new DataContainer();
		data.init();
		
		SwingUtilities.invokeLater(()->{
			BuilderFrame<SiegeEquipment, SiegeBuilderPane> bFrame = 
					new BuilderFrame<SiegeEquipment, SiegeBuilderPane>(
							data, new SiegeBuilderPane(data), SiegeEquipment.class);
			bFrame.setVisible(true);
		});
	}
	
	private ReminderField nameField, attackNameField, acField, hpField;
	private JComboBox<Source> srcCombo;
	private JComboBox<SiegeUse> useCombo;
	private JComboBox<SiegeSize> sizeCombo;
	private RichEditor attackEdit, edit;

	public SiegeBuilderPane(DataContainer data) {
		super(data);
		if(data.getSiegeEquipment() != null) {
			map = new HashMap<String, SiegeEquipment>(data.getSiegeEquipment());
		}else
			map = new HashMap<String, SiegeEquipment>();
		FillSidePane();
	}
	
	@Override
	protected void BuildPane() {
		this.setLayout(new BorderLayout());
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		this.add(mainPane, BorderLayout.CENTER);
		BuildHeadPane(mainPane);
		
		JPanel bodyPane = new JPanel();
		bodyPane.setLayout(new GridLayout(0,1));
		mainPane.add(bodyPane, BorderLayout.CENTER);
		BuildAttackPane(bodyPane);
		BuildEditPane(bodyPane);
		
		JPanel btnPane = CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
				CompFactory.createNewButton("Reset Editor", this::ResetConfirm),
				CompFactory.createNewButton("Add Siege Equipment", this::AddObj)
			});
			mainPane.add(btnPane, BorderLayout.SOUTH);
		
		JPanel sideWrapper = new JPanel();
		sideWrapper.setLayout(new BorderLayout());
		this.add(sideWrapper, BorderLayout.WEST);
		
		JButton saveBtn = CompFactory.createNewButton("Save", this::Save);
		sideWrapper.add(saveBtn, BorderLayout.SOUTH);
		
		sidePane = new JPanel();
		sidePane.setLayout(new GridLayout(0,1));
		sideWrapper.add(CompFactory.wrapPanelInScroll(sidePane));
	}
	
	private void BuildHeadPane(JPanel mPane) {
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		mPane.add(topPane, BorderLayout.NORTH);
		
		JPanel headPane = new JPanel();
		headPane.setLayout(new BorderLayout());
		topPane.add(headPane, BorderLayout.CENTER);
		
		nameField = CompFactory.createReminderField("Siege Equipment Name...", ComponentType.HEADER);
		headPane.add(nameField, BorderLayout.CENTER);
		
		srcCombo = CompFactory.createEnumCombo(Source.class, ComponentType.BODY);
		headPane.add(CompFactory.createSplitPane("Source: ", srcCombo), BorderLayout.EAST);
		
		JPanel statPane = new JPanel();
		statPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPane.add(statPane, BorderLayout.SOUTH);
		
		acField = CompFactory.createReminderField("AC...", true, 8);
		statPane.add(CompFactory.createSplitPane("Armor Class: ", acField));
		
		hpField = CompFactory.createReminderField("HP...", true, 8);
		statPane.add(CompFactory.createSplitPane("Hit Points: ", hpField));
		
		sizeCombo = CompFactory.createEnumCombo(SiegeSize.class, ComponentType.BODY);
		statPane.add(CompFactory.createSplitPane("Size: ", sizeCombo));
	}
	
	private void BuildAttackPane(JPanel bPane) {
		JPanel attackPane = new JPanel();
		attackPane.setLayout(new BorderLayout());
		attackPane.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, Color.BLACK));
		bPane.add(attackPane);
		
		attackPane.add(CompFactory.createNewLabel("Attack:", ComponentType.HEADER, -2), 
				BorderLayout.NORTH);
		
		JPanel aConfPane = new JPanel();
		aConfPane.setLayout(new BorderLayout());
		attackPane.add(aConfPane, BorderLayout.CENTER);
		
		JPanel aBasePane = new JPanel();
		aBasePane.setLayout(new FlowLayout(FlowLayout.LEFT));
		aConfPane.add(aBasePane, BorderLayout.NORTH);
		
		attackNameField = CompFactory.createReminderField("Attack name", 20);
		aBasePane.add(CompFactory.createSplitPane("Attack Name: ", attackNameField));
		
		useCombo = CompFactory.createEnumCombo(SiegeUse.class, ComponentType.BODY);
		aBasePane.add(CompFactory.createSplitPane("Siege Equipment Use:", useCombo));
		
		attackEdit = new RichEditor(data);
		aConfPane.add(attackEdit, BorderLayout.CENTER);
	}
	
	private void BuildEditPane(JPanel bPane) {
		JPanel descPane = new JPanel();
		descPane.setLayout(new BorderLayout());
		bPane.add(descPane);
		
		descPane.add(CompFactory.createNewLabel(
				"Siege Equipment Description:", ComponentType.HEADER, -2), BorderLayout.NORTH);
		
		edit = new RichEditor(data);
		descPane.add(edit, BorderLayout.CENTER);
	}
	
	@Override
	protected void SortKeys(ArrayList<String> keys) {
		Collections.sort(keys);
	}	
	
	@Override
	protected void ResetFields() {
		nameField.setText("");
		nameField.setEditable(true);
		nameField.setFocusable(true);
		
		attackNameField.setText("");
		acField.setText("");
		hpField.setText("");
		
		useCombo.setSelectedIndex(0);
		sizeCombo.setSelectedIndex(0);
		
		Container aCont = attackEdit.getParent();
		aCont.remove(attackEdit);
		attackEdit = new RichEditor(data);
		aCont.add(attackEdit, BorderLayout.CENTER);
		aCont.revalidate();
		aCont.repaint();
		
		Container eCont = edit.getParent();
		eCont.remove(edit);
		edit = new RichEditor(data);
		eCont.add(edit, BorderLayout.CENTER);
		eCont.revalidate();
		eCont.repaint();
	}


	@Override
	protected void AddObj() {
		SiegeEquipment se = new SiegeEquipment();
		se.name = nameField.getText();
		se.source = (Source) srcCombo.getSelectedItem();
		se.attackName = attackNameField.getText();
		se.ac = Integer.parseInt(acField.getText());
		se.hp = Integer.parseInt(hpField.getText());
		
		se.use = (SiegeUse) useCombo.getSelectedItem();
		se.size = (SiegeSize) sizeCombo.getSelectedItem();
		
		se.attack = DocumentHelper.deepCopyDocument(attackEdit.getStyledDocument());
		se.desc = DocumentHelper.deepCopyDocument(edit.getStyledDocument());
		map.put(se.name, se);
		
		ResetFields();
		FillSidePane();
	}

	@Override
	protected void Load(SiegeEquipment s) {
		nameField.setText(s.name);
		nameField.setEditable(false);
		nameField.setFocusable(false);
		
		srcCombo.setSelectedItem(s.source);
		attackNameField.setText(s.attackName);
		acField.setText(s.ac + "");
		hpField.setText(s.hp + "");
		
		useCombo.setSelectedItem(s.use);
		sizeCombo.setSelectedItem(s.size);
		
		attackEdit.LoadDocument(DocumentHelper.deepCopyDocument(s.attack));
		edit.LoadDocument(DocumentHelper.deepCopyDocument(s.desc));
	}

	@Override
	protected void Save() {
		data.SetSiegeEquipMap(map);
		data.SafeSaveData(MapType.SIEGEEQUIP);
	}
}