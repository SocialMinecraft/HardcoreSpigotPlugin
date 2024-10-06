package club.somc.hardcorePlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.http.WebSocket;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Give the player their daily login bonus.
 * Also set offense status.
 */
public class DailyJoin implements Listener {

    private final Database db;
    private final Logger logger;

    private final int dailyCurrency;
    private final int vipCurrency;
    private final OffsetTime reset;

    public DailyJoin(Database db, Logger logger, int dailyCurrency, int vipCurrency, OffsetTime reset) {
        this.db = db;
        this.logger = logger;
        this.dailyCurrency = dailyCurrency;
        this.vipCurrency = vipCurrency;
        this.reset = reset;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HardcorePlayer hp = new HardcorePlayer(db, player);

        try {
            OffsetDateTime lastDaily = hp.lastDaily();
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime todayResetTime = reset.atDate(LocalDate.now())
                    .atZoneSameInstant(now.getOffset())
                    .toOffsetDateTime();
            if (now.isBefore(todayResetTime)) {
                // If current time is before today's reset, check against yesterday's reset
                todayResetTime = todayResetTime.minusDays(1);
            }

            int amount = this.dailyCurrency;
            int amount_vip = player.hasPermission("hardcore.vip") ? this.vipCurrency : 0;

            if (lastDaily == null || lastDaily.isBefore(todayResetTime)) {
                logger.info("Giving " + player.getName() + " daily reward.");
                hp.giveDaily(amount, amount_vip);
                return;
            } else {
                logger.info(player.getName() + " already got today's reward.");
            }

        } catch (SQLException ex) {
            logger.warning(ex.getMessage());
        }

    }

    /*public static boolean isAfterReset(String timeString) {
        / *DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime specifiedTime = LocalTime.parse(timeString, formatter);
        LocalTime currentTime = LocalTime.now();

        return currentTime.isAfter(specifiedTime);* /

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        OffsetTime specifiedTime = OffsetTime.parse(timeString + "Z", DateTimeFormatter.ISO_OFFSET_TIME);
        OffsetTime currentTimeUTC = OffsetTime.now(ZoneOffset.UTC);

        return currentTimeUTC.isAfter(specifiedTime);
    }*/

}
