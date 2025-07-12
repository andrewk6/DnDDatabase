package data.campaign;

import java.io.Serializable;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import data.DataContainer;
import data.DataContainer.PlayerClass;

public class Player implements Serializable{
	private static final long serialVersionUID = -7556431587426395621L;
	
	public String name;
	public int passivePerc, maxHP, ac;
	public PlayerClass pClass;
	public StyledDocument note;
	
	public Player() {
		name = "";
		passivePerc = ac = maxHP = 0;
		pClass = PlayerClass.None;
		note = new DefaultStyledDocument();
	}
	
	public Player(String name) {
		this.name = name;
		passivePerc = ac = maxHP = 0;
		pClass = PlayerClass.None;
		note = new DefaultStyledDocument();
	}
	
	public Player(String n, int pP, int mH, int ac, PlayerClass c, StyledDocument note) {
		this.name = n;
		this.passivePerc = pP;
		this.maxHP = mH;
		this.ac = ac;
		this.pClass = c;
		this.note = note;
	}
}