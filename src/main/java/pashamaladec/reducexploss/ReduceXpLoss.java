package github.pashamaladec.reducexploss;

import github.pashamaladec.reducexploss.config.Config;
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
		
		var command = getCommand("reload-xploss-config");
		
		if(command != null)
			command.setExecutor(new ReloadConfigFile(config));
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	private void OnPlayerDied(PlayerDeathEvent event)
	{
		var player = event.getEntity().getPlayer();
		if(player == null)
			return;
		
		event.setShouldDropExperience(false);
		
		var toReduce = getReducedExp(player);
		var oldPlayer = event.getEntity().getPlayer();
		setExp(player, toReduce);
		sendNotify(player, oldPlayer, toReduce);
		
		event.setKeepLevel(true);
	}
	
	private void sendNotify(Player player, Player oldPlayer, int toReduce)
	{
		if(player.getUniqueId() != oldPlayer.getUniqueId())
			return;
		
		var oldLevel = getTotalPoints(oldPlayer);
		var diff = toReduce - oldLevel;
		
		var message = ChatColor.GRAY + "" + oldPlayer.getLevel() + "L " + getPointsToLevel(oldPlayer) + "P (" + oldLevel + "P) ->> ";
		message += player.getLevel() + "L " + getPointsToLevel(player) + "P (" + getTotalPoints(player) + "P)";
		
		if(diff > 0)
			message += " | " + ChatColor.DARK_GREEN + "+" + (toReduce - oldLevel) + "P";
		
		if(diff < 0)
			message += " | " + ChatColor.DARK_RED + (toReduce - oldLevel) + "P";
		
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
		
		log(player.getName() + "(" + player.getUniqueId() + ") - applying " + multiplier * 100 + "%");
		return Math.round(getTotalPoints(player) * multiplier);
	}
	
	private void setExp(Player player, int exp)
	{
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0f);
		
		player.giveExp(exp, false);
	}
	
	private int getPointsToLevel(Player player)
	{
		return Math.round(player.getExpToLevel() * player.getExp());
	}
	
	private int getTotalPoints(Player player)
	{
		var level = player.getLevel();
		
		if(level <= 16)
			return Math.round((level * level + 6f * level) + getPointsToLevel(player));
		
		if (level <= 31)
			return Math.round((2.5f * level * level - 40.5f * level + 360f) + getPointsToLevel(player));
		
		return Math.round((4.5f * level * level - 162.5f * level + 2220f) + getPointsToLevel(player));
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
