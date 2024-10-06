package club.somc.hardcorePlugin;

import club.somc.hardcorePlugin.events.*;
import club.somc.hardcorePlugin.shop.Shop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.OffsetTime;

public final class HardcorePlugin extends JavaPlugin {

    private  Database db;

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        //getConfig().get("database.host");

        // Plugin startup logic
        db = new Database();
        try {
            db.connect(
                    config.getString("database.host"),
                    config.getString("database.port"),
                    config.getString("database.database"),
                    config.getString("database.user"),
                    config.getString("database.password")
            );
        } catch (SQLException e) {
            //e.printStackTrace();
            getLogger().warning(e.getMessage());
            return;
        } catch (ClassNotFoundException e) {
            getLogger().warning(e.getMessage());
            return;
        }

        Shop shop = new Shop(db, getLogger(), config.getConfigurationSection("shop"), this);
        getCommand("shop").setExecutor(shop);
        getServer().getPluginManager().registerEvents(shop, this);

        Ghost ghost = new Ghost(config.getInt("ghost.distance", 50), db, getLogger());
        getServer().getPluginManager().registerEvents(ghost, this);

        JoinGame join = new JoinGame(db, getLogger(), shop, config.getInt("starting_currency"));
        getServer().getPluginManager().registerEvents(join, this);

        LeaveGame leave = new LeaveGame(db, getLogger());
        getServer().getPluginManager().registerEvents(leave, this);

        Death death = new Death(db, getLogger(), shop, this);
        getServer().getPluginManager().registerEvents(death, this);

        PlayerKiller playerKiller = new PlayerKiller(db, getLogger());
        getServer().getPluginManager().registerEvents(playerKiller, this);

        OffenseManager offenseManager = new OffenseManager(db, getLogger(),
                config.getInt("offense_time", 3600), this);
        getServer().getPluginManager().registerEvents(offenseManager, this);

        OffsetTime resetTime = OffsetTime.parse(config.getString("daily.reset_utc", "00:00:00") + "Z");
        DailyJoin dailyJoin = new DailyJoin(db, getLogger(),
                config.getInt("daily.currency", 75),
                config.getInt("daily.currency", 50),
                resetTime
                );
        getServer().getPluginManager().registerEvents(dailyJoin, this);

        getLogger().info("HardcorePlugin enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            if (db != null)
                db.close();
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
        }

        super.onDisable();
        getLogger().info("HardcorePlugin disabled");
    }

}
