package club.somc.hardcorePlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Date;
import java.util.logging.Logger;

public class EventListener implements Listener {

    private Logger logger;
    private LifeManager lifeManager;
    private EvilManager evilManager;
    private Database db;
    private Plugin plugin;

    public EventListener(Plugin plugin, Logger logger, LifeManager lifeManager, EvilManager evilManager, Database db) {
        this.lifeManager = lifeManager;
        this.evilManager = evilManager;
        this.logger = logger;
        this.db = db;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Record the time the player joined the server.
        // If this is the first time they have joined, re-balance their lives to average.
        // Lastly, update the player to have the correct team and evil status.

        try {
            Date lastJoined = db.playerJoined(event.getPlayer().getUniqueId());

            if (lastJoined == null) {
                int avgDeaths = db.averageDeaths(60*60*24*7); // Over last seven days
                while (avgDeaths > 0) {
                    lifeManager.playerDied(event.getPlayer(), "New Player Rebalanced.");
                    avgDeaths--;
                }
            }

            lifeManager.updatePlayer(event.getPlayer());
            evilManager.updatePlayer(event.getPlayer());
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        try {
            if (player.getKiller() != null && player.getKiller() instanceof Player) {
                Player killer = (Player) player.getKiller();
                evilManager.addOffense(killer, "Killed " + player.getName());
                evilManager.updatePlayer(killer);
            }

            lifeManager.playerDied(player, event.getDeathMessage());
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        // we need to check that the entity is a player, they had the glowing effect, and they no longer have it.
        //logger.info(event.getEntity() instanceof Player ? "t" : "f");
        //logger.info(event.getModifiedType().toString());
        //logger.info(event.getNewEffect() == null ? "" : event.getNewEffect().toString());
        //logger.info(event.getOldEffect() == null ? "" : event.getOldEffect().toString());
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getModifiedType() != PotionEffectType.GLOWING)
            return;
        if (event.getNewEffect() != null)
            return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                evilManager.updatePlayer((Player) event.getEntity());
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        });
    }
}
