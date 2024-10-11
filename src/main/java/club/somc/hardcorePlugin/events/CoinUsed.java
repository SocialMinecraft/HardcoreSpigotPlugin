package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import club.somc.hardcorePlugin.items.Coin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.logging.Logger;

public class CoinUsed implements Listener {

    private final Database database;
    private final Logger logger;

    public CoinUsed(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        logger.info(event.getAction().name());
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                if (Coin.isCoin(item)) {
                    Player player = event.getPlayer();
                    HardcorePlayer hardcorePlayer = new HardcorePlayer(database, player);

                    try {
                        hardcorePlayer.addToWallet(100, "Used Coin");
                    } catch (SQLException e) {
                        logger.warning(e.getMessage());
                        return;
                    }

                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                }
            }
            event.setCancelled(true);
        }
    }
}
