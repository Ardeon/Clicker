package ru.FIRST_114.Clicker;

import org.bukkit.Bukkit;
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
			return execute(p);
		}
		if (args.length==1) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p!=null) {
				return execute(p);
			}
		}
		return false;
	}

	boolean execute(Player p) {
		PlayerStat stat = Main.plugin.players.get(p).stat;
		long price = stat.getBoostprice();
		if (stat.score>=price) {
			stat.score -= price;
			stat.autoclickers++;
			p.sendMessage("§aВы удвоили множитель своих кликов");
		}
		else
			p.sendMessage("§eВам надо иметь " + price + "кликов");
		return true;
	}
	
}
