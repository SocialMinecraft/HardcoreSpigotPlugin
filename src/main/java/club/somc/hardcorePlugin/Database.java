package club.somc.hardcorePlugin;

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

    public void storeOffense(UUID playerUuid, String reason) throws SQLException {
        String sql = "INSERT INTO offenses (player_uuid, reason) VALUES (?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        statement.setString(2, reason);
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

    /**
     * Used to keep track of who is playing and
     * when they last connected.
     *
     * @param playerUuid
     * @return The last time the player logged in.
     *    Null if this is the first time they jointed.
     */
    public Date playerJoined(UUID playerUuid) throws SQLException {
        String sql = """
                INSERT INTO 
                  players 
                  (player_uuid) VALUES (?)
                ON CONFLICT (player_uuid) DO UPDATE SET 
                  prev_last_joined = players.last_joined,
                  last_joined = current_timestamp
                RETURNING 
                  prev_last_joined;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        Date result = null;
        while (resultSet.next()) {
            result = resultSet.getTimestamp(1);
            break;
        }
        resultSet.close();
        statement.close();

        return result;
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
