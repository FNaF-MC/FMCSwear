package hu.Pdani.FMCSwear;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class PlayerManager {
    private FMCSwearPlugin plugin;
    private int pafter;
    private int reset;
    private List<String> commands;
    private HashMap<String,Integer> counter = new HashMap<>();
    private HashMap<String,Long> last = new HashMap<>();
    private HashMap<String,String> message = new HashMap<>();
    private HashMap<String,Boolean> censor = new HashMap<>();
    public PlayerManager(FMCSwearPlugin plugin){
        this.plugin = plugin;
        this.reloadConfig();
    }
    public void reset(String player) throws FSwearException {
        if(player == null)
            throw new IllegalArgumentException("Player cannot be null!");
        if(!counter.containsKey(player))
            throw new FSwearException(message.get("norecord"));
        counter.remove(player);
        last.remove(player);
    }
    public void reloadConfig(){
        message.clear();
        pafter = plugin.getConfig().getInt("punishment.after");
        reset = plugin.getConfig().getInt("punishment.reset");
        commands = plugin.getConfig().getStringList("punishment.commands");
        message.put("next",plugin.getConfig().getString("messages.punishment.next"));
        message.put("done",plugin.getConfig().getString("messages.punishment.done"));
        message.put("norecord",plugin.getConfig().getString("messages.reset.norecord"));
    }
    public void addCount(Player player){
        String name = player.getName();
        long time = System.currentTimeMillis();
        if(last.containsKey(name)){
            long lasttime = last.get(name);
            int diff = (int)((time-lasttime)/1000);
            if(diff >= reset){
                counter.remove(name);
            }
        }
        int num = 1;
        if(counter.containsKey(name)){
            num = counter.get(name)+1;
            counter.replace(name,num);
        } else {
            counter.put(name, 1);
        }
        last.put(name,time);
        if (num == (pafter - 1)) {
            player.sendMessage(plugin.color(message.get("next")));
        } else if (num >= pafter) {
            player.sendMessage(plugin.color(message.get("done")));
            plugin.getServer().getScheduler().runTask(plugin.getPlugin(),()->{
                for(String cmd : commands){
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),cmd.replace("%player%",player.getName()));
                }
            });
        }
    }
    public void setCount(Player player, int count){
        counter.put(player.getName(),count);
    }
    public int getCount(Player player){
        if(!counter.containsKey(player.getName()))
            return 0;
        return counter.get(player.getName());
    }
    public boolean setCensor(Player player, boolean value){
        boolean def = plugin.getConfig().getBoolean("censor",true);
        boolean old;
        if(!updateCensor(player,value)){
            return !value;
        }
        if(censor.containsKey(player.getName())){
            old = censor.get(player.getName());
            censor.replace(player.getName(),value);
            return old;
        }
        censor.put(player.getName(),value);
        return def;
    }
    public boolean getCensor(Player player){
        boolean def = plugin.getConfig().getBoolean("censor",true);
        if(censor.containsKey(player.getName()))
            return censor.get(player.getName());
        return def;
    }
    public void reloadCensor() {
        if(!censor.isEmpty())
            censor.clear();
        boolean def = plugin.getConfig().getBoolean("censor",true);
        File dir = new File(plugin.getDataFolder(),"/players");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(!child.getName().toLowerCase().endsWith(".yml"))
                    continue;
                FileConfiguration fc = YamlConfiguration.loadConfiguration(child);
                String name = fc.getString("username");
                boolean value = fc.getBoolean("censor",def);
                censor.put(name,value);
            }
        }
    }
    private boolean updateCensor(Player player, boolean value){
        File cfile = new File(plugin.getDataFolder(), "players/" + player.getUniqueId().toString() + ".yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(cfile);
        fc.set("username", player.getName());
        fc.set("censor", value);
        try {
            fc.save(cfile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to save player file: " + cfile.toString());
        }
        return false;
    }
}
