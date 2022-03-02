package com.pb.commandfilter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandFilter extends JavaPlugin implements Listener {
	FileConfiguration config = this.getConfig();
	
	@Override
    public void onEnable() {
		config.options().copyDefaults(true);
		this.saveConfig();
		
		this.getCommand("commandfilter").setExecutor(new CommandFilterSet(this, config));
		
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String cmd = event.getMessage().replace("/", "");
		List<String> filters = Stream.concat(this.getConfig().getStringList("_").stream(), this.getConfig().getStringList(player.getName()).stream()).collect(Collectors.toList());
		
		for(String regex : filters) {
			try {
		        if(cmd.matches(regex)) {
		        	player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l[오류!]: &f&r명령어에 허용되지 않은 키워드가 포함되어 있습니다"));
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

