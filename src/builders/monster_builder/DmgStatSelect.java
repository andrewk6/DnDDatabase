package builders.monster_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;

import data.DataContainer.ConditionTypes;
import data.DataContainer.DamageTypes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.StyleContainer;

public class DmgStatSelect extends JDialog{
	private JTextField insertTarget;
	public boolean finished;
	
	private ArrayList<JCheckBox> dmgChecks, condChecks;
	
	
	public static void main(String[]args) {
		SwingUtilities.invokeLater(()->{
			JTextField r = new ReminderField();
			DmgStatSelect aForm = new DmgStatSelect(r);
			
			aForm.setVisible(true);
			
			Thread test = new Thread(()->{
				while(!aForm.finished) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				SwingUtilities.invokeLater(()->{
					JFrame testF = new JFrame();
					testF.setSize(400, 300);
					testF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					StyleContainer.SetFontMain(r);
					testF.setContentPane(r);
					aForm.dispose();
					testF.pack();
					testF.setSize(500, 500);
					testF.setVisible(true);
				});
			});
			test.start();	
		});
	}
	
	public DmgStatSelect(JTextField r) {
		insertTarget = r;
		dmgChecks = new ArrayList<JCheckBox>();
		condChecks = new ArrayList<JCheckBox>();
		
		this.setSize(400, 700);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		BuildWindow(this.getContentPane());
	}
	
	private String buildText() {
		String dmg = "";
		String cond = "";
		for(JCheckBox d : dmgChecks)
			if(d.isSelected())
				dmg += d.getText() + ", ";
		for(JCheckBox c : condChecks)
			if(c.isSelected())
				cond += c.getText() + ", ";
		
		if(dmg.length() > 0) {
			dmg = dmg.substring(0, dmg.length() - 2);
		}
		
		if(cond.length() > 0) {
			cond = cond.substring(0, cond.length() - 2);
		}
		
		String out = "";
		if(dmg.length() > 0 && cond.length() > 0)
			out += dmg + "; " + cond;
		else if(dmg.length() > 0 && cond.length() == 0)
			out += dmg;
		else if(dmg.length() == 0 && cond.length() > 0)
			out += cond;
		
		return out;
	}

	private void BuildWindow(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		JLabel header = new JLabel("Pick the Damage / Stat Types");
		header.setHorizontalAlignment(SwingConstants.CENTER);
		StyleContainer.SetFontHeader(header);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		cPane.add(header, BorderLayout.NORTH);
		
		JPanel selectPane = new JPanel();
		selectPane.setLayout(new GridLayout(1,2));
		BuildMainPane(selectPane);
		cPane.add(selectPane, BorderLayout.CENTER);
		
		JButton finBtn = new JButton("Insert");
		StyleContainer.SetFontBtn(finBtn);
		finBtn.addActionListener(e ->{
			finished = true;
			String build = buildText();
			if(build.length() > 0)
				insertTarget.setText(build);
			dispose();
		});
		cPane.add(finBtn, BorderLayout.SOUTH);
	}

	private void BuildMainPane(JPanel sPane) {
		JPanel dmgPane = new JPanel();
		dmgPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
		dmgPane.setLayout(new BorderLayout());
		sPane.add(dmgPane);
		
		JLabel dmgLbl = new JLabel("Damage Types:");
		StyleContainer.SetFontHeader(dmgLbl);
		dmgPane.add(dmgLbl, BorderLayout.NORTH);
		
		JPanel dmgListPane = new JPanel();
		dmgListPane.setLayout(new GridLayout(0, 1));
		dmgListPane.setBorder(null);
		BuildDamageList(dmgListPane);
		
		JScrollPane dListScroll = new JScrollPane(dmgListPane);
		dListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		dListScroll.setBorder(null);
		dmgPane.add(dListScroll, BorderLayout.CENTER);
		
		
		JPanel condPane = new JPanel();
		condPane.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.black));
		condPane.setLayout(new BorderLayout());
		sPane.add(condPane);
		
		JLabel condLbl = new JLabel("Condition Types:");
		StyleContainer.SetFontHeader(condLbl);
		condPane.add(condLbl, BorderLayout.NORTH);
		
		JPanel condListPane = new JPanel();
		condListPane.setLayout(new GridLayout(0, 1));
		BuildCondList(condListPane);
		
		JScrollPane cLScroll = new JScrollPane(condListPane);
		cLScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cLScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		cLScroll.setBorder(null);
		condPane.add(cLScroll);
	}

	private void BuildDamageList(JPanel dmgListPane) {
		for(DamageTypes t : DamageTypes.values()) {
			JCheckBox box = new JCheckBox(t.name());
			StyleContainer.SetFontHeader(box);
			dmgListPane.add(box);
			dmgChecks.add(box);
		}
		
	}
	
	private void BuildCondList(JPanel condListPane) {
		for(ConditionTypes t : ConditionTypes.values()) {
			JCheckBox box = new JCheckBox(t.name());
			StyleContainer.SetFontHeader(box);
			condListPane.add(box);
			condChecks.add(box);
		}
	}
}