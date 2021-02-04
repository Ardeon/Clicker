package ru.FIRST_114.Clicker;

import java.util.Comparator;

import ru.FIRST_114.Clicker.PlayerData.CPlayer;

public class ScoreComparator implements Comparator<CPlayer>{

	@Override
	public int compare(CPlayer o1, CPlayer o2) {
		return o2.stat.score - o1.stat.score;
	}

}
