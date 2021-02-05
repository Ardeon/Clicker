package ru.FIRST_114.Clicker.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class CPlayer {
	public BossBar bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SEGMENTED_10);
	public PlayerStat stat;
	public int clickpertick=0;
	
	
	public CPlayer(){
		stat = new PlayerStat();
		bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SOLID);
		clickpertick=0;
	}
	
	public CPlayer(PlayerStat st){
		stat = st;
		bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SOLID);
		clickpertick=0;
	}
}
