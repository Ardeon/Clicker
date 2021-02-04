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

import ru.FIRST_114.Clicker.PlayerData.CPlayer;
import ru.FIRST_114.Clicker.PlayerData.PlayerStat;

public class EventsListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) 
	{
		Player player = e.getPlayer();
		if (!Main.plugin.players.containsKey(player)) {
			PlayerStat stat = Main.plugin.bd.getOrCreatePlayerStats(player);
			Main.plugin.players.put(player, new CPlayer(stat));
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) 
	{
		if (e.getEntity()==Main.plugin.clicking) {
			/*
			if (Main.currentSkill!=null)
				switch (Main.currentSkill) {
		    	case GOLDEN:
		    		for (Entity ent : Main.clickBlock.getWorld().getNearbyEntities(Main.clickBlock.getLocation(), 10, 10, 10, Main.testplayer)) {
		    			Player p = (Player) ent;
		    			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Вы подобрали 5 монет").create());
		    			PlayerStat stat = Main.players.get(p.getUniqueId()).stat;
		    			stat.coins+=5;
		    		}
		    		break;
		    	default:
		    		for (Entity ent : Main.clickBlock.getWorld().getNearbyEntities(Main.clickBlock.getLocation(), 10, 10, 10, Main.testplayer)) {
		    			Player p = (Player) ent;
		    			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Вы подобрали монету").create());
		    			PlayerStat stat = Main.players.get(p.getUniqueId()).stat;
		    			stat.coins+=1;
		    		}
		    		break;
				}
			else {*/
			for (Entity ent : Main.plugin.clickBlock.getWorld().getNearbyEntities(Main.plugin.clickBlock.getLocation(), 10, 10, 10, Main.plugin.testplayer)) {
    			Player p = (Player) ent;
    			//p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Вы подобрали монету").create());
    			CPlayer cp = Main.plugin.players.get(p);
    			PlayerStat stat = cp.stat;
    			double pr = stat.power*stat.power*10;
    			int addcoins = (int) ((1 + stat.score/1000)*Math.pow(1.5,stat.autoclickers));
    			stat.coins+=addcoins;
    			
    			double progress = (double)stat.coins/pr;
    			String s="";
    			if(progress>=1) {
    				progress = 1;
    				s="Вы можите купиль лучшение кликов";
    			}
    			p.sendTitle("Вы подобрали монетки: +" + addcoins, s, 6, 30, 6);
    			cp.coinsbar.setTitle("Монеты: "+stat.coins);
    			cp.coinsbar.setProgress(progress);
    		}
			e.getDrops().clear();
			//}
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
					/*
					if (Main.currentSkill!=null)
					switch (Main.currentSkill) {
			    	case MISS:
			    		double rand = Math.random() * 100;
						if (rand > 60) {
							Main.clickMob.damage(((double)power)/10);
							Main.clickMob.setNoDamageTicks(0);
							stat.score = stat.score + power;
						}
			    		break;
			    	case TNT:
			    		Main.TntBlust();
			    		break;
			    	case ROTATOR:
			    		Main.clickMob.damage(((double)power)/10);
						Main.clickMob.setNoDamageTicks(0);
						stat.score = stat.score + power;
						double pitch = Math.random() * 180;
						double ang = Math.random() * 180;
						p.setRotation((float) pitch, (float) ang);
			    		break;
			    	case CREEPER:
			    		break;
			    	case GOLDEN:
			    		Main.clickMob.damage(((double)power)/10);
						Main.clickMob.setNoDamageTicks(0);
						stat.score = stat.score + power;
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Вы подобрали монету").create());
						stat.coins++;
			    		break;
			    	case ROCK:
			    		Main.clickMob.damage(0.1);
						Main.clickMob.setNoDamageTicks(0);
						stat.score = stat.score + 1;
			    		break;
			    	default:
			    		Main.clickMob.damage(((double)power)/10);
						Main.clickMob.setNoDamageTicks(0);
						stat.score = stat.score + power;
			    		break;
			    	}*/
					//else {
						Main.plugin.clicking.damage(((double)power)/10);
						Main.plugin.clicking.setNoDamageTicks(0);
						if (stat.score<5000000)
							stat.score = stat.score + power;
					//}
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
