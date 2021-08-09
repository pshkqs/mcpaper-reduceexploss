package github.pashamaladec.reducexploss;

import github.pashamaladec.reducexploss.config.Config;
import io.papermc.paper.chat.ChatFormatter;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ReduceXpLoss extends JavaPlugin implements Listener
{
	private static Logger logger;
	private final Config config = new Config(getDataFolder().getParent() + "\\reduce-xp-loss.yml");
	
	@Override
	public void onEnable()
	{
		logger = getLogger();
		
		if(config.exists() == false)
			config.create();
		
		config.load();
		
		if(config.getConfig().contains("default") == false)
		{
			config.getConfig().set("default", 1d);
			config.save();
		}
		
		getCommand("reload-xploss-config").setExecutor(new ReloadConfigFile(config));
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	private void OnPlayerDied(PlayerDeathEvent event)
	{
		var player = event.getEntity().getPlayer();
		if(player == null)
			return;
		
		event.setShouldDropExperience(false);
		
		var oldLevel = getTotal(player);
		
		player.sendMessage(getToLevel(player) + "");
		
		var message = ChatColor.GRAY + "" + player.getLevel() + "L " + getToLevel(player) + "P (" + oldLevel + "P) ->> ";
		
		var reduced = getReducedExp(player);
		var diff = reduced - oldLevel;
		setExp(player, reduced);
		
		event.setKeepLevel(true);
		message += player.getLevel() + "L " + getToLevel(player) + "P (" + getTotal(player) + "P)";
		
		if(diff < 0)
			message += " | " + ChatColor.DARK_RED + (reduced - oldLevel) + "P";
		else if(diff > 0)
			message += " | " + ChatColor.DARK_GREEN + "+" + (reduced - oldLevel) + "P";
		else
			return;
		
		player.sendMessage(message);
		
	}
	
	private int getReducedExp(Player player)
	{
		var multiplier = (float) config.getConfig().getDouble("default");
		
		for (var i = 0; i <= player.getLevel(); i++)
		{
			if(config.getConfig().contains("above-" + i))
				multiplier = (float) config.getConfig().getDouble("above-" + i);
		}
		
		log("applying " + multiplier);
		player.sendMessage("applying " + multiplier);
		return Math.round(getTotal(player) * multiplier);
	}
	
	private void setExp(Player player, int exp)
	{
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0f);
		
		player.giveExp(exp, false);
	}
	
	private int getToLevel(Player player)
	{
		return Math.round(player.getExpToLevel() * player.getExp());
	}
	
	private int getTotal(Player player)
	{
		var level = player.getLevel();
		
		if(level <= 16)
			return Math.round((level * level + 6f * level) + getToLevel(player));
		
		if (level <= 31)
			return Math.round((2.5f * level * level - 40.5f * level + 360f) + getToLevel(player));
		
		return Math.round((4.5f * level * level - 162.5f * level + 2220f) + getToLevel(player));
	}
	
	public static void log(Level level, String message)
	{
		logger.log(level, message);
	}
	public static void log(String message)
	{
		logger.log(Level.INFO, message);
	}
}
