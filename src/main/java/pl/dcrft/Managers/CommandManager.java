package pl.dcrft.Managers;

import com.earth2me.essentials.commands.WarpNotFoundException;
import net.ess3.api.IEssentials;
import net.ess3.api.InvalidWorldException;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pl.dcrft.DragonCraftCore;
import pl.dcrft.Utils.ConfigUtil;
import pl.dcrft.Utils.GroupUtil;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static pl.dcrft.DragonCraftCore.es;

public class CommandManager implements CommandExecutor {
    private static final DragonCraftCore plugin = DragonCraftCore.getInstance();

    final String prefix = LanguageManager.getMessage("prefix");

    public boolean onCommand(final @NotNull CommandSender sender, final Command cmd, final @NotNull String label, final String[] args) {

        DatabaseManager databaseManager = new DatabaseManager();

        if (cmd.getName().equalsIgnoreCase("slub")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (Bukkit.getPlayer(args[0]) == null || !Bukkit.getPlayer(args[0]).isOnline()) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            Player other = Bukkit.getPlayer(args[0]);
            if (plugin.getConfig().getStringList("staff").contains(Bukkit.getPlayer(args[0]).getName())) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (args[0].equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.send.self");
                return false;
            } else {
                if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.already");
                    return false;
                }
                if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.target_already");
                    return false;
                } else {
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", Bukkit.getOfflinePlayer(args[0]).getName());
                    ConfigManager.saveData();
                    MessageManager.sendPrefixedMessage(p, "marry.send.sent.self");

                    other.sendMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.sent.target"), p.getName()));
                    return true;
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("sakceptuj")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
                return false;
            }
            if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") != null) {
                MessageManager.sendPrefixedMessage(p, "marry.already");
                return false;
            }
            if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
                return false;
            } else {
                if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + "slub") != null) {
                    MessageManager.sendPrefixedMessage(p, "marry.target_already");
                    return false;
                }

                ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("marry_item.material")));
                item.setAmount(plugin.getConfig().getInt("marry_item.amount"));
                ItemMeta itemMeta = item.getItemMeta();
                if(plugin.getConfig().getString("marry_item.name") != null){
                    itemMeta.setDisplayName(plugin.getConfig().getString("marry_item.name"));
                }
                if(plugin.getConfig().getString("marry_item.lore") != null){
                    itemMeta.setLore(plugin.getConfig().getStringList("marry_item.lore"));
                }
                if(plugin.getConfig().getString("marry_item.enchantment.enchantment") != null && plugin.getConfig().get("marry_item.enchantment.level") != null){
                    itemMeta.addEnchant(Enchantment.getByName(plugin.getConfig().getString("marry_item.enchantment.enchantment")), plugin.getConfig().getInt("marry_item.enchantment.level"), true);
                }
                item.setItemMeta(itemMeta);

                if (!p.getInventory().containsAtLeast(item, plugin.getConfig().getInt("marry_item.amount"))) {
                    MessageManager.sendPrefixedMessage(p, "marry.send.accept.missing_item");
                    return false;
                } else {
                    p.getInventory().removeItem(item);
                    if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                        MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.send.accept.accepted");
                    }
                    MessageManager.sendPrefixedMessage(p, "marry.send.accept.accepted");
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slub", Bukkit.getOfflinePlayer(args[0]).getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", p.getName());
                    ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                    ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                    ConfigManager.saveData();

                    String updatep = "UPDATE `" + DatabaseManager.table_survival + "` SET slub = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "' WHERE nick = '" + p.getName() + "'";
                    String updateo = "UPDATE `" + DatabaseManager.table_survival + "` SET slub = '" + p.getName() + "' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                    databaseManager.update(updatep);
                    databaseManager.update(updateo);

                    if(plugin.getConfig().getString("marry_warp") != null) {
                        try {
                            Location loc = es.getWarps().getWarp(plugin.getConfig().getString("marry_warp"));
                            p.teleport(loc);
                            if(Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) Bukkit.getPlayer(args[0]).teleport(loc);
                            String x = String.valueOf(loc.getX());
                            String y = String.valueOf(loc.getY() + 5);
                            String z = String.valueOf(loc.getZ());
                            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                                    "summon firework_rocket " + x + " " + y + " " + z + " {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;1973019,11743532,3887386,5320730,2437522,8073150,2651799,11250603,4408131,14188952,4312372,14602026,6719955,12801229,15435844,15790320],FadeColors:[I;1973019,11743532,3887386,5320730,2437522,8073150,2651799,11250603,4408131,14188952,4312372,14602026,6719955,12801229,15435844,15790320]}]}}}}");
                            } catch (WarpNotFoundException | InvalidWorldException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.accept.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));
                    return false;
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("sodrzuc")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba").equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.no_ivite_send");
            } else {
                MessageManager.sendPrefixedMessage(p, "marry.send.reject.rejected");
                if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                    MessageManager.sendPrefixedMessage(Bukkit.getPlayer(args[0]), "marry.send.reject.rejected");
                }
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                ConfigManager.saveData();
            }
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("rozwod")) {
            Player p = (Player) sender;
            if (args.length == 0) {
                MessageManager.sendPrefixedMessage(p, "wrong_player_nickname");
            } else if (args[0].equalsIgnoreCase(p.getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.self");
            } else if (ConfigManager.getDataFile().getString("players." + p.getName() + ".slub") == null) {
                MessageManager.sendPrefixedMessage(p, "marry.not_in");
            } else if (ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub") == null || !ConfigManager.getDataFile().getString("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub").equalsIgnoreCase(p.getName()) || !ConfigManager.getDataFile().getString("players." + p.getName() + ".slub").equalsIgnoreCase(Bukkit.getOfflinePlayer(args[0]).getName())) {
                MessageManager.sendPrefixedMessage(p, "marry.not_with_target");
            } else {
                ConfigManager.getDataFile().set("players." + p.getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slub", null);
                ConfigManager.getDataFile().set("players." + Bukkit.getOfflinePlayer(args[0]).getName() + ".slubprosba", null);
                ConfigManager.getDataFile().set("players." + p.getName() + ".slubprosba", null);
                ConfigManager.saveData();

                String updatep = "UPDATE `" + DatabaseManager.table_survival + "` SET slub = 'NULL' WHERE nick = '" + p.getName() + "'";
                String updateo = "UPDATE `" + DatabaseManager.table_survival + "` SET slub = 'NULL' WHERE nick = '" + Bukkit.getOfflinePlayer(args[0]).getName() + "'";
                databaseManager.update(updatep);
                databaseManager.update(updateo);
                plugin.getServer().broadcastMessage(prefix + MessageFormat.format(LanguageManager.getMessage("marry.send.reject.broadcast"), p.getName(), Bukkit.getOfflinePlayer(args[0]).getName()));

            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("vip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.vip"));
            } else {
                p.chat("/rangi");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("svip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.svip"));
            } else {
                p.chat("/rangi");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("mvip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.mvip"));
            } else {
                p.chat("/rangi");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("evip")) {
            Player p = (Player) sender;
            final boolean grupa = GroupUtil.isPlayerInGroup(p, cmd.getName());
            if (grupa) {
                p.chat(plugin.getConfig().getString("commands.evip"));
            } else {
                p.chat("/rangi");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("dcc")) {
            if (args.length == 0) {
                if (sender.hasPermission("dcc.adm")) {
                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessageList(sender, "pluginhelp.contents");
                } else {
                    MessageManager.sendPrefixedMessage(sender, "notfound");
                }
            } else {
                if (!sender.hasPermission("dcc.adm")) {
                    MessageManager.sendPrefixedMessage(sender, "notfound");
                }
                final String sub = args[0];
                if (sub.equalsIgnoreCase("przeladuj")) {
                    ConfigUtil.reloadFiles();
                    MessageManager.sendPrefixedMessage(sender, "maintenance.reload_plugin");
                } else if (sub.equalsIgnoreCase("afk")) {
                    String kick_msg = LanguageManager.getMessage("afk.kick_msg");
                    int kick_warn_delay = plugin.getConfig().getInt("afk.kick_warn_delay");
                    String kick_warn_msg =  LanguageManager.getMessage("afk.kick_warn_msg");
                    String kick_warn_msg_afk =  LanguageManager.getMessage("afk.kick_warn_msg_afk");
                    int kick_delay = plugin.getConfig().getInt("afk.kick_delay");
                    String sound_on_get_warn = plugin.getConfig().getString("afk.sound_on_get_warn");
                    String sound_on_notafk = plugin.getConfig().getString("afk.sond_on_notafk");
                    String notafkmsg =  LanguageManager.getMessage("afk.kick_not_afk_msg");

                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessage(sender, "pluginhelp.afk.title");
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_msg") + " " + kick_msg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_warn_delay") + " " + kick_warn_delay);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_warn_msg") + " " + kick_warn_msg);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.kick_warn_msg_afk") + " " + kick_warn_msg_afk);
                    sender.sendMessage(LanguageManager.getMessage("pluginhelp.afk.notafkmsg") + " " + notafkmsg);
                    sender.sendMessage(plugin.getConfig().getString("afk.kick_delay") + " " + kick_delay);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_get_warn") + " " + sound_on_get_warn);
                    sender.sendMessage(plugin.getConfig().getString("afk.sound_on_notafk") + " " + sound_on_notafk);
                } else if (sub.equalsIgnoreCase("anvil")) {
                    if (!(sender instanceof Player)) {
                        MessageManager.sendPrefixedMessage(sender, "console_error");
                    } else {
                        Player p = (Player) sender;
                        if (p.getTargetBlock(null, 1) != null) {
                            Block block = p.getTargetBlock(null, 1);
                            if (block.getType() == Material.ANVIL || block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL) {

                                Location loc = block.getLocation();

                                FileConfiguration data = ConfigManager.getDataFile();

                                Set<String> anvils = data.getConfigurationSection("anvils").getKeys(false);

                                int max = 0;
                                if (anvils != null) {
                                    for (String i : anvils) {
                                        int x = data.getInt("anvils." + i + ".x");
                                        int y = data.getInt("anvils." + i + ".y");
                                        int z = data.getInt("anvils." + i + ".z");
                                        String world = data.getString("anvils." + i + ".world");
                                        Location al = new Location(Bukkit.getWorld(world), x, y, z);
                                        if (loc.equals(al)) {
                                            data.set("anvils." + i, null);
                                            MessageManager.sendPrefixedMessage(p, "anvils.deleted");
                                            ConfigManager.saveData();
                                            return true;
                                        }
                                        if (Integer.parseInt(i) >= max) {
                                            max = Integer.parseInt(i) + 1;
                                        }
                                    }
                                }

                                int x = loc.getBlockX();
                                int y = loc.getBlockY();
                                int z = loc.getBlockZ();
                                String world = loc.getWorld().getName();

                                ConfigManager.getDataFile().set("anvils." + max + ".x", x);
                                ConfigManager.getDataFile().set("anvils." + max + ".y", y);
                                ConfigManager.getDataFile().set("anvils." + max + ".z", z);
                                ConfigManager.getDataFile().set("anvils." + max + ".world", world);
                                MessageManager.sendPrefixedMessage(p, "anvils.created");

                                ConfigManager.saveData();
                            } else {
                                MessageManager.sendPrefixedMessage(p, "anvils.not_an_anvil");
                            }

                        } else {
                            MessageManager.sendPrefixedMessage(p, "anvils.not_an_anvil");
                        }
                    }
                } else {
                    sender.sendMessage("§e§lDragon§6§lCraft§b§lCore " + plugin.getDescription().getVersion());
                    MessageManager.sendMessageList(sender, "pluginhelp.contents");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("+")) {
            if (!sender.hasPermission(plugin.getConfig().getString("timedpermission"))) {
                MessageManager.sendPrefixedMessage(sender, "timedpermission.no_permission");
            } else {
                Player p = (Player) sender;
                User user = plugin.luckPerms.getPlayerAdapter(Player.class).getUser(p);
                List<InheritanceNode> nodes = user.getNodes(NodeType.INHERITANCE)
                        .stream()
                        .filter(Node::hasExpiry)
                        .filter(node -> !node.hasExpired())
                        .collect(Collectors.toList());
                if (nodes.size() == 0) {
                    MessageManager.sendPrefixedMessage(sender, "timedpermission.no_permission");
                } else {
                    Instant instant = nodes.get(0).getExpiry();

                    Date date = Date.from(instant);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'o' HH:mm");
                    String formattedDate = formatter.format(date);

                    String msg = LanguageManager.getMessage("timedpermission.expires");
                    msg = MessageFormat.format(msg, formattedDate);

                    sender.sendMessage(prefix + msg);
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("kit") || cmd.getName().equalsIgnoreCase("kits")) {
            Player p = (Player) sender;
            KitsManager.openGui(p);
        }
        if(cmd.getName().equalsIgnoreCase("migrujhome")){
            if (!sender.hasPermission("dcc.adm")) {
                MessageManager.sendPrefixedMessage(sender, "notfound");
                return false;
            }

            if(args.length < 2) {
                MessageManager.sendPrefixedMessage(sender, "migratehomes.usage");
                return false;
            }

            String from = args[0];
            String to = args[1];

            IEssentials essentials = (IEssentials)Bukkit.getPluginManager().getPlugin("Essentials");

            if(essentials == null) {
                MessageManager.sendPrefixedMessage(sender, "migratehomes.error");
                return false;
            }

            com.earth2me.essentials.User userFrom = essentials.getUser(from);
            com.earth2me.essentials.User userTo = essentials.getUser(to);

            if(userFrom == null || userTo == null) {
                MessageManager.sendPrefixedMessage(sender, "migratehomes.error");
                return false;
            }

            List<String> fromHomes = userFrom.getHomes();
            for (String fromHome : fromHomes) {
                try {
                    Location fromHomeLocation = userFrom.getHome(fromHome);
                    userFrom.delHome(fromHome);

                    userTo.setHome(fromHome, fromHomeLocation);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            sender.sendMessage(LanguageManager.getMessage("prefix") + LanguageManager.getMessage("migratehomes.success").replace("{from}", from).replace("{to}", to));
            return true;
        }
        return true;
    }
}
