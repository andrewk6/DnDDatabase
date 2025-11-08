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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.StyledDocument;

import utils.ErrorLogger;

import data.campaign.Campaign;
import data.campaign.Player;
import data.hazards.Hazard;
import data.hazards.Trap;
import data.interfaces.DataChangeListener;
import data.interfaces.SourceProvider;
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
import data.siege_equipment.SiegeEquipment;
import data.vehicles.LargeVehicle;
import data.vehicles.Mount;
import data.vehicles.Vehicle;
import gui.dialogs.ExportDialog;
import gui.dialogs.SourceToggleDialog;
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
	
	public enum PartyTier {
	    Tier1("Levels 1–4"),
	    Tier2("Levels 5–10"),
	    Tier3("Levels 11–16"),
	    Tier4("Levels 17–20");

	    private final String description;

	    PartyTier(String description) {
	        this.description = description;
	    }

	    public String getDescription() {
	        return description;
	    }
	}
	
	public enum Source{
		//Source Books
		PlayersHandbook2024("Player's Handbook"),
		DungeonMastersGuide2024("Dungeon Master's Guide"),
		MonsterManual2024("Monster Manual"), 
		VolosGuideToMonsters("Volo's Guide to Monsters"),
		XanathersGuideToEverything("Xanather's Guide to Everything"),
		MordenkainenTomeOfFoes("Mordenkainen's Tome of Foes"),
		TashasCauldronOfEverything("Tasha's Cauldron of Everything"),
		FizbansTreasuryOfDragons("Fizban's Treasury of Dragons"),
		MonstersOfTheMultivers("Monsters of the Multiverse"),
		BigbysGloryofGiants("Bigby Presents: Glory of the Giants"),
		BookOfManyThings("The Book of Many Things"),
		
		//Setting Books
		SwordCoastAdventureGuide("Sword Coast Adventurer's Guide"),
		GuildmastersGuidetoRavnica("Guildmaster's Guide to Ravnica"),
		AcquisitionsIncorporated("Acquisitions Incorporated"),
		EberronTheLastWar("Eberron: Rising from the Last War"),
		ExplorersGuideToWildemount("Explorer's Guide to Wildemount"),
		MythidOdysseysOfTheros("Mythic Oddesseys of Theros"),
		VanRichtenGuideToRavenloft("Van Richten's Guide to Revenloft"),
		StrixhavenCurriculumOfChaos("Strixhaven: A Curriculum of Chaos"),
		SpellJammerAdventuresInSpace("Spelljammer: Adventures In Space"),
		PlanescapeAdventuresInTheMultiverse("Planescape: Adventures in the Multiverse"),
		ForgottenRealmsHeroes("Forgotten Realms: Heroes of Faerun"),
		EberronForgeOfArtificer("Eberron: Forge of the Artificer"),
		
		//Adventure Books
		StarterSetLostMinePhandelver("Starter Set: Lost Mine of Phandelver"),
		HoardOfTheDragonQueen("Hoard of the Dragon Queen"),
		RiseOfTiamat("The Rise of Tiamat"),
		PrinceOfTheApocalypse("Princes of the Apocalypse"),
		OutOfTheAbyss("Out of the Abyss"),
		CurseOfStrahd("Curse of Strahd"),
		StormKingsThunder("Storm King's Thunder"),
		TalesFromYawningPortal("Tales from the Yawning Portal"),
		TombOfAnnihilation("Tomb of Annihilation"),
		WaterdeepDragonHeist("Waterdeep: Dragon Heist"),
		WaterdeepDungeonOfMadMage("Waterdeep: Dungeon of the Mad Mage"),
		GhostsOfSaltmarsh("Ghosts of Saltmarsh"),
		EssentialsDragonOfIcespirePeak("Essentials Kit: Dragon of Icespire Peak"),
		BalursGateDescentIntoAvernus("Baldur's Gate: Descent into Avernus"),
		IcewindDaleRimeOfTheFrostmaiden("Icewind Dale: Rime of the Frostmaiden"),
		CandlekeepMysteries("Candlekeep Mysteries"),
		WildBeyondWitchlight("Wild Beyond the Witchlight"),
		JourneyThroughRadiantCitadel("Journey Through the Radiant Citadel"),
		StarterSetDragonStromwreckIsle("Starter Set: Dragons of Stormwreck Isle"),
		DragonlanceShadowOfDragonQueen("Dragonlance: Shadow of the Dragon Queen"),
		KeysOfTheGoldenVault("Keys from the Golden Vault"),
		PhandelverTheShatteredObelisk("Phandelver and Below: The Shattered Obelisk"),
		VecnaEveOfRuin("Vecna Eve of Ruin"),
		QuestFromInfiniteStair("Quests From the Infinite Staircase"),
		DragonDelves("Dragon Delves Adventure Anthology"),
		StartSetHeroesOfBorderlands("Starter Set: Heroes of the Boardland"),
		ForgottenRealmsAdventures("Forgotten Realms: Adventures in Faerun"),
		//Misc
		UnearthedArcana("Unearthed Arcana"), 
		Custom("Custom");
		
		private final String srcLbl;
		
		Source(String lbl) { this.srcLbl = lbl;}
		
		public String toString() {
			return srcLbl;
		}
	}
	
	public enum PlayerClass{
		Artificer, Barbarian, Bard, Cleric, Druid, Fighter, Monk, Paladin, Ranger, 
		Rogue, Sorcerer, Warlock, Wizard, Custom, None
	}
	
	public static final File appLocal = new File(System.getenv("LOCALAPPDATA") + "\\DnD Database");
	public static final File dbFolder = new File(appLocal + "\\Databases");
	
	public static final String RULES_FILE_NAME = "Rules.xol";
	public static final String SPELLS_FILE_NAME = "Spells.sol";
	public static final String MONSTERS_FILE_NAME = "Monster.mol";
	public static final String INSERT_FILE_NAME = "Inserts.bol";
	public static final String ITEMS_FILE_NAME = "Items.iol";
	public static final String VEHICLES_FILE_NAME = "Vehicles.vol";
	public static final String SIEGE_EQUIP_FILE_NAME = "SiegeEquipment.seol";
	public static final String FEATS_FILE_NAME = "Feats.fol";
	public static final String CLASS_FILE_NAME = "Classes.clol";
	public static final String SPECIES_FILE_NAME = "Species.spol";
	public static final String BACKGROUND_FILE_NAME = "Backgrounds.bol";
	public static final String BASTION_ROOM_FILE_NAME = "Bastions.baol";
	public static final String HAZARD_FILE_NAME = "Hazards.hol";
	public static final String CONFIG_FILE_NAME = "Config.confol";
	public static final String EXTRAS_FILE_NAME = "Extras.exol";
	public static final String BASTION_RULES = "BastionRules.baol";
	
	public enum MapType {
		RULES, SPELLS, MONSTERS, INSERTS, ITEMS, VEHICLES, SIEGEEQUIP, CAMPAIGN, FEATS, CLASSES, 
		SPECIES, BACKGROUNDS, BASTION_ROOMS, HAZARDS
	}

	private HashMap<String, Rule> ruleMap;
	private HashMap<String, Spell> spellMap;
	private HashMap<String, Monster> monstMap;
	private HashMap<String, Item> itemMap;
	private HashMap<String, Vehicle> vehicleMap;
	private HashMap<String, SiegeEquipment> siegeEquipMap;
	private HashMap<String, Feat> featMap;
	private HashMap<String, DnDClass> classMap;
	private HashMap<String, Species> speciesMap;
	private HashMap<String, Background> backgroundMap;
	private HashMap<String, BastionRoom> bastionRoomMap;
	private HashMap<String, Hazard> hazardMap;
	private HashMap<String, StyledDocument> insertMap;
	private HashMap<String, StyledDocument> bastionRules;
	private Campaign camp;
	
	private ArrayList<String> ruleKeysSorted, spellKeysSorted, monstKeysSorted, insertKeysSorted,
		weaponKeysSorted, armorKeysSorted, gearKeysSorted, toolKeysSorted, magicItemKeysSorted,
		mountKeysSorted, largeVehicleKeysSorted, siegeEquipmentKeysSorted, featKeysSorted,classKeysSorted, 
		bastionRoomKeysSorted, speciesKeysSorted, backgroundKeysSorted, hazardKeysSorted, trapKeysSorted;
	

	private final AtomicInteger runningTasks = new AtomicInteger(0);
	private volatile boolean isRunning = true;
	private final BlockingQueue<Runnable> ioQueue = new LinkedBlockingQueue<>();
	
	
	private final List<DataChangeListener> updateListeners = new ArrayList<DataChangeListener>();
	private final List<LoadListener> loadListeners = new ArrayList<LoadListener>();
	private Queue<File> recentFiles;
	private ArrayList<Source> sourceFilter = new ArrayList<Source>(Arrays.asList(Source.values()));
	
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
        tasks.add(this::ImportSiegeEquipment);
        tasks.add(this::ImportFeats);
        tasks.add(this::ImportClassMap);
        tasks.add(this::ImportSpeciesMap);
        tasks.add(this::ImportBackgrounds);
        tasks.add(this::ImportBastionRooms);
        tasks.add(this::ImportHazards);
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
	
	public void init(MapType map) {
		
		 // Or however many cores/files you're importing
			List<Callable<Boolean>> tasks = new ArrayList<>();
			switch (map) {
			case MapType.SPELLS:
				tasks.add(this::ImportSpellMap);
				break;
			case MapType.RULES:
				tasks.add(this::ImportRuleMap);
				break;
			case MapType.MONSTERS:
				tasks.add(this::ImportMonsters);
				break;
			case MapType.INSERTS:
				tasks.add(this::ImportInsertHelpers);
				break;
			case MapType.ITEMS:
				tasks.add(this::ImportItems);
				tasks.add(this::ImportVehicles);
				break;
			case MapType.VEHICLES:
				tasks.add(this::ImportItems);
				tasks.add(this::ImportVehicles);
				break;
			case MapType.FEATS:
				tasks.add(this::ImportFeats);
				break;
			case MapType.CLASSES:
				tasks.add(this::ImportClassMap);
				break;
			case MapType.SPECIES:
				tasks.add(this::ImportSpeciesMap);
				break;
			case MapType.BACKGROUNDS:
				tasks.add(this::ImportBackgrounds);
				break;
			case MapType.BASTION_ROOMS:
				tasks.add(this::ImportBastionRooms);
				tasks.add(this::ImportBastionRules);
				break;
			case MapType.HAZARDS:
				tasks.add(this::ImportHazards);
				break;
			default:
				init();
			}
       
       ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
       try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			ErrorLogger.log(e);
			e.printStackTrace();
		}
       executor.shutdown();
		
		SortKeys(map);
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
						if(obj instanceof Rule r)
							ImportObject(r, r.name, ruleMap);
						else if(obj instanceof Spell s) 
							ImportObject(s, s.name, spellMap);
						else if(obj instanceof Monster m) 
							ImportObject(m, m.name, monstMap);
						else if(obj instanceof Item i) 
							ImportObject(i, i.name, itemMap);
						else if(obj instanceof Vehicle v)
							ImportObject(v, v.name, vehicleMap);
						else if(obj instanceof SiegeEquipment se)
							ImportObject(se, se.name, siegeEquipMap);
						else if(obj instanceof Feat f) 
							ImportObject(f, f.name, featMap);
						else if(obj instanceof DnDClass c) 
							ImportObject(c, c.name, classMap);
						else if(obj instanceof Species s)
							ImportObject(s, s.name, speciesMap);
						else if(obj instanceof Background b)
							ImportObject(b, b.name, backgroundMap);
						else if(obj instanceof BastionRoom b)
							ImportObject(b, b.name, bastionRoomMap);
						else if(obj instanceof Hazard h)
							ImportObject(h, h.name, hazardMap);
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
			if(exportDialog.getSiegeEquipment())
				ExportMap(siegeEquipMap, oos);
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
			if(exportDialog.getHazards())
				ExportMap(hazardMap, oos);
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

	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
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
	
	private boolean ImportSiegeEquipment() {
		siegeEquipMap = new HashMap<String, SiegeEquipment>();
		siegeEquipmentKeysSorted = new ArrayList<String>();
		File siegeFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				siegeFile = new File(dbFolder.getPath() + File.separator + SIEGE_EQUIP_FILE_NAME);
			}else {
				siegeFile = new File(SIEGE_EQUIP_FILE_NAME);
			}
		}else {
			siegeFile = new File(SIEGE_EQUIP_FILE_NAME);
		}
		
		if (siegeFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(siegeFile))) {
				while (true) {
					try {
						SiegeEquipment s = (SiegeEquipment) ois.readObject();
						siegeEquipMap.put(s.name, s);
						siegeEquipmentKeysSorted.add(s.name);
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
	
	private boolean ImportHazards() {
		hazardMap = new HashMap<String, Hazard>();
		hazardKeysSorted = new ArrayList<String>();
		trapKeysSorted = new ArrayList<String>();
		
		File hazardFile;
		
		if(dbFolder.exists()) {
			if(dbFolder.isDirectory()) {
				hazardFile = new File(dbFolder.getPath() + File.separator + HAZARD_FILE_NAME);
			}else {
				hazardFile = new File(HAZARD_FILE_NAME);
			}
		}else {
			hazardFile = new File(HAZARD_FILE_NAME);
		}
		if (hazardFile.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hazardFile))) {
				while (true) {
					try {
						Hazard h = (Hazard) ois.readObject();
						hazardMap.put(h.name, h);
						if(h instanceof Trap)
							trapKeysSorted.add(h.name);
						else
							hazardKeysSorted.add(h.name);
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
	
	public void SetSiegeEquipMap(HashMap<String, SiegeEquipment> sMap) {
		this.siegeEquipMap = sMap;
		this.siegeEquipmentKeysSorted = new ArrayList<String>(sMap.keySet());
		SortKeys(MapType.SIEGEEQUIP);
		notifyChange(MapType.SIEGEEQUIP);
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
		SortKeys(MapType.BACKGROUNDS);
		notifyChange(MapType.BACKGROUNDS);
	}
	
	public void SetBastionRoomMap(HashMap<String, BastionRoom> bMap) {
		this.bastionRoomMap = bMap;
		this.classKeysSorted = new ArrayList<String>(bMap.keySet());
		SortKeys(MapType.BASTION_ROOMS);
		notifyChange(MapType.BASTION_ROOMS);
	}
	
	public void SetHazardMap(HashMap<String, Hazard> hMap) {
		this.hazardMap = hMap;
		this.hazardKeysSorted = new ArrayList<String>();
		this.trapKeysSorted = new ArrayList<String>();
		for(Hazard h : hazardMap.values()) {
			if(h instanceof Trap)
				trapKeysSorted.add(h.name);
			else
				hazardKeysSorted.add(h.name);
		}
		SortKeys(MapType.HAZARDS);
		notifyChange(MapType.HAZARDS);
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
				SaveInserts() && SaveItems() && SaveVehicles() && SaveSiegeEquipment() && SaveCampaign() 
				&& SaveFeats()&& SaveClasses() && SaveSpecies() && SaveBackground()
				&& SaveBastionRooms() && SaveHazards();
	}

	private boolean SaveData(MapType saveOpt) {
		switch(saveOpt) {
		case MapType.RULES: return SaveRules();
		case MapType.SPELLS: return SaveSpells();
		case MapType.MONSTERS: return SaveMonsters();
		case MapType.INSERTS: return SaveInserts();
		case MapType.ITEMS: return SaveItems();
		case MapType.VEHICLES: return SaveVehicles();
		case MapType.SIEGEEQUIP: return SaveSiegeEquipment();
		case MapType.CAMPAIGN: return SaveCampaign();
		case MapType.FEATS: return SaveFeats(); 
		case MapType.CLASSES: return SaveClasses();
		case MapType.SPECIES: return SaveSpecies();
		case MapType.BACKGROUNDS: return SaveBackground();
		case MapType.BASTION_ROOMS: return SaveBastionRooms();
		case MapType.HAZARDS: return SaveHazards();
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
	
	private boolean SaveSiegeEquipment() {
		File siegefile = new File(dbFolder.getPath() + File.separator + DataContainer.SIEGE_EQUIP_FILE_NAME);
		if(!siegefile.exists()) {
			try {
				siegefile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(siegefile));
			for(String s : siegeEquipMap.keySet()) {
				oos.writeObject(siegeEquipMap.get(s));
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
	
	private boolean SaveHazards() {
		File hFile = new File(dbFolder.getPath() + File.separator + DataContainer.HAZARD_FILE_NAME);
		if(!hFile.exists()) {
			try {
				hFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hFile));
			for (String s : hazardMap.keySet()) {
				oos.writeObject(hazardMap.get(s));
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
		Collections.sort(siegeEquipmentKeysSorted);
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
		Collections.sort(hazardKeysSorted);
		Collections.sort(trapKeysSorted);
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
		case MapType.SIEGEEQUIP: Collections.sort(siegeEquipmentKeysSorted); break;
		case MapType.FEATS: Collections.sort(featKeysSorted); break;
		case MapType.CLASSES: Collections.sort(classKeysSorted); break;
		case MapType.SPECIES: Collections.sort(speciesKeysSorted); break;
		case MapType.BACKGROUNDS: Collections.sort(backgroundKeysSorted); break;
		case MapType.BASTION_ROOMS: Collections.sort(bastionRoomKeysSorted); break;	
		case MapType.HAZARDS: 
			Collections.sort(hazardKeysSorted);
			Collections.sort(trapKeysSorted);
			break;
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
	
	public Map<String, SiegeEquipment> getSiegeEquipment(){
		return Collections.unmodifiableMap(siegeEquipMap);
	}
	
	public Map<String, Hazard> getHazards(){
		return Collections.unmodifiableMap(hazardMap);
	}

//	public<T> List<String> getFilteredList(Map<String, T> map, List<String> keys){
//		if(sourceFilter.size() < Source.values().length) {
//			ArrayList<String> filteredList = new ArrayList<String>();
//			for(String key : keys) {
//				if(map.get(key) instanceof SourceProvider) {
//					
//				}else
//					filteredList.add(key);
//			}
//			
//			return Collections.unmodifiableList(filteredList);
//		}else
//			return Collections.unmodifiableList(keys);
//	}
	
	public <T extends SourceProvider> List<String> filterKeys(
	        List<String> keys,
	        Map<String, T> map
	) {
	    return keys.stream()
	               .filter(k -> {
	                   T obj = map.get(k);
	                   return obj != null && sourceFilter.contains(obj.getSource());
	               })
	               .toList();  // Java 16+ returns an immutable list
	}
	
	public void showSourceDialog(JFrame frm) {
		SourceToggleDialog srcToggle = new SourceToggleDialog(frm, sourceFilter);
		if(srcToggle.setSources) {
			sourceFilter = new ArrayList<Source>(srcToggle.getSources());
			if(camp != null)
				camp.sourceFilters = sourceFilter;
			notifyChange();
		}
		srcToggle.dispose();
	}
	
	public List<String> getRuleKeysSorted() {
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(ruleKeysSorted, ruleMap);
		return Collections.unmodifiableList(ruleKeysSorted);
	}

	public List<String> getSpellKeysSorted() {
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(spellKeysSorted, spellMap);
		return Collections.unmodifiableList(spellKeysSorted);
	}

	public List<String> getMonsterKeysSorted() {
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(monstKeysSorted, monstMap);
		return Collections.unmodifiableList(monstKeysSorted);
	}
	
	public List<String> getInsertKeysSorted(){
		return insertKeysSorted;
	}
	
	public List<String> getWeaponKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(weaponKeysSorted, itemMap);
		return Collections.unmodifiableList(weaponKeysSorted);
	}
	
	public List<String> getArmorKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(armorKeysSorted, itemMap);
		return Collections.unmodifiableList(armorKeysSorted);
	}
	
	public List<String> getGearKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(gearKeysSorted, itemMap);
		return Collections.unmodifiableList(gearKeysSorted);
	}
	
	public List<String> getToolKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(toolKeysSorted, itemMap);
		return Collections.unmodifiableList(toolKeysSorted);
	}
	
	public List<String> getMagicItemKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(magicItemKeysSorted, itemMap);
		return Collections.unmodifiableList(magicItemKeysSorted);
	}
	
	public List<String> getMountKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(mountKeysSorted, vehicleMap);
		return Collections.unmodifiableList(mountKeysSorted);
	}
	
	public List<String> getLargeVehicleKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(largeVehicleKeysSorted, vehicleMap);
		return Collections.unmodifiableList(largeVehicleKeysSorted);
	}
	
	public List<String> getSiegeEquipmentKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(siegeEquipmentKeysSorted, siegeEquipMap);
		return Collections.unmodifiableList(siegeEquipmentKeysSorted);
	}
	
	public List<String> getFeatKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(featKeysSorted, featMap);
		return Collections.unmodifiableList(featKeysSorted);
	}
	
	public List<String> getClassKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(classKeysSorted, classMap);
		return Collections.unmodifiableList(classKeysSorted);
	}
	
	public List<String> getSpeciesKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(speciesKeysSorted, speciesMap);
		return Collections.unmodifiableList(speciesKeysSorted);
	}
	
	public List<String> getBackgroundKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(backgroundKeysSorted, backgroundMap);
		return Collections.unmodifiableList(backgroundKeysSorted);
	}
	
	public List<String> getBastionRoomKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(bastionRoomKeysSorted, bastionRoomMap);
		return Collections.unmodifiableList(bastionRoomKeysSorted);
	}
	
	public List<String> getHazardKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(hazardKeysSorted, hazardMap);
		return Collections.unmodifiableList(hazardKeysSorted);
	}
	
	public List<String> getTrapKeysSorted(){
		if((sourceFilter.size() < Source.values().length))
			return filterKeys(trapKeysSorted, hazardMap);
		return Collections.unmodifiableList(trapKeysSorted);
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
				sourceFilter.clear();
				if(camp.sourceFilters != null)
					sourceFilter.addAll(camp.sourceFilters);
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
		if(!conf.exists())
			try {
				conf.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e);
				e.printStackTrace();
			}
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(conf));
			if(ruleMap != null)
				out.println(ruleMap.size());
			if(spellMap != null)
				out.println(spellMap.size());
			if(monstMap != null)
				out.println(monstMap.size());
			if(itemMap != null)
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