package gui.gui_helpers.structures;

import javax.swing.JTabbedPane;

import data.Feat;
import data.Monster;
import data.Rule;
import data.Spell;

public interface AllTab extends ContentTab
{
//	public JTabbedPane GetTabs();
	
	public void AddTab(Monster m);
	public void AddTab(Spell m);
	public void AddTab(Rule m);
	public void AddTab(Feat f);
}