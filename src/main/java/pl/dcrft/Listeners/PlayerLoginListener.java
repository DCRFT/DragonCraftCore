package pl.dcrft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import static pl.dcrft.Managers.Language.LanguageManager.getMessage;


public class PlayerLoginListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            Player player = event.getPlayer();
            if (player != null) {
            }

            if (player != null && player.hasPermission("vipslot.allow")) {
                event.allow();
                return;
            } else {
                event.setKickMessage(getMessage("prefix") + getMessage("server_full"));
            }
        }

    }
}
