package club.somc.hardcorePlugin.shop;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

public class Shop implements CommandExecutor, Listener {

    private static final String BOOK_NAME = "§6Shop";

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

        this.reviveShop = new ReviveShop(db, logger, config, this);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.BOOK && item.hasItemMeta() &&
                    BOOK_NAME.equals(item.getItemMeta().getDisplayName())) {
                event.setCancelled(true);
                this.openShop(event.getPlayer());
            }
        }
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

    private ItemStack createBook() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(BOOK_NAME);
        meta.setLore(Arrays.asList("Right-click to open the shop!"));

        // Add enchantment glow
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        // Hide the enchantment from the item's tooltip
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        book.setItemMeta(meta);
        return book;
    }

    public void giveBook(Player player) {
        player.getInventory().addItem(createBook());
        //player.sendMessage("You've received the shop Book!");
    }

}
