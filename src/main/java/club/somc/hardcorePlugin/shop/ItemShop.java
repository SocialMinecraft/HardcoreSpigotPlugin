package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemShop implements Listener {

    private static class ShopItem {
        private final Material material;
        private final int cost;
        private final int amount;

        private final PotionEffectType effectType;
        private final int duration;
        private final int amplifier;

        public ShopItem(Material material, int cost, int amount) {
            this.material = material;
            this.cost = cost;
            this.amount = amount;

            this.effectType = null;
            this.duration = 0;
            this.amplifier = 0;
        }

        public ShopItem(ConfigurationSection item) {
            this.material = Material.valueOf(item.getString("material"));
            this.cost = item.getInt("cost");
            this.amount = item.getInt("amount");

            if (this.material == Material.POTION) {
                this.effectType = PotionEffectType.getByName(item.getString("effectType"));
                this.duration = item.getInt("duration");
                this.amplifier = item.getInt("amplifier");
            } else {
                this.effectType = null;
                this.duration = 0;
                this.amplifier = 0;
            }
        }

        public ItemStack getItem(boolean shop) {
            ItemStack item = new ItemStack(this.material, this.amount);
            ItemMeta meta = item.getItemMeta();

            if (shop) {
                List<String> lore_list = Arrays.asList("§6Cost: " + this.cost, "§7Click to buy.");
                meta.setLore(lore_list);
            }

            if (this.effectType != null) {
                PotionMeta potionMeta = (PotionMeta) meta;
                potionMeta.addCustomEffect(new PotionEffect(effectType, duration * 20, amplifier), true);
                item.setItemMeta(potionMeta);
            } else {
                item.setItemMeta(meta);
            }

            return item;
        }

        public int getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return material.toString();
        }
    }

    private final Database db;
    private final Logger logger;
    private final ConfigurationSection config;

    private Map<Integer, ShopItem> shopItems = new HashMap<>();

    public ItemShop(Database db, Logger logger, ConfigurationSection config) {
        this.db = db;
        this.logger = logger;
        this.config = config;

        loadShopItems();
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

        Inventory inventory = Bukkit.createInventory(null, 27, "Item Shop §6(Currency: "+wallet+")");

        for (Map.Entry<Integer, ShopItem> entry : shopItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem(true));
        }

        //inventory.setItem(26, createCustomItem(Material.BARRIER, "§bClose", "§7Close the shop."));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Item Shop")) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        HardcorePlayer hp = new HardcorePlayer(db, player);
        int slot = event.getRawSlot();

        if (shopItems.containsKey(slot)) {
            ShopItem item = shopItems.get(slot);

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "No space in inventory.");
                player.closeInventory();
                return;
            }

            try {
                if (hp.addToWallet(item.getCost()*-1, "Spent " + item.getCost() + " on " + item.toString())) {
                    player.getInventory().addItem(item.getItem(false));
                    player.sendMessage(ChatColor.GREEN + "You received: " + item.toString());
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                }
            } catch (SQLException ex) {
                logger.warning(ex.getMessage());
            }

            player.closeInventory();

            // Here you would typically check if the player has enough money
            // For this example, we'll just give them the item
            //player.getInventory().addItem(new ItemStack(item.getMaterial()));
            //player.sendMessage("§aYou bought a " + item.getMaterial().name() + " for $" + item.getCost());
        }
    }

    private void loadShopItems() {
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        int slot = 0;
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    //Material material = Material.valueOf(itemSection.getString("material"));
                    //int cost = itemSection.getInt("cost");
                    //int amount = itemSection.getInt("amount");
                    //shopItems.put(slot, new ShopItem(material, cost, amount));
                    shopItems.put(slot, new ShopItem(itemSection));
                }
                slot++;
            }
        }
    }

    //
}
