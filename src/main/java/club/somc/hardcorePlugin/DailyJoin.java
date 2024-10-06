package club.somc.hardcorePlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.http.WebSocket;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Give the player their daily login bonus.
 * Also set offense status.
 */
public class DailyJoin implements WebSocket.Listener {

    private final Database db;
    private final Logger logger;

    private final int dailyCurrency;
    private final int vipCurrency;
    private final String resetUTC;

    public DailyJoin(Database db, Logger logger, int dailyCurrency, int vipCurrency, String resetUTC) {
        this.db = db;
        this.logger = logger;
        this.dailyCurrency = dailyCurrency;
        this.vipCurrency = vipCurrency;
        this.resetUTC = resetUTC;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    public static boolean isAfterReset(String timeString) {
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime specifiedTime = LocalTime.parse(timeString, formatter);
        LocalTime currentTime = LocalTime.now();

        return currentTime.isAfter(specifiedTime);*/

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        OffsetTime specifiedTime = OffsetTime.parse(timeString + "Z", DateTimeFormatter.ISO_OFFSET_TIME);
        OffsetTime currentTimeUTC = OffsetTime.now(ZoneOffset.UTC);

        return currentTimeUTC.isAfter(specifiedTime);

    }

}
