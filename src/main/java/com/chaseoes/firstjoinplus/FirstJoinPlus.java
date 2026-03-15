package com.chaseoes.firstjoinplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chaseoes.firstjoinplus.utilities.Utilities;

public class FirstJoinPlus extends JavaPlugin {

    private static FirstJoinPlus instance;

    public List<String> noPVP = new ArrayList<String>();
    public List<String> godMode = new ArrayList<String>();

    public static FirstJoinPlus getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FirstJoinListener(), this);
        Utilities.copyDefaultFiles();

        if (getConfig().getString("settings.teleport-delay") != null) {
            File configuration = new File(getDataFolder() + "/config.yml");
            configuration.setWritable(true);
            configuration.renameTo(new File(getDataFolder() + "/old-config.yml"));
            String[] sections = getConfig().getConfigurationSection("").getKeys(false).toArray(new String[0]);
            for (String s : sections) {
                getConfig().set(s, null);
            }
            saveConfig();
            getLogger().severe("Your configuration was outdated, so we attempted to generate a new one for you.");
        }
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        reloadConfig();
        saveConfig();
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length == 0) {
            cs.sendMessage(Component.text("[FirstJoinPlus] ", NamedTextColor.YELLOW)
                    .append(Component.text("Version ", NamedTextColor.GRAY))
                    .append(Component.text(getDescription().getVersion(), NamedTextColor.AQUA))
                    .append(Component.text(" by " + getDescription().getAuthors().get(0) + ".", NamedTextColor.GRAY)));
            cs.sendMessage(Utilities.formatCommandResponse("http://dev.bukkit.org/bukkit-plugins/firstjoinplus/"));
            return true;
        }

        if (strings.length != 1) {
            cs.sendMessage(Utilities.formatCommandResponse("Usage: /firstjoinplus <reload|setspawn|debug>"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("help")) {
            cs.sendMessage(Utilities.formatCommandResponse("Available Commands:"));
            cs.sendMessage(Utilities.formatCommandResponse("/fjp: General plugin information."));
            cs.sendMessage(Utilities.formatCommandResponse("/fjp reload: Reloads the configuration."));
            cs.sendMessage(Utilities.formatCommandResponse("/fjp setspawn: Sets the first-join spawnpoint."));
            cs.sendMessage(Utilities.formatCommandResponse("/fjp debug: Become a new player!"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            if (cs.hasPermission("firstjoinplus.reload")) {
                reloadConfig();
                saveConfig();
                Utilities.copyDefaultFiles();
                cs.sendMessage(Utilities.formatCommandResponse("Configuration reloaded."));
            } else {
                cs.sendMessage(Utilities.getNoPermissionMessage());
            }
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(Utilities.formatCommandResponse("You must be a player to do that."));
            return true;
        }

        Player player = (Player) cs;
        if (strings[0].equalsIgnoreCase("setspawn")) {
            if (cs.hasPermission("firstjoinplus.setspawn")) {
                getConfig().set("on-first-join.teleport.enabled", true);
                getConfig().set("on-first-join.teleport.x", player.getLocation().getBlockX());
                getConfig().set("on-first-join.teleport.y", player.getLocation().getBlockY());
                getConfig().set("on-first-join.teleport.z", player.getLocation().getBlockZ());
                getConfig().set("on-first-join.teleport.pitch", player.getLocation().getPitch());
                getConfig().set("on-first-join.teleport.yaw", player.getLocation().getYaw());
                getConfig().set("on-first-join.teleport.world", player.getLocation().getWorld().getName());
                saveConfig();
                reloadConfig();
                cs.sendMessage(Utilities.formatCommandResponse("Successfully set the first-join spawn location."));
            } else {
                cs.sendMessage(Utilities.getNoPermissionMessage());
            }
            return true;
        }

        if (strings[0].equalsIgnoreCase("debug")) {
            if (cs.hasPermission("firstjoinplus.debug")) {
                Utilities.debugPlayer(player, true);
            } else {
                cs.sendMessage(Utilities.getNoPermissionMessage());
            }
            return true;
        }

        cs.sendMessage(Utilities.formatCommandResponse("Unknown command. Type /fjp help for help."));
        return true;
    }

}
