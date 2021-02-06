package ru.FIRST_114.Clicker;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class setCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ((sender instanceof Player)) 
		{
			Player p = (Player) sender;
			Block b = p.getLocation().getBlock();
			if (b!=null)
			{
				int n = 1;
				if (args.length>0) {
					try {
						n = Integer.parseInt(args[0]);
					}catch (Exception e) {
						n = 0;
					}
				}
				if (n>0 && n<=7) {
					Main.plugin.config.set("block.world", b.getWorld().getName());
					Main.plugin.config.set("block."+n, b.getLocation().add(0.5, 0, 0.5));
					Main.plugin.currentLocation = b.getLocation().add(0.5, 0, 0.5);
					Main.plugin.saveYamls();
				}
				
			}
			
			return true;
			
		}
		return false;
	}

}
