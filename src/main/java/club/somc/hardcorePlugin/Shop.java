package club.somc.hardcorePlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Shop implements CommandExecutor, Listener {

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;

    public Shop(Database db, Logger logger, ConfigurationSection config) {
        this.db = db;
        this.logger = logger;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        //openCustomInventory((Player) sender);
        openReviveShop(player);
        return true;
    }

    public void openReviveShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Revive Shop §6(Currency: XXX)");

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
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.GRASS_BLOCK) {
            //player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
            //player.sendMessage("You received a diamond sword!");
        } else if (clickedItem.getType() == Material.RED_BED) {
            //player.setHealth(Math.min(player.getHealth() + 4, player.getMaxHealth()));
            //player.sendMessage("You've been healed!");
        } else if (clickedItem.getType() == Material.GOLDEN_APPLE) {
            //player.teleport(player.getWorld().getHighestBlockAt(player.getLocation().add(Math.random() * 20 - 10, 0, Math.random() * 20 - 10)).getLocation().add(0, 1, 0));
            //player.sendMessage("You've been teleported!");
        } else if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
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

}
