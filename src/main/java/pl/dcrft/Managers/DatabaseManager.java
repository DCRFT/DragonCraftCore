package pl.dcrft.Managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DatabaseManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static Connection connection;
    public static final String host = ConfigManager.getDatabaseFile().getString("host");
    public static final String database = ConfigManager.getDatabaseFile().getString("database");
    public static final String username = ConfigManager.getDatabaseFile().getString("user");
    public static final String password = ConfigManager.getDatabaseFile().getString("password");
    public static final int port = ConfigManager.getDatabaseFile().getInt("port");
    public static final String table_survival = ConfigManager.getDatabaseFile().getString("table_survival");
    public static final String table_skyblock = ConfigManager.getDatabaseFile().getString("table_skyblock");
    public static final String table_pvp = ConfigManager.getDatabaseFile().getString("table_pvp");
    public static final String table_hns = ConfigManager.getDatabaseFile().getString("table_hns");

    public static final String table_bungee = ConfigManager.getDatabaseFile().getString("table_bungee");
    private static final String properties = ConfigManager.getDatabaseFile().getString("properties");

    private static HikariDataSource dataSource;



    static String getJdbcUrl(String host, int port, String database) {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

    public static void initializeDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl(host, port, database));
        config.setUsername(username);
        config.setPassword(password);

        // See: https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("encoding", "UTF-8");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.setMaxLifetime(30000);
        config.setMaximumPoolSize(1);


        dataSource = new HikariDataSource(config);

            Bukkit.getLogger().warning(dataSource.getDataSourceProperties().toString());
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            ErrorUtil.logError(ErrorReason.DATABASE);
            throw new RuntimeException(e);
        }
    }

    private boolean checkConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                if(dataSource == null) {
                    Bukkit.getLogger().warning("DataSource is null! Initializing...");
                    initializeDataSource();
                    connection = dataSource.getConnection();
                } else {
                    connection = dataSource.getConnection();
                }
            }
        } catch (SQLException e) {
            ErrorUtil.logError(ErrorReason.DATABASE);
            throw new RuntimeException(e);
        }
        return connection;
    }

    public int update(String update) {
        if (!checkConnection()) {
            connection = getConnection();
        }
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            int result = statement.executeUpdate();
            statement.close();
            connection.close();
            return result;
        } catch (SQLException e) {
            ErrorUtil.logError(ErrorReason.DATABASE);
            throw new RuntimeException(e);
        }

    }

    public ResultSet query(String query) {
        if (!checkConnection()) {
            connection = getConnection();
        }
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            statement.close();
            connection.close();
            return result;
        } catch (SQLException e) {
            ErrorUtil.logError(ErrorReason.DATABASE);
            throw new RuntimeException(e);
        }

    }


}