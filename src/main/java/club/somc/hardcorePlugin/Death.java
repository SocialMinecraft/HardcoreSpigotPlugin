package club.somc.hardcorePlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.logging.Logger;

/**
 * Handle death and set the player to spectator.
 */
public class Death implements Listener {

    private final Database db;
    private final Logger logger;

    public Death(Database db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        HardcorePlayer hp = new HardcorePlayer(db, p);

        try {
            hp.died(e.getDeathMessage());
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return;
        }
    }
}
