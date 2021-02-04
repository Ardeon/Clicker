package ru.FIRST_114.Clicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;
import ru.FIRST_114.Clicker.PlayerData.CPlayer;
import ru.FIRST_114.Clicker.PlayerData.Kostili;
import ru.FIRST_114.Clicker.bd.SQLite;

public class Main extends JavaPlugin {
	public static Main plugin;
	public HashMap<Player,CPlayer> players = new HashMap<Player,CPlayer>();
    public File configFile;           
    public YamlConfiguration config;
    public Block clickBlock;
    public Location loc;
    public ArrayList<Kostili> topchik = null;
    public Mob clicking;
    public MobList plains = new MobList();
    public double mobHP=1;
    public Economy eco;
    public World w;
    Predicate<Entity> testplayer = p -> (p instanceof Player);  
    Predicate<Entity> testmob = p -> (p instanceof Mob);
    SQLite bd;
	BukkitRunnable savetimer = new BukkitRunnable() {
		@Override
		public void run() {
			saveAll();
			getLogger().info("clicker autosave");
		}
	};
	BukkitRunnable anticheat = new BukkitRunnable() {
		@Override
		public void run() {
			players.forEach((id, cplayer) -> cplayer.clickpertick=0);
		}
	};
	BukkitRunnable bossBarTimer = new BukkitRunnable() {
		@Override
		public void run() {
			if (clickBlock!=null) {
				World w = clickBlock.getWorld();
				if (w.getChunkAt(clickBlock).isLoaded()&&(clicking==null||clicking.isDead())) {
					NewMob();
				}
				eraseVisible();
				for (Entity ent : clickBlock.getWorld().getNearbyEntities(clickBlock.getLocation(), 10, 10, 10, testplayer)) {
					Player p = (Player) ent;
					CPlayer cp = players.get(p);
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
			clicking.remove();
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
        bd = new SQLite(this);
        String worldName = config.getString("block.world", null);
        int x = config.getInt("block.x", 0);
        int y = config.getInt("block.y", 0);
        int z = config.getInt("block.z", 0);
        
        if (worldName!=null) {
        	w = Bukkit.getWorld(worldName);
        	if (w!=null) {
	        	clickBlock = w.getBlockAt(x, y, z);
	        	loc = clickBlock.getLocation().clone().add(0.5, -1, 0.5);
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
    
    public void loadYamls() {
        try {
            config.load(configFile); //loads the contents of the File to its FileConfiguration
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveYamls() {
        try {
            config.save(configFile); //saves the FileConfiguration to its File
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void eraseVisible() {
    	players.forEach((id, cplayer) -> { 
			cplayer.bar.setVisible(false);
			cplayer.coinsbar.setVisible(false);
		}
		);
    }
    
    public void NewMob() {
    	EntityType type = plains.getRandom();
    	clicking = (Mob) w.spawnEntity(loc, type);
    	mobHP = 1;
    	plugin.getLogger().info("Create mob");
    } 
    
    public void saveAll() {
    	players.forEach((id, cplayer) -> save(id, cplayer));
    }
    
    public void save(Player player, CPlayer cplayer) {
    	bd.saveStats(player.getUniqueId().toString().toLowerCase(), cplayer.stat);
    }
    

	public void updateTop() {
		topchik = bd.getTopPlayerStats();
    }
    
    
}
