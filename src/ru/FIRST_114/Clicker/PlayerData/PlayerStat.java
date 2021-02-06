package ru.FIRST_114.Clicker.PlayerData;

public class PlayerStat {
	public int score;
	public int coins;
	public int autoclickers;
	public int power;
	
	public PlayerStat(){
		score = 0;
		coins = 0;
		autoclickers = 0;
		power = 1;
	}
	
	public long getBoostprice(){
		return 1000 * ((int) Math.pow(3, autoclickers));
	}
}
