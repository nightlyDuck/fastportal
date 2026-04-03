package unnamed_p.fastportal;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import unnamed_p.fastportal.commands.FastPortalCommand;
import unnamed_p.fastportal.handlers.CacheHandler;
import unnamed_p.fastportal.config.PluginConfig;
import unnamed_p.fastportal.handlers.InvulnerableHandler;
import unnamed_p.fastportal.listeners.PlayerMoveListener;

public class FastPortalPlugin extends JavaPlugin {
    private final PluginConfig config;
    private final CacheHandler cache;
    private final InvulnerableHandler invulnerable;

    public FastPortalPlugin() {
        this.config = new PluginConfig(this);
        this.cache = new CacheHandler(this);
        this.invulnerable = new InvulnerableHandler(this);
    }

    @Override
    public void onEnable() {
        this.config.load();
        this.cache.load();

        super.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            var rootCommand = new FastPortalCommand(this);
            commands.registrar().register(rootCommand.createRootCommand());
        });
    }

    public PluginConfig config() {
        return this.config;
    }

    public CacheHandler cache() {
        return this.cache;
    }

    public InvulnerableHandler invulnerable() {
        return this.invulnerable;
    }
}