package club.somc.hardcorePlugin;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class HardcorePlayer {

    private Database db;
    private Player player;

    public HardcorePlayer(Database db, Player player) {
        this.db = db;
        this.player = player;
    }

    public void updatePlayerState() throws SQLException {

        // Make sure dead players are ghosts.
        if (!isAlive()) player.setGameMode(GameMode.SPECTATOR);
    }

    public void died(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.DIED,
                reason
        );
    }

    public void revive(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.REVIVED,
                reason
        );
    }

    public boolean isAlive() throws SQLException {
        return this.db.isAlive(player.getUniqueId());
    }


}
