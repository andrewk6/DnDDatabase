package gui.builder_internals;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import builders.monster_builder.DmgStatSelect;
import builders.monster_builder.SkillsSelectFrame;
import data.*;
import data.DataContainer.Proficiency;
import data.DataContainer.Skills;
import gui.gui_helpers.DocumentHelper;
import gui.gui_helpers.MonsterDispPane;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.RichEditor;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import utils.ErrorLogger;

public class MonsterBuilderIFrame extends JInternalFrame {
	private DataContainer data;

	private ReminderField monsterNameField, monsterTypeField, acField, initBnsField, hpField, speedField, strField,
			dexField, conField, intField, wisField, chaField, dmgResistField, dmgVulnField, immuneField, sensesField,
			langField, crField, numLActs, numLActsBns, filter;

	private JCheckBox initProfChck, initExpertChck, strSaveP, dexSaveP, conSaveP, intSaveP, wisSaveP, chaSaveP;

	private RichEditor traits, actions, bonusActions, reactions, legendActions;

	private HashMap<DataContainer.Skills, DataContainer.Proficiency> skills;

	private ArrayList<ReminderField> tagFields;

	private Map<String, Monster> monstMap;

	private JPanel sidePane, monstGridPane, tagGrid;
	
	private JPanel traitsPane, actPane, baPane, reactPane, legendActPane;
	
	private JTabbedPane tabPane;
	
	private HashMap<String, String> rEditReps;
	
	private final Dimension limitSize = new Dimension(200, Integer.MAX_VALUE);

	public MonsterBuilderIFrame(DataContainer data) {
		this.data = data;
		System.out.println(data.getMonsterKeysSorted().size());
		tagFields = new ArrayList<ReminderField>();
		skills = new HashMap<Skills, Proficiency>();		
		BuildReplacements();
		ConfigureFrame(this.getContentPane());
	}
	
	private void BuildReplacements() {
		rEditReps = new HashMap<String, String>();
		rEditReps.put("<Name>", "");
	}

	private void ConfigureFrame(Container cPane) {
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setSize(800, 800);
		this.setTitle("Monster Builder");
		StyleContainer.SetIcon(this, StyleContainer.MONSTER_BUILDER_ICON_FILE);
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameOpened(InternalFrameEvent e) {}
			public void internalFrameClosing(InternalFrameEvent e) {setVisible(false);}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameActivated(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		
		cPane.setLayout(new BorderLayout());

		tabPane = new JTabbedPane();
//		StyleContainer.SetLookAndFeel();

		JPanel statsPane = new JPanel();
		BuildStatsPane(statsPane);
		tabPane.addTab("Details/Stats", statsPane);

		traitsPane = new JPanel();
		BuildTraitsPane(traitsPane);
		tabPane.addTab("Traits", traitsPane);

		actPane = new JPanel();
		BuildActionPane(actPane);
		tabPane.addTab("Actions", actPane);

		baPane = new JPanel();
		BuildBonusActionPane(baPane);
		tabPane.addTab("Bonus Actions", baPane);

		reactPane = new JPanel();
		BuildReactionPane(reactPane);
		tabPane.addTab("Reactions", reactPane);

		legendActPane = new JPanel();
		BuildLegendActionPane(legendActPane);
		tabPane.addTab("Legendary Actions", legendActPane);

		JPanel tagPane = new JPanel();
		BuildTagsPane(tagPane);
		tabPane.addTab("Tags", tagPane);

		cPane.add(tabPane, BorderLayout.CENTER);

		JPanel addBtnPane = new JPanel();
		addBtnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		cPane.add(addBtnPane, BorderLayout.SOUTH);
		
		JButton addMonster = new JButton("Add Monster");
		StyleContainer.SetFontBtn(addMonster);
		addMonster.addActionListener(e -> {
			if(AddMonster())
				ResetEditor();
		});
		addMonster.setFocusable(false);
		addBtnPane.add(addMonster);
		
		JButton addMonsterRetain = new JButton("Add: Retain");
		StyleContainer.SetFontBtn(addMonsterRetain);
		addMonsterRetain.addActionListener(_ -> {
			if(AddMonster()) {
				System.out.println("Adding Monster");
				BuildMonstListPane();
				try {
					ResetEditors();
				} catch (BadLocationException e1) {
					ErrorLogger.log(e1);
					e1.printStackTrace();
				}
			}
		});
		addMonsterRetain.setFocusable(false);
		addBtnPane.add(addMonsterRetain);
		

		sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		
		monstGridPane = new JPanel();
//		monstGridPane.setPreferredSize(limitSize);
//		monstGridPane.setMaximumSize(limitSize);
		LoadMonsters(data.getMonsters());
		cPane.add(sidePane, BorderLayout.WEST);
		
		JScrollPane monstScroll = new JScrollPane(monstGridPane);
		sidePane.add(monstScroll, BorderLayout.CENTER);
		monstScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		monstScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JButton saveBtn = new JButton("Save");
		StyleContainer.SetFontBtn(saveBtn);
		saveBtn.addActionListener(e ->{
			WriteMonsters();
			
		});
		sidePane.add(saveBtn, BorderLayout.SOUTH);
		
		filter = new ReminderField("Monster Filter");
		StyleContainer.SetFontMain(filter);
		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {BuildMonstListPane();}
			public void removeUpdate(DocumentEvent e) {BuildMonstListPane();}
			public void changedUpdate(DocumentEvent e) {BuildMonstListPane();}
		});
		filter.setColumns(12);
		sidePane.add(filter, BorderLayout.NORTH);
	}
	
	private void ResetEditors() throws BadLocationException {
		StyledDocument tDoc = traits.getStyledDocument();
		StyledDocument aDoc = actions.getStyledDocument();
		StyledDocument baDoc = bonusActions.getStyledDocument();
		StyledDocument rDoc = reactions.getStyledDocument();
		StyledDocument laDoc = legendActions.getStyledDocument();
		
		traitsPane.remove(traits);
		traits = new RichEditor(data, rEditReps);
		traitsPane.add(traits);
		actPane.remove(actions);
		actions = new RichEditor(data, rEditReps);
		actPane.add(actions);
		baPane.remove(bonusActions);
		bonusActions = new RichEditor(data, rEditReps);
		baPane.add(bonusActions);
		reactPane.remove(reactions);
		reactions = new RichEditor(data, rEditReps);
		reactPane.add(reactions);
		legendActPane.remove(legendActions);
		legendActions = new RichEditor(data, rEditReps);
		legendActPane.add(legendActions);
		
		DocumentHelper.insertStyledDocument(traits.getStyledDocument(), tDoc, 0);
		DocumentHelper.insertStyledDocument(actions.getStyledDocument(), aDoc, 0);
		DocumentHelper.insertStyledDocument(bonusActions.getStyledDocument(), baDoc, 0);
		DocumentHelper.insertStyledDocument(reactions.getStyledDocument(), rDoc, 0);
		DocumentHelper.insertStyledDocument(legendActions.getStyledDocument(), laDoc, 0);
	}

	private void BuildTraitsPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField traitsHead = new JTextField("Traits");
		traitsHead.setFocusable(false);
		traitsHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		traitsHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		traitsHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(traitsHead, BorderLayout.NORTH);

		traits = new RichEditor(data, rEditReps);
		tPane.add(traits, BorderLayout.CENTER);
	}

	private void BuildActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField actsHead = new JTextField("Actions");
		actsHead.setFocusable(false);
		actsHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		actsHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		actsHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(actsHead, BorderLayout.NORTH);

		actions = new RichEditor(data, rEditReps);
		tPane.add(actions, BorderLayout.CENTER);
	}

	private void BuildBonusActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField bnsActHead = new JTextField("Bonus Actions");
		bnsActHead.setFocusable(false);
		bnsActHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		bnsActHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		bnsActHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(bnsActHead, BorderLayout.NORTH);

		bonusActions = new RichEditor(data, rEditReps);
		tPane.add(bonusActions, BorderLayout.CENTER);
	}

	private void BuildReactionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField reactHead = new JTextField("Reactions");
		reactHead.setFocusable(false);
		reactHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		reactHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		reactHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(reactHead, BorderLayout.NORTH);

		reactions = new RichEditor(data, rEditReps);
		tPane.add(reactions, BorderLayout.CENTER);
	}

	private void BuildLegendActionPane(JPanel tPane) {
		tPane.setLayout(new BorderLayout());
		JTextField legendActHead = new JTextField("Legendary Actions");
		legendActHead.setFocusable(false);
		legendActHead.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
//		StyleContainer.SetFontHeader(traitsHead);
		legendActHead.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 28));
		legendActHead.setEditable(false);
//		traitsHead.setHorizontalAlignment(SwingConstants.CEN);
		tPane.add(legendActHead, BorderLayout.NORTH);

		legendActions = new RichEditor(data, rEditReps);
		tPane.add(legendActions, BorderLayout.CENTER);

		JPanel numActionsPane = new JPanel();
		numActionsPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JLabel numLabel = new JLabel("Number of Legendary Actions:");
		StyleContainer.SetFontMain(numLabel);
		numActionsPane.add(numLabel);

		numLActs = new ReminderField("", "# Uses");
		numLActs.setColumns(7);
		numLActs.setNumbersOnly();
		StyleContainer.SetFontMain(numLActs);
		numActionsPane.add(numLActs);

		JLabel lairBonus = new JLabel("Lair Bonus: ");
		StyleContainer.SetFontMain(lairBonus);
		numActionsPane.add(lairBonus);

		numLActsBns = new ReminderField("0", "# Bonus");
		numLActsBns.setColumns(7);
		numLActsBns.setNumbersOnly();
		StyleContainer.SetFontMain(numLActsBns);
		numActionsPane.add(numLActsBns);

		tPane.add(numActionsPane, BorderLayout.SOUTH);
	}

	private void BuildTagsPane(JPanel pane) {
		pane.setLayout(new BorderLayout());
		tagGrid = new JPanel();
		tagGrid.setLayout(new GridLayout(0, 2));

		JScrollPane tagScroll = new JScrollPane(tagGrid);
		pane.add(tagScroll, BorderLayout.CENTER);

		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton addTagBtn = new JButton("Add Tag");
		addTagBtn.setFocusable(false);
		StyleContainer.SetFontBtn(addTagBtn);
		addTagBtn.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> {
				AddTag();
				tagGrid.revalidate();
				tagGrid.repaint();
			});

		});
		btnPane.add(addTagBtn);
		pane.add(btnPane, BorderLayout.SOUTH);
	}

	private void BuildStatsPane(JPanel hPane) {
		hPane.setLayout(new BorderLayout());

		JPanel tPane = new JPanel();
		tPane.setLayout(new BorderLayout());
		monsterNameField = new ReminderField("", "Enter the name for the monster...");
		StyleContainer.SetFontHeader(monsterNameField);
		monsterNameField.setFont(monsterNameField.getFont().deriveFont((float) 22));
		monsterNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {SetNameMap();}
			public void removeUpdate(DocumentEvent e) {SetNameMap();}
			public void changedUpdate(DocumentEvent e) {SetNameMap();}
			private void SetNameMap() {
				rEditReps.put("<NAME>", monsterNameField.getText().toLowerCase());
			}
		});
		tPane.add(monsterNameField, BorderLayout.CENTER);

		monsterTypeField = new ReminderField("", "Enter the monster size type, alignment");
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

	private void BuildBottomSPane(JPanel sPane) {
		sPane.setLayout(new GridLayout(0, 1));
		JButton addSkills = new JButton("Add Skills");
		StyleContainer.SetFontBtn(addSkills);
		addSkills.setFocusable(false);
		addSkills.addActionListener(_ -> {
			SwingUtilities.invokeLater(() -> {
				SkillsSelectFrame skillFrame = new SkillsSelectFrame(skills);

				Thread finishedCheck = new Thread(() -> {
					while (!skillFrame.finished) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
							ErrorLogger.log(e1);
							e1.printStackTrace();
						}
					}
					skills = skillFrame.getSkills();
					skillFrame.dispose();
					System.out.println("Printing Skills:");
					for (DataContainer.Skills s : skills.keySet())
						System.out.println(s.name() + ": " + skills.get(s));
				});
				finishedCheck.start();
				skillFrame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				skillFrame.setVisible(true);
			});
		});
		sPane.add(addSkills);
		JPanel vPane = new JPanel();
		
		vPane.setLayout(new BorderLayout());
		sPane.add(vPane);
		dmgVulnField = new ReminderField("", "Enter any damage vulnerabilities");
		StyleContainer.SetFontMain(dmgVulnField);
		vPane.add(dmgVulnField, BorderLayout.CENTER);
		
		JButton setVuln = new JButton("Set Vuln");
		setVuln.setFocusable(false);
		StyleContainer.SetFontBtn(setVuln);
		setVuln.addActionListener(e ->{
			DmgStatSelect vulnSet = new DmgStatSelect(dmgVulnField);
			vulnSet.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			vulnSet.setVisible(true);
		});
		vPane.add(setVuln, BorderLayout.EAST);
		
		JPanel rPane = new JPanel();
		rPane.setLayout(new BorderLayout());
		sPane.add(rPane);
		
		dmgResistField = new ReminderField("", "Enter any damage resistances");
		StyleContainer.SetFontMain(dmgResistField);
		rPane.add(dmgResistField, BorderLayout.CENTER);
		
		JButton setResist = new JButton("Set Resist");
		StyleContainer.SetFontBtn(setResist);
		setResist.setFocusable(false);
		setResist.setFont(setResist.getFont().deriveFont((float) setResist.getFont().getSize() - 3));
		setResist.addActionListener(e ->{
			DmgStatSelect setR = new DmgStatSelect(dmgResistField);
			setR.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			setR.setVisible(true);
		});
		rPane.add(setResist, BorderLayout.EAST);
		
		JPanel iPane = new JPanel();
		iPane.setLayout(new BorderLayout());
		sPane.add(iPane);
		
		immuneField = new ReminderField("", "Enter any immunities");
		StyleContainer.SetFontMain(immuneField);
		iPane.add(immuneField, BorderLayout.CENTER);
		
		JButton setImmune = new JButton("Set Immune");
		setImmune.setFocusable(false);
		StyleContainer.SetFontBtn(setImmune);
		setImmune.setFont(setImmune.getFont().deriveFont((float) setImmune.getFont().getSize() - 3));
		setImmune.addActionListener(e ->{
			DmgStatSelect setI = new DmgStatSelect(immuneField);
			setI.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			setI.setVisible(true);
		});
		iPane.add(setImmune, BorderLayout.EAST);

		sensesField = new ReminderField("", "What senses does the monster have");
		StyleContainer.SetFontMain(sensesField);
		sPane.add(sensesField);
		langField = new ReminderField("", "What languages does the monster know");
		StyleContainer.SetFontMain(langField);
		sPane.add(langField);

		JPanel crPane = new JPanel();
		crPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel crLabel = new JLabel("CR:");
		StyleContainer.SetFontHeader(crLabel);
		crPane.add(crLabel);
		crField = new ReminderField("", "Monster's CR");
		crField.setColumns(10);
		StyleContainer.SetFontMain(dmgVulnField);
		crField.setDecimalsOnly();
		Dimension size = crField.getPreferredSize();
		size.height = 30; // your desired height
		crField.setPreferredSize(size);
		crPane.add(crField);
		sPane.add(crPane);
	}

	private JPanel BuildACPane() {
		// Add AC Section
		JPanel acPane = new JPanel();
		acPane.setLayout(new BorderLayout());

		JLabel acLabel = new JLabel("AC:");
		acLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
		StyleContainer.SetFontHeader(acLabel);
		acPane.add(acLabel, BorderLayout.WEST);

		// Add AC Field, Adjust GBC
		acField = new ReminderField("", "Please enter monsters AC...");
//				acField.SetNumbersOnly(true);
		StyleContainer.SetFontMain(acField);
		acPane.add(acField, BorderLayout.CENTER);

		return acPane;
	}

	private JPanel BuildInitPane() {
		JPanel initPane = new JPanel();
		initPane.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel initLabel = new JLabel("Initiative:");
		StyleContainer.SetFontHeader(initLabel);
		initPane.add(initLabel);

		initProfChck = new JCheckBox("Proifiency?");
		initProfChck.setFocusable(false);
		initExpertChck = new JCheckBox("Expertise?");
		initExpertChck.setFocusable(false);

		initProfChck.addActionListener(e -> {
//			if (!initProfChck.isSelected())
//				initExpertChck.setSelected(false);
			if(initProfChck.isSelected())
				if(initExpertChck.isSelected())
					initExpertChck.setSelected(false);
		});
		StyleContainer.SetFontMain(initProfChck);
		initPane.add(initProfChck);

		initExpertChck.addActionListener(e -> {
//			if (initExpertChck.isSelected()) {
//				initProfChck.setSelected(true);
//			}
			
			if(initExpertChck.isSelected())
				if(initProfChck.isSelected())
					initProfChck.setSelected(false);
		});
		StyleContainer.SetFontMain(initExpertChck);
		initPane.add(initExpertChck);

		JLabel initBnsLabel = new JLabel("Bonus:");
		initBnsLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		StyleContainer.SetFontMain(initBnsLabel);
		initPane.add(initBnsLabel);

		initBnsField = new ReminderField("0", "Init Bonus");
		initBnsField.setColumns(20);
		initBnsField.setNumbersOnly();
		initBnsField.setFocusable(false);
//		initPane.add(initBnsField);

		return initPane;
	}

	private JPanel BuildHPPane() {
		JPanel hPane = new JPanel();
		hPane.setLayout(new BorderLayout());

		JLabel hpLabel = new JLabel("HP:");
		hpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		StyleContainer.SetFontHeader(hpLabel);
		hPane.add(hpLabel, BorderLayout.WEST);

		hpField = new ReminderField("", "Set enemy HP Value and Hit Point Dice");
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

		speedField = new ReminderField("", "Set enemy Speeds");
		StyleContainer.SetFontMain(speedField);
		sPane.add(speedField, BorderLayout.CENTER);

		return sPane;
	}

	private void BuildAbilityScorePane(JPanel p) {
		JLabel save = new JLabel("Save");
		JLabel score = new JLabel("Score");
		JLabel save2 = new JLabel("Save");
		JLabel score2 = new JLabel("Score");
		JLabel save3 = new JLabel("Save");
		JLabel score3 = new JLabel("Score");

		save.setFont(StyleContainer.FNT_BODY_PLAIN.deriveFont(Font.ITALIC).deriveFont((float) 12));
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
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 1;
		gbc.gridheight = gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;

		p.add(score, gbc);
		gbc.gridx++;
		p.add(save, gbc);
		gbc.gridx += 2;
		p.add(score2, gbc);
		gbc.gridx++;
		p.add(save2, gbc);
		gbc.gridx += 2;
		p.add(score3, gbc);
		gbc.gridx++;
		p.add(save3, gbc);

		JLabel strLabel = new JLabel("STR");
		StyleContainer.SetFontHeader(strLabel);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = .2;
		p.add(strLabel, gbc);

		strField = new ReminderField("", "Str Val");
		strField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		strField.setNumbersOnly();
		gbc.gridx = 1;
		gbc.weightx = 1;
		p.add(strField, gbc);

		strSaveP = new JCheckBox("Prof");
		strSaveP.setFocusable(false);
		strSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(strSaveP);
		gbc.gridx = 2;
		p.add(strSaveP, gbc);

		JLabel dexLabel = new JLabel("DEX");
		StyleContainer.SetFontHeader(dexLabel);
		gbc.gridx = 3;
		gbc.weightx = .2;
		p.add(dexLabel, gbc);

		dexField = new ReminderField("", "Dex Val");
		dexField.setNumbersOnly();
		dexField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		gbc.weightx = 1;
		gbc.gridx = 4;
		p.add(dexField, gbc);

		dexSaveP = new JCheckBox("Prof?");
		dexSaveP.setFocusable(false);
		StyleContainer.SetFontMain(dexSaveP);
		dexSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		gbc.gridx = 5;
		p.add(dexSaveP, gbc);

		JLabel conLabel = new JLabel("CON");
		StyleContainer.SetFontHeader(conLabel);
		gbc.gridx = 6;
		gbc.weightx = .2;
		p.add(conLabel, gbc);

		conField = new ReminderField("", "Con Val");
		conField.setNumbersOnly();
		conField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		gbc.weightx = 1;
		gbc.gridx = 7;
		p.add(conField, gbc);

		conSaveP = new JCheckBox("Prof?");
		conSaveP.setFocusable(false);
		conSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(conSaveP);
		gbc.gridx = 8;
		p.add(conSaveP, gbc);

		JLabel intLabel = new JLabel("INT");
		StyleContainer.SetFontHeader(intLabel);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = .2;
		p.add(intLabel, gbc);

		intField = new ReminderField("", "Int Val");
		intField.setNumbersOnly();
		intField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		gbc.gridx = 1;
		gbc.weightx = 1;
		p.add(intField, gbc);

		intSaveP = new JCheckBox("Prof");
		intSaveP.setFocusable(false);
		intSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		StyleContainer.SetFontMain(intSaveP);
		gbc.gridx = 2;
		p.add(intSaveP, gbc);

		JLabel wisLabel = new JLabel("WIS");
		StyleContainer.SetFontHeader(wisLabel);
		gbc.gridx = 3;
		gbc.weightx = .2;
		p.add(wisLabel, gbc);

		wisField = new ReminderField("", "Wis Val");
		wisField.setNumbersOnly();
		wisField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		gbc.weightx = 1;
		gbc.gridx = 4;
		p.add(wisField, gbc);

		wisSaveP = new JCheckBox("Prof?");
		wisSaveP.setFocusable(false);
		StyleContainer.SetFontMain(wisSaveP);
		wisSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		gbc.gridx = 5;
		p.add(wisSaveP, gbc);

		JLabel chaLabel = new JLabel("CHA");
		StyleContainer.SetFontHeader(chaLabel);
		gbc.gridx = 6;
		gbc.weightx = .2;
		p.add(chaLabel, gbc);

		chaField = new ReminderField("", "Cha Val");
		chaField.setNumbersOnly();
		chaField.setFont(StyleContainer.FNT_HEADER_BOLD.deriveFont((float) 24));
		gbc.weightx = 1;
		gbc.gridx = 7;
		p.add(chaField, gbc);

		chaSaveP = new JCheckBox("Prof?");
		chaSaveP.setHorizontalAlignment(JCheckBox.CENTER);
		chaSaveP.setFocusable(false);
		StyleContainer.SetFontMain(chaSaveP);
		gbc.gridx = 8;
		p.add(chaSaveP, gbc);

	}

	private void BuildMonstListPane() {
		SwingUtilities.invokeLater(()->{
			monstGridPane.removeAll();
			monstGridPane.setLayout(new GridLayout(0, 2));
			
			ArrayList<String> sortKeys = new ArrayList<String>();
			for(String key : monstMap.keySet()) {
				if(filter.getText().length() > 0) {
					if(key.toLowerCase().contains(filter.getText().toLowerCase()))
						sortKeys.add(key);
				}else {
					sortKeys.add(key);
				}
				
			}
			Collections.sort(sortKeys);
			for (String key : sortKeys) {
//				System.out.println(key);
				String name = key;
				if(name.length() > 13)
					name = name.substring(0, 13);
				JTextField monstName = new JTextField(name);
				
				monstName.setToolTipText(key);
				StyleContainer.SetFontMain(monstName);
				monstName.setColumns(15);
				monstName.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
//						JFrame monstDisp = new JFrame();
//						monstDisp.setTitle(key);
//						monstDisp.setContentPane(new MonsterDispPane(monstMap.get(key), data, new GuiDirector(new JDesktopPane())));
//						monstDisp.setSize(645, 515);
//						monstDisp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);						
//						monstDisp.setVisible(true);
//						monstDisp.setResizable(false);
						
						int opt = JOptionPane.showConfirmDialog(null, 
								"Load: " + key + "? Any unadded work on current monster will be lost.", 
								"Load Confirm", JOptionPane.YES_NO_OPTION);
						if(opt == JOptionPane.YES_OPTION)
							LoadEditMonster(monstMap.get(key));
					}
					public void mousePressed(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {monstName.setFont(monstName.getFont().deriveFont(Font.BOLD));}
					public void mouseExited(MouseEvent e) {monstName.setFont(monstName.getFont().deriveFont(Font.PLAIN));}
				});
				monstName.setEditable(false);
				monstName.setFocusable(false);

				monstGridPane.add(monstName);

				JButton mDel = new JButton("Delete");
				StyleContainer.SetFontBtn(mDel);
				mDel.addActionListener(e -> {
					int delOpt = JOptionPane.showConfirmDialog(null, ("Delete: " + key), "Delete Confirmation", 
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(delOpt == JOptionPane.YES_OPTION) {
						SwingUtilities.invokeLater(() -> {
							monstMap.remove(key);
							monstGridPane.remove(monstName);
							monstGridPane.remove(mDel);

							monstGridPane.repaint();
							monstGridPane.revalidate();
						});
					}
					
				});
				monstGridPane.add(mDel);
			}
			monstGridPane.repaint();
			monstGridPane.revalidate();
		});
	}
	

	private boolean AddMonster() {
		MonsterData mData = new MonsterData();
//		mData.name = monsterNameField.getText();
//		mData.type = monsterTypeField.getText();
		try {
			mData.ac = acField.getText();
			mData.acts = actions.getStyledDocument();
			mData.bActs = bonusActions.getStyledDocument();
			mData.cha = Integer.parseInt(chaField.getText());
			mData.chaSProf = chaSaveP.isSelected();
			mData.con = Integer.parseInt(conField.getText());
			mData.conSProf = conSaveP.isSelected();
			mData.cr = Double.parseDouble(crField.getText());
			mData.dex = Integer.parseInt(dexField.getText());
			mData.dexSProf = dexSaveP.isSelected();
			mData.dmgResist = dmgResistField.getText();
			mData.dmgVuln = dmgVulnField.getText();
			mData.hp = hpField.getText();
			mData.initBns = Integer.parseInt(initBnsField.getText());
			mData.initExp = initExpertChck.isSelected();
			mData.initProf = initProfChck.isSelected();
			mData.inte = Integer.parseInt(intField.getText());
			mData.intSProf = intSaveP.isSelected();
			mData.invuln = immuneField.getText();
			mData.lActs = legendActions.getStyledDocument();
			if(legendActions.getText().length() > 0 && 
					Integer.parseInt(numLActs.getText()) == 0) {
				int opt = JOptionPane.showConfirmDialog(acField, 
						"Do you want to create monster with legendary actions and no uses?",
						"Confirm Legends", JOptionPane.YES_NO_CANCEL_OPTION);
				if(opt == JOptionPane.NO_OPTION) {
					throw new NumberFormatException();
				}else if(opt == JOptionPane.CANCEL_OPTION) {
					throw new Exception();
				}else {
					numLActs.setText("0");
				}
			}
			if(legendActions.getText().length() == 0) {
				numLActs.setText("0");
				numLActsBns.setText("0");
			}
			mData.lActsBns = Integer.parseInt(numLActsBns.getText());
			mData.lActsNum = Integer.parseInt(numLActs.getText());
			mData.lang = langField.getText();
			mData.name = monsterNameField.getText();
			mData.reActs = reactions.getStyledDocument();
			mData.senses = sensesField.getText();
			mData.skills = skills;
			mData.spd = speedField.getText();
			mData.str = Integer.parseInt(strField.getText());
			mData.strSProf = strSaveP.isSelected();
			ArrayList<String> tagVals = new ArrayList<String>();
			for (ReminderField r : tagFields) {
				tagVals.add(r.getText());
			}
			mData.tags = tagVals;
			mData.traits = traits.getStyledDocument();
			mData.type = monsterTypeField.getText();
			mData.wis = Integer.parseInt(wisField.getText());
			mData.wisSProf = wisSaveP.isSelected();

			Monster m = MonsterFactory.BuildMonster(mData);
			monstMap.put(m.name, m);
			return true;
		} catch (NumberFormatException e) {
			ErrorLogger.log(e);
			int opt = JOptionPane.showOptionDialog(this, "Empty fields found how to procedd", "Empty Fields Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
					new String[] {"Set all to 0", "Return to Edit"}, 1);
			if(opt == 0)
			{
				if(strField.getText().length() <= 0)
					strField.setText("0");
				if(dexField.getText().length() <= 0)
					dexField.setText("0");
				if(conField.getText().length() <= 0)
					conField.setText("0");
				if(intField.getText().length() <= 0)
					intField.setText("0");
				if(wisField.getText().length() <= 0)
					wisField.setText("0");
				if(chaField.getText().length() <= 0)
					chaField.setText("0");
				if(numLActsBns.getText().length() <= 0)
					numLActsBns.setText("0");
				if(numLActs.getText().length() <= 0)
					numLActs.setText("0");
				if(initBnsField.getText().length() <= 0)
					initBnsField.setText("0");
				if(crField.getText().length() <= 0)
					crField.setText("0");
				System.out.println("Return addmonst");
				return AddMonster();
			}
			System.out.println("Return false in NumberFornate");
			return false;
		} catch (Exception e2) {
			ErrorLogger.log(e2);
			System.out.println("Still editing, return false");
			return false;
		}
	}
	
	private void LoadEditMonster(Monster m) {
		ResetEditor();
		monsterNameField.setText(m.name);
		monsterNameField.setEditable(false);
		monsterNameField.setFocusable(false);
		monsterTypeField.setText(m.typeSizeAlignment);
		acField.setText(m.ac);
		initBnsField.setText("0");
		hpField.setText(m.hp);
		speedField.setText(m.speed);
		strField.setText("" + m.stats[Monster.STR]);
		dexField.setText("" + m.stats[Monster.DEX]);
		conField.setText("" + m.stats[Monster.CON]);
		intField.setText("" + m.stats[Monster.INT]);
		wisField.setText("" + m.stats[Monster.WIS]);
		chaField.setText("" + m.stats[Monster.CHA]);
		dmgResistField.setText(m.dmgRes);
		dmgVulnField.setText(m.dmgVul);
		immuneField.setText(m.immune);
		sensesField.setText(m.senses);
		langField.setText(m.languages);
		crField.setText(m.cr + "");
		numLActs.setText(m.lActNum + "");
		numLActsBns.setText(m.lActBns + "");
		int prof = m.profByCR();
		int init = m.GetInitBonus();
		int dex = m.stats[Monster.DEX];
		if(init > Monster.AbilityModCalc(dex)) {
			if(prof+Monster.AbilityModCalc(dex) == init) {
				initProfChck.setSelected(true);
				initExpertChck.setSelected(false);
			}else {
				initProfChck.setSelected(false);
				initExpertChck.setSelected(true);
			}
		}
		strSaveP.setSelected(m.saves[Monster.STR]);
		dexSaveP.setSelected(m.saves[Monster.DEX]);
		conSaveP.setSelected(m.saves[Monster.CON]);
		intSaveP.setSelected(m.saves[Monster.INT]);
		wisSaveP.setSelected(m.saves[Monster.WIS]);
		chaSaveP.setSelected(m.saves[Monster.CHA]);
		
//		traitsPane.remove(traits);
		removeAndDergister(traitsPane, traits);
		traits = new RichEditor(data, rEditReps);
		traits.LoadDocument(m.traits);
		traitsPane.add(traits);
//		actPane.remove(actions);
		removeAndDergister(actPane, actions);
		actions = new RichEditor(data, rEditReps);
		actions.LoadDocument(m.actions);
		actPane.add(actions);
//		baPane.remove(bonusActions);
		removeAndDergister(baPane, bonusActions);
		bonusActions = new RichEditor(data, rEditReps);
		bonusActions.LoadDocument(m.bonusActions);
		baPane.add(bonusActions);
//		reactPane.remove(reactions);
		removeAndDergister(reactPane, reactions);
		reactions = new RichEditor(data, rEditReps);
		reactions.LoadDocument(m.reactions);
		reactPane.add(reactions);
//		legendActPane.remove(legendActions);
		removeAndDergister(legendActPane, legendActions);
		legendActions = new RichEditor(data, rEditReps);
		legendActions.LoadDocument(m.legendActions);
		legendActPane.add(legendActions);
		
		skills = m.skills;
		tagFields.clear();
		
		for(String s : m.tags)
			AddTag(s);
		
		tabPane.setSelectedIndex(0);
		
		repaint();
		revalidate();
	}
	
	private void AddTag() {
		ReminderField tagField = new ReminderField("", "Enter Tag");
		StyleContainer.SetFontHeader(tagField);

		JButton dTagBtn = new JButton("Delete");
		StyleContainer.SetFontBtn(dTagBtn);
		dTagBtn.setFocusable(false);
		dTagBtn.addActionListener(e2 -> {
			SwingUtilities.invokeLater(() -> {
				tagFields.remove(tagField);
				tagGrid.remove(tagField);
				tagGrid.remove(dTagBtn);

				tagGrid.revalidate();
				tagGrid.repaint();
			});
		});

		tagGrid.add(tagField);
		tagGrid.add(dTagBtn);
		tagField.requestFocus();
		tagFields.add(tagField);
	}
	
	private void AddTag(String s) {
		ReminderField tagField = new ReminderField("", "Enter Tag");
		tagField.setText(s);
		StyleContainer.SetFontHeader(tagField);

		JButton dTagBtn = new JButton("Delete");
		StyleContainer.SetFontBtn(dTagBtn);
		dTagBtn.setFocusable(false);
		dTagBtn.addActionListener(e2 -> {
			SwingUtilities.invokeLater(() -> {
				tagFields.remove(tagField);
				tagGrid.remove(tagField);
				tagGrid.remove(dTagBtn);

				tagGrid.revalidate();
				tagGrid.repaint();
			});
		});

		tagGrid.add(tagField);
		tagGrid.add(dTagBtn);
		tagField.requestFocus();
		tagFields.add(tagField);
	}

	private void LoadMonsters(Map<String, Monster> mIn) {
		monstMap = new HashMap<String, Monster>(mIn);
		BuildMonstListPane();
	}
	
	public boolean WriteMonsters() {
		data.SetMonstersMap(monstMap);
		data.SafeSaveData(DataContainer.MONSTERS);
		return true;
	}
	
	private void ResetEditor() {
		BuildMonstListPane();
		monsterNameField.setText("");
		monsterNameField.setEditable(true);
		monsterNameField.setFocusable(true);
		monsterTypeField.setText("");
		acField.setText("");
		initBnsField.setText("0");
		hpField.setText("");
		speedField.setText("");
		strField.setText("");
		dexField.setText("");
		conField.setText("");
		intField.setText("");
		wisField.setText("");
		chaField.setText("");
		dmgResistField.setText("");
		dmgVulnField.setText("");
		immuneField.setText("");
		sensesField.setText("");
		langField.setText("");
		crField.setText("");
		numLActs.setText("");
		numLActsBns.setText("0");
		initProfChck.setSelected(false);
		initExpertChck.setSelected(false);
		strSaveP.setSelected(false);
		dexSaveP.setSelected(false);
		conSaveP.setSelected(false);
		intSaveP.setSelected(false);
		wisSaveP.setSelected(false);
		chaSaveP.setSelected(false);
		
		tagGrid.removeAll();
		
//		traitsPane.remove(traits);
		removeAndDergister(traitsPane, traits);
		traits = new RichEditor(data, rEditReps);
		traitsPane.add(traits);
//		actPane.remove(actions);
		removeAndDergister(actPane, actions);
		actions = new RichEditor(data, rEditReps);
		actPane.add(actions);
//		baPane.remove(bonusActions);
		removeAndDergister(baPane, bonusActions);
		bonusActions = new RichEditor(data, rEditReps);
		baPane.add(bonusActions);
//		reactPane.remove(reactions);
		removeAndDergister(reactPane, reactions);
		reactions = new RichEditor(data, rEditReps);
		reactPane.add(reactions);
//		legendActPane.remove(legendActions);
		removeAndDergister(legendActPane, legendActions);
		legendActions = new RichEditor(data, rEditReps);
		legendActPane.add(legendActions);
		
		skills = new HashMap<Skills, Proficiency>();
		tagFields.clear();
		
		tabPane.setSelectedIndex(0);
		monsterNameField.requestFocus(); 
		
		repaint();
		revalidate();
	}
	
	public void removeAndDergister(JPanel pane, RichEditor r) {
		data.deregisterListener(r);
		pane.remove(r);
	}
}