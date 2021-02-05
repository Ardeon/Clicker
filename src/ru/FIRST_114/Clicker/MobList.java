package ru.FIRST_114.Clicker;

import java.util.HashSet;

import org.bukkit.entity.EntityType;

public class MobList {
	public HashSet<EntityType> types = new HashSet<EntityType>();
	
	public MobList() {
		types.clear();
    	types.add(EntityType.ZOMBIE);
    	types.add(EntityType.SKELETON);
    	types.add(EntityType.HUSK);
    	types.add(EntityType.STRAY);
    	types.add(EntityType.ZOMBIE_VILLAGER);
    	types.add(EntityType.EVOKER);
    	types.add(EntityType.VINDICATOR);
    	types.add(EntityType.PILLAGER);
    	types.add(EntityType.RAVAGER);
    	types.add(EntityType.DROWNED);
    	types.add(EntityType.HOGLIN);
    	types.add(EntityType.ZOGLIN);
    	types.add(EntityType.PIGLIN);
    	types.add(EntityType.GUARDIAN);
    	types.add(EntityType.ZOMBIFIED_PIGLIN);
    	types.add(EntityType.IRON_GOLEM);
    	types.add(EntityType.POLAR_BEAR);
    	types.add(EntityType.WOLF);
    	types.add(EntityType.PANDA);
    	types.add(EntityType.SPIDER);
    	types.add(EntityType.STRIDER);
    	types.add(EntityType.OCELOT);
    	types.add(EntityType.CAT);
    	types.add(EntityType.TURTLE);
    	types.add(EntityType.PIG);
    	types.add(EntityType.FOX);
    	types.add(EntityType.COW);
    	types.add(EntityType.MUSHROOM_COW);
    	types.add(EntityType.HORSE);
    	types.add(EntityType.MULE);
    	types.add(EntityType.DONKEY);
    	types.add(EntityType.CHICKEN);
    	types.add(EntityType.CREEPER);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public MobList exept(HashSet<EntityType> etypes) {
		MobList newList = new MobList();
		newList.types.remove(etypes);
		return newList;
	}
	
	public EntityType getRandom() {
		int size = types.size();
		int a = (int) (Math.random()*(size));
		return (EntityType)types.toArray()[a];
	}
	
}
