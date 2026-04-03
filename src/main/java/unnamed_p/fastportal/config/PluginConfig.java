package unnamed_p.fastportal.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.apache.logging.log4j.util.Strings;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import unnamed_p.fastportal.FastPortalPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class PluginConfig {
    private final static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private boolean enableFileCache;

    private Component enabled;
    private Component disabled;
    private Component onlyAsPlayer;
    private Component reloaded;
    private Component onlyForSurvival;

    private final FastPortalPlugin plugin;

    public PluginConfig(FastPortalPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            var config = this.createConfiguration();

            this.enableFileCache = config.node("settings", "enable-file-cache").getBoolean();

            this.enabled = MINI_MESSAGE.deserialize(this.get(config, "messages", "enabled"));
            this.disabled = MINI_MESSAGE.deserialize(this.get(config, "messages", "disabled"));
            this.onlyAsPlayer = MINI_MESSAGE.deserialize(this.get(config, "messages", "only-as-player"));
            this.reloaded = MINI_MESSAGE.deserialize(this.get(config, "messages", "reloaded"));
            this.onlyForSurvival = MINI_MESSAGE.deserialize(this.get(config, "messages", "only-for-survival"));
        } catch (IOException exception) {
            this.plugin.getSLF4JLogger().error("Cannot load plugin configuration, see:", exception);
        }
    }

    public boolean isEnableFileCache() {
        return this.enableFileCache;
    }

    public Component enabled() {
        return this.enabled;
    }

    public Component disabled() {
        return this.disabled;
    }

    public Component onlyAsPlayer() {
        return this.onlyAsPlayer;
    }

    public Component onlyForSurvival() {
        return this.onlyForSurvival;
    }

    public Component reloaded() {
        return this.reloaded;
    }

    private CommentedConfigurationNode createConfiguration() throws IOException {
        var pluginDir = this.plugin.getDataFolder().toPath();

        if (!Files.exists(pluginDir)) {
            Files.createDirectories(pluginDir);
        }

        var configPath = pluginDir.resolve("configuration.yaml");

        if (!Files.exists(configPath)) {
            try (var resource = this.plugin.getResource("templates/configuration.yaml")) {
                Files.copy(resource, configPath);
            }
        }

        return YamlConfigurationLoader.builder().path(configPath).indent(4).build().load();
    }

    private String get(CommentedConfigurationNode rootNode, Object... path) throws IOException {
        var value = rootNode.node(path).getString();

        if (value == null) {
            throw new IOException("option at path " + Strings.join(Arrays.asList(path), '.') + " is null!");
        }

        return value;
    }
}
