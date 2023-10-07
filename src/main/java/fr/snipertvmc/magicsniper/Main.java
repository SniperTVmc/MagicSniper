package fr.snipertvmc.magicsniper;

import fr.snipertvmc.magicsniper.commands.admin.CommandMagicSniper;
import fr.snipertvmc.magicsniper.listeners.PlayerListener;
import fr.snipertvmc.magicsniper.listeners.PlayerLogin;
import fr.snipertvmc.magicsniper.managers.ConfigurationFilesManager;
import fr.snipertvmc.magicsniper.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    private static Main instance;
    private final ConfigurationFilesManager configurationFilesManager = new ConfigurationFilesManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();


    @Override
    public void onEnable() {

        instance = this;

        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §dActivation de §5MagicSniper §dv" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §fMade by §dSniper_TVmc");
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");

        Plugin NBTApi = Bukkit.getServer().getPluginManager().getPlugin("NBTAPI");

        if (NBTApi == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §cCe plugin a besoin de §fNBTAPI §cafin de fonctionner correctement.");
            Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §8§l» §7Lien de téléchargement: §fhttps://spigotmc.org/resources/7939/");
            Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");
            Bukkit.getPluginManager().disablePlugin(this);
            return;

        } else {
            Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §aPlugin trouvé: §fNBTAPI v" + NBTApi.getDescription().getVersion());
            Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");
        }

        configurationFilesManager.reloadConfigurationFiles();

        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §dLe plugin §5MagicSniper §dest désormais activé.");
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLogin(), this);

        getCommand("magicsniper").setExecutor(new CommandMagicSniper());

    }


    @Override
    public void onDisable() {

        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §dDésactivation de §5MagicSniper §dv" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8] §fMade by §dSniper_TVmc");
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§5MagicSniper§8]");

    }


    public static Main getInstance() {
        return instance;
    }

    public ConfigurationFilesManager getFilesManager() {
        return configurationFilesManager;
    }

    public YamlConfiguration getPlayerDataFile(String playerName) {
        return playerDataManager.getPlayerDataFile(playerName);
    }

    public YamlConfiguration savePlayerDataFile(Player player, YamlConfiguration yamlPlayerDataFile) {
        return playerDataManager.savePlayerDataFile(player, yamlPlayerDataFile);
    }
}
