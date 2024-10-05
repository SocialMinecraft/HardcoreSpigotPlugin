package club.somc.hardcorePlugin;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

/**
 * Limit Ghost movement to where they died
 */
public class Ghost implements Listener {

    private int max_distance;
    private Logger logger;

    public Ghost(int max_distance, Logger logger) {
        this.max_distance = max_distance;
        this.logger = logger;
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
}
