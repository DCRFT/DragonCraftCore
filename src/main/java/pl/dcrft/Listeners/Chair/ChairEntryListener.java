package pl.dcrft.Listeners.Chair;

import me.NoChance.PvPManager.Managers.PlayerHandler;
import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.PvPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;

public class ChairEntryListener implements Listener {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();
    final String prefix = LanguageManager.getMessage("prefix");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            Block block = event.getClickedBlock();
            if (Tag.STAIRS.isTagged(block.getType())) {
                Stairs stairs = (Stairs) block.getBlockData();
                if (block.getRelative(BlockFace.UP).getType().equals(Material.AIR)
                        && stairs.getShape().equals(Stairs.Shape.STRAIGHT)
                        && stairs.getHalf().equals(Bisected.Half.BOTTOM)) {

                    Player p = event.getPlayer();

                    PvPManager pvpmanager = PvPManager.getInstance();
                    PlayerHandler playerHandler = pvpmanager.getPlayerHandler();
                    PvPlayer pvplayer = playerHandler.get(p);
                    if (pvplayer.isInCombat()) {
                        return;
                    }

                    Location loc = block.getLocation();
                    if (p.getLocation().distance(loc) <=1) {
                        loc.setX(loc.getBlockX() + 0.5);
                        loc.setZ(loc.getBlockZ() + 0.5);
                        loc.setY(loc.getBlockY() - 0.5);

                        ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

                        armorStand.setVisible(false);
                        armorStand.setCustomName("Chair");
                        armorStand.setCustomNameVisible(false);
                        armorStand.setGravity(false);
                        armorStand.setSmall(true);

                        Location ploc = p.getLocation();

                        Vector direction = ploc.getDirection().multiply(-1);
                        ploc.setDirection(direction);

                        p.teleport(ploc);

                        armorStand.addPassenger(p);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix + LanguageManager.getMessage("chairs.entry"))), 2);
                    }
                }
            }
        }
    }
}
