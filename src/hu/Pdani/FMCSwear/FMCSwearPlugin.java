package hu.Pdani.FMCSwear;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import hu.Pdani.FMCSwear.listeners.CommandListener;
import hu.Pdani.FMCSwear.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;

public class FMCSwearPlugin extends JavaPlugin {
    private static JavaPlugin plugin;
    private static PlayerManager playerManager;
    @Override
    public void onEnable() {
        File cfile = new File(getDataFolder(),"config.yml");
        if(!cfile.exists()) {
            saveDefaultConfig();
        } else {
            if(getConfig().getInt("version",0) != getConfig().getDefaults().getInt("version")){
                getConfig().options().copyDefaults(true);
                getConfig().set("version",getConfig().getDefaults().getInt("version"));
                saveConfig();
            }
        }
        StringBuilder authors = new StringBuilder();
        Iterator<String> listauthors = getDescription().getAuthors().iterator();
        while(listauthors.hasNext()){
            if(authors.length() == 0)
                authors = new StringBuilder(listauthors.next());
            else
                authors.append(", ").append(listauthors.next());
        }
        plugin = this;
        if(hasSkript() && getConfig().getBoolean("useskript",true)){
            getLogger().log(Level.INFO,"Skript found, registering classes...");
            SkriptAddon addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("hu.Pdani.FMCSwear", "skript");
                getLogger().log(Level.INFO,"Classes registered successfully!");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE,"Class registration failed!");
                getLogger().log(Level.SEVERE,e.toString());
            }
        }
        playerManager = new PlayerManager(this);
        getCommand("fmcswear").setExecutor(new CommandListener(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);
        getLogger().log(Level.INFO,"The plugin is now enabled.");
        getLogger().log(Level.INFO,"Created by: "+authors.toString());
        getLogger().log(Level.INFO,"Version "+getDescription().getVersion());
    }
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO,"The plugin is now disabled.");
    }

    private boolean hasSkript() {
        Plugin pl = getServer().getPluginManager().getPlugin("Skript");
        if (pl == null) {
            return false;
        }
        return (pl instanceof Skript);
    }

    public JavaPlugin getPlugin() {
        return getJavaPlugin();
    }
    public static JavaPlugin getJavaPlugin(){
        return plugin;
    }

    public PlayerManager getPlayerManager() {
        return getPM();
    }
    public static PlayerManager getPM(){
        return playerManager;
    }

    public String color(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }
}
