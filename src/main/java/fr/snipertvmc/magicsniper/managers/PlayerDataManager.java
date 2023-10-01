package fr.snipertvmc.magicsniper.managers;

import fr.snipertvmc.magicsniper.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class PlayerDataManager {

    public YamlConfiguration getPlayerDataFile(String playerName) {

        File yamlPlayerDataFile = new File(Main.getInstance().getDataFolder(), "players/" + playerName + ".yml");

        if(!yamlPlayerDataFile.exists()) {
            yamlPlayerDataFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("players/" + playerName + ".yml", false);
        }

        YamlConfiguration.loadConfiguration(yamlPlayerDataFile);
        YamlConfiguration playerDataFile = YamlConfiguration.loadConfiguration(new File(Main.getInstance().getDataFolder(), "players/" + playerName + ".yml"));

        return playerDataFile;
    }


    public YamlConfiguration savePlayerDataFile(Player player, YamlConfiguration yamlPlayerDataFile) {

        File playerDataFile = new File(Main.getInstance().getDataFolder(), "players/" + player.getName() + ".yml");

        try {
            yamlPlayerDataFile.save(playerDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return yamlPlayerDataFile;
    }


    public void createPlayerDataFile(Player player) {

        Main.getInstance().saveResource("players/PLAYER_EXAMPLE.yml", false);

        File examplePlayerDateFile = new File(Main.getInstance().getDataFolder(), "players/PLAYER_EXAMPLE.yml");
        File playerDataFile = new File(Main.getInstance().getDataFolder(), "players/" + player.getName() + ".yml");

        if (examplePlayerDateFile.exists()) {
            examplePlayerDateFile.renameTo(playerDataFile);
        }
    }


    public void addSpellCooldownToPlayerData(Player player, String spellName) {

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);
        YamlConfiguration playerDataFile = getPlayerDataFile(player.getName());

        if (spellFile.get("PLAYER_DATA.SPELLS_COOLDOWN." + spellName) == null) {

            long timeInSeconds = Instant.now().getEpochSecond();
            playerDataFile.set("PLAYER_DATA.SPELLS_COOLDOWN." + spellName, timeInSeconds);
            playerDataFile = savePlayerDataFile(player, playerDataFile);
        }
    }


    public void removeSpellCooldownFromPlayerData(Player player, String spellName) {

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);
        YamlConfiguration playerDataFile = getPlayerDataFile(player.getName());

        if (spellFile.getInt("PLAYER_DATA.SPELLS_COOLDOWN." + spellName) >= 0) {

            playerDataFile.set("PLAYER_DATA.SPELLS_COOLDOWN." + spellName, null);
            playerDataFile = savePlayerDataFile(player, playerDataFile);
        }
    }
}
