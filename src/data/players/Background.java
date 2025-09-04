package data.players;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.StyledDocument;

import data.Feat;
import data.DataContainer.Abilities;
import data.DataContainer.Skills;
import data.items.Item;

public class Background implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895794921904291263L;
	
	public String name;
	public StyledDocument desc;
	
	public Abilities[] statBonus = new Abilities[3];
	public Feat startFeat;
	public ArrayList<Skills> skills;
	public String toolProf;
	
	public HashMap<Item, Integer> startEquip;
}