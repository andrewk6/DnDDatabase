package gui.gui_helpers;

import javax.swing.*;
import javax.swing.text.*;

import data.DataContainer;
import data.Rule;
import data.Spell;
import gui.ComboIFrame;
import gui.ItemIFrame;
import gui.MonsterIFrame;
import gui.RuleIFrame;
import gui.SpellIFrame;
import gui.campaign.PartyIFrame;
import gui.gui_helpers.structures.GuiDirector;
import gui.gui_helpers.structures.StyleContainer;
import data.Monster;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

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
    
    private MonsterIFrame monstTabs;
    private SpellIFrame spellTabs;
    private RuleIFrame ruleTabs;

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
    
    public void SetMonsterTabbedPane(MonsterIFrame mPane) {
    	monstTabs = mPane;
    }
    public void SetSpellTabbedPane(SpellIFrame mPane) {
    	spellTabs = mPane;
    }
    public void SetRuleTabbedPane(RuleIFrame mPane) {
    	ruleTabs = mPane;
    }
    
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
            
            String combo = ruleName + spellName + monsterName;
            combo = combo.replace("null", "");
            if(itemName != null|| playerName != null) {
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
            	if(ruleName != null && spellName == null && monsterName == null)
            		gd.getComboFrame().AddTab(data.getRules().get(ruleName));
            	else if(ruleName == null && spellName != null && monsterName == null)
            		gd.getComboFrame().AddTab(data.getSpells().get(spellName));
            	else if(ruleName == null && spellName == null && monsterName != null)
            		gd.getComboFrame().AddTab(data.getMonsters().get(monsterName));
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
                }
            }
        }
    }

    private StyledDocument cloneDocument(StyledDocument original) {
        DefaultStyledDocument copy = new DefaultStyledDocument();
        try {
            copy.insertString(0, original.getText(0, original.getLength()),
                    original.getCharacterElement(0).getAttributes());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return copy;
    }
}