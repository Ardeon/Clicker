package ru.ardeon.Clicker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClickerReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		Main.plugin.startClicker();
		Main.plugin.load();
		Main.plugin.updateTop();
		return true;
	}

}
