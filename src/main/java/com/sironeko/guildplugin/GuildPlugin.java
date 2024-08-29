package com.sironeko.guildplugin;

import com.sironeko.guildplugin.commands.GuildCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuildPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("guild").setExecutor(new GuildCommand());}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
