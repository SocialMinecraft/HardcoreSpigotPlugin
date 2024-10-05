package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Shop implements CommandExecutor, Listener {

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;
    private final Plugin plugin;

    private final ReviveShop reviveShop;

    public Shop(Database db, Logger logger, ConfigurationSection config, Plugin plugin) {
        this.db = db;
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;

        this.reviveShop = new ReviveShop(db, logger, config);
        plugin.getServer().getPluginManager().registerEvents(this.reviveShop, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        this.openShop(player);

        return true;
    }

    public void openShop(Player player) {
        // If player is dead open revive shop.
        this.reviveShop.openShop(player);

        // Else open shop to select between buffs and items
    }

}
