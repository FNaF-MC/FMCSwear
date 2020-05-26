package hu.Pdani.FMCSwear;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class PlayerManager {
    private FMCSwearPlugin plugin;
    private int pafter;
    private int reset;
    private List<String> commands;
    private HashMap<String,Integer> counter = new HashMap<>();
    private HashMap<String,Long> last = new HashMap<>();
    private HashMap<String,String> message = new HashMap<>();
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
}
