package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Update player stats when they log out.
 */
public class LeaveGame implements Listener {

    private Database db;
    private Logger logger;

    public LeaveGame(Database db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            db.updatePlayer(player);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
}
