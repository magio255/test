package me.jules.magiocore;

import org.bukkit.plugin.java.JavaPlugin;

public class MagioCore extends JavaPlugin {
    private HomeManager homeManager;
    private HomeGui homeGui;
    private TpaManager tpaManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        homeManager = new HomeManager(this);
        homeGui = new HomeGui(this, homeManager);
        tpaManager = new TpaManager(this);

        HomeCommands homeCommands = new HomeCommands(this, homeManager);
        getCommand("home").setExecutor(homeCommands);
        getCommand("sethome").setExecutor(homeCommands);

        TpaCommands tpaCommands = new TpaCommands(this, tpaManager);
        getCommand("tpa").setExecutor(tpaCommands);
        getCommand("tpahere").setExecutor(tpaCommands);
        getCommand("tpacancel").setExecutor(tpaCommands);
        getCommand("tpaoff").setExecutor(tpaCommands);
        getCommand("tpaccept").setExecutor(tpaCommands);
        getCommand("tpadeny").setExecutor(tpaCommands);

        SpawnCommands spawnCommands = new SpawnCommands(this);
        getCommand("spawn").setExecutor(spawnCommands);
        getCommand("setspawn").setExecutor(spawnCommands);

        GamemodeCommands gmCommands = new GamemodeCommands();
        getCommand("gmc").setExecutor(gmCommands);
        getCommand("gms").setExecutor(gmCommands);
        getCommand("gmsp").setExecutor(gmCommands);
        getCommand("gma").setExecutor(gmCommands);

        RtpCommand rtpCommand = new RtpCommand(this);
        getCommand("rtp").setExecutor(rtpCommand);

        FlySpeedCommand flySpeedCommand = new FlySpeedCommand();
        getCommand("flyspeed").setExecutor(flySpeedCommand);

        PlaytimeCommand playtimeCommand = new PlaytimeCommand();
        getCommand("playtime").setExecutor(playtimeCommand);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MagioCoreExpansion(this).register();
        }

        getServer().getPluginManager().registerEvents(homeGui, this);
        getServer().getPluginManager().registerEvents(rtpCommand, this);
        getServer().getPluginManager().registerEvents(flySpeedCommand, this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        getLogger().info("MagioCore has been enabled!");
    }

    public HomeGui getHomeGui() {
        return homeGui;
    }

    @Override
    public void onDisable() {
        getLogger().info("MagioCore has been disabled!");
    }
}
