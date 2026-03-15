package com.chaseoes.firstjoinplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chaseoes.firstjoinplus.utilities.Utilities;

@SuppressWarnings("UnstableApiUsage")
public class FirstJoinPlus extends JavaPlugin {

    private static FirstJoinPlus instance;

    public List<String> noPVP = new ArrayList<>();
    public List<String> godMode = new ArrayList<>();

    public static FirstJoinPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FirstJoinListener(), this);
        Utilities.copyDefaultFiles();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
            event.registrar().register(
                FirstJoinCommand.create().build(),
                "FirstJoinPlus command(s).",
                List.of("fjp")
            )
        );

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

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        reloadConfig();
        saveConfig();
    }

}
