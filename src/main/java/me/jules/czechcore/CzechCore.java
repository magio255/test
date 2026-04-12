package me.jules.czechcore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CzechCore extends JavaPlugin {
    private HomeManager homeManager;
    private HomeGui homeGui;
    private TpaManager tpaManager;
    private Economy econ = null;
    private CoinflipManager coinflipManager;
    private CoinflipGui coinflipGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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

        InvseeCommand invseeCommand = new InvseeCommand();
        getCommand("invsee").setExecutor(invseeCommand);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CzechCoreExpansion(this).register();
        }

        getServer().getPluginManager().registerEvents(homeGui, this);
        getServer().getPluginManager().registerEvents(rtpCommand, this);
        getServer().getPluginManager().registerEvents(flySpeedCommand, this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        coinflipManager = new CoinflipManager();
        coinflipGui = new CoinflipGui(this, coinflipManager);
        CoinflipCommand coinflipCommand = new CoinflipCommand(this, coinflipManager);
        getCommand("coinflip").setExecutor(coinflipCommand);
        getCommand("coinflip").setTabCompleter(coinflipCommand);
        getServer().getPluginManager().registerEvents(coinflipGui, this);

        getLogger().info("CzechCore has been enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public CoinflipGui getCoinflipGui() {
        return coinflipGui;
    }

    public HomeGui getHomeGui() {
        return homeGui;
    }

    @Override
    public void onDisable() {
        getLogger().info("CzechCore has been disabled!");
    }
}
