package club.somc.hardcorePlugin;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

/**
 * Limit Ghost movement to where they died
 */
public class Ghost implements Listener {

    private final int max_distance;
    private final Logger logger;
    private final Database db;

    public Ghost(int max_distance, Database db, Logger logger) {
        this.max_distance = max_distance;
        this.logger = logger;
        this.db = db;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR) return;

        // get player death location
        Location loc = player.getLastDeathLocation();
        if (loc == null) {
            logger.warning("Missing death location for spectator player: " + player.getName());
            return;
        }

        // If player is too far, teleport them back.
        if (loc.distance(player.getLocation()) > max_distance) {
            player.teleport(loc);
            player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        // no need to do anything if we are moving to spectator.
        if (event.getNewGameMode() == GameMode.SPECTATOR) return;

        logger.info("Hello");

        HardcorePlayer hc = new HardcorePlayer(db, player);
        try {
            if (!hc.isAlive()) {
                player.setGameMode(GameMode.SPECTATOR);
                event.setCancelled(true);
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}
