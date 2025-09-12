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
import java.util.Arrays;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.ldap.SortKey;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import utils.ErrorLogger;

import data.campaign.Campaign;
import data.campaign.Player;
import data.items.Armor;
import data.items.Gear;
import data.items.Item;
import data.items.MagicItem;
import data.items.ToolSet;
import data.items.Weapon;
import data.players.Background;
import data.players.BastionRoom;
import data.players.Species;
import data.players.classes.DnDClass;
import data.vehicles.LargeVehicle;
import data.vehicles.Mount;
import data.vehicles.Vehicle;
import gui.gui_helpers.ExportDialog;
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
		PlayersHandbook2024, DungeonMastersGuide2024, MonsterManual2024, VecnaEveOfRuin, Custom, 
		TashasCauldronOfEverything, XanathersGuideToEverything,
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
	public static final String VEHICLES_FILE_NAME = "Vehicles.vol";
	public static final String FEATS_FILE_NAME = "Feats.fol";
	public static final String CLASS_FILE_NAME = "Classes.clol";
	public static final String SPECIES_FILE_NAME = "Species.spol";
	public static final String BACKGROUND_FILE_NAME = "Backgrounds.bol";
	public static final String BASTION_ROOM_FILE_NAME = "Bastions.baol";
	public static final String CONFIG_FILE_NAME = "Config.confol";
	public static final String EXTRAS_FILE_NAME = "Extras.exol";
	public static final String BASTION_RULES = "BastionRules.baol";
	
	public enum MapType {
		RULES, SPELLS, MONSTERS, INSERTS, ITEMS, VEHICLES, CAMPAIGN, FEATS, CLASSES, 
		SPECIES, BACKGROUND, BASTION_ROOMS
	}

	private HashMap<String, Rule> ruleMap;
	private HashMap<String, Spell> spellMap;
	private HashMap<String, Monster> monstMap;
	private HashMap<String, Item> itemMap;
	private HashMap<String, Vehicle> vehicleMap;
	private HashMap<String, Feat> featMap;
	private HashMap<String, DnDClass> classMap;
	private HashMap<String, Species> speciesMap;
	private HashMap<String, Background> backgroundMap;
	private HashMap<String, BastionRoom> bastionRoomMap;
	private HashMap<String, StyledDocument> insertMap;
	private HashMap<String, StyledDocument> bastionRules;
	private Campaign camp;
	
	private ArrayList<String> ruleKeysSorted, spellKeysSorted, monstKeysSorted, insertKeysSorted,
		weaponKeysSorted, armorKeysSorted, gearKeysSorted, toolKeysSorted, magicItemKeysSorted,
		mountKeysSorted, largeVehicleKeysSorted, featKeysSorted,classKeysSorted, 
		bastionRoomKeysSorted, speciesKeysSorted, backgroundKeysSorted;
	

	private final AtomicInteger runningTasks = new AtomicInteger(0);
	private volatile boolean isRunning = true;
	private final BlockingQueue<Runnable> ioQueue = new LinkedBlockingQueue<>();
	
	
	private final List<DataChangeListener> updateListeners = new ArrayList<DataChangeListener>();
	private final List<LoadListener> loadListeners = new ArrayList<LoadListener>();
	private Queue<File> recentFiles;
	
	private String lastCampPath;
	private boolean initiatlized;
	
	private ExportDialog exportDialog;
	private boolean exportReady;

	public DataContainer() {
		StartIOThread();
		System.out.println(dbFolder.exists() + "/" + appLocal.exists());
	}
	
	public void init() {
		
		 // Or however many cores/files you're importing
        List<Callable<Boolean>> tasks = new ArrayList<>();
        tasks.add(this::ImportRuleMap);
        tasks.add(this::ImportSpellMap);
        tasks.add(this::ImportMonsters);
        tasks.add(this::ImportInsertHelpers);
        tasks.add(this::ImportItems);
        tasks.add(this::ImportVehicles);
        tasks.add(this::ImportFeats);
        tasks.add(this::ImportClassMap);
        tasks.add(this::ImportSpeciesMap);
        tasks.add(this::ImportBackgrounds);
        tasks.add(this::ImportBastionRooms);
        tasks.add(this::ImportBastionRules);
        
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
		}
        executor.shutdown();
		
		SortKeys();
		LoadConfig();
		loadFinsihed();
		initiatlized = true;
	}
	
	public void buildExportDialog(JFrame frm) {
		exportDialog = new ExportDialog(frm);
		exportReady = true;
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
	            	ErrorLogger.log(e);
	                e.printStackTrace();
	            }
	        }
	    });
	    worker.setDaemon(false); // make it non-daemon so JVM waits for it to finish
	    worker.start();
	}
	
	public void ImportData() {
		JFileChooser fChoose = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Export Files (*.exol)", "exol");
		fChoose.setFileFilter(filter);
		
		int approve = fChoose.showOpenDialog(null);
		
		if(approve == JFileChooser.APPROVE_OPTION) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fChoose.getSelectedFile()));
				while(true) {
					try {
						Object obj = ois.readObject();
						if(obj instanceof Rule)
							ImportObject((Rule) obj, ((Rule)obj).name, ruleMap);
						else if(obj instanceof Spell) 
							ImportObject((Spell) obj, ((Spell)obj).name, spellMap);
						else if(obj instanceof Monster) 
							ImportObject((Monster) obj, ((Monster)obj).name, monstMap);
						else if(obj instanceof Item) 
							ImportObject((Item) obj, ((Item)obj).name, itemMap);
						else if(obj instanceof Vehicle)
							ImportObject((Vehicle)obj, ((Vehicle)obj).name, vehicleMap);
						else if(obj instanceof Feat) 
							ImportObject((Feat) obj, ((Feat)obj).name, featMap);
						else if(obj instanceof DnDClass) 
							ImportObject((DnDClass) obj, ((DnDClass)obj).name, classMap);
						else if(obj instanceof Species)
							ImportObject((Species) obj, ((Species)obj).name, speciesMap);
						else if(obj instanceof Background)
							ImportObject((Background)obj, ((Background)obj).name, backgroundMap);
						else if(obj instanceof BastionRoom)
							ImportObject((BastionRoom)obj, ((BastionRoom)obj).name, bastionRoomMap);
						else throw new IllegalArgumentException("Not proper object");
						
					} catch (ClassNotFoundException e) {
						ErrorLogger.log(e);
						e.printStackTrace();
					} catch (EOFException eof) {
			            ois.close();
			            break;
			        }
				}
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		SafeSaveData();
		SortKeys();
		notifyChange();
	}
	
	private <T> void ImportObject(T obj, String key, HashMap<String, T> map) {
		if(!map.keySet().contains(key)) {
			map.put(key, obj);
		}
	}
	
	public void ExportData() {
		if(exportReady)
			exportDialog.openDialog();
		else return;
		
		if(exportDialog.export) {
			File out = exportDialog.expoTarget;
			if(!out.exists()) {
				try {
					out.createNewFile();
				} catch (IOException e) {
					ErrorLogger.log(e);
					e.printStackTrace();
				}
			}
			ioQueue.offer(()->{
				WriteExport(out);
			});
		}
	}
	
	private void WriteExport(File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			if(exportDialog.getRules())
				ExportMap(ruleMap, oos);
			if(exportDialog.getSpells())
				ExportMap(spellMap, oos);
			if(exportDialog.getMonsters())
				ExportMap(monstMap, oos);
			if(exportDialog.getItems())
				ExportMap(itemMap, oos);
			if(exportDialog.getVehicles())
				ExportMap(vehicleMap, oos);
			if(exportDialog.getFeats())
				ExportMap(featMap, oos);
			if(exportDialog.getClasses())
				ExportMap(classMap, oos);
			if(exportDialog.getBackgrounds())
				ExportMap(backgroundMap, oos);
			if(exportDialog.getSpecies())
				ExportMap(speciesMap, oos);
			if(exportDialog.getBastionRooms())
				ExportMap(bastionRoomMap, oos);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
		}
	}
	
	private <T> void ExportMap(HashMap<String, T> exMap, ObjectOutputStream oos) throws IOException {
		for(T obj : exMap.values()) {
			oos.writeObject(obj);
		}
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
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean ImportBastionRules() {
		File bastRuleFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				bastRuleFile = new File(dbFolder.getPath() + File.separator + BASTION_RULES);
			}else {
				bastRuleFile = new File(BASTION_RULES);
			}
		}else
			bastRuleFile = new File(BASTION_RULES);
		if(bastRuleFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bastRuleFile))) {
				bastionRules = (HashMap<String, StyledDocument>) ois.readObject();
				ois.close();
				return true;
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}else {
			try {
				bastRuleFile.createNewFile();
				bastionRules = new HashMap<String, StyledDocument>();
			} catch (IOException e) {
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
				while (true) {
					try {
						Feat f = (Feat) ois.readObject();
						featMap.put(f.name, f);
						featKeysSorted.add(f.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
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
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private boolean ImportVehicles() {
		vehicleMap = new HashMap<String, Vehicle>();
		mountKeysSorted = new ArrayList<String>();
		largeVehicleKeysSorted = new ArrayList<String>();
		
		File vehicleFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				vehicleFile = new File(dbFolder.getPath() + File.separator + VEHICLES_FILE_NAME);
			}else {
				vehicleFile = new File(VEHICLES_FILE_NAME);
			}
		}else {
			vehicleFile = new File(VEHICLES_FILE_NAME);
		}
		if(vehicleFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(vehicleFile))){
				while(true) {
					try {
						Vehicle v = (Vehicle) ois.readObject();
						vehicleMap.put(v.name, v);
						switch(v) {
						case Mount m: mountKeysSorted.add(m.name); break;
						case LargeVehicle lv: largeVehicleKeysSorted.add(lv.name); break;
						default: throw new IllegalArgumentException("Unexpected value: " + v);
						}
					}catch(EOFException eof) {
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private boolean ImportClassMap() {
		classMap = new HashMap<String, DnDClass>();
		classKeysSorted = new ArrayList<String>();
		File classFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				classFile = new File(dbFolder.getPath() + File.separator + CLASS_FILE_NAME);
			}else {
				classFile = new File(FEATS_FILE_NAME);
			}
		}else {
			classFile = new File(FEATS_FILE_NAME);
		}
		
		if (classFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(classFile))) {
				while (true) {
					try {
						DnDClass p = (DnDClass) ois.readObject();
						classMap.put(p.name, p);
						classKeysSorted.add(p.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private boolean ImportSpeciesMap() {
		speciesMap = new HashMap<String, Species>();
		speciesKeysSorted = new ArrayList<String>();
		File speciesFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				speciesFile = new File(dbFolder.getPath() + File.separator + SPECIES_FILE_NAME);
			}else {
				speciesFile = new File(SPECIES_FILE_NAME);
			}
		}else {
			speciesFile = new File(SPECIES_FILE_NAME);
		}
		
		if (speciesFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(speciesFile))) {
				while (true) {
					try {
						Species s = (Species) ois.readObject();
						speciesMap.put(s.name, s);
						speciesKeysSorted.add(s.name);
					} catch (EOFException eof) {
						ois.close();
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private boolean ImportBackgrounds() {
		backgroundMap = new HashMap<String, Background>();
		backgroundKeysSorted = new ArrayList<String>();
		File backgroundFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				backgroundFile = new File(dbFolder.getPath() + File.separator + BACKGROUND_FILE_NAME);
			}else {
				backgroundFile = new File(SPECIES_FILE_NAME);
			}
		}else {
			backgroundFile = new File(SPECIES_FILE_NAME);
		}
		
		if (backgroundFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backgroundFile))) {
				while (true) {
					try {
						Background b = (Background) ois.readObject();
						backgroundMap.put(b.name, b);
						backgroundKeysSorted.add(b.name);
					} catch (EOFException eof) {
						ois.close();
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private boolean ImportBastionRooms() {
		bastionRoomMap = new HashMap<String, BastionRoom>();
		bastionRoomKeysSorted = new ArrayList<String>();
		File bastFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				bastFile = new File(dbFolder.getPath() + File.separator + BASTION_ROOM_FILE_NAME);
			}else {
				bastFile = new File(BASTION_ROOM_FILE_NAME);
			}
		}else {
			bastFile = new File(BASTION_ROOM_FILE_NAME);
		}
		if (bastFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bastFile))) {
				while (true) {
					try {
						BastionRoom s = (BastionRoom) ois.readObject();
						bastionRoomMap.put(s.name, s);
						bastionRoomKeysSorted.add(s.name);
					} catch (EOFException eof) {
						// End of file reached
						return true;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				ErrorLogger.log(e);
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
	
	private void notifyChange(MapType mapType) {
		for(DataChangeListener tar : updateListeners) {
			System.out.println("In Loop" + tar.getClass().getName());
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
				SafeSaveData(MapType.CAMPAIGN);
			shutDownAndWait();
			SaveConfig();
		}
		System.out.println("Exiting now, out of shutdown and wait");
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
		SortKeys(MapType.MONSTERS);
		notifyChange(MapType.MONSTERS);
	}
	
	public void setInserts(HashMap<String, StyledDocument> in ) {
		insertMap = in;
		this.insertKeysSorted = new ArrayList<String>(insertMap.keySet());
		SortKeys(MapType.INSERTS);
		notifyChange(MapType.INSERTS);
	}

	public void setSpellMap(HashMap<String, Spell> spellMap) {
		this.spellMap = spellMap;
		this.spellKeysSorted = new ArrayList<String>(this.spellMap.keySet());
		SortKeys(MapType.SPELLS); 
		notifyChange(MapType.SPELLS);
	}
	
	public void setRuleMap(HashMap<String, Rule> ruleMap) {
		this.ruleMap = ruleMap;
		this.ruleKeysSorted = new ArrayList<String>(this.ruleMap.keySet());
		SortKeys(MapType.RULES);
		notifyChange(MapType.RULES);
	}
	
	public void setFeatMap(HashMap<String, Feat> featMap) {
		this.featMap = featMap;
		this.featKeysSorted = new ArrayList<String>(this.featMap.keySet());
		SortKeys(MapType.FEATS);
		notifyChange(MapType.FEATS);
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
		SortKeys(MapType.ITEMS);
		notifyChange(MapType.ITEMS);
	}
	
	public void SetVehiclesMap(HashMap<String, Vehicle> vMap) {
		this.vehicleMap = vMap;
		mountKeysSorted = new ArrayList<String>();
		largeVehicleKeysSorted = new ArrayList<String>();
		for(String s : vehicleMap.keySet()) {
			switch(vehicleMap.get(s)) {
			case Mount m: mountKeysSorted.add(m.name); break;
			case LargeVehicle lv: largeVehicleKeysSorted.add(lv.name); break;
			default: throw new IllegalArgumentException("Unexpected value: " + vehicleMap.get(s));
			}
		}
		SortKeys(MapType.VEHICLES);
		notifyChange(MapType.VEHICLES);
	}
	
	public void SetClassMap(HashMap<String, DnDClass> cMap) {
		this.classMap = cMap;
		this.classKeysSorted = new ArrayList<String>(cMap.keySet());
		SortKeys(MapType.CLASSES);
		notifyChange(MapType.CLASSES);
	}
	
	public void SetSpeciesMap(HashMap<String, Species> sMap) {
		this.speciesMap = sMap;
		this.speciesKeysSorted = new ArrayList<String>(sMap.keySet());
		SortKeys(MapType.SPECIES);
		notifyChange(MapType.SPECIES);
	}
	
	public void SetBackgroundMap(HashMap<String, Background> bMap) {
		this.backgroundMap = bMap;
		this.backgroundKeysSorted = new ArrayList<String>(bMap.keySet());
		SortKeys(MapType.BACKGROUND);
		notifyChange(MapType.BACKGROUND);
	}
	
	public void SetBastionRoomMap(HashMap<String, BastionRoom> bMap) {
		this.bastionRoomMap = bMap;
		this.classKeysSorted = new ArrayList<String>(bMap.keySet());
		SortKeys(MapType.BASTION_ROOMS);
		notifyChange(MapType.BASTION_ROOMS);
	}
	
	public void SafeSaveData() {
		System.out.println("Safe Save Enter");
		ioQueue.offer(this::SaveData);
	}
	
	public void SafeSaveData(MapType mapType) {
		System.out.println("Safe Save Enter");
		ioQueue.offer(()->{
			SaveData(mapType);
		});
	}
	
	private boolean SaveData() {
		return SaveRules() && SaveSpells() && SaveMonsters() && 
				SaveInserts() && SaveItems() && SaveVehicles() && SaveCampaign() 
				&& SaveFeats()&& SaveClasses() && SaveSpecies() && SaveBackground()
				&& SaveBastionRooms();
	}

	private boolean SaveData(MapType saveOpt) {
		switch(saveOpt) {
		case MapType.RULES: return SaveRules();
		case MapType.SPELLS: return SaveSpells();
		case MapType.MONSTERS: return SaveMonsters();
		case MapType.INSERTS: return SaveInserts();
		case MapType.ITEMS: return SaveItems();
		case MapType.VEHICLES: return SaveVehicles();
		case MapType.CAMPAIGN: return SaveCampaign();
		case MapType.FEATS: return SaveFeats(); 
		case MapType.CLASSES: return SaveClasses();
		case MapType.SPECIES: return SaveSpecies();
		case MapType.BACKGROUND: return SaveBackground();
		case MapType.BASTION_ROOMS: return SaveBastionRooms();
		default: throw new IllegalArgumentException("Invalid save option");
		}
	}
	
	private boolean SaveInserts() {
		File inFile = new File(dbFolder.getPath() + File.separator + DataContainer.INSERT_FILE_NAME);
		System.out.println("Saving Inserts");
		if(!inFile.exists()) {
			try {
				inFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
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
	        	ErrorLogger.log(e);
	            System.err.println("Failed to create file: " + e.getMessage());
	            return false;
	        }
	    }

	    byte[] originalContents = null;

	    try {
	        originalContents = Files.readAllBytes(saveFile.toPath());
	    } catch (IOException e) {
	    	ErrorLogger.log(e);
	        System.err.println("Failed to read original file: " + e.getMessage());
	        return false;
	    }

	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
	        for (Iterator<Map.Entry<String, Rule>> it = ruleMap.entrySet().iterator(); it.hasNext(); ) {
	            Map.Entry<String, Rule> entry = it.next();
	            try {
	                oos.writeObject(entry.getValue());
	            } catch (IOException ex) {
	            	ErrorLogger.log(ex);
	                System.err.println("Failed to serialize rule: " + entry.getKey() + " - removing it.");
	                it.remove(); // remove faulty rule
	            }
	        }
	        oos.flush();
	        return true;
	    } catch (IOException e) {
	    	ErrorLogger.log(e);
	        System.err.println("Serialization failed: " + e.getMessage());
	        try {
	            Files.write(saveFile.toPath(), originalContents); // restore
	            System.out.println("Original file restored.");
	        } catch (IOException ex) {
	        	ErrorLogger.log(ex);
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
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveVehicles() {
		File vehicleFile = new File(dbFolder.getPath() + File.separator + DataContainer.VEHICLES_FILE_NAME);
		if(!vehicleFile.exists()) {
			try {
				vehicleFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(vehicleFile));
			for(String s : vehicleMap.keySet()) {
				oos.writeObject(vehicleMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		}catch(IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveClasses() {
		File classFile = new File(dbFolder.getPath() + File.separator + DataContainer.CLASS_FILE_NAME);
		if(!classFile.exists()) {
			try {
				classFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(classFile));
			for (String s : classMap.keySet()) {
				oos.writeObject(classMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveSpecies() {
		File speciesFile = new File(dbFolder.getPath() + File.separator + DataContainer.SPECIES_FILE_NAME);
		if(!speciesFile.exists()) {
			try {
				speciesFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(speciesFile));
			for (String s : speciesMap.keySet()) {
				oos.writeObject(speciesMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveBackground() {
		File backgroundFile = new File(dbFolder.getPath() + File.separator + DataContainer.BACKGROUND_FILE_NAME);
		if(!backgroundFile.exists()) {
			try {
				backgroundFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backgroundFile));
			for (String s : backgroundMap.keySet()) {
				oos.writeObject(backgroundMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean SaveBastionRooms() {
		File bFile = new File(dbFolder.getPath() + File.separator + DataContainer.BASTION_ROOM_FILE_NAME);
		if(!bFile.exists()) {
			try {
				bFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(bFile));
			for (String s : bastionRoomMap.keySet()) {
				oos.writeObject(bastionRoomMap.get(s));
			}
			oos.flush();
			oos.close();
			return true;
		} catch (IOException e) {
			ErrorLogger.log(e);
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
	        	ErrorLogger.log(e);
	            Thread.currentThread().interrupt();
	        }
	    }
	    System.out.println("Finished Shutdown/Wait loop");
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
		Collections.sort(mountKeysSorted);
		Collections.sort(largeVehicleKeysSorted);
		Collections.sort(featKeysSorted);
		Collections.sort(classKeysSorted);
		Collections.sort(speciesKeysSorted);
		Collections.sort(backgroundKeysSorted);
		Collections.sort(bastionRoomKeysSorted);
	}
	
	private void SortKeys(MapType mapType) {
		switch(mapType) {
		case MapType.SPELLS: Collections.sort(spellKeysSorted); break;
		case MapType.RULES: Collections.sort(ruleKeysSorted); break;
		case MapType.MONSTERS: Collections.sort(monstKeysSorted); break;
		case MapType.INSERTS: Collections.sort(insertKeysSorted); break;
		case MapType.ITEMS:
			Collections.sort(weaponKeysSorted);
			Collections.sort(armorKeysSorted);
			Collections.sort(gearKeysSorted);
			Collections.sort(toolKeysSorted);
			Collections.sort(magicItemKeysSorted);
			break;
		case MapType.VEHICLES:
			Collections.sort(mountKeysSorted);
			Collections.sort(largeVehicleKeysSorted);
			break;
		case MapType.FEATS: Collections.sort(featKeysSorted); break;
		case MapType.CLASSES: Collections.sort(classKeysSorted); break;
		case MapType.SPECIES: Collections.sort(speciesKeysSorted); break;
		case MapType.BACKGROUND: Collections.sort(backgroundKeysSorted); break;
		case MapType.BASTION_ROOMS: Collections.sort(bastionRoomKeysSorted); break;		
		default: throw new IllegalArgumentException("Invalid Map Type.");
		}
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
	
	public Map<String, DnDClass> getClasses(){
		if(classMap == null)
			return null;
		return Collections.unmodifiableMap(classMap);
	}
	
	public Map<String, Species> getSpecies(){
		if(speciesMap == null)
			return null;
		return Collections.unmodifiableMap(speciesMap);
	}
	
	public Map<String, Background> getBackgrounds(){
		if(backgroundMap == null)
			return null;
		return Collections.unmodifiableMap(backgroundMap);
	}
	
	public Map<String, BastionRoom> getBastionRooms(){
		if(bastionRoomMap == null)
			return null;
		return Collections.unmodifiableMap(bastionRoomMap);
	}
	
	public Map<String, StyledDocument> getBastionRules(){
		if(bastionRules == null)
			return null;
		return Collections.unmodifiableMap(bastionRules);
	}
	
	public Map<String, Item> getItems(){
		return Collections.unmodifiableMap(itemMap);
	}
	
	public Map<String, Vehicle> getVehicles(){
		return Collections.unmodifiableMap(vehicleMap);
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
	
	public List<String> getMountKeysSorted(){
		return Collections.unmodifiableList(mountKeysSorted);
	}
	
	public List<String> getLargeVehicleKeysSorted(){
		return Collections.unmodifiableList(largeVehicleKeysSorted);
	}
	
	public List<String> getFeatKeysSorted(){
		return Collections.unmodifiableList(featKeysSorted);
	}
	
	public List<String> getClassKeysSorted(){
		return Collections.unmodifiableList(classKeysSorted);
	}
	
	public List<String> getSpeciesKeysSorted(){
		return Collections.unmodifiableList(speciesKeysSorted);
	}
	
	public List<String> getBackgroundKeysSorted(){
		return Collections.unmodifiableList(backgroundKeysSorted);
	}
	
	public List<String> getBastionRoomKeysSorted(){
		return Collections.unmodifiableList(bastionRoomKeysSorted);
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
					ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
				ErrorLogger.log(e);
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
			ErrorLogger.log(e);
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
				ErrorLogger.log(e);
				e.printStackTrace();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}else {
			System.out.println("Error loading");
			recentFiles = new LinkedList<File>();
		}
	}
}