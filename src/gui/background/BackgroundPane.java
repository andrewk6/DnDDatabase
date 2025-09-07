package gui.background;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.Feat;
import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.items.Item;
import data.players.Background;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CompFactory.ComponentType;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.GuiDirector;

public class BackgroundPane extends JPanel
{
	public static void main(String[] args) {
		DataContainer data = new DataContainer();
		data.init();
		SwingUtilities.invokeLater(()->{
			JFrame frm = new JFrame();
			frm.addWindowListener(CompFactory.createSafeExitWindowListener(frm, data));
			frm.setContentPane(new BackgroundPane(data.getBackgrounds().get("Wayfarer"), 
					data, new GuiDirector(new JDesktopPane())));
			frm.pack();
			frm.setSize(new Dimension(800, 800));
			frm.setVisible(true);
		});
	}
	private DataContainer data;
	private GuiDirector gd;
	private Background b;
	
	public BackgroundPane(Background b, DataContainer data, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		this.b = b;

		BuildContent();
	}
	
	/*	
	public ArrayList<Item> startEquip;
	public int startGoldWithEquip;
	
	public final int noEquipGold = 50;
	 */
	private void BuildContent() {
		this.setLayout(new BorderLayout());
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());
		this.add(hPane, BorderLayout.NORTH);
		
		JLabel nameLbl = CompFactory.createNewLabel(b.name, ComponentType.HEADER);
		hPane.add(nameLbl, BorderLayout.CENTER);
		
		JLabel srcLbl = CompFactory.createNewLabel("Source: " + b.src.toString(), ComponentType.BODY);
		hPane.add(srcLbl, BorderLayout.EAST);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(0,1));
		this.add(mainPane, BorderLayout.CENTER);

		HoverTextPane descPane = CompFactory.createHoverTextPane(data, gd, b.desc, ComponentType.BODY);
		JScrollPane descScroll = new JScrollPane(descPane);
		mainPane.add(descScroll);
		
		JPanel detailPane = new JPanel();
		detailPane.setLayout(new GridLayout(0,1));
		mainPane.add(detailPane);
		
		detailPane.add(getAbilityPane());
		detailPane.add(getFeatPane());
		detailPane.add(getSkillPane());
		detailPane.add(getToolPane());
		detailPane.add(getEquipPane());
	}
	
	private JPanel getAbilityPane() {
		JPanel abilityPane = new JPanel();
		abilityPane.setLayout(new BorderLayout());
		
		JLabel abilityLbl = CompFactory.createNewLabel("Ability Scores: ", ComponentType.HEADER);
		abilityPane.add(abilityLbl, BorderLayout.WEST);
		
		JLabel abilityVal = CompFactory.createNewLabel(b.statBonus[0].toString() + ", "
				+ b.statBonus[1] + ", "
				+ b.statBonus[2], 
				ComponentType.BODY);
		abilityPane.add(abilityVal, BorderLayout.CENTER);
		return abilityPane;
	}
	
	private JPanel getFeatPane() {
		JPanel featPane = new JPanel();
		featPane.setLayout(new BorderLayout());
		
		JLabel featLbl = CompFactory.createNewLabel("Feat: ", ComponentType.HEADER);
		featPane.add(featLbl, BorderLayout.WEST);
		
		JLabel featVal = CompFactory.createNewLabel(b.startFeat.name, ComponentType.BODY);
		featPane.add(featVal, BorderLayout.CENTER);
		
		return featPane;
	}
	
	private JPanel getSkillPane() {
		JPanel skillPane = new JPanel();
		skillPane.setLayout(new BorderLayout());
		
		JLabel skillLbl = CompFactory.createNewLabel("Skill Proficiencies: ", ComponentType.HEADER);
		skillPane.add(skillLbl, BorderLayout.WEST);
		
		String skillString = "";
		for(Skills s : b.skills) {
			skillString += s.toString() + ", ";
		}
		skillString = skillString.substring(0, skillString.length() - ", ".length());
		
		JLabel skillsVal = CompFactory.createNewLabel(skillString, ComponentType.BODY);
		skillPane.add(skillsVal, BorderLayout.CENTER);
		
		return skillPane;
	}
	
	private JPanel getToolPane() {
		JPanel toolPane = new JPanel();
		toolPane.setLayout(new BorderLayout());
		
		JLabel toolLbl = CompFactory.createNewLabel("Tool Proficencies: ", ComponentType.HEADER);
		toolPane.add(toolLbl, BorderLayout.WEST);
		
		JLabel toolVal = CompFactory.createNewLabel(b.toolProf, ComponentType.BODY);
		toolPane.add(toolVal, BorderLayout.CENTER);
		
		return toolPane;
	}
	
	private JPanel getEquipPane() {
		JPanel equipPane = new JPanel();
		equipPane.setLayout(new BorderLayout());
		
		JLabel equipLbl = CompFactory.createNewLabel("Equipment: ", ComponentType.HEADER);
		equipPane.add(equipLbl, BorderLayout.WEST);
		
		String equipString = "Choose (A) or (B): (A) ";
		
		for(int i = 0; i < b.startEquip.size(); i ++) {
			int num = Collections.frequency(b.startEquip, b.startEquip.get(i));
			System.out.println(b.startEquip.get(i) + ": " + num);
			if(num > 1)
				equipString += num + " " + b.startEquip.get(i).name + ", ";
			else
				equipString += b.startEquip.get(i).name + ", ";
			b.startEquip.removeAll(Collections.singleton(b.startEquip.get(i)));
		}
		
		equipString += b.startGoldWithEquip + " GP; or (B) " + b.noEquipGold + " GP";
		
		JLabel equipVal = CompFactory.createNewLabel(equipString, ComponentType.BODY);
		equipPane.add(equipVal, BorderLayout.CENTER);
		
		return equipPane;
	}
}