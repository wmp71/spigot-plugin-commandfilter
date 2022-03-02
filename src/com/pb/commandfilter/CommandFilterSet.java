package com.pb.commandfilter;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandFilterSet implements CommandExecutor {
	private CommandFilter plugin;
	FileConfiguration config;
	String type;
	
	public CommandFilterSet(CommandFilter plugin, FileConfiguration config, String type) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.type = type;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if((sender instanceof Player && !sender.isOp()) && !sender.hasPermission("commandfilter.modifyconfig")) {
			/* &e&l[오류!]: &f&r권한이 부족합니다 */
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("no-permission-message")));
			return true;
		}
		
		if(args.length < 3) return false;
		
		String action = args[0];
		
		if(action.equals("config")) {
			String key = args[1];
			String val = args[2];
			if(!key.matches("^(filter[-]ops|filtered[-]chat[-]message|filtered[-]command[-]message|config[-]saved[-]message|no[-]permission[-]message|invalid[-]configuration[-]message)$")) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("invalid-configuration-message")));
				return true;
			}
			if(key.matches("^(filter[-]ops)$")) {
				if(!val.equals("true") && !val.equals("false")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("invalid-configuration-message")));
					return true;
				}
				config.set(key, Boolean.parseBoolean(val));
			} else {
				config.set(key, val);
			}
			plugin.saveConfig();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("config-saved-message")));
			return true;
		}
		
		String target = args[1];
		if(!target.equals("*")) {
			Player pp = Bukkit.getPlayer(target);
			if(pp == null) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("invalid-username-message")));
				return true;
			}
			target = "" + pp.getUniqueId();
		}
		String regex  = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		
		if(target.equals("*")) target = "_";
		List<String> filters = config.getStringList(type + "-filters." + target);
		
		if(action.equals("add")) {
			filters.add(regex);
		} else if(action.equals("remove")) {
			filters.remove(regex);
		} else {
			return false;
		}
		
		config.set(type + "-filters." + target, filters);
		plugin.saveConfig();
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("config-saved-message")));
		
		return true;
	}
}
