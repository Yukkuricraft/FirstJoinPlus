package com.chaseoes.firstjoinplus;

import com.chaseoes.firstjoinplus.utilities.Utilities;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("UnstableApiUsage")
public class FirstJoinCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("firstjoinplus")
            .executes(ctx -> {
                var sender = ctx.getSource().getSender();
                sender.sendMessage(Component.text("[FirstJoinPlus] ", NamedTextColor.YELLOW)
                        .append(Component.text("Version ", NamedTextColor.GRAY))
                        .append(Component.text(FirstJoinPlus.getInstance().getDescription().getVersion(), NamedTextColor.AQUA))
                        .append(Component.text(" by " + FirstJoinPlus.getInstance().getDescription().getAuthors().get(0) + ".", NamedTextColor.GRAY)));
                sender.sendMessage(Utilities.formatCommandResponse("http://dev.bukkit.org/bukkit-plugins/firstjoinplus/"));
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("help")
                .executes(ctx -> {
                    var sender = ctx.getSource().getSender();
                    sender.sendMessage(Utilities.formatCommandResponse("Available Commands:"));
                    sender.sendMessage(Utilities.formatCommandResponse("/fjp: General plugin information."));
                    sender.sendMessage(Utilities.formatCommandResponse("/fjp reload: Reloads the configuration."));
                    sender.sendMessage(Utilities.formatCommandResponse("/fjp setspawn: Sets the first-join spawnpoint."));
                    sender.sendMessage(Utilities.formatCommandResponse("/fjp debug: Become a new player!"));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission("firstjoinplus.reload"))
                .executes(ctx -> {
                    var sender = ctx.getSource().getSender();
                    FirstJoinPlus.getInstance().reloadConfig();
                    FirstJoinPlus.getInstance().saveConfig();
                    Utilities.copyDefaultFiles();
                    sender.sendMessage(Utilities.formatCommandResponse("Configuration reloaded."));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("setspawn")
                .requires(source -> source.getSender() instanceof Player p && p.hasPermission("firstjoinplus.setspawn"))
                .executes(ctx -> {
                    var player = (Player) ctx.getSource().getSender();
                    var config = FirstJoinPlus.getInstance().getConfig();
                    config.set("on-first-join.teleport.enabled", true);
                    config.set("on-first-join.teleport.x", player.getLocation().getBlockX());
                    config.set("on-first-join.teleport.y", player.getLocation().getBlockY());
                    config.set("on-first-join.teleport.z", player.getLocation().getBlockZ());
                    config.set("on-first-join.teleport.pitch", player.getLocation().getPitch());
                    config.set("on-first-join.teleport.yaw", player.getLocation().getYaw());
                    config.set("on-first-join.teleport.world", player.getLocation().getWorld().getName());
                    FirstJoinPlus.getInstance().saveConfig();
                    FirstJoinPlus.getInstance().reloadConfig();
                    player.sendMessage(Utilities.formatCommandResponse("Successfully set the first-join spawn location."));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("debug")
                .requires(source -> source.getSender() instanceof Player p && p.hasPermission("firstjoinplus.debug"))
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    Utilities.resetPlayerState(player);
                    FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(
                            new FirstJoinEvent(new PlayerJoinEvent(player,
                                    Component.text(player.getName() + " joined for the first time!"))));
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

}
