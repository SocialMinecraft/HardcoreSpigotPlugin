package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReviveShop implements Listener {

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;
    private final Location spawn;

    public ReviveShop(Database db, Logger logger, ConfigurationSection config) {
        this.db = db;
        this.logger = logger;
        this.config = config;

        Location _spawn = Bukkit.getWorlds().get(0).getSpawnLocation();;
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                _spawn = w.getSpawnLocation();
                break;
            }
        }
        this.spawn = _spawn;
    }

    public void openShop(Player player) {
        HardcorePlayer hp = new HardcorePlayer(db, player);

        int wallet = 0;
        try {
            wallet = hp.getWallet();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, "Revive Shop §6(Currency: "+wallet+")");

        inventory.setItem(11, createCustomItem(Material.GRASS_BLOCK, "§bRevive Spawn", "§6Cost: " + config.getInt("revive"), "§7Click to revive at spawn"));
        inventory.setItem(13, createCustomItem(Material.RED_BED, "§bRevive Bed", "§6Cost: " + config.getInt("revive"), "§7Click to revive at your bed."));
        inventory.setItem(15, createCustomItem(Material.GOLDEN_APPLE, "§bRevive Here", "§6Cost: " + config.getInt("revive"), "§7Click to revive here."));

        inventory.setItem(26, createCustomItem(Material.BARRIER, "§bClose", "§7Close the shop."));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onReviveShopClick(InventoryClickEvent event) {
        //if (!event.getView().getTitle().equals("Custom Menu")) return;
        if (!event.getView().getTitle().startsWith("Revive Shop")) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        HardcorePlayer hp = new HardcorePlayer(db, player);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int revive_cost = config.getInt("revive");
        try {
            if (clickedItem.getType() == Material.GRASS_BLOCK) {
                Location loc = ensureSpawnSafety(spawn);
                if (hp.addToWallet(revive_cost*-1, "Spent " + revive_cost + " on revive.")) {
                    player.teleport(loc);
                    hp.revive("Purchased revive.");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                    return;
                }
            } else if (clickedItem.getType() == Material.RED_BED) {
                Location loc = player.getRespawnLocation();
                if (loc == null) {
                    player.sendMessage(ChatColor.RED + "No bed found.");
                    return;
                }
                if (hp.addToWallet(revive_cost*-1, "Spent " + revive_cost + " on revive.")) {
                    player.teleport(loc);
                    hp.revive("Purchased revive.");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                    return;
                }
            } else if (clickedItem.getType() == Material.GOLDEN_APPLE) {
                Location loc = player.getLocation();
                if (hp.addToWallet(revive_cost*-1, "Spent " + revive_cost + " on revive.")) {
                    player.teleport(loc);
                    hp.revive("Purchased revive.");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                    return;
                }
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        } catch (SQLException ex) {
            logger.warning(ex.getMessage());
        }
    }

    private ItemStack createCustomItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore_list = Arrays.asList(lore);
        meta.setLore(lore_list);
        item.setItemMeta(meta);
        return item;
    }

    private static Location ensureSpawnSafety(Location location) {
        // Find the highest non-air block at the spawn coordinates
        location.setY(location.getWorld().getHighestBlockYAt(location));

        // Move up to ensure the player spawns above ground
        location.add(0, 1, 0);

        return location;
    }
}
