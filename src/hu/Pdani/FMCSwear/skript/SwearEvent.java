package hu.Pdani.FMCSwear.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.sun.istack.internal.Nullable;
import hu.Pdani.FMCSwear.FMCSwearPlugin;
import hu.Pdani.FMCSwear.event.PlayerSwearEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class SwearEvent extends SkriptEvent {
    static {
        Skript.registerEvent("Player Swear", SwearEvent.class, PlayerSwearEvent.class, "[player] swear");
        EventValues.registerEventValue(PlayerSwearEvent.class, Player.class, new Getter<Player, PlayerSwearEvent>() {
            @Override
            @Nullable
            public Player get(PlayerSwearEvent e) {
                return e.getPlayer();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return !((PlayerSwearEvent) event).isCancelled();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "on [player] swear";
    }
}
