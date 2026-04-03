package unnamed_p.fastportal.handlers;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import unnamed_p.fastportal.FastPortalPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.UUID;

public class CacheHandler {
    private final ObjectSet<String> undelayedCache = new ObjectArraySet<>();
    private final FastPortalPlugin plugin;

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode fileCache;

    public CacheHandler(FastPortalPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!this.plugin.config().isEnableFileCache()) {
            this.plugin.getSLF4JLogger().warn("File cache is disabled!");
            this.plugin.getSLF4JLogger().warn("The plugin will not remember players who want to go through portals without delay after a server restart.");
            return;
        }

        try {
            this.fileCache = this.loadCache();

            var rawCache = this.fileCache.node("cache").getList(String.class);

            if (rawCache != null && !rawCache.isEmpty()) {
                this.undelayedCache.addAll(rawCache);
            }
        } catch (IOException exception) {
            this.plugin.getSLF4JLogger().error("Cannot initialize file cache, see:", exception);
        }
    }

    public void remember(UUID uuid) {
        this.undelayedCache.add(uuid.toString());
        this.saveCache();
    }

    public void forget(UUID uuid) {
        this.undelayedCache.remove(uuid.toString());
        this.saveCache();
    }

    public Collection<String> undelayedCache() {
        return ObjectSets.unmodifiable(this.undelayedCache);
    }

    public YamlConfigurationLoader cacheLoader() {
        return this.loader;
    }

    public CommentedConfigurationNode fileCache() {
        return this.fileCache;
    }

    private void saveCache() {
        if (!this.plugin.config().isEnableFileCache()) return;

        var cacheCopy = ImmutableList.copyOf(this.undelayedCache);

        this.plugin.getServer().getAsyncScheduler().runNow(this.plugin, _ -> {
            try {
                this.fileCache.node("cache").setList(String.class, cacheCopy);
                this.loader.save(this.fileCache);
            } catch (IOException exception) {
                this.plugin.getSLF4JLogger().error("Cannot save file cache, see:", exception);
            }
        });
    }

    private CommentedConfigurationNode loadCache() throws IOException {
        this.loader = this.createLoader();
        return this.loader.load();
    }

    private YamlConfigurationLoader createLoader() throws IOException {
        var pluginDir = this.plugin.getDataFolder().toPath();

        if (!Files.exists(pluginDir)) {
            Files.createDirectories(pluginDir);
        }

        return YamlConfigurationLoader.builder()
                .path(pluginDir.resolve("cache.yaml"))
                .indent(4)
                .build();
    }
}
