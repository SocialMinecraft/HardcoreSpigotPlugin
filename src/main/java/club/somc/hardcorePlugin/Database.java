package club.somc.hardcorePlugin;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Database {

    public enum EventType {
        DIED("died"),
        REVIVED("revived"),
        OFFENSE("offense"),
        TRANSACTION("transaction"),
        DAILY("daily");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        public String toSqlString() {
            return name;
        }
    }

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
     * @return True if the player is new, else false.
     */
    public boolean updatePlayer(Player player) throws SQLException {
        String sql = """
                INSERT INTO 
                  players 
                  (player_uuid, playtime, name) VALUES (?, ?, ?)
                ON CONFLICT (player_uuid) DO UPDATE SET 
                  playtime = EXCLUDED.playtime,
                  name = EXCLUDED.name,
                  last_seen = current_timestamp
                RETURNING
                    (xmax = 0) AS is_new
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, player.getUniqueId());
        statement.setInt(2, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        statement.setString(3, player.getName());
        //statement.execute();
        //statement.close();
        ResultSet resultSet = statement.executeQuery();

        boolean result = false;
        while (resultSet.next()) {
            result = resultSet.getBoolean(1);
            break;
        }
        resultSet.close();
        statement.close();

        return result;
    }

    public void addEvent(Player player, EventType eventType, String description) throws SQLException {
        String sql = """
                INSERT INTO 
                  events 
                  (player_uuid, playtime, type, description) VALUES (?, ?, ?::event_type, ?)
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, player.getUniqueId());
        statement.setInt(2, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        statement.setString(3, eventType.toSqlString());
        statement.setString(4, description);
        statement.execute();
        statement.close();
    }

    public boolean isAlive(UUID playerUuid) throws SQLException {
        String sql = """
                SELECT 
                    type
                FROM
                    events
                WHERE
                    player_uuid = ? AND
                    (type = 'died'::event_type OR type = 'revived'::event_type) 
                ORDER BY 
                    playtime DESC
                LIMIT 1
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        boolean isAlive = true;
        while (resultSet.next()) {
            String type = resultSet.getString(1);
            isAlive = !EventType.DIED.toSqlString().equals(type);
            break;
        }
        resultSet.close();
        statement.close();

        return isAlive;
    }

    public int lastOffensePlaytime(UUID playerUuid) throws SQLException {
        String sql = """
                SELECT 
                    playtime
                FROM
                    events
                WHERE
                    player_uuid = ? AND
                    type = 'offense'::event_type
                ORDER BY 
                    playtime DESC
                LIMIT 1
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();

        int playtime = -1;
        while (resultSet.next()) {
            playtime = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return playtime;
    }

    public OffsetDateTime lastDaily(UUID playerUuid) throws SQLException {
        String sql = """
                SELECT 
                    stamp
                FROM
                    events
                WHERE
                    player_uuid = ? AND
                    type = 'daily'::event_type
                ORDER BY 
                    stamp DESC
                LIMIT 1
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();


        OffsetDateTime lastDaily = null;
        while (resultSet.next()) {
            lastDaily = resultSet.getObject(1, OffsetDateTime.class);
            break;
        }
        resultSet.close();
        statement.close();

        return lastDaily;
    }

    public int getWallet(UUID playerUuid) throws SQLException {
        String sql = """
                SELECT 
                    wallet
                FROM
                    players
                WHERE
                    player_uuid = ?
                LIMIT 1
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, playerUuid);
        ResultSet resultSet = statement.executeQuery();
        int wallet = 0;
        while (resultSet.next()) {
            wallet = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return wallet;
    }

    public int addToWallet(UUID playerUuid, int amount) throws SQLException {
        String sql = """
                UPDATE 
                    players
                SET
                    wallet = wallet + ?
                WHERE
                    player_uuid = ?
                RETURNING
                    wallet
                ;
                """;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, amount);
        statement.setObject(2, playerUuid);
        ResultSet resultSet = statement.executeQuery();
        int wallet = 0;
        while (resultSet.next()) {
            wallet = resultSet.getInt(1);
            break;
        }
        resultSet.close();
        statement.close();

        return wallet;
    }
}
