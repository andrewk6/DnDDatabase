package builders.monster_builder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


import data.DataContainer;
import data.DataContainer.DamageTypes;
import gui.gui_helpers.ReminderField;
import gui.gui_helpers.structures.StyleContainer;

public class AttackInsertForm extends JDialog
{
	public boolean finished;
	
	private int aMod, p;
	
	private ReminderField title, attackMod, baseDmg, reach, rangeLow, rangeHigh;
	private JCheckBox mAttack, rAttack, hAttack;
	private HashMap<ReminderField, JComboBox> dmgVals;	
	private JComboBox<String> baseDmgType;
	
	private JPanel rCPane;
	
	String [] dmgTypes;
	
	public static void main(String[]args) {
		SwingUtilities.invokeLater(()->{
			AttackInsertForm aForm = new AttackInsertForm();
			
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
					JTextPane docShow = new JTextPane();
					try {
						docShow.setDocument(aForm.getAttackString());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					StyleContainer.SetFontMain(docShow);
					testF.setContentPane(docShow);
					aForm.dispose();
					testF.pack();
					testF.setSize(500, 500);
					testF.setVisible(true);
				});
			});
			test.start();
			
		});
	}
	
	public AttackInsertForm() {
		dmgVals = new HashMap<ReminderField, JComboBox>();
		finished = false;
		
		dmgTypes = new String[DamageTypes.values().length];
		int iter = 0;
		for(DamageTypes e : DamageTypes.values()) {
			dmgTypes[iter] = e.name();
			iter++;
		}
		
		BuildWindow(this.getContentPane());
		this.pack();
		this.setSize(this.getWidth() + 100, this.getHeight() + 100);
		
	}
	
	public void BuildWindow(Container cPane) {
		cPane.setLayout(new BorderLayout());
		
		BuildDetails(cPane);
		BuildAttackDetailsPane(cPane);
		
		JButton buildAttack = new JButton("Finish/Build Attack");
		StyleContainer.SetFontBtn(buildAttack);
		buildAttack.addActionListener(e ->{
			finished = checkFinished();
			if(finished) {
				this.dispose();
			}
		});
		cPane.add(buildAttack, BorderLayout.SOUTH);
	}
	
	private void BuildDetails(Container cPane) {
		JPanel hPane = new JPanel();
		hPane.setLayout(new GridLayout(0,1));
		cPane.add(hPane, BorderLayout.NORTH);
		
		title = new ReminderField("", "Attack name");
		StyleContainer.SetFontHeader(title);
		hPane.add(title);
		
		JPanel typePane = new JPanel();
		typePane.setLayout(new FlowLayout(FlowLayout.CENTER));
		hPane.add(typePane);
		
		mAttack = new JCheckBox("Melee Attack");
		mAttack.setSelected(true);
		mAttack.addActionListener(e ->{
			if(mAttack.isSelected()) {
				rAttack.setSelected(false);
				hAttack.setSelected(false);
				CardLayout cl = (CardLayout) rCPane.getLayout();
				cl.show(rCPane, "reach");
			}
		});
		mAttack.setFocusable(false);
		typePane.add(mAttack);
		
		rAttack = new JCheckBox("Ranged Attack");
		rAttack.setSelected(false);
		rAttack.setFocusable(false);
		rAttack.addActionListener(e ->{
			if(rAttack.isSelected()) {
				mAttack.setSelected(false);
				hAttack.setSelected(false);
				CardLayout cl = (CardLayout) rCPane.getLayout();
				cl.show(rCPane, "range");
			}
				
		});
		typePane.add(rAttack);
		
		hAttack = new JCheckBox("Hybrid Attack");
		hAttack.setSelected(false);
		hAttack.setFocusable(false);
		hAttack.addActionListener(e ->{
			if(hAttack.isSelected()){
				mAttack.setSelected(false);
				rAttack.setSelected(false);
				CardLayout cl = (CardLayout) rCPane.getLayout();
				cl.show(rCPane, "hybrid");
			}
		});
		typePane.add(hAttack);
	}

	private void BuildAttackDetailsPane(Container cPane) {
		JPanel attackPane = new JPanel();
		attackPane.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, Color.black));
		attackPane.setLayout(new BorderLayout());
		cPane.add(attackPane, BorderLayout.CENTER);
		
		JPanel attackConfigPane = new JPanel();
		attackConfigPane.setLayout(new BoxLayout(attackConfigPane, BoxLayout.Y_AXIS));
		attackPane.add(attackConfigPane, BorderLayout.NORTH);
		
		JPanel attackModPane = new JPanel();
		attackModPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		attackConfigPane.add(attackModPane, BorderLayout.NORTH);
		
		JLabel attackModLbl = new JLabel("Attack Modifier:");
		StyleContainer.SetFontHeader(attackModLbl);
		attackModPane.add(attackModLbl);
		
		attackMod = new ReminderField("Mod");
		attackMod.setNumbersOnly();
		attackMod.setColumns(8);
		StyleContainer.SetFontMain(attackMod);
		attackModPane.add(attackMod);
		
		JPanel reachPane = new JPanel();
		reachPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		attackConfigPane.add(reachPane);
		BuildRangeReachPane(reachPane);
		
		JPanel dmgGrid = new JPanel();
//		dmgGrid.setLayout(new GridLayout(0,1));
//		dmgGrid.setLayout(new BoxLayout(dmgGrid, BoxLayout.Y_AXIS));
		dmgGrid.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		gbc.weighty = .0001;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		
		JScrollPane dmgGridScroll = new JScrollPane(dmgGrid);
		dmgGridScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		dmgGridScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		attackPane.add(dmgGridScroll, BorderLayout.CENTER);
		
		JPanel bPane = new JPanel();
		bPane.setLayout(new BoxLayout(bPane, BoxLayout.LINE_AXIS));
		dmgGrid.add(bPane, gbc);
		
		JLabel baseLbl = new JLabel("Base Damage:");
		StyleContainer.SetFontHeader(baseLbl);
		bPane.add(baseLbl);
		
		baseDmg = new ReminderField("Base Damage");
		StyleContainer.SetFontMain(baseDmg);
		dmgVals.remove(baseDmg);
		bPane.add(baseDmg);
		
		baseDmgType = new JComboBox<String>();
		baseDmgType.setModel(new DefaultComboBoxModel<String>(dmgTypes));
		bPane.add(baseDmgType);
		
		
		JButton addDBtn = new JButton("Add Damage");
		StyleContainer.SetFontBtn(addDBtn);
		addDBtn.addActionListener(e ->{
			SwingUtilities.invokeLater(()->{
				JPanel dmgPane = new JPanel();
//				dmgPane.setLayout(new GridLayout(0, 3));
				dmgPane.setLayout(new BoxLayout(dmgPane, BoxLayout.LINE_AXIS));
				gbc.gridy ++;
				System.out.println(gbc.gridy);
				dmgGrid.add(dmgPane, gbc);
				
				ReminderField bnsDmg = new ReminderField("Bonus Damage");
				StyleContainer.SetFontMain(bnsDmg);
				dmgPane.add(bnsDmg);
				
				JComboBox<String> dmgType = new JComboBox<String>();
				dmgType.setModel(new DefaultComboBoxModel<String>(dmgTypes));
				dmgPane.add(dmgType);
				
				dmgVals.put(bnsDmg, dmgType);
				
				JButton delBtn = new JButton("Delete");
				StyleContainer.SetFontBtn(delBtn);
				delBtn.addActionListener(e2 ->{
					SwingUtilities.invokeLater(()->{
//						gbc.gridy --;
						System.out.println(gbc.gridy);
						dmgVals.remove(bnsDmg);
						dmgGrid.remove(dmgPane);
						
						dmgGrid.repaint();
						dmgGrid.revalidate();
					});

				});
				dmgPane.add(delBtn);
				dmgGrid.repaint();
				dmgGrid.revalidate();
				bnsDmg.requestFocus();
			});
		});
		attackPane.add(addDBtn, BorderLayout.SOUTH);
	}
	
	private void BuildRangeReachPane(Container dmgGrid) {
		rCPane = new JPanel();
		rCPane.setLayout(new CardLayout());
		dmgGrid.add(rCPane);
		
		
		JPanel rPane = new JPanel();
//		rPane.setLayout(new BoxLayout(rPane, BoxLayout.LINE_AXIS));
		rPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		rCPane.add(rPane, "reach");
		
		JLabel reachLbl = new JLabel("Reach:");
		StyleContainer.SetFontHeader(reachLbl);
		rPane.add(reachLbl);
		
		reach = new ReminderField("Reach...");
		reach.setNumbersOnly();
		reach.setColumns(8);
		StyleContainer.SetFontMain(reach);
		rPane.add(reach);
		
		
		JPanel rngPane = new JPanel();
		rngPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		rCPane.add(rngPane, "range");
		
		JLabel rangeLbl = new JLabel("Range:");
		StyleContainer.SetFontHeader(rangeLbl);
		rngPane.add(rangeLbl);
		
		rangeLow = new ReminderField("Low");
		rangeLow.setNumbersOnly();
		rangeLow.setColumns(8);
		StyleContainer.SetFontMain(rangeLow);
		rngPane.add(rangeLow);
		
		rangeHigh = new ReminderField("High");
		rangeHigh.setNumbersOnly();
		rangeHigh.setColumns(8);
		StyleContainer.SetFontMain(rangeHigh);
		rngPane.add(rangeHigh);
		
		JPanel hPane = new JPanel();
		hPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		rCPane.add(hPane, "hybrid");
		
		JLabel rHLabel = new JLabel("Reach:");
		StyleContainer.SetFontHeader(rHLabel);
		hPane.add(rHLabel);
		
		ReminderField hRField = new ReminderField("Reach...");
		hRField.setDocument(reach.getDocument());
		hRField.setNumbersOnly();
		hRField.setColumns(6);
		StyleContainer.SetFontMain(hRField);
		hPane.add(hRField);
		
		JLabel rngHLbl = new JLabel("Range:");
		StyleContainer.SetFontHeader(rngHLbl);
		hPane.add(rngHLbl);
		
		ReminderField rHLow = new ReminderField("Low");
		rHLow.setDocument(rangeLow.getDocument());
		rHLow.setNumbersOnly();
		rHLow.setColumns(6);
		StyleContainer.SetFontMain(rHLow);
		hPane.add(rHLow);
		
		ReminderField rHHigh = new ReminderField("High");
		rHHigh.setNumbersOnly();
		rHHigh.setDocument(rangeHigh.getDocument());
		rHHigh.setColumns(6);
		StyleContainer.SetFontMain(rHHigh);
		hPane.add(rHHigh);
		
	}
	
	public StyledDocument getAttackString() throws BadLocationException {
		StyledDocument doc = new DefaultStyledDocument();
		
		StyleContext context = new StyleContext();
        AttributeSet bold = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Bold, true);
        AttributeSet italic = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
        
        doc.insertString(doc.getLength(), title.getText() + ".", bold);
        if(mAttack.isSelected()) {
        	 doc.insertString(doc.getLength(), " Melee Attack Roll: ", italic);
        }else if(rAttack.isSelected()) {
        	 doc.insertString(doc.getLength(), " Ranged Attack Roll: ", italic);
        }else {
        	 doc.insertString(doc.getLength(), " Melee or Ranged Attack Roll: ", italic);
        }
       
        
        String descModRch = "+" + attackMod.getText();
        if(mAttack.isSelected()) {
        	descModRch +=  ", reach " + reach.getText() + "ft. ";
        }else if(rAttack.isSelected()){
        	if(Integer.parseInt(rangeLow.getText()) != Integer.parseInt(rangeHigh.getText()))
        		descModRch += ", range " + rangeLow.getText() + "/" + rangeHigh.getText() + "ft. ";
        	else
        		descModRch += ", range " + rangeHigh.getText() + "ft. ";
        }else {
        	descModRch +=  ", reach " + reach.getText() + "ft. ";
        	if(Integer.parseInt(rangeLow.getText()) != Integer.parseInt(rangeHigh.getText()))
        		descModRch += "range " + rangeLow.getText() + "/" + rangeHigh.getText() + "ft. ";
        	else
        		descModRch += "range " + rangeHigh.getText() + "ft. ";
        }
        
        doc.insertString(doc.getLength(), descModRch, null);
        doc.insertString(doc.getLength(), "Hit:", italic);
        
        String descMain = " " + baseDmg.getText() +" " + baseDmgType.getSelectedItem() + " damage";
        if(dmgVals.keySet().size() > 0) {
        	descMain += " plus ";
        	for(ReminderField r : dmgVals.keySet()) {
            	descMain += r.getText() + " " + dmgVals.get(r).getSelectedItem() + " damage and ";
            }
        	
        	descMain = descMain.substring(0, descMain.length() - 5);
        }
        descMain += ".";
        doc.insertString(doc.getLength(), descMain, null);
		
		return doc;
	}
	
	private boolean checkFinished() {
		boolean r;
		if(mAttack.isSelected()) {
			r = reach.getText().length() > 0;
		}else if(rAttack.isSelected()){
			r = rangeLow.getText().length() > 0 && 
					rangeHigh.getText().length() > 0;
		}else {
			r = reach.getText().length() > 0 &&
					rangeLow.getText().length() > 0 &&
					rangeHigh.getText().length() > 0;
		}
		System.out.println(r);
//		boolean r = ((mAttack.isSelected()) ? reach.getText().length() > 0 : 
//				rangeLow.getText().length() > 0 && rangeHigh.getText().length() > 0);
		
		boolean dmg = true;
		for(ReminderField rem : dmgVals.keySet()) {
			if(rem.getText().length() <= 0)
				dmg = false;
		}
		
		return (title.getText().length() > 0 && attackMod.getText().length() > 0 &&
				baseDmg.getText().length() > 0 && r && dmg);
		
		
	}
}