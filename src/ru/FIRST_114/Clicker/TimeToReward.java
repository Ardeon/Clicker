package ru.FIRST_114.Clicker;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class TimeToReward {
	ZonedDateTime nextMonday;
	ZoneId here = ZoneId.systemDefault();
	
	public void calculateNewDate() {
		
	    ZonedDateTime now = ZonedDateTime.now(here);
		nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
		
	}
	public TimeToReward() {
		calculateNewDate();
	}
	public String getTimeToReward() {
		//long time = ChronoUnit.MILLIS.between(ZonedDateTime.now(here), nextMonday);
		long days = ChronoUnit.DAYS.between(ZonedDateTime.now(here), nextMonday);
		long hours = ChronoUnit.HOURS.between(ZonedDateTime.now(here), nextMonday)%24;
		long minuts = ChronoUnit.MINUTES.between(ZonedDateTime.now(here), nextMonday)%60;
		
		String string = "&e"+days+"&aд &e"+hours+"&aч &e"+minuts+"&aм";//"Dд HHч MMм""yyyy.MM.dd G 'at' HH:mm:ss z"
		return string;
	}
}
