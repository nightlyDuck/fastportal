package unnamed_p.fastportal.handlers;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import unnamed_p.fastportal.FastPortalPlugin;

import java.util.concurrent.TimeUnit;

public class InvulnerableHandler {
    private final FastPortalPlugin plugin;

    public InvulnerableHandler(FastPortalPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyInvulnerableFor(CraftPlayer player) {
        player.getHandle().getAbilities().invulnerable = true;

        this.plugin.getServer().getAsyncScheduler().runDelayed(this.plugin, _ -> {
            player.getHandle().getAbilities().invulnerable = false;
        }, 60, TimeUnit.MILLISECONDS);
    }
}
