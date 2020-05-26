package hu.Pdani.FMCSwear.listeners;

import hu.Pdani.FMCSwear.FMCSwearPlugin;
import hu.Pdani.FMCSwear.event.PlayerSwearEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class PlayerListener implements Listener {
    private FMCSwearPlugin plugin;
    public PlayerListener(FMCSwearPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String msg = event.getMessage();
        List<String> badwords = plugin.getConfig().getStringList("badwords");
        List<String> falsepositive = plugin.getConfig().getStringList("falsepositive");
        double maxpercent = plugin.getConfig().getDouble("percent");
        if(msg.contains(" ")){
            for(String m : msg.split(" ")){
                m = clean(m);
                if(falsepositive.contains(m))
                    continue;
                if(badwords.contains(m)){
                    PlayerSwearEvent swear = new PlayerSwearEvent(player);
                    plugin.getServer().getPluginManager().callEvent(swear);
                    if(!swear.isCancelled()){
                        if(plugin.getConfig().getBoolean("blockmessage",true))
                            event.setCancelled(true);
                        checkPunishment(player);
                        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                        return;
                    }
                }
            }
            for(String bad : badwords){
                for(String ms : msg.split(" ")) {
                    ms = clean(ms);
                    if(falsepositive.contains(ms))
                        continue;
                    if (bad.length() == ms.length()) {
                        double weight = 100.00 / (double) bad.length();
                        double current = 0.00;
                        for (int i = 0; i < bad.length(); i++) {
                            char b = bad.charAt(i);
                            char m = ms.charAt(i);
                            if (b == m)
                                current += weight;
                        }
                        if (current >= maxpercent) {
                            PlayerSwearEvent swear = new PlayerSwearEvent(player);
                            plugin.getServer().getPluginManager().callEvent(swear);
                            if (!swear.isCancelled()) {
                                if(plugin.getConfig().getBoolean("blockmessage",true))
                                    event.setCancelled(true);
                                checkPunishment(player);
                                player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                                return;
                            }
                        }
                    }
                }
            }
            msg = clean(msg);
            for(String bad : badwords){
                if(msg.contains(bad)){
                    PlayerSwearEvent swear = new PlayerSwearEvent(player);
                    plugin.getServer().getPluginManager().callEvent(swear);
                    if(!swear.isCancelled()){
                        if(plugin.getConfig().getBoolean("blockmessage",true))
                            event.setCancelled(true);
                        checkPunishment(player);
                        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                        return;
                    }
                }
            }
        } else {
            String clean = clean(msg);
            if(falsepositive.contains(clean))
                return;
            if(badwords.contains(clean) || badwords.contains(msg)){
                PlayerSwearEvent swear = new PlayerSwearEvent(player);
                plugin.getServer().getPluginManager().callEvent(swear);
                if(!swear.isCancelled()){
                    if(plugin.getConfig().getBoolean("blockmessage",true))
                        event.setCancelled(true);
                    checkPunishment(player);
                    player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                    return;
                }
            }
            for(String bad : badwords){
                if(bad.length() == msg.length()){
                    double weight = 100.00 / (double) bad.length();
                    double current = 0.00;
                    for(int i = 0; i < bad.length(); i++){
                        char b = bad.charAt(i);
                        char m = msg.charAt(i);
                        if(b == m)
                            current += weight;
                    }
                    if(current >= maxpercent){
                        PlayerSwearEvent swear = new PlayerSwearEvent(player);
                        plugin.getServer().getPluginManager().callEvent(swear);
                        if(!swear.isCancelled()){
                            if(plugin.getConfig().getBoolean("blockmessage",true))
                                event.setCancelled(true);
                            checkPunishment(player);
                            player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                            return;
                        }
                    }
                }
                if(bad.length() == clean.length()){
                    double weight = 100.00 / (double) bad.length();
                    double current = 0.00;
                    for(int i = 0; i < bad.length(); i++){
                        char b = bad.charAt(i);
                        char m = clean.charAt(i);
                        if(b == m)
                            current += weight;
                    }
                    if(current >= maxpercent){
                        PlayerSwearEvent swear = new PlayerSwearEvent(player);
                        plugin.getServer().getPluginManager().callEvent(swear);
                        if(!swear.isCancelled()){
                            if(plugin.getConfig().getBoolean("blockmessage",true))
                                event.setCancelled(true);
                            checkPunishment(player);
                            player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                            return;
                        }
                    }
                }
            }
            for(String bad : badwords){
                if(msg.contains(bad)){
                    PlayerSwearEvent swear = new PlayerSwearEvent(player);
                    plugin.getServer().getPluginManager().callEvent(swear);
                    if(!swear.isCancelled()){
                        if(plugin.getConfig().getBoolean("blockmessage",true))
                            event.setCancelled(true);
                        checkPunishment(player);
                        player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked")));
                        return;
                    }
                }
            }
        }
    }
    private String clean(String msg){
        String regex = plugin.getConfig().getString("regex");
        if(regex.equals(""))
            return msg;
        return msg.replaceAll(regex, "");
    }
    private void checkPunishment(Player player){
        if(plugin.getConfig().getBoolean("punishment.enabled",true)) {
            plugin.getPlayerManager().addCount(player);
        }
    }
}
