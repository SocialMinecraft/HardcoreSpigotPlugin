package club.somc.hardcorePlugin;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Database {

    private Connection connection;

    public Database() {

    }

    public void connect(String host, String port, String db, String user, String password) throws SQLException, ClassNotFoundException {
        //Class.forName("org.postgresql.jdbc.Driver");
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+db, user, password);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Update the player data.
     *
     * @param Player The player to update the database for.
     */
    public void updatePlayer(Player player) throws SQLException {
        String sql = """
                INSERT INTO 
                  players 
                  (player_uuid, playtime, name) VALUES (?, ?, ?)
                ON CONFLICT (player_uuid) DO UPDATE SET 
                  playtime = EXCLUDED.playtime,
                  name = EXCLUDED.name,
                  last_seen = current_timestamp
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, player.getUniqueId());
        statement.setInt(2, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        statement.setString(3, player.getName());
        statement.execute();
        statement.close();
    }



    /* OLD CODE */

    public void storeOffense(UUID playerUuid, int playtime, String reason) throws SQLException {
        String sql = "INSERT INTO offenses (player_uuid, reason, playtime) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        statement.setString(2, reason);
        statement.setInt(3, playtime);
        statement.execute();
        statement.close();
    }

    public int offenseCount(UUID playerUuid) throws SQLException {
        String sql = "SELECT COUNT(*) FROM offenses WHERE player_uuid = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        int result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return result;
    }

    public void storeDeath(UUID playerUuid, String reason, int playtime) throws SQLException {
        String sql = "INSERT INTO deaths (player_uuid, reason, playtime) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        statement.setString(2, reason);
        statement.setInt(3, playtime);
        statement.execute();
        statement.close();
    }

    public int deathCount(UUID playerUuid) throws SQLException {

        String sql = "SELECT COUNT(*) FROM deaths WHERE player_uuid = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        int result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return result;
    }

    public void storeExtraLife(UUID playerUuid, int playtime, String reason) throws SQLException {
        String sql = "INSERT INTO extra_lives (player_uuid, reason, playtime) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        statement.setString(2, reason);
        statement.setInt(3, playtime);
        statement.execute();
        statement.close();
    }

    public int extraLifeCount(UUID playerUuid) throws SQLException {

        String sql = "SELECT COUNT(*) FROM extra_lives WHERE player_uuid = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        int result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return result;
    }

    public void updatePlaytime(UUID playerUuid, int playtime) throws SQLException {
        String sql = "UPDATE players SET playtime = ? WHERE player_uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, playtime);
        statement.setObject(2, playerUuid);
        statement.execute();
    }

    public void updateUsername(UUID playerUuid, String username) throws SQLException {
        String sql = "UPDATE players SET name = ? WHERE player_uuid = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setObject(2, playerUuid);
        statement.execute();
    }

    /**
     * Get the average number of deaths players have who
     * have been active in the last X amount of time.
     *
     * Used to balance new players joining late.
     *
     * @param secondsAgo
     * @return Rounded number of deaths for active players.
     */
    public int averageDeaths(int secondsAgo) throws SQLException {
        String sql = """
                SELECT 
                    (
                      SELECT 
                        COUNT(*) 
                      FROM 
                        deaths 
                      WHERE 
                        deaths.player_uuid = players.player_uuid
                    )
                FROM 
                  players 
                WHERE
                  last_joined >= ?
                """;
        PreparedStatement statement = connection.prepareStatement(sql);

        Calendar cal  = Calendar.getInstance();
        cal.add(Calendar.SECOND, secondsAgo*-1);
        statement.setDate(1, new java.sql.Date(cal.getTime().getTime()));

        ResultSet resultSet = statement.executeQuery();

        int players = 0;
        int deaths = 0;
        while (resultSet.next()) {
            players++;
            deaths += resultSet.getInt(1);
        }
        resultSet.close();
        statement.close();

        return players < 3 ? 0 : Math.round(deaths/players);
    }

}
