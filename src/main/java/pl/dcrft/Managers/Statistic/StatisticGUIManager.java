package pl.dcrft.Managers.Statistic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;
import pl.dcrft.Managers.Statistic.ServerStatistic.HNS;
import pl.dcrft.Managers.Statistic.ServerStatistic.PvP;
import pl.dcrft.Managers.Statistic.ServerStatistic.SkyBlock;
import pl.dcrft.Managers.Statistic.ServerStatistic.Survival;

import java.sql.SQLException;
import java.util.HashMap;

import static pl.dcrft.Managers.MessageManager.sendPrefixedMessage;

public class StatisticGUIManager {
    public static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    private static ServerType getCurrentProfile(Player p) {
        if(p == null || p.getOpenInventory() == null || p.getOpenInventory().getTopInventory() == null || !p.getOpenInventory().getTitle().contains(LanguageManager.getMessage("statistics.title"))) {
            return null;
        }
        if(p.getOpenInventory().getItem(3).getItemMeta().hasEnchant(Enchantment.THORNS) && !p.getOpenInventory().getItem(5).getItemMeta().hasEnchant(Enchantment.THORNS)) return ServerType.Survival;

        if(!p.getOpenInventory().getItem(3).getItemMeta().hasEnchant(Enchantment.THORNS) && p.getOpenInventory().getItem(5).getItemMeta().hasEnchant(Enchantment.THORNS)) return ServerType.SkyBlock;

        return null;
    }

    public static void showStatistics(ServerType serverType, Player sender, String p) {
        if(getCurrentProfile(sender) != null && getCurrentProfile(sender).equals(serverType)){
            return;
        }

        sender.closeInventory();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!StatisticManager.checkPlayer(p)) {
                sendPrefixedMessage(sender, "wrong_player_nickname");
                return;
            }

            HashMap<StatisticType, String> statistics = null;
            try {
                statistics = StatisticManager.getStatistics(serverType, p);
            } catch (SQLException e) {
                e.printStackTrace();
                sendPrefixedMessage(sender, "wrong_player_nickname");
                return;
            }

            String rank = statistics.get(StatisticType.RANK);
            String since = statistics.get(StatisticType.SINCE);
            String online = statistics.get(StatisticType.ONLINE);


            ItemStack glowa = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta meta = (SkullMeta) glowa.getItemMeta();

            meta.setOwner(p);
            meta.setDisplayName(LanguageManager.getMessage("statistics.head") + " " + p);
            glowa.setItemMeta(meta);

            Inventory inv = Bukkit.createInventory(null, 54, LanguageManager.getMessage("statistics.title") + p);

            inv.setItem(13, glowa);


            final ItemStack survi = new ItemStack(Material.IRON_PICKAXE);
            final ItemMeta metaSurvi = survi.getItemMeta();
            metaSurvi.setDisplayName(LanguageManager.getMessage("statistics.server.survival"));

            final ItemStack sky = new ItemStack(Material.GRASS_BLOCK);
            final ItemMeta metaSky = sky.getItemMeta();
            metaSky.setDisplayName(LanguageManager.getMessage("statistics.server.skyblock"));

            final ItemStack pvp = new ItemStack(Material.IRON_SWORD);
            final ItemMeta metaPvP = pvp.getItemMeta();
            metaPvP.setDisplayName(LanguageManager.getMessage("statistics.server.pvp"));

            final ItemStack hns = new ItemStack(Material.CRAFTING_TABLE);
            final ItemMeta metaHns = hns.getItemMeta();
            metaHns.setDisplayName(LanguageManager.getMessage("statistics.server.hns"));


            String kills,deaths,kdr,blocks,level,money,timeplayed,marry,assists,ranking,killstreak;
            kills = deaths = kdr = blocks = level = money = timeplayed = marry = assists = ranking = killstreak = null;

            switch (serverType){
                case Survival:
                    kills = statistics.get(StatisticType.SURVIVAL_KILLS);
                    deaths = statistics.get(StatisticType.SURVIVAL_DEATHS);
                    kdr = statistics.get(StatisticType.SURVIVAL_KDR);
                    blocks = statistics.get(StatisticType.SURVIVAL_BLOCKS);
                    timeplayed = statistics.get(StatisticType.SURVIVAL_TIMEPLAYED);
                    marry = statistics.get(StatisticType.SURVIVAL_MARRY);


                    metaSurvi.addEnchant(Enchantment.THORNS, 10, true);
                    inv = Survival.setServerInventory(inv, p, kills, deaths, kdr, blocks, timeplayed, marry);
                    break;

                case SkyBlock:
                    kills = statistics.get(StatisticType.SKYBLOCK_KILLS);
                    deaths = statistics.get(StatisticType.SKYBLOCK_DEATHS);
                    kdr = statistics.get(StatisticType.SKYBLOCK_KDR);
                    level = statistics.get(StatisticType.SKYBLOCK_LEVEL);
                    money = statistics.get(StatisticType.SKYBLOCK_MONEY);
                    timeplayed = statistics.get(StatisticType.SKYBLOCK_TIMEPLAYED);
                    marry = statistics.get(StatisticType.SKYBLOCK_MARRY);


                    metaSky.addEnchant(Enchantment.THORNS, 10, true);
                    inv = SkyBlock.setServerInventory(inv, p, kills, deaths, kdr, level, money, timeplayed, marry);
                    break;

                case PvP:
                    kills = statistics.get(StatisticType.PVP_KILLS);
                    deaths = statistics.get(StatisticType.PVP_DEATHS);
                    assists = statistics.get(StatisticType.PVP_ASSISTS);
                    kdr = statistics.get(StatisticType.PVP_KDR);
                    level = statistics.get(StatisticType.PVP_LEVEL);
                    ranking = statistics.get(StatisticType.PVP_RANKING);
                    money = statistics.get(StatisticType.PVP_KASA);
                    killstreak = statistics.get(StatisticType.PVP_KILLSTREAK);
                    timeplayed = statistics.get(StatisticType.PVP_TIMEPLAYED);


                    metaPvP.addEnchant(Enchantment.THORNS, 10, true);
                    inv = PvP.setServerInventory(inv, p, kills, deaths, assists, kdr, level, ranking, money, killstreak, timeplayed);
                    break;

                case HNS:
                    kills = statistics.get(StatisticType.HNS_KILLS);
                    deaths = statistics.get(StatisticType.HNS_DEATHS);
                    money = statistics.get(StatisticType.HNS_KASA);
                    timeplayed = statistics.get(StatisticType.HNS_TIMEPLAYED);


                    metaHns.addEnchant(Enchantment.THORNS, 10, true);
                    inv = HNS.setServerInventory(inv, p, kills, deaths, money, timeplayed);
                    break;

            }

            survi.setItemMeta(metaSurvi);
            inv.setItem(2, survi);

            sky.setItemMeta(metaSky);
            inv.setItem(3, sky);

            pvp.setItemMeta(metaPvP);
            inv.setItem(4, pvp);

            hns.setItemMeta(metaHns);
            inv.setItem(5, hns);

            final ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            final ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

            int[] white = new int[]{10, 11, 15, 16, 28, 34, 37, 38, 40, 42, 43};
            for (int i : white) {
                inv.setItem(i, whiteGlass);
            }

            ItemStack onlineStatus = new ItemStack(Material.LIME_DYE);
            ItemMeta metaOnlineStatus = onlineStatus.getItemMeta();

            if (online.equalsIgnoreCase("teraz")) {
                onlineStatus = new ItemStack(Material.LIME_DYE);
                metaOnlineStatus.setDisplayName(LanguageManager.getMessage("statistics.status.online"));
            } else {
                onlineStatus = new ItemStack(Material.RED_DYE);
                metaOnlineStatus.setDisplayName(LanguageManager.getMessage("statistics.status.offline") + online);
            }
            onlineStatus.setItemMeta(metaOnlineStatus);
            inv.setItem(12, onlineStatus);

            ItemStack rankStatus = new ItemStack(Material.EMERALD);
            ItemMeta metaRankStatus = rankStatus.getItemMeta();
            metaRankStatus.setDisplayName(LanguageManager.getMessage("statistics.rank") + " " + rank);
            rankStatus.setItemMeta(metaRankStatus);
            inv.setItem(14, rankStatus);

            ItemStack sinceStatus = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta metaSinceStatus = sinceStatus.getItemMeta();
            metaSinceStatus.setDisplayName(LanguageManager.getMessage("statistics.since") + " " + since);
            sinceStatus.setItemMeta(metaSinceStatus);
            inv.setItem(39, sinceStatus);

            ItemStack versionStatus = new ItemStack(Material.REDSTONE_TORCH);
            ItemMeta metaVersionStatus = versionStatus.getItemMeta();
            metaVersionStatus.setDisplayName(LanguageManager.getMessage("statistics.plugin_version") + " " + plugin.getDescription().getVersion());
            versionStatus.setItemMeta(metaVersionStatus);
            inv.setItem(41, versionStatus);



            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null) inv.setItem(i, blackGlass);
            }


            Inventory finalInv = inv;
            Bukkit.getScheduler().runTask(plugin, () -> sender.openInventory(finalInv));


        });


    }

}
