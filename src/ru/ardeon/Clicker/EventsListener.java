package ru.ardeon.Clicker;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;

import ru.ardeon.Clicker.PlayerData.CPlayer;
import ru.ardeon.Clicker.PlayerData.PlayerStat;

public class EventsListener implements Listener {
	Main plugin;
	
	EventsListener(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) 
	{
		Player player = e.getPlayer();
		PlayerStat stat = plugin.bd.getOrCreatePlayerStats(player);
		plugin.players.put(player, new CPlayer(stat));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) 
	{
		Player player = e.getPlayer();
		plugin.removePlayer(player);
	}

	@EventHandler
	public void onServerStart(ServerLoadEvent e)
	{
		plugin.startClicker();
	}
	
	@EventHandler
	public void onEntitySplit(SlimeSplitEvent e) 
	{
		Entity entity = e.getEntity();
		Slime slime = (Slime)entity;
		if (!slime.hasAI()) {
			e.setCancelled(true);
		}
			
	}
	
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e) 
	{
		Entity entity = e.getEntity();
		if (entity==plugin.clicking) {
			e.setCancelled(true);
		}
			
	}
	
	@EventHandler
	public void onEntityDamageOther(EntityDamageByEntityEvent e) 
	{
		Entity entity = e.getDamager();
		if (entity==plugin.clicking) {
			e.setCancelled(true);
		}
			
	}
	
	@EventHandler
	public void onMountEntity(EntityMountEvent e) 
	{
		Entity entity = e.getMount();
		if (entity==plugin.clicking) {
			e.setCancelled(true);
		}
			
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) 
	{
		Entity entity = e.getEntity();
		if (entity==plugin.clicking) {
			e.getDrops().clear();//EntityType.
		}
			
	}
	
	@EventHandler
	public void onPlayerHitMob(EntityDamageByEntityEvent e) 
	{
		Entity ent = e.getEntity();
		if (ent==plugin.clicking) {
			Entity entdamager = e.getDamager();
			if (entdamager instanceof Player) {
				AttributeInstance maxhealth = plugin.clicking.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				Player p = (Player) entdamager;
				CPlayer cp = plugin.players.get(p);
				PlayerStat stat = cp.stat;
				BossBar bar = cp.bar;
				int clicks = cp.clickpertick;
				
				if (!plugin.clicking.isDead()&&clicks<1)
				{
					
					plugin.w.spawnParticle(Particle.CRIT_MAGIC, plugin.clicking.getEyeLocation(), 3);
					plugin.clicking.damage(1);
					plugin.clicking.setNoDamageTicks(0);
					if (stat.score<2000000000)
						stat.score += (int) Math.pow(2, stat.autoclickers);
					plugin.mobHP = plugin.clicking.getHealth()/maxhealth.getValue();
					bar.setTitle("Счёт: "+stat.score);
					cp.clickpertick++;
					bar.setProgress(plugin.mobHP);
				}
				if (plugin.clicking.isDead() ) {
					
					if (plugin.clicking.getType().equals(EntityType.CREEPER)) {
						plugin.currentLocation.getWorld().createExplosion(plugin.currentLocation, 0, false, false);
						plugin.w
						.getNearbyEntities(plugin.currentLocation, 10, 10, 10, plugin.testPlayer).forEach(
				    			entity -> ((Player)entity)
				    			.setVelocity(entity.getLocation().toVector().subtract(plugin.currentLocation.toVector()).normalize().multiply(1.2)));
						p.setVelocity(p.getLocation().toVector().subtract(plugin.currentLocation.toVector()).normalize().multiply(2.5).add(new Vector(0,1.7,0)));
						
					} else {
						plugin.randomTeleport();
						plugin.NewMob();
					}
				}
			}
			e.setCancelled(true);
		}
	}

}
