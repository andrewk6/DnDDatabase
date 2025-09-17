package data.players;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.text.StyledDocument;

import data.Feat;
import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.DataContainer.Source;
import data.items.Item;

public class Background implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895794921904291263L;
	
	public String name;
	public StyledDocument desc;
	public Source src;
	
	public Abilities[] statBonus = new Abilities[3];
	public Feat startFeat;
	public ArrayList<Skills> skills;
	public String toolProf;
	
	public ArrayList<Item> startEquip;
	public int startGoldWithEquip;
	
	public final int noEquipGold = 50;
}