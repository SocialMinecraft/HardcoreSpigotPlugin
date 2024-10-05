package club.somc.hardcorePlugin;

import org.bukkit.entity.Player;

import java.sql.SQLException;

public class HardcorePlayer {

    private Database db;
    private Player player;

    public HardcorePlayer(Database db, Player player) {
        this.db = db;
        this.player = player;
    }

    public void died(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.DEATH,
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

    public boolean isAlive() {
        return true;
    }


}
