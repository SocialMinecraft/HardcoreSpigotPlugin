package club.somc.hardcorePlugin;

import org.bukkit.Bukkit;
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
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1*60*20, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1*60*20, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30*20, 3));

                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1*60*20, 100));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1*60*20, 100));

                player.getInventory().setItemInOffHand(new ItemStack(Material.ROTTEN_FLESH, 9));
                player.getInventory().setItem(8, new ItemStack(Material.TORCH, 1));

                player.getInventory().setItem(0, new ItemStack(Material.STICK, 1));
                ItemMeta ws = player.getInventory().getItem(0).getItemMeta();
                ws.setDisplayName("Pointy Stick");
                ws.setEnchantmentGlintOverride(true);
                player.getInventory().getItem(0).setItemMeta(ws);
            }
        }, 1L);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        int playtime =  player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        try {
            db.updatePlaytime(player.getUniqueId(), playtime);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Record the time the player joined the server.
        // If this is the first time they have joined, re-balance their lives to average.
        // Lastly, update the player to have the correct team and evil status.

        try {
            /*Date lastJoined =*/ db.playerJoined(event.getPlayer().getUniqueId());

            /*if (lastJoined == null) {
                int avgDeaths = db.averageDeaths(60*60*24*7); // Over last seven days
                while (avgDeaths > 0) {
                    lifeManager.playerDied(event.getPlayer(), "New Player Rebalanced.");
                    avgDeaths--;
                }
            }*/

            // Update username
            db.updateUsername(event.getPlayer().getUniqueId(), event.getPlayer().getName());

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
