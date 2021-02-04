package ru.FIRST_114.Clicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    public Location currentLocation;
    public Location[] locations = new Location[4];
    public ArrayList<Kostili> topchik = null;
    public Mob clicking;
    EntityType type;
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
			if (currentLocation!=null) {
				World w = currentLocation.getWorld();
				if (w.getChunkAt(currentLocation).isLoaded()&&(clicking==null||clicking.isDead())) {
					NewMob();
				}
				eraseVisible();
				for (Entity ent : currentLocation.getWorld().getNearbyEntities(currentLocation, 10, 10, 10, testplayer)) {
					Player p = (Player) ent;
					CPlayer cp = players.get(p);
					BossBar bar = cp.bar;
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
        load();
        getServer().getPluginCommand("clickerset").setExecutor(new setCommand());
        getServer().getPluginCommand("clicks").setExecutor(new ClicksCommand());
        getServer().getPluginCommand("clicker").setExecutor(new ClickerCommand());
        getServer().getPluginCommand("clickerreload").setExecutor(new ClickerReloadCommand());
    	getServer().getPluginManager().registerEvents(new EventsListener(this), this);
    	savetimer.runTaskTimerAsynchronously(this, 2000L, 30000L);
    	bossBarTimer.runTaskTimer(this, 20L, 40L);
    	anticheat.runTaskTimer(this, 20L, 2L);
    	topUpdate.runTaskTimer(this, 60L, 7200L);
    }
    
    public void load() {
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
        
        if (worldName!=null) {
        	w = Bukkit.getWorld(worldName);
        	if (w!=null) {
	        	for (int i = 1; i <= 4; i++) {
	        		locations[i-1] = config.getLocation("block."+i, null);
	        	}
	        	if (locations[0]!=null)
	        		currentLocation = locations[0];
	        }
        }
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
		}
		);
    }
    
    public void NewMob() {
    	type = plains.getRandom();
    	clicking = (Mob) w.spawnEntity(currentLocation, type);
    	clicking.setAI(false);
    	clicking.setCanPickupItems(false);
		PotionEffect ef = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false, false);
    	ef.apply(clicking);
    	AttributeInstance maxhealth = clicking.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    	int random = (int) (5 + Math.random()*6);
    	maxhealth.setBaseValue(random);
    	clicking.setHealth(random);
    	mobHP = 1;
    	w.getNearbyEntities(currentLocation, 10, 10, 10, testplayer).forEach(
    			e -> ((Player)e).setVelocity(e.getLocation().toVector().subtract(currentLocation.toVector()).normalize().multiply(0.5)));
    	switch (type) {
    	case PIG:
    		clicking.setCustomName("Свиния Пучкова");
    		w.getNearbyEntities(currentLocation, 20, 20, 20, testplayer).forEach(e -> ((Player)e).sendMessage("§6§nДементий,§r§9 народ требует свиней!"));
    		break;
    	case BEE:
    	case VEX:
    	case PARROT:
    		clicking.teleport(currentLocation.clone().add(0, 1, 0));
		default:
			break;
    	}
    		
    }
    
    public void randomTeleport() {
    	int random = (int) (Math.random()*4);
    	if (locations[random]!=null)
    		currentLocation = locations[random];
	    	switch (type) {
	    	case BEE:
	    	case VEX:
	    	case PARROT:
	    		clicking.teleport(currentLocation.clone().add(0, 1, 0));
	    	default:
				break;
	    	}
    	clicking.teleport(currentLocation);
    }
    
    public void saveAll() {
    	players.forEach((id, cplayer) -> save(id));
    }
    
    public void save(Player player) {
    	bd.saveStats(player.getUniqueId().toString().toLowerCase(), players.get(player).stat);
    }
    
    public void removePlayer(Player player) {
    	save(player);
    	players.remove(player);
    }

	public void updateTop() {
		topchik = bd.getTopPlayerStats();
    }
    
    
}
