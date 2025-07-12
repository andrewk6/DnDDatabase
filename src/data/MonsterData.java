package data;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.StyledDocument;

public class MonsterData {
	public String name, type, ac, hp, spd, dmgResist, dmgVuln, invuln, senses, lang;

	public int initBns, str, dex, con, inte, wis, cha, lActsNum, lActsBns;

	public double cr;

	public boolean initProf, initExp, strSProf, dexSProf, conSProf, intSProf, wisSProf, chaSProf;

	public StyledDocument traits, acts, bActs, lActs, reActs;

	public HashMap<DataContainer.Skills, DataContainer.Proficiency> skills;

	public ArrayList<String> tags;
}
