package pl.dcrft.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.ConfigManager;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PlayerQuitListener implements Listener {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {

        DatabaseManager databaseManager = new DatabaseManager();

        Player p = event.getPlayer();

        if(p.hasPermission("dcc.login.admin")) return;

        if (!event.getPlayer().hasPermission("panel.adm")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'o' HH:mm");
            LocalDateTime now = LocalDateTime.now();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                String update = PlaceholderAPI.setPlaceholders(event.getPlayer(), "UPDATE " + DatabaseManager.table_bungee + " SET online='"+ dtf.format(now) + "' WHERE nick = '" + event.getPlayer().getName() + "'");
                databaseManager.update(update);
            });
        }
    }
}
