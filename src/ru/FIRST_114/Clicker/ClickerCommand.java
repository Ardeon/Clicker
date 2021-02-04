package ru.FIRST_114.Clicker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClickerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (args.length==1)
		{
			switch (args[0])
			{
			case "updatetop":
			{
				Main.plugin.updateTop();
				return true;
			}
			default:
				return false;
			}
		}
		return false;
	}

}
