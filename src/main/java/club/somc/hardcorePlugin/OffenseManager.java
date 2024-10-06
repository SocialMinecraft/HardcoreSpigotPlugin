package club.somc.hardcorePlugin;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Ensure the glowing effects do not get removed for a
 * player who has commited an offense until the timer runs
 * out.
 *
 * Also update their hearts if the timer does run out.
 */
public class OffenseManager implements Listener {

    private final Database db;
    private final Logger logger;
    private final int offense_time;
    private final JavaPlugin plugin;

    public OffenseManager(Database db, Logger logger, int offense_time, JavaPlugin plugin) {
        this.db = db;
        this.logger = logger;
        this.offense_time = offense_time;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        HardcorePlayer hp = new HardcorePlayer(db, player);

        int lastOffensePlaytime = -1;
        try {
            lastOffensePlaytime = hp.lastOffense();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return;
        }

        if (lastOffensePlaytime < 0) {
            return;
        }

        int playtimeOffenseExpires = lastOffensePlaytime + offense_time * 20;
        int timeLeft = playtimeOffenseExpires - player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if (timeLeft <= 0) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, timeLeft, 1, false, false));
        });
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (event.getModifiedType() != PotionEffectType.GLOWING)
            return;
        if (event.getNewEffect() != null)
            return;

        HardcorePlayer hp = new HardcorePlayer(db, player);

        int lastOffensePlaytime = -1;
        try {
            lastOffensePlaytime = hp.lastOffense();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return;
        }

        if (lastOffensePlaytime < 0) {
            return;
        }

        int playtimeOffenseExpires = lastOffensePlaytime + offense_time * 20;
        int timeLeft = playtimeOffenseExpires - player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if (timeLeft <= 0) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, timeLeft, 1, false, false));
        });
    }

}
