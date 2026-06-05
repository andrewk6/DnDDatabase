package builders.bastion_builder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import builders.utlities.AbstractBuilderPane;
import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;

public class BastionRuleBuilderPane extends AbstractBuilderPane<StyledDocument>
{
	private ReminderField title;
	private RichEditor edit;
	
	public BastionRuleBuilderPane(DataContainer data) {
		super(data);
		map = new HashMap<String, StyledDocument>(data.getBastionRules());
		FillSidePane();
	}

	@Override
	protected void SortKeys(ArrayList<String> keys) {}

	@Override
	protected void BuildPane() {
		this.setLayout(new BorderLayout());
		mainPane = new JPanel(new BorderLayout());
		sidePane = new JPanel(new GridLayout(0,1));
		this.add(mainPane, BorderLayout.CENTER);
		this.add(sidePane, BorderLayout.WEST);
		
		title = CompFactory.createReminderField("Rule name");
		mainPane.add(title, BorderLayout.NORTH);
		
		edit = new RichEditor(data);
		mainPane.add(edit);
		
		mainPane.add(CompFactory.createButtonFlowPane(FlowLayout.RIGHT, new JButton[] {
			CompFactory.createNewButton("Add Rule", _-> AddObj()),
			CompFactory.createNewButton("Reset Editor", _-> Reset(null)),
			CompFactory.createNewButton("Save", _-> Save())
		}), BorderLayout.SOUTH);
	}

	@Override
	protected void ResetFields() {
		title.setText("");
		
		mainPane.remove(edit);
		edit = new RichEditor(data);
		mainPane.add(edit);
		
		mainPane.revalidate();
		mainPane.repaint();
	}

	@Override
	protected void AddObj() {
		map.put(title.getText(), edit.getStyledDocument());
		ResetFields();
		FillSidePane();
	}

	@Override
	protected void Load(String s) {
		title.setText(s);
		
		mainPane.remove(edit);
		edit = new RichEditor(data);
		edit.LoadDocument(map.get(s));
		mainPane.add(edit);
	}

	@Override
	protected void Save() {
		data.setBastionRules(map);
	}
	
}