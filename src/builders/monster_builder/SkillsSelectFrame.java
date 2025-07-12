package builders.monster_builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import data.DataContainer;
import data.DataContainer.Proficiency;
import data.DataContainer.Skills;
import gui.gui_helpers.structures.StyleContainer;

public class SkillsSelectFrame extends JDialog
{
//	public static void main(String[]args) {
//		SwingUtilities.invokeLater(()->{
//			SkillsSelectFrame frm = new SkillsSelectFrame();
//			frm.setVisible(true);
//		});
//	}
//	
	private HashMap<Skills, Proficiency> skills;
	private HashMap<Skills, Proficiency> skillsIn;
	public boolean finished;
	
	private JPanel sPane;
	
	public SkillsSelectFrame(HashMap<Skills, Proficiency> skillsIn) {
		super();
		this.skillsIn = skillsIn;
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setSize(300, 600);
		this.getContentPane().setLayout(new BorderLayout());
		skills = new HashMap<DataContainer.Skills, DataContainer.Proficiency>();
		
		BuildSkillsPane(this.getContentPane());
		
		JLabel skillLabel = new JLabel("Skill Selection");
		skillLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.BLACK));
		skillLabel.setHorizontalAlignment(SwingConstants.CENTER);
		skillLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 26));
		this.getContentPane().add(skillLabel, BorderLayout.NORTH);
		
		JButton finishBtn = new JButton("Finished");
		finishBtn.addActionListener(e ->{
			finished = true;
		});
		StyleContainer.SetFontBtn(finishBtn);
		this.getContentPane().add(finishBtn, BorderLayout.SOUTH);
		
	}
	
	public void BuildSkillsPane(Container cPane)
	{
		sPane = new JPanel();
		sPane.setLayout(new GridBagLayout());
		sPane.setBorder(null);
		
		int iter = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		for(DataContainer.Skills s : DataContainer.Skills.values())
		{
			gbc.gridx = 0;
			gbc.gridy = iter;
			gbc.gridheight = 1;
			gbc.gridwidth = 1;
			gbc.weightx = 1;
			JLabel sLabel = new JLabel(s.name());
			StyleContainer.SetFontHeader(sLabel);
			sLabel.setHorizontalAlignment(SwingConstants.CENTER);
			sPane.add(sLabel, gbc);
			
			SkillsBox profBox = new SkillsBox(s, DataContainer.Proficiency.Profieient);
			SkillsBox exptBox = new SkillsBox(s, DataContainer.Proficiency.Expertise);
			
			profBox.addActionListener(e->{
				if(profBox.isSelected())
					exptBox.setSelected(false);
			});
			
			exptBox.addActionListener(e->{
				if(exptBox.isSelected())
					profBox.setSelected(false);
			});
			
			if(skillsIn != null) {
				if(skillsIn.containsKey(s)) {
					if(skillsIn.get(s) == Proficiency.Expertise)
						exptBox.setSelected(true);
					else if(skillsIn.get(s) == Proficiency.Profieient)
						profBox.setSelected(true);
				}
			}
			
			
			
			gbc.gridx ++;
			gbc.weightx = .3;
			sPane.add(profBox, gbc);
			gbc.gridx ++;
			sPane.add(exptBox, gbc);
			iter++;
		}
		
		JScrollPane skillScroll = new JScrollPane(sPane);
		skillScroll.setBorder(null);
		skillScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		skillScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cPane.add(skillScroll, BorderLayout.CENTER);
	}
	
	public HashMap<DataContainer.Skills, DataContainer.Proficiency> getSkills(){
		buildSkillsMap();
		if(finished) {
			return skills;
		}else {
			return null;
		}
	}

	private void buildSkillsMap() {
		skills = new HashMap<DataContainer.Skills, DataContainer.Proficiency>();
		for(Component c : sPane.getComponents()) {
			if(c instanceof SkillsBox) {
				SkillsBox sBx = (SkillsBox)c;
				if(sBx.isSelected()) {
					skills.put(sBx.skill, sBx.prof);
				}
			}
		}
	}
}

class SkillsBox extends JCheckBox
{
	public DataContainer.Skills skill;
	public DataContainer.Proficiency prof;
	public SkillsBox(DataContainer.Skills skill, DataContainer.Proficiency prof) {
		super();
		this.skill = skill;
		this.prof = prof;
		if(prof == DataContainer.Proficiency.Profieient) {
			this.setText("Prof");
		}else {
			this.setText("Expert");
		}
	}
}