package pl.dcrft;

import net.ess3.api.IEssentials;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.dcrft.Listeners.Anvil.AnvilBreakListener;
import pl.dcrft.Listeners.Anvil.AnvilDamageListener;
import pl.dcrft.Listeners.Chair.ChairBreakListener;
import pl.dcrft.Listeners.Chair.ChairEntryListener;
import pl.dcrft.Listeners.Chair.ChairExitListener;
import pl.dcrft.Listeners.*;
import pl.dcrft.Managers.*;
import pl.dcrft.Managers.Panel.PanelManager;
import pl.dcrft.Utils.CommandUtils.CommandRunUtil;
import pl.dcrft.Utils.ConfigUtil;
import pl.dcrft.Utils.DatabaseUtil;
import pl.dcrft.Utils.ErrorUtils.ErrorReason;
import pl.dcrft.Utils.ErrorUtils.ErrorUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DragonCraftCore extends JavaPlugin implements Listener, CommandExecutor {
    private static DragonCraftCore instance;
    public static LuckPerms luckPerms;

    public static boolean isSkyblock;

    public static DragonCraftCore getInstance() {
        return instance;
    }

    public static IEssentials es = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

    public Map<String, Object> filters;

    public DragonCraftCore() {
        this.filters = new HashMap<>();
    }

    public void onEnable() {

        instance = this;

        ConfigUtil.initializeFiles();
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerUseListener(), this);
        getServer().getPluginManager().registerEvents(new CommandPreprocessListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilDamageListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilBreakListener(), this);
        getServer().getPluginManager().registerEvents(new ChairEntryListener(), this);
        getServer().getPluginManager().registerEvents(new ChairExitListener(), this);
        getServer().getPluginManager().registerEvents(new ChairBreakListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new UnknownCommandEvent(), this);



        getLogger().info(LanguageManager.getMessage("plugin.header"));
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info(LanguageManager.getMessage("plugin.enabled") + getDescription().getVersion());
        getLogger().info(LanguageManager.getMessage("plugin.footer"));

        DatabaseManager.openConnection();

        if(this.getConfig().getString("server.type").equalsIgnoreCase("skyblock")) isSkyblock = true;

        this.filters = this.getConfig().getConfigurationSection("filters").getValues(true);

        List<Command> commands = PluginCommandYamlParser.parse(this);
        for (Command command : commands) {
            getCommand(command.getName()).setExecutor(new CommandManager());
        }
        BroadcasterManager.startBroadcast();
        TopHologramManager.startBroadcast();
        DatabaseUtil.initializeTables();
        PanelManager.updatePanels();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();

        }
        for(String cmd : getConfig().getConfigurationSection("aliases").getKeys(false)){
            Bukkit.getCommandMap().register(cmd, new CommandRunUtil(cmd));
        }
        SessionManager.getRunnable().runTaskTimer(this, 0L, 1200L);
    }

    public void onDisable() {
        if (DatabaseManager.connection != null) {
                    try {
                        DatabaseManager.connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        ErrorUtil.logError(ErrorReason.DATABASE);
                    }
        }
        getLogger().info(LanguageManager.getMessage("plugin.header"));
        getLogger().info("§e§lDragon§6§lCraft§a§lCore");
        getLogger().info(LanguageManager.getMessage("plugin.disabled") + getDescription().getVersion());
        getLogger().info(LanguageManager.getMessage("plugin.footer"));
        TopHologramManager.hologram.delete();
    }
}
