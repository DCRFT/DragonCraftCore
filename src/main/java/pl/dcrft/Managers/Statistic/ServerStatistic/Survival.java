package pl.dcrft.Managers.Statistic.ServerStatistic;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Managers.LanguageManager;

import java.util.ArrayList;

public class Survival {

    private static DragonCraftCore plugin = DragonCraftCore.getInstance();

    public static Inventory setServerInventory(Inventory inv, String player, String kills, String deaths, String kdr, String blocks, String timeplayed, String marry) {


        ItemStack marryStatus = new ItemStack(Material.MAP);
        ItemMeta metaMarryStatus = marryStatus.getItemMeta();
        if (marry != null && !marry.equalsIgnoreCase("NULL")) {
            metaMarryStatus.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + marry);
        } else {
            metaMarryStatus.setDisplayName(LanguageManager.getMessage("statistics.marry.title") + " " + LanguageManager.getMessage("statistics.marry.none"));
        }
        marryStatus.setItemMeta(metaMarryStatus);
        inv.setItem(23, marryStatus);



        ItemStack blocksStatus = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta metaBlocksStatus = blocksStatus.getItemMeta();
        metaBlocksStatus.setDisplayName(LanguageManager.getMessage("statistics.blocks") + " " + blocks);
        blocksStatus.setItemMeta(metaBlocksStatus);
        inv.setItem(21, blocksStatus);


        ItemStack pvpStatus = new ItemStack(Material.IRON_SWORD);
        ItemMeta metaPvpStatus = pvpStatus.getItemMeta();
        metaPvpStatus.setDisplayName(LanguageManager.getMessage("statistics.kills") + " " + kills);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(LanguageManager.getMessage("statistics.deaths") + " " + deaths);
        lore.add(LanguageManager.getMessage("statistics.kdr") + " " + kdr);
        metaPvpStatus.setLore(lore);
        pvpStatus.setItemMeta(metaPvpStatus);
        inv.setItem(19, pvpStatus);


        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta10 = clock.getItemMeta();
        meta10.setDisplayName(LanguageManager.getMessage("statistics.timeplayed") + " " + timeplayed);
        clock.setItemMeta(meta10);
        inv.setItem(30, clock);

        return inv;
    }

}
