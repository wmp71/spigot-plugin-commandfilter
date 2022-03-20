package com.pb.commandfilter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandFilter extends JavaPlugin implements Listener {
	FileConfiguration config = this.getConfig();
	
	@Override
    public void onEnable() {
		config.options().copyDefaults(true);
		this.saveConfig();

		this.getCommand("commandfilter").setExecutor(new CommandFilterSet(this, config, "command"));
		this.getCommand("chatfilter").setExecutor(new CommandFilterSet(this, config, "chat"));
		
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(player == null) return;
		if(!this.getConfig().getBoolean("filter-ops") && player.isOp()) return;
		String msg = event.getMessage();
		List<String> filters = Stream.concat(this.getConfig().getStringList("chat-filters._").stream(), this.getConfig().getStringList("chat-filters." + player.getUniqueId()).stream()).collect(Collectors.toList());
		
		for(String regex : filters) {
			try {
		        if(msg.matches(regex)) {
		        	player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("filtered-chat-message")));
		    		event.setCancelled(true);
		    		return;
		        }
		    } catch (PatternSyntaxException e) {
		        continue;
		    }
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(player == null) return;
		if(!this.getConfig().getBoolean("filter-ops") && player.isOp()) return;
		String cmd = event.getMessage().replaceFirst("[/]", "");

		List<String> filters = Stream.concat(this.getConfig().getStringList("command-filters._").stream(), this.getConfig().getStringList("command-filters." + player.getUniqueId()).stream()).collect(Collectors.toList());
		
		for(String regex : filters) {
			try {
		        if(cmd.matches(regex)) {
		        	/* "&e&l[오류!]: &f&r명령어에 허용되지 않은 키워드가 포함되어 있습니다" */
		        	player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("filtered-command-message")));
		    		event.setCancelled(true);
		    		return;
		        }
		    } catch (PatternSyntaxException e) {
		        continue;
		    }
		}
	}

    @Override
    public void onDisable() {
		
    }
}

