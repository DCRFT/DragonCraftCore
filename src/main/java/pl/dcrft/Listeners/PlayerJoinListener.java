package pl.dcrft.Listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;
import pl.dcrft.Utils.RoundUtil;

import java.util.UUID;


public class PlayerJoinListener implements Listener {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        DatabaseManager databaseManager = new DatabaseManager();

        Player p = e.getPlayer();

        if (!e.getPlayer().hasPermission("pt.adm")) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                try {

                    String online = "teraz";
                    if(p.hasPermission("dcc.login.admin")) online = "-";
                    databaseManager.update("UPDATE " + DatabaseManager.table_bungee + " SET online='" + online + "' WHERE nick = '" + e.getPlayer().getName() + "'");
                    int kille = p.getStatistic(Statistic.PLAYER_KILLS);
                    int dedy = p.getStatistic(Statistic.DEATHS);
                    float kdr;
                    String ranga;
                    String update;
                    if (dedy == 0) {
                        kdr = (float) kille;
                    } else if (kille == 0) {
                        kdr = 0.0F;
                    } else {
                        kdr = (float) kille / (float) dedy;
                    }
                    kdr = RoundUtil.round(kdr, 2);

                    String kills = String.valueOf(p.getStatistic(Statistic.PLAYER_KILLS));
                    String deaths = String.valueOf(p.getStatistic(Statistic.DEATHS));

                    //Statistic expansion doubles the mine_block value, see https://github.com/PlaceholderAPI/Statistics-Expansion/issues/13
                    //So we're gonna divide it by 2 as a workaround :p
                    String blocks = String.valueOf(Integer.parseInt(PlaceholderAPI.setPlaceholders(p, "%statistic_mine_block%")) / 2);

                    int timeSeconds = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

                    String timeplayed = String.valueOf(timeSeconds);

                    if (plugin.isSkyblock) {

                        String poziom = "0";
                        if (SuperiorSkyblockAPI.getPlayer(p).getIsland() != null){
                            poziom = SuperiorSkyblockAPI.getPlayer(p).getIsland().getIslandLevel().toString();
                        }

                        UUID uuid = p.getUniqueId();

                        String kasa = String.valueOf(Economy.getMoneyExact(uuid));

                        update = "UPDATE `" + DatabaseManager.table_skyblock + "` SET kille = '" + kills + "', dedy = '" + deaths + "', kdr = '" + kdr + "', poziom = '" + poziom + "', kasa = '" + kasa + "', czasgry = '" + timeplayed + "' WHERE nick = '" + e.getPlayer().getName() + "'";
                    } else {
                        update = "UPDATE `" + DatabaseManager.table_survival + "` SET kille = '" + kills + "', dedy = '" + deaths + "', kdr = '" + kdr + "', bloki = '" + blocks + "', czasgry = '" + timeplayed + "' WHERE nick = '" + e.getPlayer().getName() + "'";
                    }
                    databaseManager.update(update);

                } catch (UserDoesNotExistException e2) {
                    e2.printStackTrace();
                    ErrorUtil.logError(ErrorReason.OTHER);
                }

            }, 20L);
        }
    }
}
