package pl.dcrft.Managers;

import org.bukkit.scheduler.BukkitRunnable;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.Connection;
import java.sql.DriverManager;
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

    public static final String table_bungee = ConfigManager.getDatabaseFile().getString("table_bungee");
    public static final String properties = ConfigManager.getDatabaseFile().getString("properties");

    public static String table = null;


    public static boolean openConnection() {
                try {
                    if (connection == null || connection.isClosed()) {
                        synchronized(plugin) {
                                Class.forName("com.mysql.jdbc.Driver");
                                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + properties, username, password);
                                
                            switch (plugin.getConfig().getString("server.type")) {
                                case "survival" -> table = table_survival;
                                case "skyblock" -> table = table_skyblock;
                            }
                        return true;
                        }
                    }
                    return true;
                } catch (SQLException | ClassNotFoundException e) {
                    ErrorUtil.logError(ErrorReason.DATABASE);
                    e.printStackTrace();
                    return false;
                }
            }

    public static void closeConnection() {
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    ErrorUtil.logError(ErrorReason.DATABASE);
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);



    }
}
