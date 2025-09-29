package gui.gui_helpers;

import javax.swing.*;
import javax.swing.text.*;

import data.DataContainer;
import data.Rule;
import data.Spell;
import gui.ItemIFrame;
import gui.campaign.PartyIFrame;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import gui.monsters.MonsterDispPane;
import utils.ErrorLogger;
import data.Monster;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

@SuppressWarnings("serial")
public class HoverTextPane extends JTextPane {
    private final Map<String, Rule> ruleMap;
    private final Map<String, Spell> spellMap;
    private final Map<String, Monster> monsterMap;
    
    private final DataContainer data;
    private final JDesktopPane desktop;
    private final GuiDirector gd;
    private final JPopupMenu popup;
    private final JTextPane popupTextPane;
    private final JScrollPane popupScroll;
    
//    private MonsterIFrame monstTabs;
//    private SpellIFrame spellTabs;
//    private RuleIFrame ruleTabs;

    public HoverTextPane(DataContainer d, GuiDirector gD, JDesktopPane desktop) {
        this.data = d;
        this.ruleMap = d.getRules();
        this.spellMap = d.getSpells();
        this.monsterMap = d.getMonsters();
        
        this.desktop = desktop;
        this.gd = gD;
        
        setEditable(false);
        setHighlighter(null);
        setFont(StyleContainer.FNT_BODY_PLAIN);
        setFocusable(false);

        popupTextPane = new JTextPane();
        popupTextPane.setEditable(false);
        popupTextPane.setOpaque(true);
        popupTextPane.setBackground(new Color(255, 255, 230));

        popupScroll = new JScrollPane(popupTextPane);
        popupScroll.setPreferredSize(new Dimension(300, 200));

        popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        popup.setLayout(new BorderLayout());
        popup.add(popupScroll, BorderLayout.CENTER);
        popup.pack();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseHover(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                popup.setVisible(false);
            }
        });

        popupScroll.addMouseWheelListener(MouseEvent::consume);
    }
    
//    public void SetMonsterTabbedPane(MonsterIFrame mPane) {
//    	monstTabs = mPane;
//    }
//    public void SetSpellTabbedPane(SpellIFrame mPane) {
//    	spellTabs = mPane;
//    }
//    public void SetRuleTabbedPane(RuleIFrame mPane) {
//    	ruleTabs = mPane;
//    }
    
    public boolean getScrollableTracksViewportWidth() {
		return true;
    }

    public void setDocument(StyledDocument doc) {
        setStyledDocument(doc);
        setCaretPosition(0);
        setEditable(false);
    }

    private void handleMouseHover(MouseEvent e) {
        int pos = viewToModel2D(e.getPoint());
        if (pos >= 0) {
            StyledDocument doc = getStyledDocument();
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attr = elem.getAttributes();

            String ruleName = (String) attr.getAttribute("ruleLink");
            String spellName = (String) attr.getAttribute("spelllink");
            String monsterName = (String) attr.getAttribute("monstlink");

            popup.setVisible(false); // hide first in case of content swap

            if (ruleName != null && ruleMap.containsKey(ruleName)) {
                popupScroll.setViewportView(popupTextPane); // ensure text pane is used
                popupTextPane.setStyledDocument(cloneDocument(ruleMap.get(ruleName).ruleDoc));
//                showPopup(e);
                return;
            }
            if (spellName != null && spellMap.containsKey(spellName)) {
                popupScroll.setViewportView(popupTextPane); // ensure text pane is used
                popupTextPane.setStyledDocument(cloneDocument(spellMap.get(spellName).spellDoc));
//                showPopup(e);
                return;
            }
            if (monsterName != null && monsterMap.containsKey(monsterName)) {
                Monster monster = monsterMap.get(monsterName);
                MonsterDispPane monsterPanel = new MonsterDispPane(monster, data, gd);
                monsterPanel.setPreferredSize(new Dimension(300, 200));
                popupScroll.setViewportView(monsterPanel); // swap to monster panel
//                showPopup(e);
                return;
            }
        }

        popup.setVisible(false);
    }

    @SuppressWarnings("unused")
	private void showPopup(MouseEvent e) {
        Point screenLoc = e.getLocationOnScreen();
        popup.setLocation(screenLoc.x, screenLoc.y );
        popup.setVisible(true);
        popup.show(this, screenLoc.x, screenLoc.y);
    }

    private void handleMouseClick(MouseEvent e) {
        int pos = viewToModel2D(e.getPoint());
        if (pos >= 0) {
            StyledDocument doc = getStyledDocument();
            Element elem = doc.getCharacterElement(pos);
            AttributeSet attr = elem.getAttributes();

            String ruleName = (String) attr.getAttribute("ruleLink");
            String spellName = (String) attr.getAttribute("spellLink");
            String monsterName = (String) attr.getAttribute("monstLink");
            String itemName = (String) attr.getAttribute("itemLink");
            String playerName = (String) attr.getAttribute("playerLink");
            String featName = (String) attr.getAttribute("featLink");
            String className = (String) attr.getAttribute("classLink");
            String speciesName = (String)attr.getAttribute("speciesLink");
            String backgroundName = (String)attr.getAttribute("backgroundLink");
            String hazardName = (String)attr.getAttribute("hazardLink");
            
			if (itemName != null || playerName != null) {
				if (itemName != null) {
					if (gd.getIFrame() == null) {
						ItemIFrame iFrame = new ItemIFrame(data, gd, desktop);
						desktop.add(iFrame);
						gd.RegisterFrame(iFrame);
					}
					if (data.getItems().keySet().contains(itemName)||
							data.getVehicles().keySet().contains(itemName)||
							data.getSiegeEquipment().keySet().contains(itemName)) {
						gd.handleFrame(itemName, true);
					}
				} else {
					if (gd.getPFrame() == null) {
						PartyIFrame pFrame = new PartyIFrame(data, gd);
						desktop.add(pFrame);
						gd.RegisterFrame(pFrame);
					}
					gd.handleFrame(playerName, false);
				}
			} else if (gd.getComboFrame() != null) {
				if (ruleName != null)
					gd.getComboFrame().AddTab(data.getRules().get(ruleName));
				if (spellName != null)
					gd.getComboFrame().AddTab(data.getSpells().get(spellName));
				if (monsterName != null)
					gd.getComboFrame().AddTab(data.getMonsters().get(monsterName));
				if (featName != null)
					gd.getComboFrame().AddTab(data.getFeats().get(featName));
				if (className != null)
					gd.getComboFrame().AddTab(data.getClasses().get(className));
				if (speciesName != null)
					gd.getComboFrame().AddTab(data.getSpecies().get(speciesName));
				if (backgroundName != null)
					gd.getComboFrame().AddTab(data.getBackgrounds().get(backgroundName));
				if (hazardName != null)
					gd.getComboFrame().AddTab(data.getHazards().get(hazardName));
			}else {
				if (ruleName != null) {
					if(gd.getrFrame() != null) {
                		gd.getrFrame().AddTab(ruleName);
                		gd.popRFrame();
					}
				}
				if (spellName != null)
					if(gd.getsFrame() != null) {
                		gd.getsFrame().AddSpellTab(spellName);
                		gd.popSFrame();
					}
				if (monsterName != null)
					if(gd.getmFrame() != null) {
                		gd.getmFrame().AddMonsterPane(monsterName);
                		gd.popMFrame();
					}
				if (featName != null)
					if(gd.getFFrame() != null) {
                		gd.getFFrame().AddFeatTab(featName);
                		gd.popFFrame();
					}
				if (className != null)
					if(gd.getClFrame() != null) {
                		gd.getClFrame().AddTab(data.getClasses().get(className));
                		gd.popClFrame();
					}
				if (speciesName != null)
					if(gd.getSpFrame() != null) {
                		gd.getSpFrame().AddTab(data.getSpecies().get(speciesName));
                		gd.popSpFrame();
					}
				if (backgroundName != null)
					if(gd.getBFrame() != null) {
                		gd.getBFrame().AddTab(data.getBackgrounds().get(backgroundName));
                		gd.popBFrame();
					}
				if (hazardName != null)
					if(gd.getHFrame() != null) {
                		gd.getHFrame().AddTab(data.getHazards().get(hazardName));
                		gd.popHFrame();
					}
			}

            /*
            String combo = ruleName + spellName + monsterName + 
            		playerName + featName + className + speciesName +
            		backgroundName;
            combo = combo.replace("null", "");
            if(itemName != null || playerName != null) {
            	if(itemName != null) {
            		if(gd.getIFrame() == null) {
            			ItemIFrame iFrame = new ItemIFrame(data, gd, desktop);
                		desktop.add(iFrame);
                		gd.RegisterFrame(iFrame);
            		}
            		if(data.getMagicItemKeysSorted().contains(itemName)) {
            			gd.handleFrame(itemName, true);
            		}
            	}
            	else {
            		if(gd.getPFrame() == null) {
            			PartyIFrame pFrame = new PartyIFrame(data, gd);
                		desktop.add(pFrame);
                		gd.RegisterFrame(pFrame);
            		}
            		gd.handleFrame(playerName, false);
            	}
            }else if(gd.getComboFrame() != null && combo.length() > 0) {
            	if(ruleName != null && spellName == null && monsterName == null
            			&& featName == null && className == null && speciesName == null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getRules().get(ruleName));
            	else if(ruleName == null && spellName != null && monsterName == null
            			&& featName == null && className == null && speciesName == null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getSpells().get(spellName));
            	else if(ruleName == null && spellName == null && monsterName != null
            			&& featName == null && className == null && speciesName == null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getMonsters().get(monsterName));
            	else if(ruleName == null && spellName == null && monsterName == null
            			&& featName != null && className == null && speciesName == null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getFeats().get(featName));
            	else if(ruleName == null && spellName == null && monsterName == null
            			&& featName == null && className != null && speciesName == null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getClasses().get(className));
            	else if(ruleName == null && spellName == null && monsterName == null
            			&& featName == null && className == null && speciesName != null
            			&& backgroundName == null)
            		gd.getComboFrame().AddTab(data.getSpecies().get(speciesName));
            	else if(ruleName == null && spellName == null && monsterName == null
            			&& featName == null && className == null && speciesName == null
            			&& backgroundName != null)
            		gd.getComboFrame().AddTab(data.getBackgrounds().get(backgroundName));
            	else if(gd.getComboFrame() instanceof ComboIFrame)
            		((ComboIFrame) gd.getComboFrame()).AddTabDirector(combo);
            }else {
            	if (ruleName != null && ruleMap.containsKey(ruleName)) {
                	if(gd.getrFrame() != null) {
                		gd.getrFrame().AddTab(ruleName);
                		gd.popRFrame();
                	}else {
                		desktop.add(new RuleIFrame(data, gd, desktop));
                		desktop.revalidate();
                		desktop.repaint();
                		gd.getrFrame().AddTab(ruleName);
                	}
                } else if (spellName != null && spellMap.containsKey(spellName)) {
                	if(gd.getsFrame() != null) {
                		gd.getsFrame().AddSpellTab(spellName);
                		gd.popSFrame();
                	}else {
                		desktop.add(new SpellIFrame(data, gd, desktop));
                		desktop.revalidate();
                		desktop.repaint();
                		gd.getsFrame().AddSpellTab(spellName);
                	}
                } else if (monsterName != null && monsterMap.containsKey(monsterName)) {
//                    openMonsterFrame(monsterMap.get(monsterName), monsterName);
                	if(gd.getmFrame() != null) {
                		gd.getmFrame().AddMonsterPane(monsterName);
                		gd.popMFrame();
                	}else {
                		desktop.add(new MonsterIFrame(data, gd, desktop));
                		desktop.revalidate();
                		desktop.repaint();
                		gd.getmFrame().AddMonsterPane(monsterName);
                	}
                }else if(featName != null && data.getFeats().containsKey(featName)) {
                	if(gd.getFFrame() != null) {
                		gd.getFFrame().AddFeatTab(featName);
                		gd.popFFrame();
                	}
                }else if(className != null && data.getClasses().containsKey(className)) {
                	if(gd.getClFrame() != null) {
                		gd.getClFrame().AddTab(data.getClasses().get(className));
                		gd.popClFrame();
                	}
                }else if(speciesName != null && data.getSpecies().containsKey(speciesName)) {
                	if(gd.getSpFrame() != null) {
                		gd.getSpFrame().AddTab(data.getSpecies().get(speciesName));
                		gd.popSpFrame();
                	}
                }else if(backgroundName != null && data.getBackgrounds().containsKey(backgroundName)) {
                	if(gd.getBFrame() != null) {
                		gd.getBFrame().AddTab(data.getBackgrounds().get(backgroundName));
                		gd.popBFrame();
                	}
                }
            }*/
        }
    }

    private StyledDocument cloneDocument(StyledDocument original) {
        DefaultStyledDocument copy = new DefaultStyledDocument();
        try {
            copy.insertString(0, original.getText(0, original.getLength()),
                    original.getCharacterElement(0).getAttributes());
        } catch (BadLocationException e) {
        	ErrorLogger.log(e);
            e.printStackTrace();
        }
        return copy;
    }
}