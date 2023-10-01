package fr.snipertvmc.magicsniper.listeners;

import fr.snipertvmc.magicsniper.Main;
import fr.snipertvmc.magicsniper.managers.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class PlayerLogin implements Listener {

    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        File playerDataFile = new File(Main.getInstance().getDataFolder(), "players/" + event.getPlayer().getName() + ".yaml");
        if(!playerDataFile.exists()) {
            playerDataManager.createPlayerDataFile(event.getPlayer());
        }
    }
}
