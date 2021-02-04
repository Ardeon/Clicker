package ru.FIRST_114.Clicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;

import net.milkbowl.vault.economy.Economy;
import ru.FIRST_114.Clicker.PlayerData.CPlayer;
import ru.FIRST_114.Clicker.PlayerData.Kostili;
import ru.FIRST_114.Clicker.PlayerData.PlayerStat;

public class Main extends JavaPlugin {
	public static Main plugin;
	public static HashMap<UUID,CPlayer> players = new HashMap<UUID,CPlayer>();
	static Gson gson = new Gson();
    public static Logger log = Logger.getLogger("Clicker");
    public static File configFile;           
    public static YamlConfiguration config;
    public static Block clickBlock;
    public static Location loc;
    public static ArrayList<Kostili> topchik = null;
    //public static UUID[] top = {null,null,null,null,null};
    public static ClickMob clicking;
    public static MobList plains = new MobList();
    public static double mobHP=1;
    public static Economy eco;
    public static World w;
    static Predicate<Entity> testplayer = p -> (p instanceof Player);  
    static Predicate<Entity> testmob = p -> (p instanceof Mob);
	BukkitRunnable savetimer = new BukkitRunnable() {
		@Override
		public void run() {
			saveAll();
			log.info("clicker autosave");
		}
	};
	BukkitRunnable anticheat = new BukkitRunnable() {
		@Override
		public void run() {
			players.forEach((id, cplayer) -> cplayer.clickpertick=0);
		}
	};
	//savetimer.runTaskTimerAsynchronously(this, 2000L, 18000L);
	BukkitRunnable bossBarTimer = new BukkitRunnable() {
		@Override
		public void run() {
			if (clickBlock!=null) {
				World w = clickBlock.getWorld();
				if (w.getChunkAt(clickBlock).isLoaded()&&(clicking==null||clicking.mob==null||clicking.mob.isDead())) {
					NewMob(clicking);
				}
				eraseVisible();
				for (Entity ent : clickBlock.getWorld().getNearbyEntities(clickBlock.getLocation(), 10, 10, 10, testplayer)) {
					Player p = (Player) ent;
					CPlayer cp = players.get(p.getUniqueId());
					BossBar bar = cp.bar;
					cp.coinsbar.setVisible(true);
					cp.coinsbar.addPlayer(p);
					double pr = cp.stat.power*cp.stat.power*10;
					double progress = (double)cp.stat.coins/pr;
	    			if(progress>=1) 
	    				progress = 1;
					cp.coinsbar.setProgress(progress);
					bar.setTitle("Счёт: "+cp.stat.score);
					bar.setVisible(true);
					bar.addPlayer(p);
					bar.setProgress(mobHP);
				}
			}
		}
	};
	BukkitRunnable topUpdate = new BukkitRunnable() {
		@Override
		public void run() {
			updateTop();
		}
	};
	
	@Override
    public void onDisable() {
		saveAll();
		if (clicking!=null)
			clicking.mob.remove();
    }

    @Override
    public void onEnable() {
    	RegisteredServiceProvider<Economy> reg = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (reg != null) {
            eco = reg.getProvider();
        }
    	plugin = this;
        configFile = new File(getDataFolder(), "config.yml"); 
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
        	try {
				configFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadYamls();
        loadScore();
        String worldName = config.getString("block.world", null);
        int x = config.getInt("block.x", 0);
        int y = config.getInt("block.y", 0);
        int z = config.getInt("block.z", 0);
        
        if (worldName!=null) {
        	w = Bukkit.getWorld(worldName);
        	if (w!=null) {
	        	clickBlock = w.getBlockAt(x, y, z);
	        	loc = Main.clickBlock.getLocation().clone().add(0.5, -1, 0.5);
	        }
        }
        getServer().getPluginCommand("clickerset").setExecutor(new setCommand());
        getServer().getPluginCommand("clicks").setExecutor(new ClicksCommand());
        getServer().getPluginCommand("clicker").setExecutor(new ClickerCommand());
    	getServer().getPluginManager().registerEvents(new EventsListener(), this);
    	savetimer.runTaskTimerAsynchronously(this, 2000L, 1800L);
    	bossBarTimer.runTaskTimer(this, 20L, 40L);
    	anticheat.runTaskTimer(this, 20L, 2L);
    	topUpdate.runTaskTimer(this, 60L, 72000L);
    }
    
    public static void loadYamls() {
        try {
            config.load(configFile); //loads the contents of the File to its FileConfiguration
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveYamls() {
        try {
            config.save(configFile); //saves the FileConfiguration to its File
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void eraseVisible() {
    	players.forEach((id, cplayer) -> { 
			cplayer.bar.setVisible(false);
			cplayer.coinsbar.setVisible(false);
		}
		);
    }
    
    public static void loadScore() {
    	File f = new File(Main.plugin.getDataFolder()+File.separator+"Players");
    	for (File file : f.listFiles()) {
    		String uuid = file.getName().split("\\.")[0];
    		PlayerStat stat = getFromFile(file);
    		Main.players.put(UUID.fromString(uuid), new CPlayer(stat));
    	}
    } 
    
    public static void NewMob(ClickMob previos) {
    	clicking = new ClickMob(previos);
    	w.getNearbyEntities(loc, 10, 10, 10, testplayer).forEach(e -> ((Player)e).setVelocity(e.getLocation().toVector().subtract(loc.toVector()).normalize()));
    	mobHP = 1;
    	log.info("Create mob");
    } 
    
    public static void saveAll() {
    	players.forEach((id, cplayer) -> save(id, cplayer));
    }
    
    public static void save(UUID id, CPlayer cplayer) {
    	File f = new File(Main.plugin.getDataFolder()+File.separator+"Players", id.toString()+".json");
		f.getParentFile().mkdirs();
		try(FileWriter writer = new FileWriter(f, false)) {
			String json = gson.toJson(cplayer.stat);
			writer.write(json);
			writer.close();
        }
        catch(IOException ex) {
            log.info(ex.toString());
        } 
    }
    

	public static void updateTop() {
		topchik = new ArrayList<Kostili>();
    	players.entrySet().stream().sorted(Map.Entry.comparingByValue(new ScoreComparator())).forEach(Main::addd);
    	if (topchik.size()>0) {
    		for (int i =0;i<topchik.size();i++) {
    			log.info(""+topchik.get(i).player.stat.score);
    		}
    	}
    }
    
	public static void addd(Map.Entry<UUID, CPlayer> in) {
		topchik.add(new Kostili(in.getKey(), in.getValue()) );
    }
	
    public static PlayerStat read(UUID id) {
    	File f = new File(Main.plugin.getDataFolder()+File.separator+"Players", id.toString()+".json");
		return getFromFile(f);
    }
    
    public static PlayerStat getFromFile(File f) {
    	f.getParentFile().mkdirs();
    	PlayerStat stat;
    	if (f.exists()) {
			FileReader fr;
			String json = "";
			try {
				fr = new FileReader(f);
				Scanner scan = new Scanner(fr);
		        while (scan.hasNextLine()) {
		            json = json.concat(scan.nextLine());
		            //log.info(json);
		        }
		        scan.close();
		        fr.close();
			} catch (FileNotFoundException e1) {
				log.info("open error");
			} catch (IOException e1) {
				log.info("error");
			}
			stat = gson.fromJson(json, PlayerStat.class);
		}
		else {
			stat = new PlayerStat();
		}
    	return stat;
    }
    
}
