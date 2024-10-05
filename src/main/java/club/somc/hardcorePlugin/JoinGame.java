package club.somc.hardcorePlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Make sure the player state is correct and their profiles updated.
 */
public class JoinGame implements Listener {

    private Database db;
    private Logger logger;

    public JoinGame(Database db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            db.updatePlayer(player);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        // todo - update status and effects.
    }
}
