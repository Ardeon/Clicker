package ru.FIRST_114.Clicker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.FIRST_114.Clicker.PlayerData.PlayerStat;

public class ClickBoostCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ((sender instanceof Player)) 
		{
			Player p = (Player) sender;
			PlayerStat stat = Main.plugin.players.get(p).stat;
			
			int boost = stat.autoclickers;
			long price = 1000 * ((int) Math.pow(3, boost));
			if (args.length!=0) {
				p.sendMessage("§eВам надо иметь " + price + "кликов");
				return true;
			}
			if (stat.score>=price) {
				stat.score -= price;
				stat.autoclickers++;
				p.sendMessage("§aВы удвоили множитель своих кликов");
			}
			else
				p.sendMessage("§eВам надо иметь " + price + "кликов");
			return true;
		}
		return false;
	}

}
