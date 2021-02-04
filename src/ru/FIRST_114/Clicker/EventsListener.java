package ru.FIRST_114.Clicker;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.FIRST_114.Clicker.PlayerData.CPlayer;
import ru.FIRST_114.Clicker.PlayerData.PlayerStat;

public class EventsListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) 
	{
		Player player = e.getPlayer();
		PlayerStat stat = Main.plugin.bd.getOrCreatePlayerStats(player);
		Main.plugin.players.put(player, new CPlayer(stat));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) 
	{
		Player player = e.getPlayer();
		Main.plugin.removePlayer(player);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) 
	{
		if (e.getEntity()==Main.plugin.clicking) {
			e.getDrops().clear();
		}
			
	}
	
	@EventHandler
	public void onPlayerHitMob(EntityDamageByEntityEvent e) 
	{
		Entity ent = e.getEntity();
		if (ent==Main.plugin.clicking) {
			Entity entdamager = e.getDamager();
			if (entdamager instanceof Player) {
				AttributeInstance maxhealth = Main.plugin.clicking.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				Player p = (Player) entdamager;
				CPlayer cp = Main.plugin.players.get(p);
				PlayerStat stat = cp.stat;
				BossBar bar = cp.bar;
				int power = stat.power;
				int clicks = cp.clickpertick;
				
				if (!Main.plugin.clicking.isDead()&&clicks<1)
				{
					Main.plugin.w.spawnParticle(Particle.CRIT_MAGIC, Main.plugin.clicking.getEyeLocation(), 3);
					Main.plugin.clicking.damage(((double)power)/10);
					Main.plugin.clicking.setNoDamageTicks(0);
					if (stat.score<5000000)
						stat.score = stat.score + power;
					Main.plugin.mobHP = Main.plugin.clicking.getHealth()/maxhealth.getValue();
					bar.setTitle("Счёт: "+stat.score);
					cp.clickpertick++;
					bar.setProgress(Main.plugin.mobHP);
				}
			}
			e.setCancelled(true);
		}
	}

}
