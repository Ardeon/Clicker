package ru.ardeon.Clicker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.ardeon.Clicker.PlayerData.PlayerStat;

public class ClicksCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ((sender instanceof Player)) 
		{
			Player p = (Player) sender;
			PlayerStat stat = Main.plugin.players.get(p).stat;
			if (args.length==1)
			{
				int removed;
				try {
					removed=Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e)
				{
					removed=0;
					return false;
				}
				stat.score-=removed;
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

}
