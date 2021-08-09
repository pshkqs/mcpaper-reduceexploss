package github.pashamaladec.reducexploss.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static github.pashamaladec.reducexploss.ReduceXpLoss.log;

public class Config
{
	private final File file;
	private FileConfiguration configuration;
	
	public Config(String path)
	{
		file = new File(path);
	}
	
	public FileConfiguration getConfig()
	{
		if (configuration == null)
			throw new NullPointerException();
		
		return configuration;
	}
	
	public boolean exists()
	{
		if(file.exists())
			log(Level.INFO, "File " + file.getName() + " exists");
		else
			log(Level.WARNING, "File " + file.getName() + " NOT exists");
		
		return file.exists();
	}
	
	public boolean create()
	{
		log(Level.INFO, "Creating yaml-configuration: " + file.getName());
		
		try
		{
			log(Level.INFO, "Yaml-configuration creating: " + file.getAbsolutePath());
			return file.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean load()
	{
		if (file.isFile() == false)
		{
			log(Level.WARNING, file.getAbsolutePath() + " is not a file\nCAN'T LOAD");
			return false;
		}
		
		configuration = YamlConfiguration.loadConfiguration(file);
		log(Level.INFO, "Loaded config " + file.getName());
		return true;
	}
	
	public void save()
	{
		try
		{
			configuration.save(file);
		} catch (IOException e)
		{
			log(Level.WARNING, file.getName() + " couldn't save");
			return;
		}
		
		log(Level.INFO, "Saved config " + file.getName());
	}
}