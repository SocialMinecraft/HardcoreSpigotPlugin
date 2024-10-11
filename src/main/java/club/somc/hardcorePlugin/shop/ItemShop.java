package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import club.somc.hardcorePlugin.items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

        private final int cost;
        private final ItemStack item;

        public ShopItem(ItemStack itemStack, int cost) {
            this.item = itemStack;
            this.cost = cost;
        }

        public ShopItem(ConfigurationSection item) {
            this.cost = item.getInt("cost");
            this.item = configToItemStack(item);
        }

        private ItemStack configToItemStack(ConfigurationSection section) {
            Material material = Material.getMaterial(section.getString("material"));
            int amount = section.getInt("amount");

            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();

            if (material == Material.POTION) {
                PotionEffectType effectType = PotionEffectType.getByName(section.getString("effectType"));
                int duration = section.getInt("duration");
                int amplifier = section.getInt("amplifier");

                PotionMeta potionMeta = (PotionMeta) meta;
                potionMeta.addCustomEffect(new PotionEffect(effectType, duration * 20, amplifier), true);
                item.setItemMeta(potionMeta);
            } else {
                item.setItemMeta(meta);
            }

            return item;
        }

        public ItemStack getItemShop() {
            ItemStack re = this.item.clone();
            ItemMeta meta = re.getItemMeta();

            List<String> lore_list = Arrays.asList("§6Cost: " + this.cost, "§7Click to buy.");
            meta.setLore(lore_list);
            re.setItemMeta(meta);

            return re;
        }



        public ItemStack getItem() {
            return this.item.clone();
        }

        public int getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return this.item.getType().toString();
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

        Inventory inventory = Bukkit.createInventory(null, 9*4, "Item Shop §6(Currency: "+wallet+")");

        for (Map.Entry<Integer, ShopItem> entry : shopItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemShop());
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
                    player.getInventory().addItem(item.getItem());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "You received: " + item.toString());
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                    player.sendMessage(ChatColor.RED + "You don't have enough currency.");
                }
            } catch (SQLException ex) {
                logger.warning(ex.getMessage());
            }

            //player.closeInventory();
        }
    }

    private void loadShopItems() {
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        int slot = 0;

        // Add coin to shop
        {
            slot++;
            slot++;

            CopperCoin cp = new CopperCoin();
            shopItems.put(slot, new ShopItem(cp, cp.getValue()));
            slot++;

            SilverCoin sp = new SilverCoin();
            shopItems.put(slot, new ShopItem(sp, sp.getValue()));
            slot++;

            ElectrumCoin ep = new ElectrumCoin();
            shopItems.put(slot, new ShopItem(ep, ep.getValue()));
            slot++;

            GoldCoin gp = new GoldCoin();
            shopItems.put(slot, new ShopItem(gp, gp.getValue()));
            slot++;

            PlatinumCoin pp = new PlatinumCoin();
            shopItems.put(slot, new ShopItem(pp, pp.getValue()));
            slot++;
        }

        slot = 9;

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    shopItems.put(slot, new ShopItem(itemSection));
                }
                slot++;
            }
        }
    }
}
