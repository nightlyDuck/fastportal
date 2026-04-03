package unnamed_p.fastportal.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import unnamed_p.fastportal.FastPortalPlugin;

public class FastPortalCommand {
    private final FastPortalPlugin plugin;

    public FastPortalCommand(FastPortalPlugin plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> createRootCommand() {
        var builder = LiteralArgumentBuilder.<CommandSourceStack>literal("fastportal");

        builder.requires(ctx -> ctx.getSender().hasPermission("fastportal.use"));
        builder.executes(ctx -> {
            if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                ctx.getSource().getSender().sendMessage(this.plugin.config().onlyAsPlayer());
                return Command.SINGLE_SUCCESS;
            }

            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                ctx.getSource().getSender().sendMessage(this.plugin.config().onlyForSurvival());
                return Command.SINGLE_SUCCESS;
            }

            if (this.plugin.cache().undelayedCache().contains(player.getUniqueId().toString())) {
                player.sendMessage(this.plugin.config().disabled());
                this.plugin.cache().forget(player.getUniqueId());
            } else {
                player.sendMessage(this.plugin.config().enabled());
                this.plugin.cache().remember(player.getUniqueId());
            }

            return Command.SINGLE_SUCCESS;
        });

        builder.then(this.createReloadCommand());

        return builder.build();
    }

    private LiteralCommandNode<CommandSourceStack> createReloadCommand() {
        var builder = LiteralArgumentBuilder.<CommandSourceStack>literal("reload");

        builder.requires(ctx -> ctx.getSender().hasPermission("fastportal.reload"));

        builder.executes(ctx -> {
            this.plugin.getSLF4JLogger().info("Trying to reload plugin configuration...");

            this.plugin.config().load();
            this.plugin.cache().load();

            ctx.getSource().getSender().sendMessage(this.plugin.config().reloaded());

            return Command.SINGLE_SUCCESS;
        });

        return builder.build();
    }
}
