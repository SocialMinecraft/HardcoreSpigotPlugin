package club.somc.hardcorePlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
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
