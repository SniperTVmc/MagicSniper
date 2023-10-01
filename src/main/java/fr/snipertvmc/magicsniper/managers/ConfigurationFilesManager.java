package fr.snipertvmc.magicsniper.managers;

import fr.snipertvmc.magicsniper.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigurationFilesManager {

    private YamlConfiguration configurationFile;
    private YamlConfiguration messagesFile;


    public void loadConfigurationFile() {

        File yamlConfigurationFile = new File(Main.getInstance().getDataFolder(), "configuration.yml");

        if(!yamlConfigurationFile.exists()) {
            yamlConfigurationFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("configuration.yml", false);
        }

        YamlConfiguration.loadConfiguration(yamlConfigurationFile);
        configurationFile = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), "configuration.yml"));
    }


    public void loadMessagesFile() {

        File yamlMessagesFile = new File(Main.getInstance().getDataFolder(), "messages.yml");

        if(!yamlMessagesFile.exists()) {
            yamlMessagesFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("messages.yml", false);
        }

        YamlConfiguration.loadConfiguration(yamlMessagesFile);
        messagesFile = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), "messages.yml"));
    }


    public void reloadConfigurationFiles()  {
        loadConfigurationFile();
        loadMessagesFile();
    }


    public YamlConfiguration getConfigurationFile() {
        return configurationFile;
    }

    public YamlConfiguration getMessagesFile() {
        return messagesFile;
    }

    public YamlConfiguration getSpellFile(String fileName) {

        File yamlSpellFile = new File(Main.getInstance().getDataFolder(), "spells/" + fileName + ".yml");

        if(!yamlSpellFile.exists()) {
            yamlSpellFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("spells/" + fileName + ".yml", false);
        }

        YamlConfiguration.loadConfiguration(yamlSpellFile);
        YamlConfiguration spellFile = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), "spells/" + fileName + ".yml"));

        return spellFile;
    }

}
