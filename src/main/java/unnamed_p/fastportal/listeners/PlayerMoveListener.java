package unnamed_p.fastportal.listeners;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import unnamed_p.fastportal.FastPortalPlugin;

public class PlayerMoveListener implements Listener {
    private final FastPortalPlugin plugin;

    public PlayerMoveListener(FastPortalPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        var player = (CraftPlayer) event.getPlayer();

        if (!this.plugin.cache().undelayedCache().contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        if (!(player.getGameMode() == GameMode.CREATIVE) && player.hasPermission("fastportal.use")
                && event.getTo() != null && (!isNetherPortal(event.getFrom().getBlock())
                && isNetherPortal(event.getTo().getBlock()))) {
            this.plugin.invulnerable().applyInvulnerableFor(player);
        }
    }

    private boolean isNetherPortal(Block block) {
        return block.getType().name().equals("NETHER_PORTAL") || block.getType().name().equals("PORTAL");
    }
}