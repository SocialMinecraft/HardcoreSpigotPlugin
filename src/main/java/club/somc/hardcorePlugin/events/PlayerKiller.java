package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * If a player killed another player, add an offense to them.
 */
public class PlayerKiller implements Listener {

    final Database db;
    final Logger logger;

    public PlayerKiller(Database db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        try {
            if (player.getKiller() != null && player.getKiller() instanceof Player) {
                Player killer = (Player) player.getKiller();
                HardcorePlayer hk = new HardcorePlayer(db, killer);
                hk.offenseCommited("Killed " + player.getName());
            }
        } catch (SQLException ex) {
            logger.warning(ex.getMessage());
        }
    }

}
