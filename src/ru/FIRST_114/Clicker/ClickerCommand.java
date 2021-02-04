package ru.FIRST_114.Clicker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.FIRST_114.Clicker.PlayerData.PlayerStat;

public class ClickerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ((sender instanceof Player)) 
		{
			Player p = (Player) sender;
			PlayerStat stat = Main.plugin.players.get(p).stat;
			int power = stat.power;
			//int score = stat.score;
			int coins = stat.coins;
			int price = power*power*10;
			if (args.length==1)
			{
				switch (args[0])
				{
				case "addpower":
				{
					if (coins>=price)
					{
						if (power>40) {
							p.sendMessage("§5У вас слишком много силы кликов");
							break;
						}
							
						stat.power++;
						stat.coins-=price;
						p.sendMessage("§5Сила кликов увеличена");
						p.sendMessage("§5Сила кликов: "+stat.power);
						p.sendMessage("§5Монетки: "+stat.coins);
						p.sendMessage("§5Счёт: "+stat.score);
					}
					else
					{
						p.sendMessage("§5У вас недостаточно монеток");
						p.sendMessage("§5Монетки: "+stat.coins);
						p.sendMessage("§5текущая цена: "+price);
					}
					break;
				}
				/*
				case "sellpower":
				{
					if (power>5)
					{
						Main.eco.depositPlayer(p, price/10);
						stat.power=1;
						p.sendMessage("§5Вы обменяли силу кликов на деньги");
						p.sendMessage("§5Сила кликов: "+stat.power);
						p.sendMessage("§5Счёт: "+stat.score);
					}
					else
					{
						p.sendMessage("§5У вас недостаточно силы кликов");
					}
					break;
				}
				*/
				case "reward":
				{
					if (stat.score>1000000)
					{
						
							Main.plugin.eco.depositPlayer(p, stat.score/10);
							stat.autoclickers++;
							stat.score=0;
							stat.coins=0;
							stat.power=0;
							//p.sendMessage("§5Вы обменяли 10000 кликов на 1000$");
							//p.sendMessage("§5Сила кликов: "+stat.power);
							//p.sendMessage("§5Счёт: "+stat.score);
							//p.sendMessage("§5Монетки: "+stat.coins);
						
					}
					else
					{
						p.sendMessage("§5У вас недостаточно кликов");
					}
					break;
				}
				default:
					return false;
				}
			}
			else {
				p.sendMessage("§5Сила кликов: "+stat.power);
				p.sendMessage("§5Счёт: "+stat.score);
				p.sendMessage("§2Увеличить силу кликов на 1: /clicker addpower, текущая цена: "+price);
				//if (power>5)
					//p.sendMessage("§2Обменять силу кликов на деньги: /clicker sellpower, за "+power+" силы кликов вы получите:"+price/10+"$");
			}
			
			return true;
			
		}
		return false;
	}

}
