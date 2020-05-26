package hu.Pdani.FMCSwear.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import hu.Pdani.FMCSwear.FMCSwearPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class SwearRecord extends SimpleExpression<Integer> {
    static {
        Skript.registerExpression(SwearRecord.class, Integer.class, ExpressionType.COMBINED,"swear[ing] [record] of %player%");
    }

    private Expression<Player> player;
    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }
    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] e, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        player = (Expression<Player>) e[0];
        return true;
    }
    @Override
    public String toString(Event e, boolean arg1) {
        return "nametag of %player%";
    }
    @Override
    protected Integer[] get(Event e) {
            return new Integer[]{FMCSwearPlugin.getPM().getCount(player.getSingle(e))};
    }
    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode){
        if (mode == Changer.ChangeMode.SET) {
            FMCSwearPlugin.getPM().setCount(player.getSingle(e),(int)delta[0]);
        }
        if (mode == Changer.ChangeMode.RESET) {
            FMCSwearPlugin.getPM().setCount(player.getSingle(e),0);
        }
    }
    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Integer.class);
        }
        return null;
    }
}
