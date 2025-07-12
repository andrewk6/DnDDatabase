package data;


public class MonsterFactory {
	public static Monster BuildMonster(MonsterData d) {
		Monster m = new Monster();
		int[] stats = new int[] { d.str, d.dex, d.con, d.inte, d.wis, d.cha };

		boolean[] saveProfs = new boolean[] { d.strSProf, d.dexSProf, d.conSProf, d.intSProf, d.wisSProf, d.chaSProf };

		m.ac = d.ac;
		m.actions = d.acts;
		m.bonusActions = d.bActs;
		m.cr = d.cr;
		m.hp = d.hp;
		m.init = GetInit(d.dex, d.cr, d.initBns, d.initProf, d.initExp);
		m.lActBns = d.lActsBns;
		m.lActNum = d.lActsNum;
		m.languages = d.lang;
		m.legendActions = d.lActs;
		m.name = d.name;
		m.reactions = d.reActs;
//		m.saves = GetSaveArray(stats, saveProfs);
		m.saves = saveProfs;
		m.senses = d.senses;
		m.skills = d.skills;
		m.speed = d.spd;
		m.stats = stats;
		m.tags = d.tags;
		m.traits = d.traits;
		m.typeSizeAlignment = d.type;
		m.dmgRes = d.dmgResist;
		m.dmgVul = d.dmgVuln;
		m.immune = d.invuln;
		return m;
	}

	public static String GetInit(int dex, double cr, int bns, boolean prof, boolean expt) {
		String init = "";
		int profBns;
		if (prof)
			profBns = Monster.ProfCRCalc(cr);
		else if (expt)
			profBns = Monster.ProfCRCalc(cr) * 2;
		else
			profBns = 0;
		int initScore = Monster.AbilityModCalc(dex) + bns + profBns;

		init += (initScore > 0) ? "+" + initScore + "(" + (10 + initScore) + ")" : 
			initScore + "(" + (10 + initScore) + ")";
//		init += ;
		return init;
	}
}