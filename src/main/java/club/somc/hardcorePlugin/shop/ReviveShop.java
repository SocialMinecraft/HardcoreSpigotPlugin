package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ReviveShop implements Listener {

    private static class ShopSummon {

        private final int cost;
        private final ItemStack item;
        private final EntityType entityType;

        public ShopSummon(int cost, ItemStack item, EntityType entityType) {
            this.cost = cost;
            this.item = item;
            this.entityType = entityType;
        }

        public ItemStack getShopIcon() {
            ItemStack re = this.item.clone();
            ItemMeta meta = re.getItemMeta();

            List<String> lore_list = Arrays.asList("§6Cost: " + this.cost, "§7Click to summon.");
            meta.setLore(lore_list);
            re.setItemMeta(meta);

            return re;
        }

        public void summon(Location location) {
            World world = location.getWorld();
            world.spawnEntity(location, EntityType.WARDEN);
        }

        public int getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return this.entityType.toString();
        }
    }

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;
    private final Location spawn;
    private final Shop shop;

    private final Map<Integer, ShopSummon> shopItems = new HashMap<>();

    public ReviveShop(Database db, Logger logger, ConfigurationSection config, Shop shop) {
        this.db = db;
        this.logger = logger;
        this.config = config;
        this.shop = shop;

        Location _spawn = Bukkit.getWorlds().get(0).getSpawnLocation();;
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                _spawn = w.getSpawnLocation();
                break;
            }
        }
        this.spawn = _spawn;

        loadReviveShopSummons();
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

        inventory.setItem(8, createCustomItem(Material.BARRIER, "§bClose", "§7Close the shop."));

        for (Map.Entry<Integer, ShopSummon> entry : shopItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getShopIcon());
        }

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
                    shop.giveBook(player);
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
                    shop.giveBook(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                    return;
                }
            } else if (clickedItem.getType() == Material.GOLDEN_APPLE) {
                Location loc = player.getLocation();
                if (hp.addToWallet(revive_cost*-1, "Spent " + revive_cost + " on revive.")) {
                    player.teleport(loc);
                    hp.revive("Purchased revive.");
                    shop.giveBook(player);
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

    private void loadReviveShopSummons() {
        ConfigurationSection itemsSection = config.getConfigurationSection("revive_summons");
        int slot = 18;

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    EntityType entityType = EntityType.valueOf(itemSection.getString("entity"));
                    String name = itemSection.getString("name");
                    String texture = itemSection.getString("texture");
                    int cost = itemSection.getInt("cost");

                    // create head
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwner(texture);
                    meta.setDisplayName("Summon " + name);
                    head.setItemMeta(meta);

                    shopItems.put(slot, new ShopSummon(cost, head, entityType));
                }
                slot++;
            }
        }
    }
}
