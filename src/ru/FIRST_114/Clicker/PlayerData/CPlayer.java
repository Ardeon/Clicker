package ru.FIRST_114.Clicker.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class CPlayer {
	public BossBar bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG);
	public BossBar coinsbar = Bukkit.createBossBar("coins", BarColor.YELLOW, BarStyle.SOLID, BarFlag.CREATE_FOG);
	public PlayerStat stat;
	public int clickpertick=0;
	
	
	public CPlayer(){
		stat = new PlayerStat();
		bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG);
		bar.removeFlag(BarFlag.CREATE_FOG);
		coinsbar = Bukkit.createBossBar("coins", BarColor.YELLOW, BarStyle.SOLID, BarFlag.CREATE_FOG);
		coinsbar.removeFlag(BarFlag.CREATE_FOG);
		coinsbar.setTitle("Монеты: ");
		clickpertick=0;
	}
	
	public CPlayer(PlayerStat st){
		stat = st;
		bar = Bukkit.createBossBar("hp", BarColor.GREEN, BarStyle.SOLID, BarFlag.CREATE_FOG);
		bar.removeFlag(BarFlag.CREATE_FOG);
		coinsbar = Bukkit.createBossBar("coins", BarColor.YELLOW, BarStyle.SOLID, BarFlag.CREATE_FOG);
		coinsbar.removeFlag(BarFlag.CREATE_FOG);
		coinsbar.setTitle("Монеты: ");
		clickpertick=0;
	}
}
