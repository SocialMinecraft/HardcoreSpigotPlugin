package club.somc.hardcorePlugin;

import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to manage negative effects on
 * players who have been evil.
 *
 * Reduction to hearts, set aura.
 */
public class EvilManager {

    private Map<UUID, Integer> isEvilCache = new HashMap<>();
    private Database db;

    public EvilManager(Database db) {
        this.db = db;
    }

    /**
     * Call each time effects drop off a player.
     * @param player
     * @throws SQLException
     */
    public void updatePlayer(Player player) throws SQLException {
        int evilCount = isEvil(player.getUniqueId());
        if (evilCount <= 0)
            return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));

        int playerHeartLoss = Math.min(evilCount * 4, 16);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20-playerHeartLoss);
    }

    public void addOffense(Player player, String resaon) throws SQLException {
        db.storeOffense(player.getUniqueId(), player.getStatistic(Statistic.PLAY_ONE_MINUTE), resaon);

        if (isEvilCache.containsKey(player.getUniqueId())) {
            isEvilCache.put(player.getUniqueId(), isEvilCache.get(player.getUniqueId()) + 1);
        } else {
            isEvilCache.put(player.getUniqueId(), 1);
        }
    }

    private int isEvil(UUID uuid) throws SQLException {
        if (isEvilCache.containsKey(uuid))
            return isEvilCache.get(uuid);

        int evilCount = db.offenseCount(uuid);
        isEvilCache.put(uuid, evilCount);
        return evilCount;
    }
}
