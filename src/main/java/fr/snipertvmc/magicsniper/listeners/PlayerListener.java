package fr.snipertvmc.magicsniper.listeners;

import fr.snipertvmc.magicsniper.Main;
import fr.snipertvmc.magicsniper.managers.ItemManager;
import fr.snipertvmc.magicsniper.managers.SpellManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;

public class PlayerListener implements Listener {

    private final SpellManager spellManager = new SpellManager();
    private final ItemManager itemManager = new ItemManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getItem() == null) {
            return;
        }

        Action eventAction = event.getAction();
        if (eventAction.equals(Action.RIGHT_CLICK_AIR) ||
                eventAction.equals(Action.RIGHT_CLICK_BLOCK) ||
                eventAction.equals(Action.LEFT_CLICK_AIR) ||
                eventAction.equals(Action.LEFT_CLICK_BLOCK)) {

            if(!itemManager.isSpellItem(event.getItem())) {
                return;
            }

            Player player = event.getPlayer();

            String folderPath = "spells/";
            File folder = new File(Main.getInstance().getDataFolder(), folderPath);
            File[] files = folder.listFiles();

            if(files == null) {
                return;
            }

            for (File file : files) {
                if (file.isFile()) {

                    String spellName = file.getName().replace(".yml", "");
                    if (itemManager.getSpellName(event.getItem()).equals(spellName)) {

                        if (itemManager.getSpellOwner(event.getItem()).equals(player.getName())) {

                            if (player.isSneaking()) {
                                spellManager.useUltimeEffect(player, spellName, eventAction);
                                break;

                            } else {
                                if (eventAction.equals(Action.RIGHT_CLICK_AIR) || eventAction.equals(Action.RIGHT_CLICK_BLOCK)) {
                                    spellManager.togglePlayerSpell(player, file.getName());
                                    break;
                                }
                            }
                        } else {

                            String messagesPrefix =
                                    Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                                            .replace("&", "§");

                            String messageNotSpellOwner =
                                    Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.NOT_SPELL_OWNER")
                                            .replace("&", "§");

                            player.sendMessage(messagesPrefix + messageNotSpellOwner);
                        }
                    }
                }
            }
        }
    }
}
