package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import club.somc.hardcorePlugin.items.*;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.PLAYER_HEAD) {

                Player player = event.getPlayer();
                HardcorePlayer hardcorePlayer = new HardcorePlayer(database, player);

                if (CopperCoin.is(item)) {
                    useCoin(hardcorePlayer, item, new CopperCoin());
                    event.setCancelled(true);
                } else if (SilverCoin.is(item)) {
                    useCoin(hardcorePlayer, item, new SilverCoin());
                    event.setCancelled(true);
                } else if (ElectrumCoin.is(item)) {
                    useCoin(hardcorePlayer, item, new ElectrumCoin());
                    event.setCancelled(true);
                } else if (GoldCoin.is(item)) {
                    useCoin(hardcorePlayer, item, new GoldCoin());
                    event.setCancelled(true);
                } else if (PlatinumCoin.is(item)) {
                    useCoin(hardcorePlayer, item, new PlatinumCoin());
                    event.setCancelled(true);
                }
            }
        }
    }

    private void useCoin(HardcorePlayer player,  ItemStack item, Coin coin) {

        // verify the item is in the player inventory
        if(!player.getPlayer().getInventory().contains(item)) return;

        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_CHAIN_BREAK, 1.0f, 2.0f);

        try {
            player.addToWallet(coin.getValue(), "Used " + coin.getName());
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return;
        }

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getPlayer().getInventory().remove(item);
        }
    }
}
