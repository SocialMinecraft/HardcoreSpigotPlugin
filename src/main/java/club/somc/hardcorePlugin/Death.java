package club.somc.hardcorePlugin;

import club.somc.hardcorePlugin.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

/**
 * Handle death and set the player to spectator.
 */
public class Death implements Listener {

    private final Database db;
    private final Logger logger;
    private final Shop shop;
    private final Plugin plugin;

    public Death(Database db, Logger logger, Shop shop, Plugin plugin) {
        this.db = db;
        this.logger = logger;
        this.shop = shop;
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        HardcorePlayer hp = new HardcorePlayer(db, p);

        try {
            hp.died(e.getDeathMessage());
            hp.updatePlayerState();
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        //if (p.getGameMode() != GameMode.SPECTATOR) return;
        HardcorePlayer hp = new HardcorePlayer(db, p);

        try {
            if (!hp.isAlive()) {
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        p.teleport(p.getLastDeathLocation());
                        shop.openShop(p);
                    }
                }, 5L);
            }
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return;
        }
    }
}
