package hu.Pdani.FMCSwear.listeners;

import hu.Pdani.FMCSwear.FMCSwearPlugin;
import hu.Pdani.FMCSwear.FSwearException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {
    private FMCSwearPlugin plugin;
    public CommandListener(FMCSwearPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!sender.hasPermission("fmcswear.admin"))
            return true;
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("reset")){
                if(args.length < 2) {
                    sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.reset.player")));
                    return true;
                }
                try {
                    plugin.getPlayerManager().reset(args[1]);
                } catch (FSwearException e){
                    sender.sendMessage(plugin.color("&c"+e.getMessage()));
                }
                sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.reset.done").replace("%player%",args[1].toLowerCase())));
                return true;
            }
        }
        plugin.reloadConfig();
        plugin.getPlayerManager().reloadConfig();
        sender.sendMessage(plugin.color(plugin.getConfig().getString("messages.reload")));
        return true;
    }
}
