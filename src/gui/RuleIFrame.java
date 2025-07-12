package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import data.DataContainer;
import gui.gui_helpers.CompFactory;
import gui.gui_helpers.CustomDesktopIcon;
import gui.gui_helpers.HoverTextPane;
import gui.gui_helpers.structures.ContentTab;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;

public class RuleIFrame extends JInternalFrame implements ContentTab{
	private DataContainer data;
	private GuiDirector gd;
	private JDesktopPane dPane;
	private HoverTextPane hPane;
	private JTextField ruleTitle;
	private JPanel ruleGridPane, cardPane;
	private JTextField ruleFilter;
	
	private JTabbedPane ruleTabs;

	public RuleIFrame(DataContainer data, GuiDirector guiD, JDesktopPane dPane) {
		this.data = data;
		this.dPane = dPane;
		gd = guiD;

		BuildFrame();
		BuildContent(getContentPane());
		BuildSidePane(getContentPane());
		
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		if(gd.getrFrame() == null)
			gd.NotifyFocus(this);
	}

	private void BuildFrame() {
		getContentPane().setLayout(new BorderLayout());
		setSize(800, 800);
		setTitle("Rules Database");
		setIconifiable(true);
		setClosable(true);
		setMaximizable(true);
		setResizable(true);
		
		this.addInternalFrameListener(GuiDirector.getContentTabListener(gd, this));
		this.addInternalFrameListener(CompFactory.createNonCloseListener(this));
		
		try {
			BufferedImage iconImage = ImageIO.read(
				    getClass().getResource("/"+ StyleContainer.RULE_ICON_FILE));
			setDesktopIcon(new CustomDesktopIcon(this, iconImage));
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.RULE_ICON_FILE));
			this.setFrameIcon(icon);
		} catch (IOException e) {
			ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(StyleContainer.RULE_ICON_FILE));
			this.setFrameIcon(icon);
		}		
	}
	
	public void BuildSidePane(Container cPane){
		JPanel sPane = new JPanel();
		sPane.setLayout(new BorderLayout());
		
		ruleFilter = new JTextField();
		ruleFilter.setToolTipText("Enter a rule filter");
		ruleFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {FillSidePane();};
			public void insertUpdate(DocumentEvent e) {FillSidePane();}
			public void changedUpdate(DocumentEvent e) {FillSidePane();}
		});
		StyleContainer.SetFontHeader(ruleFilter);
		sPane.add(ruleFilter, BorderLayout.NORTH);
		
		ruleGridPane = new JPanel();
		ruleGridPane.setLayout(new GridLayout(0,1));
		FillSidePane();
		
		JScrollPane spellGridScroll = new JScrollPane(ruleGridPane);
		spellGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spellGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		sPane.add(spellGridScroll, BorderLayout.CENTER);
		cPane.add(sPane, BorderLayout.WEST);
	}
	
	public void FillSidePane() {
		SwingUtilities.invokeLater(()->{
			ruleGridPane.removeAll();
			List<String> keys = data.getRuleKeysSorted();
			for(String s : keys) {
				if(s.toLowerCase().startsWith(ruleFilter.getText().toLowerCase()) || ruleFilter.getText().length() == 0) {
					JTextField sField = new JTextField(s);
					StyleContainer.SetFontHeader(sField);
					sField.setEditable(false);
					sField.setFocusable(false);
					sField.setColumns(25);
					sField.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent e) {
//							ruleTitle.setText(s);
//							hPane.setDocument(data.getRules().get(s).ruleDoc);
							if(gd.getComboFrame() != null) {
								gd.getComboFrame().AddTab(data.getRules().get(s));
							}else {
								AddTab(s);
								CheckTabs();
							}
						}
						public void mousePressed(MouseEvent e) {}
						public void mouseReleased(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						
					});
					ruleGridPane.add(sField);
				}
			}
			ruleGridPane.revalidate();
			ruleGridPane.repaint();
		});
	}
	
	public void AddTab(String key) {
		JPanel rPane = new JPanel();
		rPane.setLayout(new BorderLayout());
		ruleTabs.addTab(key, rPane);
		
		JTextField rTitle = new JTextField(key);
		rTitle.setEditable(false);
		rTitle.setFocusable(false);
		rTitle.setHorizontalAlignment(JTextField.CENTER);
		StyleContainer.SetFontHeader(rTitle);
		rPane.add(rTitle, BorderLayout.NORTH);
		
		HoverTextPane ruleDesc = new HoverTextPane(data, gd, dPane);
		ruleDesc.setDocument(data.getRules().get(key).ruleDoc);
		ruleDesc.SetRuleTabbedPane(this);
		JScrollPane rScroll = new JScrollPane(ruleDesc);
		rScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		rPane.add(rScroll, BorderLayout.CENTER);
		
		JPanel btnFlow = new JPanel();
		btnFlow.setLayout(new FlowLayout(FlowLayout.RIGHT));
		rPane.add(btnFlow, BorderLayout.SOUTH);
		
		JButton removeRule = new JButton("Remove " + key);
		StyleContainer.SetFontBtn(removeRule);
		removeRule.addActionListener(e ->{
			int index = ruleTabs.indexOfComponent(rPane);
			if(index != -1) {
				ruleTabs.removeTabAt(index);
			}
			
			CheckTabs();
		});
		btnFlow.add(removeRule);
		CheckTabs();
		ruleTabs.setSelectedComponent(rPane);
	}
	
	public void CheckTabs() {
		if(ruleTabs.getTabCount() == 1) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "ruletabs");
		}else if(ruleTabs.getTabCount() <= 0) {
			CardLayout cl = (CardLayout) cardPane.getLayout();
			cl.show(cardPane, "norule");
		}
	}
	
	private void BuildContent(Container cPane) {
		cardPane = new JPanel();
		cardPane.setLayout(new CardLayout());
		cPane.add(cardPane, BorderLayout.CENTER);
		
		JLabel noRuleLabel = new JLabel("No Rules Loaded");
		StyleContainer.SetFontHeader(noRuleLabel);
		cardPane.add(noRuleLabel, "norule");
		
		ruleTabs = new JTabbedPane();
		cardPane.add(ruleTabs, "ruletabs");
		
		CardLayout cl = (CardLayout) cardPane.getLayout();
		cl.show(cardPane, "norule");
	}

//	private void BuildContent(Container cPane) {
//		hPane = new HoverTextPane(data, dPane);
//		JScrollPane spellScroller = new JScrollPane(hPane);
//		spellScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		cPane.add(spellScroller, BorderLayout.CENTER);
//		
//		ruleTitle = new JTextField("NO RULE SELECTED");
//		ruleTitle.setToolTipText("Name of the rule");
//		ruleTitle.setEditable(false);
//		ruleTitle.setFocusable(false);
//		ruleTitle.setHorizontalAlignment(JTextField.CENTER);
//		StyleContainer.SetFontHeader(ruleTitle);
//		cPane.add(ruleTitle, BorderLayout.NORTH);
//	}
	
	public void SetRule(String rule) {
		ruleFilter.setText(rule);
		FillSidePane();
		hPane.setDocument(data.getRules().get(rule).ruleDoc);
	}
	
	public JTabbedPane GetTabs() {
		return ruleTabs;
	}
}