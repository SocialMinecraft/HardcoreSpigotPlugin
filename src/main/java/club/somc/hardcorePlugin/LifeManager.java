package club.somc.hardcorePlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.sql.SQLException;

/**
 * LifeManager will set a player's color and game mode
 * base on their lives left.
 */
public class LifeManager {

    //enum LIFE {FIRST, SECOND, THIRD, GHOST};
    private Server server;
    private Database db;

    private Team green;
    private Team yellow;
    private Team red;
    private Team ghost;

    public LifeManager(Server server, Database db) {
        this.server = server;
        this.db = db;
        this.setupTeams();
    }

    /**
     * Run when a player logins in to set their team and game mode.
     *
     * @param player
     */
    public void updatePlayer(Player player) throws SQLException {

        // Get Player state
        int deaths = db.deathCount(player.getUniqueId());
        int extraLives = db.extraLifeCount(player.getUniqueId());
        deaths = Math.max(deaths-extraLives, 0);

        switch (deaths) {
            case 0:
                player.setGameMode(GameMode.SURVIVAL);
                green.addPlayer(player);
                break;
            case 1:
                player.setGameMode(GameMode.SURVIVAL);
                yellow.addPlayer(player);
                break;
            case 2:
                player.setGameMode(GameMode.SURVIVAL);
                red.addPlayer(player);
                break;
            default:
                player.setGameMode(GameMode.SPECTATOR);
                ghost.addPlayer(player);
                break;
        }
    }

    /**
     * Call when a player dies to reduce their lives,
     * and update game mode if needed.
     *
     * @param player
     * @param reason
     */
    public void playerDied(Player player, String reason) throws SQLException {

        // Get playtime and record to database
        int playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        // Update database
        db.storeDeath(player.getUniqueId(), reason, playtime);

        // update player
        this.updatePlayer(player);
    }


    private void setupTeams() {
        ScoreboardManager manager = this.server.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard(); // example used getNewScoreboard

        if (board.getTeam("no_deaths") != null) {
            green = board.getTeam("no_deaths");
            yellow = board.getTeam("one_death");
            red = board.getTeam("final_life");
            return;
        }

        green = board.registerNewTeam("no_deaths");
        green.setColor(ChatColor.GREEN);
        green.setPrefix("[No Deaths] ");

        yellow = board.registerNewTeam("one_death");
        yellow.setColor(ChatColor.YELLOW);
        yellow.setPrefix("[One Death] ");

        red = board.registerNewTeam("final_life");
        red.setColor(ChatColor.RED);
        red.setPrefix("[Final Life] ");

        ghost = board.registerNewTeam("ghost");
        ghost.setColor(ChatColor.GRAY);
        ghost.setPrefix("[Ghost] ");
    }
}
