package ru.FIRST_114.Clicker;

import java.util.HashSet;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ClickMob {
	EntityType type = EntityType.ZOMBIE;
	ClickMob[] previousMobs = new ClickMob[5]; //{null,null,null};
	MobSkill skill;
	Mob mob;
	
	public ClickMob() {
		
	}
	
	private ClickMob getPrevios() {
		return previousMobs[0];
	}
	
	@SuppressWarnings("unused")
	public ClickMob(ClickMob previos) {
		HashSet<EntityType> types = new HashSet<EntityType>();
		previousMobs[0] = previos;
		if (previousMobs[0]!=null) {
			types.add(previousMobs[0].mob.getType());
		}
		for (int i = 0;i<=3;i++) {
			if (previousMobs[i]!=null) {
				previousMobs[i+1] = previousMobs[i].getPrevios();
				if (previousMobs[i+1]!=null) {
					types.add(previousMobs[i+1].mob.getType());
				}
			}
		}
		if (previousMobs[4]!=null) {
			ClickMob last = previousMobs[4].getPrevios();
			last = null;
		}
		type = Main.plains.exept(types).getRandom();
		mob = (Mob)  Main.w.spawnEntity(Main.loc, type);
		mob.setAI(false);
		mob.setCanPickupItems(false);
		PotionEffect ef = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false, false);
    	ef.apply(mob);
    	AttributeInstance maxhealth = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    	maxhealth.setBaseValue(1000);
    	mob.setHealth(1000);
    	/*
    	if (clickMob.getType().equals(EntityType.PIG))
    	{
    		maxhealth.setBaseValue(500);
        	clickMob.setHealth(500);
        	clickMob.setCustomName("Свиния Пучкова");
        	w.getNearbyEntities(loc, 20, 20, 20, testplayer).forEach(e -> ((Player)e).sendMessage("§6§nДементий,§r§9 народ требует свиней!"));
    	}
    	*/
	}
	/*
    public void TntBlust() {
    	mob.getNearbyEntities(7, 7, 7).forEach(e -> {
    		(e).setVelocity(e.getLocation().toVector().subtract(clickMob.getLocation().toVector()).normalize().multiply(2).setY(0.8));
    		if (e instanceof Player)
    			((Player)e).playSound(clickMob.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    	}
    	);
    	mob.remove();
    }
	*/
}
