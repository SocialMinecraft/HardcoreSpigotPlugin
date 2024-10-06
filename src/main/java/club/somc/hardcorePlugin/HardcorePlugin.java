package club.somc.hardcorePlugin;

import club.somc.hardcorePlugin.shop.Shop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class HardcorePlugin extends JavaPlugin {

    private  Database db;
    private EvilManager evilManager;
    private LifeManager lifeManager;

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

        /*evilManager = new EvilManager(db);
        lifeManager = new LifeManager(getServer(), db);

        getServer().getPluginManager().registerEvents(new EventListener(this, getLogger(),lifeManager,evilManager,db), this);

        getCommand("extra_life").setExecutor(new GrantExtraLifeCommand(getLogger(), lifeManager));
        getCommand("extra_life").setTabCompleter(new GrantExtraLifeCommand(getLogger(), lifeManager));

        getCommand("mark_evil").setExecutor(new MarkEvilCommand(getLogger(), evilManager));
        getCommand("mark_evil").setTabCompleter(new MarkEvilCommand(getLogger(), evilManager));

        getLogger().info("HardcorePlugin enabled");*/

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

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        //OffsetTime resetTime = OffsetTime.parse(config.getString("daily.reset_utc", "00:00:00") + "Z", formatter);
        OffsetTime resetTime = OffsetTime.parse(config.getString("daily.reset_utc", "00:00:00") + "Z");
        DailyJoin dailyJoin = new DailyJoin(db, getLogger(),
                config.getInt("daily.currency", 75),
                config.getInt("daily.currency", 50),
                resetTime
                );
        getServer().getPluginManager().registerEvents(dailyJoin, this);
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
