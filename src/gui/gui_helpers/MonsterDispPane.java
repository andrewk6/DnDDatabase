package gui.gui_helpers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.ListFormat.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;

import data.*;
import gui.MonsterIFrame;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class MonsterDispPane extends JTabbedPane {
	private DataContainer data;
	private GuiDirector gd;
	private Monster m;

	private JTextField monsterNameField, monsterTypeField, acField, initBnsField, hpField, speedField, strField,
			dexField, conField, intField, wisField, chaField, crField, numLActs, numLActsBns;
	
	private JTextArea immuneField, dmgResistField, dmgVulnField, sensesField, langField, skillsField;
			
	private JLabel strSaveP, dexSaveP, conSaveP, intSaveP, wisSaveP, chaSaveP;

	private HoverTextPane traits, actions, bonusActions, reactions, legendActions;

//	private HashMap<DataContainer.Skills, DataContainer.Proficiency> skills;

	private ArrayList<ReminderField> tagFields;

	private Map<String, Monster> monsters;

	private JPanel sidePane;
	
	private JDesktopPane deskPane;
	private MonsterIFrame monstFrm;

	public static void main(String[] args) {
		DataContainer data = new DataContainer();
		SwingUtilities.invokeLater(() -> {
			JFrame monstDisp = new JFrame();
			monstDisp.setContentPane(new MonsterDispPane(data.getMonsters().get("Wolf"), data, new GuiDirector(new JDesktopPane())));
			monstDisp.setSize(800, 800);
			monstDisp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			monstDisp.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {data.Exit();}
				public void windowClosed(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
			});
			monstDisp.setVisible(true);		
		});
	}

	public MonsterDispPane(Monster m, DataContainer d, GuiDirector gd) {
		this.data = d;
		this.gd = gd;
		this.m = m;
		this.deskPane = new JDesktopPane();
		
		tagFields = new ArrayList<ReminderField>();
		ConfigureFrame();
	}

	public MonsterDispPane(DataContainer data, String key, GuiDirector gd) {
		this.data = data;
		this.gd = gd;
		this.m = data.getMonsters().get(key);
		this.deskPane = new JDesktopPane();
		
		tagFields = new ArrayList<ReminderField>();
		ConfigureFrame();
	}
	
	public MonsterDispPane(Monster m, DataContainer d, GuiDirector gd, JDesktopPane deskPane) {
		this.data = d;
		this.gd = gd;
		this.m = m;
		this.deskPane = deskPane;
		
		tagFields = new ArrayList<ReminderField>();
		ConfigureFrame();
	}
	
	public MonsterDispPane(DataContainer data, GuiDirector gd, String key, JDesktopPane deskPane) {
		this.data = data;
		this.gd = gd;
		this.m = data.getMonsters().get(key);
		this.deskPane = deskPane;
		
		tagFields = new ArrayList<ReminderField>();
		ConfigureFrame();
	}
	
	public void SetMonstIFrame(MonsterIFrame iFrame) {
		monstFrm = iFrame;
		SetHoverIFrames(this);
	}
	
	private void SetHoverIFrames(Container cont) {
		for(Component c : cont.getComponents()) {
			if(c instanceof HoverTextPane) {
//				((HoverTextPane)c).SetMonsterTabbedPane(monstFrm);
			}else if(c instanceof Container)
				SetHoverIFrames((Container)c);
		}
	}

	private void ConfigureFrame() {

		JPanel statsPane = new JPanel();
		BuildStatsPane(statsPane);
		addTab("Details/Stats", statsPane);

		if(m.traits.getLength() > 0) {
			JPanel traitsPane = new JPanel();
			BuildTraitsPane(traitsPane);
			addTab("Traits", traitsPane);
		}

		if(m.actions.getLength() > 0) {
			JPanel actPane = new JPanel();
			BuildActionPane(actPane);
			addTab("Actions", actPane);
		}
		
		if(m.bonusActions.getLength() > 0) {
			JPanel baPane = new JPanel();
			BuildBonusActionPane(baPane);
			addTab("Bonus Actions", baPane);
		}
		
		if(m.reactions.getLength() > 0) {
			JPanel reactPane = new JPanel();
			BuildReactionPane(reactPane);
			addTab("Reactions", reactPane);
		}
		
		if(m.legendActions.getLength() > 0) {
			JPanel legendActPane = new JPanel();
			BuildLegendActionPane(legendActPane);
			addTab("Legendary Actions", legendActPane);
		}
		

		JPanel tagPane = new JPanel();
		BuildTagsPane(tagPane);
		addTab("Tags", tagPane);
		
		StyleContainer.setAllNonFocusable(this);
	}

	private void BuildTraitsPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField traitsHead = new JTextField("Traits");
		traitsHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		traitsHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		traitsHead.setEditable(false);
		tPane.add(traitsHead, BorderLayout.NORTH);

		traits = new HoverTextPane(data, gd, deskPane);
		traits.setDocument(m.traits);
		
		JScrollPane tScroll = new JScrollPane(traits);
		tPane.add(tScroll, BorderLayout.CENTER);
	}

	private void BuildActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField actsHead = new JTextField("Actions");
		actsHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		actsHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		actsHead.setEditable(false);
		tPane.add(actsHead, BorderLayout.NORTH);

		actions = new HoverTextPane(data, gd, deskPane);
		actions.setDocument(m.actions);
		
		JScrollPane aScroll = new JScrollPane(actions);
		tPane.add(aScroll, BorderLayout.CENTER);
	}

	private void BuildBonusActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField bnsActHead = new JTextField("Bonus Actions");
		bnsActHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		bnsActHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		bnsActHead.setEditable(false);
		tPane.add(bnsActHead, BorderLayout.NORTH);

		bonusActions = new HoverTextPane(data, gd, deskPane);
		bonusActions.setDocument(m.bonusActions);
		
		JScrollPane baScroll = new JScrollPane(bonusActions);
		tPane.add(baScroll, BorderLayout.CENTER);
	}

	private void BuildReactionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField reactHead = new JTextField("Reactions");
		reactHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		reactHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		reactHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(reactHead, BorderLayout.NORTH);

		reactions = new HoverTextPane(data, gd, deskPane);
		reactions.setDocument(m.reactions);
		
		JScrollPane rScroll = new JScrollPane(reactions);
		tPane.add(rScroll, BorderLayout.CENTER);
	}

	private void BuildLegendActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField legendActHead = new JTextField("Legendary Actions");
		legendActHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		legendActHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		legendActHead.setEditable(false);
		tPane.add(legendActHead, BorderLayout.NORTH);

		legendActions = new HoverTextPane(data, gd, deskPane);
		legendActions.setDocument(m.legendActions);
		
		JScrollPane laScroll = new JScrollPane(legendActions);
		tPane.add(laScroll, BorderLayout.CENTER);

		JPanel numActionsPane = new JPanel();
		numActionsPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JLabel numLabel = new JLabel("Number of Legendary Actions:");
		StyleContainer.SetFontMain(numLabel);
		numActionsPane.add(numLabel);

		numLActs = new JTextField("" + m.lActNum);
		numLActs.setColumns(7);
		numLActs.setEditable(false);
		numLActs.setFocusable(false);
		StyleContainer.SetFontHeader(numLActs);
		numActionsPane.add(numLActs);

		JLabel lairBonus = new JLabel("Lair Bonus: ");
		StyleContainer.SetFontMain(lairBonus);
		numActionsPane.add(lairBonus);

		numLActsBns = new JTextField("" + m.lActBns);
		numLActsBns.setColumns(7);
		numLActsBns.setEditable(false);
		numLActsBns.setFocusable(false);
		StyleContainer.SetFontHeader(numLActsBns);
		numActionsPane.add(numLActsBns);

		tPane.add(numActionsPane, BorderLayout.SOUTH);
	}

	private void BuildTagsPane(JPanel pane) {
		pane.setLayout(new BorderLayout());
		JPanel tagGrid = new JPanel();
		tagGrid.setLayout(new GridLayout(0, 1));
		
		for(String t : m.tags) {
			JTextField tField = new JTextField(t);
			tField.setEditable(false);
			tField.setFocusable(false);
			StyleContainer.SetFontHeader(tField);
			tagGrid.add(tField);
		}
		JScrollPane tagScroll = new JScrollPane(tagGrid);
		pane.add(tagScroll, BorderLayout.CENTER);
	}

	private void BuildStatsPane(JPanel hPane) {
		hPane.setLayout(new BorderLayout());

		JPanel tPane = new JPanel();
		tPane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
		tPane.setLayout(new BorderLayout());
		monsterNameField = new JTextField(m.name);
		monsterNameField.setEditable(false);
		monsterNameField.setFocusable(false);
		StyleContainer.SetFontHeader(monsterNameField);
		monsterNameField.setFont(monsterNameField.getFont().deriveFont((float) 22));
		tPane.add(monsterNameField, BorderLayout.CENTER);

		monsterTypeField = new JTextField(m.typeSizeAlignment);
		monsterTypeField.setEditable(false);
		monsterTypeField.setFocusable(false);
		monsterTypeField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont(Font.ITALIC));
		tPane.add(monsterTypeField, BorderLayout.SOUTH);

		hPane.add(tPane, BorderLayout.NORTH);

		JPanel statPane = new JPanel();
		statPane.setLayout(new BorderLayout());
		
		JPanel topSPane = new JPanel();
		topSPane.setLayout(new GridLayout(0, 1));
		topSPane.add(BuildACPane());
		topSPane.add(BuildInitPane());
		topSPane.add(BuildHPPane());
		topSPane.add(BuildSpeedPane());

		statPane.add(topSPane, BorderLayout.NORTH);

		JPanel abilityScorePane = new JPanel();
		BuildAbilityScorePane(abilityScorePane);
		statPane.add(abilityScorePane, BorderLayout.CENTER);

		hPane.add(statPane, BorderLayout.CENTER);

		JPanel bottomSPane = new JPanel();
		BuildBottomSPane(bottomSPane);
		hPane.add(bottomSPane, BorderLayout.SOUTH);

	}

	/*
//	private void BuildBottomSPane(JPanel sPane) {
//		sPane.setLayout(new GridBagLayout());
//		
//		int row = 0;
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridheight = 1;
//		gbc.gridwidth = 1;
//		gbc.gridx = row;
//		gbc.gridy = 0;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		
//		
//		
//		if(m.getSkillString().length() > 0) {
//			JLabel skillLbl = new JLabel("Skills");
//			gbc.weightx = .2;
//			StyleContainer.SetFontHeader(skillLbl);
//			sPane.add(skillLbl, gbc);
//			
//			JTextField skills = new JTextField(m.getSkillString());
//			gbc.gridx ++;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			skills.setEditable(false);
//			skills.setFocusable(false);
//			skills.setBorder(null);
//			StyleContainer.SetFontMain(skills);
//			sPane.add(skills, gbc);
//			row++;
//		}
//		
//		if(m.dmgVul.length() > 0) {
//			JLabel vulLabel = new JLabel("Vulnerabilities");
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = .2;
//			gbc.anchor = GridBagConstraints.CENTER;
//			StyleContainer.SetFontHeader(vulLabel);
//			sPane.add(vulLabel, gbc);
//
//			dmgVulnField = new JTextField(m.dmgVul);
//			gbc.gridx = 1;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			StyleContainer.SetFontMain(dmgVulnField);
//			dmgVulnField.setBorder(null);
//			dmgVulnField.setEditable(false);
//			dmgVulnField.setFocusable(false);
//			sPane.add(dmgVulnField, gbc);
//			row++;
//		}
//		
//		if(m.dmgRes.length() > 0) {
//			JLabel resistLbl = new JLabel("Resistances");
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = .2;
//			gbc.anchor = GridBagConstraints.CENTER;
//			StyleContainer.SetFontHeader(resistLbl);
//			sPane.add(resistLbl, gbc);
//			
//			dmgResistField = new JTextField(m.dmgRes);
//			gbc.gridx = 1;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			StyleContainer.SetFontMain(dmgResistField);
//			dmgResistField.setEditable(false);
//			dmgResistField.setFocusable(false);
//			dmgResistField.setBorder(null);
//			sPane.add(dmgResistField, gbc);
//			row++;
//		}
//		
//		if(m.immune.length() > 0) {
//			JLabel immuneLbl = new JLabel("Immunities");
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = .2;
//			gbc.anchor = GridBagConstraints.CENTER;
//			StyleContainer.SetFontHeader(immuneLbl);
//			sPane.add(immuneLbl, gbc);
//			
//			immuneField = new JTextArea(m.immune);
//			gbc.gridx = 1;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			gbc.fill = GridBagConstraints.HORIZONTAL;
//			StyleContainer.SetFontMain(immuneField);
//			immuneField.setEditable(false);
//			immuneField.setFocusable(false);
//			immuneField.setBorder(null);
//			immuneField.setLineWrap(false);
//			
//			FontMetrics fm = immuneField.getFontMetrics(immuneField.getFont());
//			int lineHeight = fm.getHeight();
//			int scrollbarHeight = UIManager.getInt("ScrollBar.width");
//			int totalHeight = lineHeight + scrollbarHeight + 4;
//			
//			immuneField.setMargin(new Insets(2, 2, 2, 2));
//			immuneField.setPreferredSize(new Dimension(200, totalHeight));
//			
//			JScrollPane immuneScroll = new JScrollPane(immuneField);
//			immuneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//			immuneScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			immuneScroll.setPreferredSize(new Dimension(300, totalHeight));
//			immuneScroll.getViewport().setOpaque(false);
//			immuneScroll.getViewport().setBorder(null);
//			sPane.add(immuneScroll, gbc);
//			row ++;
//		}
//		
//		if(m.senses.length() > 0) {
//			JLabel sensesLbl = new JLabel("Senses");
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = .2;
//			gbc.anchor = GridBagConstraints.CENTER;
//			StyleContainer.SetFontHeader(sensesLbl);
//			sPane.add(sensesLbl, gbc);
//
//			sensesField = new JTextField(m.senses);
//			gbc.gridx = 1;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			StyleContainer.SetFontMain(sensesField);
//			sensesField.setEditable(false);
//			sensesField.setFocusable(false);
//			sensesField.setBorder(null);
//			sPane.add(sensesField, gbc);
//			row++;
//		}
//		
//		if(m.languages.length() > 0) {
//			JLabel langLbl = new JLabel("Languages");
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = .2;
//			gbc.anchor = GridBagConstraints.CENTER;
//			StyleContainer.SetFontHeader(langLbl);
//			sPane.add(langLbl, gbc);
//			
//			langField = new JTextField(m.languages);
//			gbc.gridx = 1;
//			gbc.weightx = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			StyleContainer.SetFontMain(langField);
//			langField.setEditable(false);
//			langField.setFocusable(false);
//			langField.setBorder(null);
//			sPane.add(langField, gbc);
//			row++;
//		}
//		
//		if(m.getCRString().length() > 0) {
//			JPanel crPane = new JPanel();
//			gbc.gridy = row;
//			gbc.gridx = 0;
//			gbc.weightx = 2;
//			gbc.gridwidth = 2;
//			gbc.anchor = GridBagConstraints.LINE_START;
//			crPane.setLayout(new FlowLayout(FlowLayout.LEFT));
//			
//			JLabel crLabel = new JLabel("CR");
//			StyleContainer.SetFontHeader(crLabel);
//			crPane.add(crLabel);
//			
//			crField = new JTextField(m.getCRString());
//			crField.setColumns(20);
//			crField.setEditable(false);
//			crField.setFocusable(false);
//			crField.setBorder(null);
//			StyleContainer.SetFontMain(crField);
//			Dimension size = crField.getPreferredSize();
//			size.height = 30; // your desired height
//			crField.setPreferredSize(size);
//			crPane.add(crField);
//			sPane.add(crPane, gbc);
//		}
//	}
	
	private void BuildBottomSPane(JPanel sPane) {
	    sPane.setLayout(new GridBagLayout());
	    int row = 0;
	    GridBagConstraints gbc = new GridBagConstraints();

	    // Common Insets for padding (optional)
	    Insets labelInsets = new Insets(2, 2, 2, 5);
	    Insets fieldInsets = new Insets(2, 0, 2, 2);

	    if (m.getSkillString().length() > 0) {
	        JLabel skillLbl = new JLabel("Skills");
	        StyleContainer.SetFontHeader(skillLbl);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.gridwidth = 1;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(skillLbl, gbc);

	        JTextField skills = new JTextField(m.getSkillString());
	        skills.setEditable(false);
	        skills.setFocusable(false);
	        skills.setBorder(null);
	        StyleContainer.SetFontMain(skills);

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(skills, gbc);

	        row++;
	    }

	    if (m.dmgVul.length() > 0) {
	        JLabel vulLabel = new JLabel("Vulnerabilities");
	        StyleContainer.SetFontHeader(vulLabel);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(vulLabel, gbc);

	        dmgVulnField = new JTextField(m.dmgVul);
	        dmgVulnField.setEditable(false);
	        dmgVulnField.setFocusable(false);
	        dmgVulnField.setBorder(null);
	        StyleContainer.SetFontMain(dmgVulnField);

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(dmgVulnField, gbc);

	        row++;
	    }

	    if (m.dmgRes.length() > 0) {
	        JLabel resistLbl = new JLabel("Resistances");
	        StyleContainer.SetFontHeader(resistLbl);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(resistLbl, gbc);

	        dmgResistField = new JTextField(m.dmgRes);
	        dmgResistField.setEditable(false);
	        dmgResistField.setFocusable(false);
	        dmgResistField.setBorder(null);
	        StyleContainer.SetFontMain(dmgResistField);

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(dmgResistField, gbc);

	        row++;
	    }

	    if (m.immune.length() > 0) {
	        JLabel immuneLbl = new JLabel("Immunities");
	        StyleContainer.SetFontHeader(immuneLbl);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(immuneLbl, gbc);

	        immuneField = new JTextArea(m.immune);
	        immuneField.setEditable(false);
	        immuneField.setFocusable(false);
	        immuneField.setBorder(null);
	        immuneField.setLineWrap(false);
	        immuneField.setWrapStyleWord(false);
	        immuneField.setMargin(new Insets(2, 2, 2, 2));
	        
	        StyleContainer.SetFontMain(immuneField);

	        FontMetrics fm = immuneField.getFontMetrics(immuneField.getFont());
	        int lineHeight = fm.getHeight();
	        int scrollbarHeight = UIManager.getInt("ScrollBar.height");
	        if (scrollbarHeight == 0) scrollbarHeight = 16; // fallback
	        int totalHeight = lineHeight + scrollbarHeight + 4;
	        int textWidth = fm.stringWidth(m.immune) + immuneField.getMargin().left + immuneField.getMargin().right + 10; // add some padding

	        JScrollPane immuneScroll = new JScrollPane(immuneField,
	                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        immuneScroll.setBorder(null);
	        immuneScroll.setMinimumSize(new Dimension(textWidth, totalHeight));
	        immuneScroll.setPreferredSize(new Dimension(300, totalHeight));

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(immuneScroll, gbc);

	        row++;
	    }

	    if (m.senses.length() > 0) {
	        JLabel sensesLbl = new JLabel("Senses");
	        StyleContainer.SetFontHeader(sensesLbl);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(sensesLbl, gbc);

	        sensesField = new JTextField(m.senses);
	        sensesField.setEditable(false);
	        sensesField.setFocusable(false);
	        sensesField.setBorder(null);
	        StyleContainer.SetFontMain(sensesField);

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(sensesField, gbc);

	        row++;
	    }

	    if (m.languages.length() > 0) {
	        JLabel langLbl = new JLabel("Languages");
	        StyleContainer.SetFontHeader(langLbl);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.weightx = 0.2;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = labelInsets;
	        sPane.add(langLbl, gbc);

	        langField = new JTextField(m.languages);
	        langField.setEditable(false);
	        langField.setFocusable(false);
	        langField.setBorder(null);
	        StyleContainer.SetFontMain(langField);

	        gbc.gridx = 1;
	        gbc.weightx = 2.0;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = fieldInsets;
	        sPane.add(langField, gbc);

	        row++;
	    }

	    if (m.getCRString().length() > 0) {
	        JPanel crPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        JLabel crLabel = new JLabel("CR");
	        StyleContainer.SetFontHeader(crLabel);
	        crPane.add(crLabel);

	        crField = new JTextField(m.getCRString());
	        crField.setColumns(20);
	        crField.setEditable(false);
	        crField.setFocusable(false);
	        crField.setBorder(null);
	        StyleContainer.SetFontMain(crField);
	        Dimension size = crField.getPreferredSize();
	        size.height = 30; // desired height
	        crField.setPreferredSize(size);
	        crPane.add(crField);

	        gbc.gridx = 0;
	        gbc.gridy = row;
	        gbc.gridwidth = 2;
	        gbc.weightx = 2.0;
	        gbc.weighty = 0;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        gbc.insets = labelInsets;
	        sPane.add(crPane, gbc);
	    }
	}
	*/
	
	private void BuildBottomSPane(JPanel sPane) {
	    sPane.setLayout(new GridBagLayout());
	    
	    int row = 0;
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridheight = 1;
	    gbc.gridwidth = 1;
	    gbc.gridy = 0;
	    gbc.weighty = 1;
	    gbc.fill = GridBagConstraints.BOTH;

	    BiFunction<String, JTextArea, JPanel> createScrollableTextArea = (text, textArea) -> {
	        textArea.setText(text);
	        textArea.setEditable(false);
	        textArea.setFocusable(false);
	        textArea.setBorder(null);
	        textArea.setLineWrap(false);
	        textArea.setWrapStyleWord(false);
	        textArea.setBackground(null);
	        textArea.setBorder(null);
	        textArea.setMargin(new Insets(2, 2, 2, 2));

	        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
	        int lineHeight = fm.getHeight();
	        int scrollbarHeight = UIManager.getInt("ScrollBar.height");
	        if (scrollbarHeight == 0) scrollbarHeight = 16;

	        int totalHeight = lineHeight + scrollbarHeight + 4;

	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	        scrollPane.setPreferredSize(new Dimension(0, totalHeight));
	        scrollPane.setMinimumSize(new Dimension(0, totalHeight));
	        scrollPane.getViewport().setOpaque(false);
	        scrollPane.getViewport().setBorder(null);
	        scrollPane.setBorder(null);

	        JPanel wrapper = new JPanel(new BorderLayout());
	        wrapper.setBorder(null);
	        wrapper.add(scrollPane, BorderLayout.CENTER);

	        return wrapper;
	    };

	    if (m.getSkillString().length() > 0) {
	        JLabel skillLbl = new JLabel("Skills");
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;                   // smaller weight for label
	        gbc.fill = GridBagConstraints.NONE; // no fill for label
	        gbc.anchor = GridBagConstraints.LINE_START;  // left align label
	        StyleContainer.SetFontHeader(skillLbl);
	        sPane.add(skillLbl, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;                   // larger weight for text area
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        skillsField = new JTextArea();
	        StyleContainer.SetFontMain(skillsField);
	        JPanel skillsPanel = createScrollableTextArea.apply(m.getSkillString(), skillsField);
	        sPane.add(skillsPanel, gbc);

	        row++;
	    }

	    if (m.dmgVul.length() > 0) {
	        JLabel vulLabel = new JLabel("Vulnerabilities");
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        StyleContainer.SetFontHeader(vulLabel);
	        sPane.add(vulLabel, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        dmgVulnField = new JTextArea();
	        StyleContainer.SetFontMain(dmgVulnField);
	        JPanel dmgVulnPanel = createScrollableTextArea.apply(m.dmgVul, dmgVulnField);
	        sPane.add(dmgVulnPanel, gbc);

	        row++;
	    }

	    if (m.dmgRes.length() > 0) {
	        JLabel resistLbl = new JLabel("Resistances");
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        StyleContainer.SetFontHeader(resistLbl);
	        sPane.add(resistLbl, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        dmgResistField = new JTextArea();
	        StyleContainer.SetFontMain(dmgResistField);
	        JPanel dmgResistPanel = createScrollableTextArea.apply(m.dmgRes, dmgResistField);
	        sPane.add(dmgResistPanel, gbc);

	        row++;
	    }

	    if (m.immune.length() > 0) {
	        JLabel immuneLbl = new JLabel("Immunities");
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        StyleContainer.SetFontHeader(immuneLbl);
	        sPane.add(immuneLbl, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        immuneField = new JTextArea();
	        StyleContainer.SetFontMain(immuneField);
	        JPanel immunePanel = createScrollableTextArea.apply(m.immune, immuneField);
	        sPane.add(immunePanel, gbc);

	        row++;
	    }

	    if (m.senses.length() > 0) {
	        JLabel sensesLbl = new JLabel("Senses");
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        StyleContainer.SetFontHeader(sensesLbl);
	        sPane.add(sensesLbl, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        sensesField = new JTextArea();
	        StyleContainer.SetFontMain(sensesField);
	        JPanel sensesPanel = createScrollableTextArea.apply(m.senses, sensesField);
	        sPane.add(sensesPanel, gbc);

	        row++;
	    }

	    if (m.languages.length() > 0) {
	        JLabel langLbl = new JLabel("Languages");
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 0.1;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;
	        StyleContainer.SetFontHeader(langLbl);
	        sPane.add(langLbl, gbc);

	        gbc.gridx = 1;
	        gbc.weightx = 0.9;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        langField = new JTextArea();
	        StyleContainer.SetFontMain(langField);
	        JPanel langPanel = createScrollableTextArea.apply(m.languages, langField);
	        sPane.add(langPanel, gbc);

	        row++;
	    }

	    if (m.getCRString().length() > 0) {
	        JPanel crPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        gbc.gridy = row;
	        gbc.gridx = 0;
	        gbc.weightx = 1;
	        gbc.gridwidth = 2;
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.anchor = GridBagConstraints.LINE_START;

	        JLabel crLabel = new JLabel("CR");
	        StyleContainer.SetFontHeader(crLabel);
	        crPane.add(crLabel);

	        crField = new JTextField(m.getCRString());
	        crField.setColumns(20);
	        crField.setEditable(false);
	        crField.setFocusable(false);
	        crField.setBorder(null);
	        StyleContainer.SetFontMain(crField);
	        Dimension size = crField.getPreferredSize();
	        size.height = 30;
	        crField.setPreferredSize(size);
	        crPane.add(crField);

	        sPane.add(crPane, gbc);
	    }
	}




	private JPanel BuildACPane() {
		// Add AC Section
		JPanel acPane = new JPanel();
		acPane.setLayout(new BorderLayout());

		JLabel acLabel = new JLabel("AC:");
		acLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
		StyleContainer.SetFontHeader(acLabel);
		acPane.add(acLabel, BorderLayout.WEST);

		acField = new JTextField(m.ac);
		StyleContainer.SetFontMain(acField);
		acField.setBorder(null);
		acField.setEditable(false);
		acField.setFocusable(false);
		acPane.add(acField, BorderLayout.CENTER);

		return acPane;
	}
	
	
	

	private JPanel BuildInitPane() {
		JPanel initPane = new JPanel();
		initPane.setLayout(new BorderLayout());

		JLabel initLabel = new JLabel("Initiative:");
		StyleContainer.SetFontHeader(initLabel);
		initPane.add(initLabel, BorderLayout.WEST);
		
		JTextField initField = new JTextField(m.init);
		initField.setEditable(false);
		initField.setFocusable(false);
		initField.setBorder(null);
		StyleContainer.SetFontMain(initField);
		initPane.add(initField, BorderLayout.CENTER);
		
		return initPane;
	}

	private JPanel BuildHPPane() {
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());

		JLabel hpLabel = new JLabel("HP:");
		hpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		StyleContainer.SetFontHeader(hpLabel);
		hPane.add(hpLabel, BorderLayout.WEST);

		hpField = new JTextField(m.hp);
		hpField.setEditable(false);
		hpField.setFocusable(false);
		hpField.setBorder(null);
		StyleContainer.SetFontMain(hpField);
		hPane.add(hpField, BorderLayout.CENTER);

		return hPane;
	}

	private JPanel BuildSpeedPane() {
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());

		JLabel sLabel = new JLabel("Speed:");
		sLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		StyleContainer.SetFontHeader(sLabel);
		sPane.add(sLabel, BorderLayout.WEST);

		speedField = new JTextField(m.speed);
		speedField.setEditable(false);
		speedField.setFocusable(false);
		speedField.setBorder(null);
		StyleContainer.SetFontMain(speedField);
		sPane.add(speedField, BorderLayout.CENTER);

		return sPane;
	}

	private void BuildAbilityScorePane(JPanel p) {
		JLabel save = new JLabel("Save");
		JLabel score = new JLabel("Mod");
		JLabel save2 = new JLabel("Save");
		JLabel score2 = new JLabel("Mod");
		JLabel save3 = new JLabel("Save");
		JLabel score3 = new JLabel("Mod");

//		save.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont(Font.ITALIC).deriveFont((float) 12));
		save.setFont(StyleContainer.FNT_HEADER_BOLD);
		score.setFont(save.getFont());
		save2.setFont(save.getFont());
		score2.setFont(save.getFont());
		save3.setFont(save.getFont());
		score3.setFont(save.getFont());

		save.setHorizontalAlignment(JLabel.CENTER);
		score.setHorizontalAlignment(JLabel.CENTER);
		save2.setHorizontalAlignment(JLabel.CENTER);
		score2.setHorizontalAlignment(JLabel.CENTER);
		save3.setHorizontalAlignment(JLabel.CENTER);
		score3.setHorizontalAlignment(JLabel.CENTER);

		p.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1;
		gbc.gridheight = gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.weighty = .2;
		p.add(score, gbc);
		gbc.gridx++;
		p.add(save, gbc);
		gbc.gridx += 3;
		p.add(score2, gbc);
		gbc.gridx++;
		p.add(save2, gbc);
		gbc.gridx += 3;
		p.add(score3, gbc);
		gbc.gridx++;
		p.add(save3, gbc);

		gbc.weighty = 1;
		JLabel strLabel = new JLabel("STR");
		strLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		strLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = .2;
		p.add(strLabel, gbc);

		strField = new JTextField("" + m.stats[Monster.STR]);
		strField.setEditable(false);
		strField.setFocusable(false);
		strField.setHorizontalAlignment(SwingConstants.CENTER);
		strField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		strField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		gbc.gridx = 1;
		gbc.weightx = 1;
		p.add(strField, gbc);
		
		JLabel strModLbl = new JLabel(m.getAbilityModString(Monster.STR));
		strModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		strModLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gbc.gridx = 2;
		StyleContainer.SetFontMain(strModLbl);
		p.add(strModLbl, gbc);

		strSaveP = new JLabel(m.getSaveString(Monster.STR));
		strSaveP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.black));
		strSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(strSaveP);
//		strSave
		gbc.gridx = 3;
		p.add(strSaveP, gbc);

		JLabel dexLabel = new JLabel("DEX");
		dexLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		dexLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gbc.gridx = 4;
		gbc.weightx = .2;
		p.add(dexLabel, gbc);

		dexField = new JTextField("" + m.stats[Monster.DEX]);
		dexField.setEditable(false);
		dexField.setFocusable(false);
		dexField.setHorizontalAlignment(SwingConstants.CENTER);
		dexField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		dexField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		gbc.weightx = 1;
		gbc.gridx = 5;
		p.add(dexField, gbc);
		
		JLabel dexModLbl = new JLabel(m.getAbilityModString(Monster.DEX));
		dexModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		dexModLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gbc.gridx++;
		StyleContainer.SetFontMain(dexModLbl);
		p.add(dexModLbl, gbc);

		dexSaveP = new JLabel(m.getSaveString(Monster.DEX));
		dexSaveP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.black));
		StyleContainer.SetFontMain(dexSaveP);
		dexSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		gbc.gridx ++;
		p.add(dexSaveP, gbc);

		JLabel conLabel = new JLabel("CON");
		conLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		conLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		gbc.gridx ++;
		gbc.weightx = .2;
		p.add(conLabel, gbc);

		conField = new JTextField("" + m.stats[Monster.CON]);
		conField.setEditable(false);
		conField.setFocusable(false);
		conField.setHorizontalAlignment(SwingConstants.CENTER);
		conField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		conField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		gbc.weightx = 1;
		gbc.gridx ++;
		p.add(conField, gbc);
		
		JLabel conModLbl = new JLabel(m.getAbilityModString(Monster.CON));
		conModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		conModLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		gbc.gridx ++;
		StyleContainer.SetFontMain(conModLbl);
		p.add(conModLbl, gbc);

		conSaveP = new JLabel(m.getSaveString(Monster.CON));
		conSaveP.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		conSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(conSaveP);
		gbc.gridx ++;
		p.add(conSaveP, gbc);

		JLabel intLabel = new JLabel("INT");
		intLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = .2;
		p.add(intLabel, gbc);

		intField = new JTextField("" + m.stats[Monster.INT]);
		intField.setEditable(false);
		intField.setFocusable(false);
		intField.setBorder(null);
		intField.setHorizontalAlignment(SwingConstants.CENTER);
		intField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		gbc.gridx ++;
		gbc.weightx = 1;
		p.add(intField, gbc);
		
		JLabel intModLbl = new JLabel(m.getAbilityModString(Monster.INT));
		intModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		StyleContainer.SetFontMain(intModLbl);
		gbc.gridx++;
		p.add(intModLbl, gbc);

		intSaveP = new JLabel(m.getSaveString(Monster.INT));
		intSaveP.setHorizontalAlignment(SwingConstants.CENTER);
		intSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		intSaveP.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
		StyleContainer.SetFontMain(intSaveP);
		gbc.gridx ++;
		p.add(intSaveP, gbc);
		
		JLabel wisLabel = new JLabel("WIS");
		wisLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		gbc.gridx ++;
		gbc.weightx = .2;
		p.add(wisLabel, gbc);

		wisField = new JTextField("" + m.stats[Monster.WIS]);
		wisField.setEditable(false);
		wisField.setFocusable(false);
		wisField.setBorder(null);
		wisField.setHorizontalAlignment(SwingConstants.CENTER);
		wisField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		gbc.weightx = 1;
		gbc.gridx ++;
		p.add(wisField, gbc);
		
		JLabel wisModLbl = new JLabel(m.getAbilityModString(Monster.WIS));
		wisModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		StyleContainer.SetFontMain(wisModLbl);
		gbc.gridx ++;
		p.add(wisModLbl, gbc);
		

		wisSaveP = new JLabel(m.getSaveString(Monster.WIS));
		wisSaveP.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
		StyleContainer.SetFontMain(wisSaveP);
		wisSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		gbc.gridx ++;
		p.add(wisSaveP, gbc);

		JLabel chaLabel = new JLabel("CHA");
		chaLabel.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 22));
		gbc.gridx ++;
		gbc.weightx = .2;
		p.add(chaLabel, gbc);

		chaField = new JTextField("" + m.stats[Monster.CHA]);
		chaField.setEditable(false);
		chaField.setFocusable(false);
		chaField.setBorder(null);
		chaField.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont((float) 20));
		chaField.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.weightx = 1;
		gbc.gridx ++;
		p.add(chaField, gbc);
		
		JLabel chaModLbl = new JLabel(m.getAbilityModString(Monster.CHA));
		chaModLbl.setHorizontalAlignment(SwingConstants.CENTER);
		StyleContainer.SetFontMain(chaModLbl);
		gbc.gridx ++;
		p.add(chaModLbl, gbc);

		chaSaveP = new JLabel(m.getSaveString(Monster.CHA));
		
		chaSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(chaSaveP);
		gbc.gridx ++;
		p.add(chaSaveP, gbc);
	}
}