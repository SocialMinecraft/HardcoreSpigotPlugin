package club.somc.hardcorePlugin;

import club.somc.hardcorePlugin.shop.Shop;
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

    private final Database db;
    private final Logger logger;
    private final Shop shop;
    private final int initalCurrency;

    public JoinGame(Database db, Logger logger, Shop shop, int initalCurrency) {
        this.db = db;
        this.logger = logger;
        this.shop = shop;
        this.initalCurrency = initalCurrency;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HardcorePlayer hc = new HardcorePlayer(db, player);

        Boolean is_new;
        Boolean is_alive;
        try {
            is_new = db.updatePlayer(player);
            hc.updatePlayerState();
            is_alive = hc.isAlive();

            if (is_new) hc.addToWallet(initalCurrency, initalCurrency + " starting currency received.");
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return;
        }
        logger.info("Is new? " + player.getName() + " - " + is_new);

        // if the player is dead, lets show them the revive shop.
        if (!is_alive) shop.openShop(player);
    }
}
