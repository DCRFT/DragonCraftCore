package pl.dcrft.Managers.Statistic;

import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class StatisticManager {

    public static boolean checkPlayer(String p) {
        final DatabaseManager databaseManager =  new DatabaseManager();
        boolean result = false;
        try {
            ResultSet ogol = databaseManager.query("SELECT * FROM `" + DatabaseManager.table_bungee + "` WHERE nick = '" + p + "'");
            result = ogol.next();
            ogol.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static HashMap<StatisticType, String> getStatistics(ServerType serverType, String p) throws SQLException {
        final DatabaseManager databaseManager =  new DatabaseManager();

        String rank;
        String since;
        String online;

        HashMap<StatisticType, String> stats = new HashMap<>();

        ResultSet result = null;
        switch (serverType) {
            case Survival:
                result = databaseManager.query("SELECT " + DatabaseManager.table_bungee + ".nick AS ogolem_nick, " + DatabaseManager.table_bungee + ".since AS ogolem_since, " + DatabaseManager.table_bungee + ".online AS ogolem_online, " + DatabaseManager.table_bungee + ".ranga AS ogolem_ranga, " +

                        DatabaseManager.table_survival + ".nick AS result_nick, " +
                        DatabaseManager.table_survival + ".kille AS result_kille, " +
                        DatabaseManager.table_survival + ".dedy AS result_dedy, " +
                        DatabaseManager.table_survival + ".kdr AS result_kdr, " +
                        DatabaseManager.table_survival + ".bloki AS result_bloki, " +
                        DatabaseManager.table_survival + ".czasgry AS result_czasgry, " +
                        DatabaseManager.table_survival + ".slub AS result_slub" +

                        " FROM " + DatabaseManager.table_bungee + " " +
                        "LEFT JOIN " + DatabaseManager.table_survival + " " +
                        "ON " + DatabaseManager.table_survival + ".nick = " + DatabaseManager.table_bungee + ".nick" + " " +
                        "WHERE " + DatabaseManager.table_bungee + ".nick = '" + p + "'");
                if(!result.next()) return null;

                stats.put(StatisticType.SURVIVAL_KILLS, result.getString("result_kille"));
                stats.put(StatisticType.SURVIVAL_DEATHS, result.getString("result_dedy"));
                stats.put(StatisticType.SURVIVAL_KDR, String.valueOf(result.getFloat("result_kdr")));
                stats.put(StatisticType.SURVIVAL_BLOCKS, result.getString("result_bloki"));
                stats.put(StatisticType.SURVIVAL_TIMEPLAYED, TimeUtil.convertSecs(Integer.parseInt(result.getString("result_czasgry"))));
                stats.put(StatisticType.SURVIVAL_MARRY, result.getString("result_slub"));

                break;
            case SkyBlock:
                result = databaseManager.query("SELECT " + DatabaseManager.table_bungee + ".nick AS ogolem_nick, " + DatabaseManager.table_bungee + ".since AS ogolem_since, " + DatabaseManager.table_bungee + ".online AS ogolem_online, " + DatabaseManager.table_bungee + ".ranga AS ogolem_ranga, " +

                        DatabaseManager.table_skyblock + ".nick AS result_nick, " +
                        DatabaseManager.table_skyblock + ".kille AS result_kille, " +
                        DatabaseManager.table_skyblock + ".dedy AS result_dedy, " +
                        DatabaseManager.table_skyblock + ".kdr AS result_kdr, " +
                        DatabaseManager.table_skyblock + ".poziom AS result_poziom, " +
                        DatabaseManager.table_skyblock + ".kasa AS result_kasa, " +
                        DatabaseManager.table_skyblock + ".czasgry AS result_czasgry, " +
                        DatabaseManager.table_skyblock + ".slub AS result_slub" +

                        " FROM " + DatabaseManager.table_bungee + " " +

                        "LEFT JOIN " + DatabaseManager.table_skyblock + " " +
                        "ON " + DatabaseManager.table_skyblock + ".nick = " + DatabaseManager.table_bungee + ".nick" + " " +

                        "WHERE " + DatabaseManager.table_bungee + ".nick = '" + p + "'");
                if(!result.next()) return null;

                stats.put(StatisticType.SKYBLOCK_KILLS, result.getString("result_kille"));
                stats.put(StatisticType.SKYBLOCK_DEATHS, result.getString("result_dedy"));
                stats.put(StatisticType.SKYBLOCK_KDR, String.valueOf(result.getFloat("result_kdr")));
                stats.put(StatisticType.SKYBLOCK_LEVEL, result.getString("result_poziom"));
                stats.put(StatisticType.SKYBLOCK_MONEY, result.getString("result_kasa"));
                stats.put(StatisticType.SURVIVAL_TIMEPLAYED, TimeUtil.convertSecs(Integer.parseInt(result.getString("result_czasgry"))));
                stats.put(StatisticType.SURVIVAL_MARRY, result.getString("result_slub"));
                break;
            case PvP:
                result = databaseManager.query("SELECT " + DatabaseManager.table_bungee + ".nick AS ogolem_nick, " + DatabaseManager.table_bungee + ".since AS ogolem_since, " + DatabaseManager.table_bungee + ".online AS ogolem_online, " + DatabaseManager.table_bungee + ".ranga AS ogolem_ranga, " +

                        DatabaseManager.table_pvp + ".nick AS result_nick, " +
                        DatabaseManager.table_pvp + ".kille AS result_kille, " +
                        DatabaseManager.table_pvp + ".dedy AS result_dedy, " +
                        DatabaseManager.table_pvp + ".asysty AS result_asysty, " +
                        DatabaseManager.table_pvp + ".kdr AS result_kdr, " +
                        DatabaseManager.table_pvp + ".ranking AS result_ranking, " +
                        DatabaseManager.table_pvp + ".poziom AS result_poziom, " +
                        DatabaseManager.table_pvp + ".kasa AS result_kasa, " +
                        DatabaseManager.table_pvp + ".killstreak AS result_killstreak, " +
                        DatabaseManager.table_pvp + ".czasgry AS result_czasgry" +

                        " FROM " + DatabaseManager.table_bungee + " " +

                        "LEFT JOIN " + DatabaseManager.table_pvp + " " +
                        "ON " + DatabaseManager.table_pvp + ".nick = " + DatabaseManager.table_bungee + ".nick" + " " +

                        "WHERE " + DatabaseManager.table_bungee + ".nick = '" + p + "'");
                if(!result.next()) return null;

                stats.put(StatisticType.PVP_KILLS, result.getString("result_kille"));
                stats.put(StatisticType.PVP_DEATHS, result.getString("result_dedy"));
                stats.put(StatisticType.PVP_ASSISTS, result.getString("result_asysty"));
                stats.put(StatisticType.PVP_KDR, String.valueOf(result.getFloat("result_kdr")));
                stats.put(StatisticType.PVP_RANKING, result.getString("result_ranking"));
                stats.put(StatisticType.PVP_LEVEL, result.getString("result_poziom"));
                stats.put(StatisticType.PVP_KASA, result.getString("result_kasa"));
                stats.put(StatisticType.PVP_KILLSTREAK, result.getString("result_killstreak"));
                stats.put(StatisticType.PVP_TIMEPLAYED, TimeUtil.convertSecs(Integer.parseInt(result.getString("result_czasgry"))));
                break;
            case HNS:
                result = databaseManager.query("SELECT " + DatabaseManager.table_bungee + ".nick AS ogolem_nick, " + DatabaseManager.table_bungee + ".since AS ogolem_since, " + DatabaseManager.table_bungee + ".online AS ogolem_online, " + DatabaseManager.table_bungee + ".ranga AS ogolem_ranga, " +

                        DatabaseManager.table_hns + ".nick AS result_nick, " +
                        DatabaseManager.table_hns + ".kille AS result_kille, " +
                        DatabaseManager.table_hns + ".dedy AS result_dedy, " +
                        DatabaseManager.table_hns + ".kasa AS result_kasa, " +
                        DatabaseManager.table_hns + ".czasgry AS result_czasgry" +

                        " FROM " + DatabaseManager.table_bungee + " " +

                        "LEFT JOIN " + DatabaseManager.table_hns + " " +
                        "ON " + DatabaseManager.table_hns + ".nick = " + DatabaseManager.table_bungee + ".nick" + " " +

                        "WHERE " + DatabaseManager.table_bungee + ".nick = '" + p + "'");
                if(!result.next()) return null;

                stats.put(StatisticType.HNS_KILLS, result.getString("result_kille"));
                stats.put(StatisticType.HNS_DEATHS, result.getString("result_dedy"));
                stats.put(StatisticType.HNS_KASA, result.getString("result_kasa"));
                stats.put(StatisticType.HNS_TIMEPLAYED, TimeUtil.convertSecs(Integer.parseInt(result.getString("result_czasgry"))));

                break;
        }

        online = result.getString("ogolem_online");
        since = result.getString("ogolem_since");
        rank = result.getString("ogolem_ranga");
        if (online == null) {
            online = "?";
        }
        if (since == null) {
            since = "?";
        }

        stats.put(StatisticType.RANK, rank);
        stats.put(StatisticType.SINCE, since);
        stats.put(StatisticType.ONLINE, online);



                return stats;
            }
}
