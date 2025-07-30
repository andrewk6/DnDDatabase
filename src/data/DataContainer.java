package data;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import data.campaign.Campaign;
import data.campaign.Player;
import data.items.Armor;
import data.items.Gear;
import data.items.Item;
import data.items.MagicItem;
import data.items.ToolSet;
import data.items.Weapon;
import gui.gui_helpers.structures.LoadListener;

public class DataContainer {
	public enum Skills {
		Athletics, Acrobatices, SleightofHand, Stealth, Arcana, History, Investigation, Nature, Religion,
		AnimalHandling, Insight, Medecine, Perception, Survival, Deception, Intimidation, Performance, Persuasion

	}
	
	public enum Proficiency {
		None, Profieient, Expertise
	}
	
	public enum DamageTypes {
		Acid, Bludgeoning, Cold, Fire, Force, Lightning, Necrotic, 
		Piercing, Poison, Psychic, Radiant, Slashing, Thunder
		
	}
	
	public enum ConditionTypes {
		 Blinded, Charmed, Deafened, Exhaustion, Frightened, Grappled, Incapacitated, Invisible,
		 Paralyzed, Petrified, Poisoned, Prone, Restrained, Slowed, Stunned, Unconscious
	}
	
	public enum Abilities {
		Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma
	}
	
	public enum Source{
		PlayersHandbook2024, DungeonMastersGuide2024, MonsterManual2024, VecnaEveOfRuin, Custom
	}
	
	public enum PlayerClass{
		Artificer, Barbarian, Bard, Cleric, Druid, Fighter, Monk, Paladin, Ranger, Rogue, Sorcerer, Warlock, Wizard,
		Custom, None
	}
	
	public static final File appLocal = new File(System.getenv("LOCALAPPDATA") + "\\DnD Database");
	public static final File dbFolder = new File(appLocal + "\\Databases");
	
	public static final String RULES_FILE_NAME = "Rules.xol";
	public static final String SPELLS_FILE_NAME = "Spells.sol";
	public static final String MONSTERS_FILE_NAME = "Monster.mol";
	public static final String INSERT_FILE_NAME = "Inserts.bol";
	public static final String ITEMS_FILE_NAME = "Items.iol";
	public static final String FEATS_FILE_NAME = "Feats.fol";
	public static final String CONFIG_FILE_NAME = "Config.confol";
	public static final String EXTRAS_FILE_NAME = "Extras.exol";
	
	public static final int RULES = 0;
	public static final int SPELLS = 1;
	public static final int MONSTERS = 2;
	public static final int INSERTS  = 3;
	public static final int ITEMS = 4;
	public static final int CAMPAIGN = 5;
	public static final int FEATS = 6;

	private HashMap<String, Rule> ruleMap;
	private HashMap<String, Spell> spellMap;
	private HashMap<String, Monster> monstMap;
	private HashMap<String, Item> itemMap;
	private HashMap<String, Feat> featMap;
	private HashMap<String, StyledDocument> insertMap;
	private Campaign camp;
	
	private ArrayList<String> ruleKeysSorted, spellKeysSorted, monstKeysSorted, insertKeysSorted,
		weaponKeysSorted, armorKeysSorted, gearKeysSorted, toolKeysSorted, magicItemKeysSorted, featKeysSorted;
	

	private final AtomicInteger runningTasks = new AtomicInteger(0);
	private volatile boolean isRunning = true;
	private final BlockingQueue<Runnable> ioQueue = new LinkedBlockingQueue<>();
	
	
	private final List<DataChangeListener> updateListeners = new ArrayList<DataChangeListener>();
	private final List<LoadListener> loadListeners = new ArrayList<LoadListener>();
	private Queue<File> recentFiles;
	
	private String lastCampPath;
	private boolean initiatlized;

	public DataContainer() {
		StartIOThread();
		System.out.println(dbFolder.exists() + "/" + appLocal.exists());
	}
	
	public void init() {
		ImportRuleMap();
		ImportSpellMap();
		ImportMonsters();
		ImportInsertHelpers();
		ImportItems();
		ImportFeats();
		
		SortKeys();
		LoadConfig();
		loadFinsihed();
		initiatlized = true;
	}
	
	public void registerLoadListener(LoadListener loader) {
		loadListeners.add(loader);
	}
	
	public void loadFinsihed() {
		for(LoadListener l : loadListeners) {
			l.onDataLoaded();
		}
		
		loadListeners.clear();
	}
	
	private void StartIOThread() {
		Thread worker = new Thread(() -> {
	        while (isRunning || !ioQueue.isEmpty()) {
	            try {
	                Runnable task = ioQueue.take();
	                runningTasks.incrementAndGet();
	                try {
	                    task.run();
	                } finally {
	                    runningTasks.decrementAndGet();
	                    System.out.println("Save complete");
	                }
	            } catch (InterruptedException e) {
	                // Thread interrupted: check if should stop
	                if (!isRunning) break;
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    });
	    worker.setDaemon(false); // make it non-daemon so JVM waits for it to finish
	    worker.start();
	}

	private boolean ImportInsertHelpers() {
		File insertHFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				insertHFile = new File(dbFolder.getPath() + File.separator + INSERT_FILE_NAME);
			}else {
				insertHFile = new File(INSERT_FILE_NAME);
			}
		}else
			insertHFile = new File(INSERT_FILE_NAME);
		System.out.println(insertHFile.getAbsolutePath());
		if(insertHFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(insertHFile))) {
				insertMap = (HashMap<String, StyledDocument>) ois.readObject();
				ois.close();
				insertKeysSorted = new ArrayList<String>();
				for(String k : insertMap.keySet()) {
					insertKeysSorted.add(k);
				}
				return true;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}else {
			try {
				insertHFile.createNewFile();
				insertMap = new HashMap<String, StyledDocument>();
				insertKeysSorted = new ArrayList<String>();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean ImportRuleMap() {
		File rulesFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				rulesFile = new File(dbFolder.getPath() + File.separator + RULES_FILE_NAME);
			}else {
				rulesFile = new File(RULES_FILE_NAME);
			}
		}else {
			rulesFile = new File(RULES_FILE_NAME);
		}
		
		insertMap = new HashMap<String, StyledDocument>();
		if (rulesFile.exists()) {
			ruleMap = new HashMap<String, Rule>();
			ruleKeysSorted = new ArrayList<String>();
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rulesFile))) {
				while (true) {
					try {
						Rule obj = (Rule) ois.readObject();
						ruleMap.put(obj.name, obj);
						ruleKeysSorted.add(obj.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			ruleMap = new HashMap<String, Rule>();
			return false;
		}
	}

	private boolean ImportSpellMap() {
		spellMap = new HashMap<String, Spell>();
		spellKeysSorted = new ArrayList<String>();
		File spellFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				spellFile = new File(dbFolder.getPath() + File.separator + SPELLS_FILE_NAME);
			}else {
				spellFile = new File(SPELLS_FILE_NAME);
			}
		}else {
			spellFile = new File(SPELLS_FILE_NAME);
		}
		if (spellFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(spellFile))) {
				while (true) {
					try {
						Spell s = (Spell) ois.readObject();
						spellMap.put(s.name, s);
						spellKeysSorted.add(s.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	private boolean ImportMonsters() {
		monstMap = new HashMap<String, Monster>();
		monstKeysSorted = new ArrayList<String>();
//		MonstTestData();
		File monstFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				monstFile = new File(dbFolder.getPath() + File.separator + MONSTERS_FILE_NAME);
			}else {
				monstFile = new File(MONSTERS_FILE_NAME);
			}
		}else {
			monstFile = new File(MONSTERS_FILE_NAME);
		}
		
		if (monstFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(monstFile))) {
				while (true) {
					try {
						Monster m = (Monster) ois.readObject();
						monstMap.put(m.name, m);
						monstKeysSorted.add(m.name);
					} catch (EOFException eof) {
						// End of file reached
						
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;		
	}
	
	private boolean ImportFeats() {
		featMap = new HashMap<String, Feat>();
		featKeysSorted = new ArrayList<String>();
		File featFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				featFile = new File(dbFolder.getPath() + File.separator + FEATS_FILE_NAME);
			}else {
				featFile = new File(FEATS_FILE_NAME);
			}
		}else {
			featFile = new File(FEATS_FILE_NAME);
		}
		
		if (featFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(featFile))) {
				System.out.println("Loading Feats:");
				while (true) {
					try {
						Feat f = (Feat) ois.readObject();
						System.out.println(f.name);
						featMap.put(f.name, f);
						featKeysSorted.add(f.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;		
	}
	
	private boolean ImportItems() {
		itemMap = new HashMap<String, Item>();
		weaponKeysSorted = new ArrayList<String>();
		armorKeysSorted = new ArrayList<String>();
		gearKeysSorted = new ArrayList<String>();
		toolKeysSorted = new ArrayList<String>();
		magicItemKeysSorted = new ArrayList<String>();
		
		File itemFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				itemFile = new File(dbFolder.getPath() + File.separator + ITEMS_FILE_NAME);
			}else {
				itemFile = new File(ITEMS_FILE_NAME);
			}
		}else {
			itemFile = new File(ITEMS_FILE_NAME);
		}
		if(itemFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(itemFile))){
				while(true) {
					try {
						Item i = (Item) ois.readObject();
						itemMap.put(i.name, i);
						switch(i) {
						case Weapon w -> {weaponKeysSorted.add(w.name);}
						case Armor a -> {armorKeysSorted.add(a.name);}
						case Gear g -> {gearKeysSorted.add(g.name);}
						case ToolSet t -> {toolKeysSorted.add(t.name);}
						case MagicItem m -> {magicItemKeysSorted.add(m.name);}
						default -> throw new IllegalArgumentException("Unexpected value: " + i);
						}
					}catch(EOFException eof) {
						System.out.println("Finished Load Items");
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private void notifyChange() {
		for(DataChangeListener tar : updateListeners)
			tar.onMapUpdated();
	}
	
	private void notifyChange(int mapType) {
		for(DataChangeListener tar : updateListeners) {
			tar.onMapUpdated(mapType);
		}
	}
	
	public void registerListener(DataChangeListener tar) {
		updateListeners.add(tar);
	}
	
	public void deregisterListener(DataChangeListener tar) {
		updateListeners.remove(tar);
	}
	
	public void Exit() {
		if(initiatlized) {
			if(isCampaignLoaded())
				SafeSaveData(CAMPAIGN);
			shutDownAndWait();
			SaveConfig();
		}
		System.exit(0);
	}
	
//	public void AddMonster(Monster m) {
//		if(!monstMap.keySet().contains(m.name)) {
//			monstMap.put(m.name, m);
//			monstKeysSorted.add(m.name);
//			Collections.sort(monstKeysSorted);
//			notifyChange(DataContainer.MONSTERS);
//		}else {
//			int opt = JOptionPane.showConfirmDialog(null, "Insert and override: " + m.name, 
//					"Insert/Override Monster", JOptionPane.YES_NO_OPTION);
//			if(opt == JOptionPane.YES_OPTION) {
//				monstMap.put(m.name, m);
//				monstKeysSorted.add(m.name);
//				Collections.sort(monstKeysSorted);
//				notifyChange(DataContainer.MONSTERS);
//			}
//		}
//	}
	
	public void SetMonstersMap(Map<String, Monster> monstMap2) {
		this.monstMap = (HashMap<String, Monster>) monstMap2;
		this.monstKeysSorted = new ArrayList<String>(monstMap.keySet());
		SortKeys(DataContainer.MONSTERS);
		notifyChange(DataContainer.MONSTERS);
	}
	
	public void setInserts(HashMap<String, StyledDocument> in ) {
		insertMap = in;
		this.insertKeysSorted = new ArrayList<String>(insertMap.keySet());
		SortKeys(DataContainer.INSERTS);
		notifyChange(DataContainer.INSERTS);
	}

	public void setSpellMap(HashMap<String, Spell> spellMap) {
		this.spellMap = spellMap;
		this.spellKeysSorted = new ArrayList<String>(this.spellMap.keySet());
		SortKeys(DataContainer.SPELLS); 
		notifyChange(DataContainer.SPELLS);
	}
	
	public void setRuleMap(HashMap<String, Rule> ruleMap) {
		this.ruleMap = ruleMap;
		this.ruleKeysSorted = new ArrayList<String>(this.ruleMap.keySet());
		SortKeys(DataContainer.RULES);
		notifyChange(DataContainer.RULES);
	}
	
	public void setFeatMap(HashMap<String, Feat> featMap) {
		this.featMap = featMap;
		this.featKeysSorted = new ArrayList<String>(this.featMap.keySet());
		SortKeys(DataContainer.FEATS);
		notifyChange(DataContainer.FEATS);
	}
	
	public void SetItemMap(HashMap<String, Item> iMap) {
		this.itemMap = iMap;
		weaponKeysSorted = new ArrayList<String>();
		armorKeysSorted = new ArrayList<String>();
		gearKeysSorted = new ArrayList<String>();
		toolKeysSorted = new ArrayList<String>();
		magicItemKeysSorted = new ArrayList<String>();
		for(String s : itemMap.keySet()) {
			Item i = itemMap.get(s);
			switch(i) {
			case Weapon w: weaponKeysSorted.add(w.name); break;
			case Armor a: armorKeysSorted.add(a.name); break;
			case Gear g: gearKeysSorted.add(g.name); break;
			case ToolSet t: toolKeysSorted.add(t.name); break;
			case MagicItem m: magicItemKeysSorted.add(m.name); break;
			default: throw new IllegalArgumentException("Unexpected value: " + i);
			}
		}
		SortKeys(DataContainer.ITEMS);
		notifyChange(DataContainer.ITEMS);
	}
	
	public void SafeSaveData() {
		System.out.println("Safe Save Enter");
		ioQueue.offer(this::SaveData);
	}
	
	public void SafeSaveData(int mapType) {
		System.out.println("Safe Save Enter");
		ioQueue.offer(()->{
			SaveData(mapType);
		});
	}
	
	private boolean SaveData() {
		return SaveRules() && SaveSpells() && SaveMonsters() && 
				SaveInserts() && SaveItems() && SaveCampaign() && SaveFeats();
	}

	private boolean SaveData(int saveOpt) {
		if(saveOpt == RULES)
			return SaveRules();
		else if(saveOpt == SPELLS)
			return SaveSpells();
		else if(saveOpt == MONSTERS)
			return SaveMonsters();
		else if(saveOpt == INSERTS)
			return SaveInserts();
		else if(saveOpt == ITEMS)
			return SaveItems();
		else if(saveOpt == CAMPAIGN)
			return SaveCampaign();
		else if(saveOpt == FEATS)
			return SaveFeats();
		else throw  new IllegalArgumentException("Invalid save option");
//		System.out.println("Save Complete");
	}
	
	private boolean SaveInserts() {
		File inFile = new File(dbFolder.getPath() + File.separator + DataContainer.INSERT_FILE_NAME);
		System.out.println("Saving Inserts");
		if(!inFile.exists()) {
			try {
				inFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(inFile));
			oos.writeObject(insertMap);
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveRules() {
		File saveFile = new File(dbFolder.getPath() + File.separator + RULES_FILE_NAME);
	    if (!saveFile.exists()) {
	        try {
	            saveFile.createNewFile();
	        } catch (IOException e) {
	            System.err.println("Failed to create file: " + e.getMessage());
	            return false;
	        }
	    }

	    byte[] originalContents = null;

	    try {
	        originalContents = Files.readAllBytes(saveFile.toPath());
	    } catch (IOException e) {
	        System.err.println("Failed to read original file: " + e.getMessage());
	        return false;
	    }

	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
	        for (Iterator<Map.Entry<String, Rule>> it = ruleMap.entrySet().iterator(); it.hasNext(); ) {
	            Map.Entry<String, Rule> entry = it.next();
	            try {
	                oos.writeObject(entry.getValue());
	            } catch (IOException ex) {
	                System.err.println("Failed to serialize rule: " + entry.getKey() + " - removing it.");
	                it.remove(); // remove faulty rule
	            }
	        }
	        oos.flush();
	        return true;
	    } catch (IOException e) {
	        System.err.println("Serialization failed: " + e.getMessage());
	        try {
	            Files.write(saveFile.toPath(), originalContents); // restore
	            System.out.println("Original file restored.");
	        } catch (IOException ex) {
	            System.err.println("Failed to restore original file: " + ex.getMessage());
	        }
	        return false;
	    }
	}
	
	private boolean SaveSpells() {
		File spellFile = new File(dbFolder.getPath() + File.separator + DataContainer.SPELLS_FILE_NAME);
		if(!spellFile.exists()) {
			try {
				spellFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(spellFile));
			for (String s : spellMap.keySet()) {
				oos.writeObject(spellMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveMonsters() {
		File monstFile = new File(dbFolder.getPath() + File.separator + DataContainer.MONSTERS_FILE_NAME);
		if(!monstFile.exists()) {
			try {
				monstFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(monstFile));
			for (String s : monstMap.keySet()) {
				oos.writeObject(monstMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveFeats() {
		File featFile = new File(dbFolder.getPath() + File.separator + DataContainer.FEATS_FILE_NAME);
		if(!featFile.exists()) {
			try {
				featFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(featFile));
			for (String s : featMap.keySet()) {
				oos.writeObject(featMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveItems() {
		File itemFile = new File(dbFolder.getPath() + File.separator + DataContainer.ITEMS_FILE_NAME);
		if(!itemFile.exists()) {
			try {
				itemFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(itemFile));
			for(String s : itemMap.keySet()) {
				oos.writeObject(itemMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void shutDownAndWait() {
		System.out.println("Finishing saves before shutdown");
		isRunning = false;  // tell worker to stop after finishing current + queued tasks

	    // Wait until queue empty AND runningTasks == 0
	    while (!ioQueue.isEmpty() || runningTasks.get() > 0) {
	        try {
	            Thread.sleep(50);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }
	}
	
	public ArrayList<String> matchMonsterTag(String tag){
		ArrayList<String> matches = new ArrayList<String>();
		for(String s : monstMap.keySet())
			for(String t : monstMap.get(s).tags)
				if(t.toLowerCase().startsWith(tag.toLowerCase()))
					matches.add(s);
		return matches;
	}
	
	public ArrayList<String> getCustomMonsterKeys(){
		ArrayList<String> customs = new ArrayList<String>();
		for(String s : monstKeysSorted)
			if(monstMap.get(s).custom)
				customs.add(s);
		return customs;
	}
	
	public ArrayList<String> getMonsterKeysSource(Source searchSource){
		ArrayList<String> keys = new ArrayList<String>();
		for(String s : monstKeysSorted)
			if(monstMap.get(s).source == searchSource)
				keys.add(s);
		return keys;
	}

	private void SortKeys() {
		Collections.sort(spellKeysSorted);
		Collections.sort(ruleKeysSorted);
		Collections.sort(monstKeysSorted);
		Collections.sort(insertKeysSorted);
		
		Collections.sort(weaponKeysSorted);
		Collections.sort(armorKeysSorted);
		Collections.sort(gearKeysSorted);
		Collections.sort(toolKeysSorted);
		Collections.sort(magicItemKeysSorted);
		Collections.sort(featKeysSorted);
	}
	
	private void SortKeys(int mapType) {
		switch(mapType) {
		case DataContainer.SPELLS: Collections.sort(spellKeysSorted); break;
		case DataContainer.RULES: Collections.sort(ruleKeysSorted); break;
		case DataContainer.MONSTERS: Collections.sort(monstKeysSorted); break;
		case DataContainer.INSERTS: Collections.sort(insertKeysSorted); break;
		case DataContainer.ITEMS:
			Collections.sort(weaponKeysSorted);
			Collections.sort(armorKeysSorted);
			Collections.sort(gearKeysSorted);
			Collections.sort(toolKeysSorted);
			Collections.sort(magicItemKeysSorted);
			break;
		case DataContainer.FEATS: Collections.sort(featKeysSorted); break;
		default: throw new IllegalArgumentException("Invalid Map Type.");
		}
	}

	public boolean UpdateData() {
		boolean importStatus = ImportRuleMap() && ImportSpellMap();
		SortKeys();
		return importStatus;

	}
	
	public String getCampaignName() {
		return camp.saveLoc.getName();
	}
	
	public Map<String, Rule> getRules() {
		return Collections.unmodifiableMap(ruleMap);
	}

	public Map<String, Spell> getSpells() {
		return Collections.unmodifiableMap(spellMap);
	}

	public Map<String, Monster> getMonsters() {
		return Collections.unmodifiableMap(monstMap);
	}
	
	public Map<String, StyledDocument> getInserts(){
		return Collections.unmodifiableMap(insertMap);
	}
	
	public Map<String, Feat> getFeats(){
		if(featMap == null)
			return null;
		return Collections.unmodifiableMap(featMap);
	}
	
	public Map<String, Item> getItems(){
		return Collections.unmodifiableMap(itemMap);
	}

	public List<String> getRuleKeysSorted() {
		return Collections.unmodifiableList(ruleKeysSorted);
	}

	public List<String> getSpellKeysSorted() {
		return Collections.unmodifiableList(spellKeysSorted);
	}

	public List<String> getMonsterKeysSorted() {
		return Collections.unmodifiableList(monstKeysSorted);
	}
	
	public List<String> getInsertKeysSorted(){
		return insertKeysSorted;
	}
	
	public List<String> getWeaponKeysSorted(){
		return Collections.unmodifiableList(weaponKeysSorted);
	}
	
	public List<String> getArmorKeysSorted(){
		return Collections.unmodifiableList(armorKeysSorted);
	}
	
	public List<String> getGearKeysSorted(){
		return Collections.unmodifiableList(gearKeysSorted);
	}
	
	public List<String> getToolKeysSorted(){
		return Collections.unmodifiableList(toolKeysSorted);
	}
	
	public List<String> getMagicItemKeysSorted(){
		return Collections.unmodifiableList(magicItemKeysSorted);
	}
	
	public List<String> getFeatKeysSorted(){
		return Collections.unmodifiableList(featKeysSorted);
	}
	
	public HashMap<String, Monster> getUnsafe(){
		return monstMap;
	}
	/*
	 * Campaign Methods
	 */
	public boolean isCampaignLoaded() {
		return camp != null;
	}
	
	public void LoadCampaign(Campaign c) {
		this.camp = c;
		if(!recentFiles.contains(c.saveLoc)) {
			if(recentFiles.size() == 5)
				recentFiles.poll();
			recentFiles.offer(c.saveLoc);
		}
	}
	
	public void AddPlayer(Player p) {
		if(isCampaignLoaded())
			camp.AddPlayer(p);
	}
	
	public void DeletePlayer(String p) {
		camp.RemovePlayer(p);
	}
	
	public void DeletePlayer(Player p) {
		camp.RemovePlayer(p);
	}
	
	public Map<String, Player> getParty(){
		if(isCampaignLoaded())
			return Collections.unmodifiableMap(camp.party);
		else
			return null;
	}
	
	public void AddNote(String title, StyledDocument note) {
		camp.notes.put(title, note);
	}
	
	public void DeleteNote(String title) {
		camp.notes.remove(title);
	}
	
	public StyledDocument getNote(String key){
		return camp.notes.get(key);
	}
	
	public Set<String> getNoteKeys(){
		return camp.notes.keySet();
	}
	
	public Queue<File> getRecentFiles(){
		return new LinkedList<File>(recentFiles);
	}
	
	private boolean SaveCampaign() {
		if(isCampaignLoaded()) {
			if(!camp.saveLoc.exists()) {
				try {
					camp.saveLoc.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(camp.saveLoc));
				oos.writeObject(camp);
				oos.flush();
				oos.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else
			return false;
		
	}
	
	public boolean LoadCampaign(File campFile) {
		if (campFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(campFile))) {
				camp = (Campaign) ois.readObject();
				if(!recentFiles.contains(campFile)) {
					if(recentFiles.size() == 5)
						recentFiles.poll();
					recentFiles.offer(campFile);
				}
				
				return true;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public String getLastCampPath() {
		return lastCampPath;
	}
	
	private void SaveConfig() {
		File conf = new File(appLocal.getPath() + File.separator + CONFIG_FILE_NAME);
		System.out.println(conf.getPath());
		if(!conf.exists())
			try {
				conf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(conf));
			out.println(ruleMap.size());
			out.println(spellMap.size());
			out.println(monstMap.size());
			out.println(itemMap.size());
			if(camp != null)
				out.println(camp.saveLoc.getParent());
			else if(lastCampPath != null)
				if(lastCampPath.length() > 0)
					out.println(lastCampPath);
			out.flush();
			out.close();
			
			if(recentFiles.size() > 0) {
				File f = new File(appLocal.getPath() + File.separator + EXTRAS_FILE_NAME);
				if(!f.exists())
					f.createNewFile();
				ObjectOutputStream oOut = new ObjectOutputStream(new FileOutputStream(f));
				oOut.writeObject(recentFiles);
				oOut.flush();
				oOut.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void LoadConfig() {
		File conf = new File(appLocal.getPath() + File.separator + CONFIG_FILE_NAME);
		if(conf.exists()) {
			try {
				BufferedReader read = new BufferedReader(new FileReader(conf));
				System.out.println(read.readLine());
				System.out.println(read.readLine());
				System.out.println(read.readLine());
				System.out.println(read.readLine());
				
				lastCampPath = read.readLine();
				read.close();
				
				File f = new File(appLocal.getPath() + File.separator + EXTRAS_FILE_NAME);
				if(f.exists()) {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
					recentFiles = (Queue<File>) in.readObject();
					in.close();
				}else {
					recentFiles = new LinkedList<File>();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("Error loading");
			recentFiles = new LinkedList<File>();
		}
	}
}