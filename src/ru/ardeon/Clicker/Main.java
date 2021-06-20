package ru.ardeon.Clicker;

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
import ru.ardeon.Clicker.PlayerData.CPlayer;
import ru.ardeon.Clicker.PlayerData.Kostili;
import ru.ardeon.Clicker.PlayerData.PlayerStat;
import ru.ardeon.Clicker.bd.SQLite;

public class Main extends JavaPlugin {
	public static Main plugin;
	public HashMap<Player,CPlayer> players = new HashMap<Player,CPlayer>();
    public File configFile;           
    public YamlConfiguration config;
    public Location currentLocation;
    public Location[] locations = new Location[7];
    private int position = 0;
    public ArrayList<Kostili> playersTop = null;
    public ArrayList<Kostili> previousTop = null;
    public Mob clicking;
    EntityType type;
    public MobList plains = new MobList();
    public double mobHP=1;
    public Economy eco;
    public World w;
    public TimeToReward timeToReward = new TimeToReward();
    Predicate<Entity> testPlayer = p -> (p instanceof Player);
    Predicate<Entity> testMob = p -> (p instanceof Mob);
    SQLite bd;
	BukkitRunnable saveTimer;
	BukkitRunnable antiCheat;
	BukkitRunnable clickerTimer;
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
		saveTimer.cancel();
		antiCheat.cancel();
		clickerTimer.cancel();
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
        getServer().getPluginCommand("clickboost").setExecutor(new ClickBoostCommand());
        getServer().getPluginCommand("clicks").setExecutor(new ClicksCommand());
        getServer().getPluginCommand("clicker").setExecutor(new ClickerCommand());
        getServer().getPluginCommand("clickerreload").setExecutor(new ClickerReloadCommand());
    	getServer().getPluginManager().registerEvents(new EventsListener(this), this);
    	
    	for (Player player : Bukkit.getOnlinePlayers()) {
    		PlayerStat stat = plugin.bd.getOrCreatePlayerStats(player);
    		players.put(player, new CPlayer(stat));
    	}
    }

	public void startClicker() {
		if (saveTimer != null) {
			try{
				if (!saveTimer.isCancelled()) {
					saveTimer.cancel();
				}
			} catch (IllegalStateException e) {
			}
		}
		if (clickerTimer != null) {
			try{
				if (!clickerTimer.isCancelled()) {
					clickerTimer.cancel();
				}
			} catch (IllegalStateException e) {
			}
		}
		if (antiCheat != null) {
			try{
				if (!antiCheat.isCancelled()) {
					antiCheat.cancel();
				}
			} catch (IllegalStateException e) {
			}
		}
		if (topUpdate != null) {
			try{
				if (!topUpdate.isCancelled()) {
					topUpdate.cancel();
				}
			} catch (IllegalStateException e) {
			}
		}
		saveTimer = new BukkitRunnable() {
			@Override
			public void run() {
				saveAll();
				timeToReward = new TimeToReward();
				getLogger().info("clicker autosave");
			}
		};
		antiCheat = new BukkitRunnable() {
			@Override
			public void run() {
				players.forEach((id, cplayer) -> cplayer.clickpertick=0);
			}
		};
		clickerTimer = new BukkitRunnable() {
			@Override
			public void run() {
				if (currentLocation!=null) {
					World w = currentLocation.getWorld();
					if (w.getChunkAt(currentLocation).isLoaded()&&(clicking==null||clicking.isDead())) {
						randomTeleport();
						NewMob();
					}
					eraseVisible();
					for (Entity ent : currentLocation.getWorld().getNearbyEntities(currentLocation, 10, 10, 10, testPlayer)) {
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
		topUpdate = new BukkitRunnable() {
			@Override
			public void run() {
				updateTop();
			}
		};

		saveTimer.runTaskTimerAsynchronously(this, 2000L, 30000L);
		clickerTimer.runTaskTimer(this, 20L, 10L);
		antiCheat.runTaskTimer(this, 20L, 4L);
		topUpdate.runTaskTimer(this, 60L, 1200L);
	}
    
    public void load() {
    	timeToReward = new TimeToReward();
    	configFile = new File(getDataFolder(), "config.yml"); 
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
        	try {
				configFile.createNewFile();
			} catch (IOException e) {
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
	        	for (int i = 1; i <= 7; i++) {
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
    	int hp = 10;
    	int players = w.getNearbyEntities(currentLocation, 10, 10, 10, testPlayer).size();
    	hp+=10* players;
    	maxhealth.setBaseValue(hp);
    	clicking.setHealth(hp);
    	mobHP = 1;
    	w.getNearbyEntities(currentLocation, 10, 10, 10, testPlayer).forEach(
    			e -> ((Player)e).setVelocity(e.getLocation().toVector().subtract(currentLocation.toVector()).normalize().multiply(0.5)));
    	switch (type) {
    	case PIG:
    		clicking.setCustomName("Свинья Пучкова");
    		w.getNearbyEntities(currentLocation, 20, 20, 20, testPlayer).forEach(e -> ((Player)e).sendMessage("§6§nДементий,§r§9 народ требует свиней!"));
    		break;
    	case CREEPER:
    		maxhealth.setBaseValue(3);
        	clicking.setHealth(3);
    		new BukkitRunnable() {
    			int i = 5;
    			@Override
    			public void run() {
    				if(i<1) {
    					clicking.remove();
    					
    					this.cancel();
    				}
    				else
    					w.getNearbyEntities(currentLocation, 20, 20, 20, testPlayer).forEach(e -> ((Player)e).sendTitle("§6Не БИТЬ", ""+i, 0, 20, 0));
    				if(!clicking.getType().equals(EntityType.CREEPER))
    					this.cancel();
    				i--;
    			}
    		}.runTaskTimer(plugin, 0, 20);
    		break;
		default:
			break;
    	}
    		
    }
    
    public void randomTeleport() {
    	if (Math.random()>0.5) {
    		position++;
    		if (position>=7)
    			position=0;
    	}
    	else {
    		position--;
    		if (position<0)
    			position=6;
    	}
    	
    	if (locations[position]!=null)
    		currentLocation = locations[position];
    	//clicking.teleport(currentLocation);
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
		saveAll();
		playersTop = bd.getTopPlayerStats(false);
		previousTop = bd.getTopPlayerStats(true);
    }
	
	public void ResetTop() {
		saveAll();
		eraseVisible();
		players.clear();
		bd.resetProgress();
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerStat stat = plugin.bd.getOrCreatePlayerStats(player);
			players.put(player, new CPlayer(stat));
		}
		updateTop();
    }
    
}
