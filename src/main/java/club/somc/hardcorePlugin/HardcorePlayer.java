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

        // Ensure correct game mode.
        if (isAlive())
            player.setGameMode(GameMode.SURVIVAL);
        else
            player.setGameMode(GameMode.SPECTATOR);
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

    public int getWallet() throws SQLException {
        return this.db.getWallet(player.getUniqueId());
    }

    public boolean addToWallet(int amount, String description) throws SQLException {
        if (this.getWallet() + amount < 0)
            return false;

        this.db.addEvent(
                this.player,
                Database.EventType.TRANSACTION,
                description
        );

        this.db.addToWallet(player.getUniqueId(), amount);

        return true;
    }


}
