package github.pashamaladec.reducexploss;

import github.pashamaladec.reducexploss.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigFile implements CommandExecutor
{
	private final Config config;
	
	public ReloadConfigFile(Config config)
	{
		this.config = config;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		sender.sendMessage("Config reloaded");
		return config.load();
	}
}
