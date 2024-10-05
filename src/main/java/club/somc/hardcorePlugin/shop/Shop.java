package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Shop implements CommandExecutor, Listener {

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;
    private final Plugin plugin;

    private final ReviveShop reviveShop;
    private final ItemShop itemShop;

    public Shop(Database db, Logger logger, ConfigurationSection config, Plugin plugin) {
        this.db = db;
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;

        this.reviveShop = new ReviveShop(db, logger, config);
        plugin.getServer().getPluginManager().registerEvents(this.reviveShop, plugin);

        this.itemShop = new ItemShop(db, logger, config);
        plugin.getServer().getPluginManager().registerEvents(this.itemShop, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return this.openShop(player);
    }

    public boolean openShop(Player player) {

        HardcorePlayer hp = new HardcorePlayer(db, player);

        try {
            if (hp.isAlive()) {
                this.itemShop.openShop(player);
            } else {
                this.reviveShop.openShop(player);
            }
        } catch (SQLException e) {
          logger.info(e.getMessage());
          return false;
        }

        return true;
    }

}
