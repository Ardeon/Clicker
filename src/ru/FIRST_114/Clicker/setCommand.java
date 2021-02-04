package ru.FIRST_114.Clicker;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;


public class setCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if ((sender instanceof Player)) 
		{
			Player p = (Player) sender;
			RayTraceResult r = p.rayTraceBlocks(6);
			Block b = r.getHitBlock().getRelative(r.getHitBlockFace().getOppositeFace());
			if (b!=null)
			{
				Main.plugin.config.set("block.world", b.getWorld().getName());
				Main.plugin.config.set("block.x", b.getX());
				Main.plugin.config.set("block.y", b.getY());
				Main.plugin.config.set("block.z", b.getZ());
				Main.plugin.clickBlock = b;
				Main.plugin.saveYamls();
			}
			
			return true;
			
		}
		return false;
	}

}
