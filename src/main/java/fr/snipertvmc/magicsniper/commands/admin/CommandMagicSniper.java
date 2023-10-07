package fr.snipertvmc.magicsniper.commands.admin;

import fr.snipertvmc.magicsniper.Main;
import fr.snipertvmc.magicsniper.managers.ConfigurationFilesManager;
import fr.snipertvmc.magicsniper.managers.ItemManager;
import fr.snipertvmc.magicsniper.managers.PlayerDataManager;
import fr.snipertvmc.magicsniper.managers.SpellManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandMagicSniper implements CommandExecutor, TabCompleter {

    private final ItemManager itemManager = new ItemManager();
    private final SpellManager spellManager = new SpellManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final ConfigurationFilesManager configFilesManager = new ConfigurationFilesManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        String mainPermission = Main.getInstance().getFilesManager().getConfigurationFile().getString("PERMISSIONS.COMMANDS.MAIN");
        if (!player.hasPermission(mainPermission)) {
            sendMagicSniperNoPermissionMessage(player);
            return false;
        }

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        if(args.length > 0) {
            String firstArg = args[0];

            if (firstArg.equalsIgnoreCase("help")) {
                sendMagicSniperCommandHelpMessage(player);
                return false;

            } else if (firstArg.equalsIgnoreCase("give")) {

                String givePermission = Main.getInstance().getFilesManager().getConfigurationFile().getString("PERMISSIONS.COMMANDS.GIVE");
                if (!player.hasPermission(givePermission)) {
                    sendMagicSniperNoPermissionMessage(player);
                    return false;
                }

                if (args.length < 2) {
                    String messageNoSpell =
                            Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.NO_SPELL")
                                    .replace("&", "§");
                    player.sendMessage(messagesPrefix + messageNoSpell);
                    return false;

                } else {
                    String secondArg = args[1];
                    boolean isSpellNameExists = false;

                    for (String spellName : spellManager.getSpellsList()) {
                        if (secondArg.equals(spellName)) {
                            isSpellNameExists = true;
                            break;
                        }
                    }

                    if (!isSpellNameExists) {
                        String messageDontExists =
                                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.SPELL_DONT_EXISTS")
                                        .replace("&", "§");
                        player.sendMessage(messagesPrefix + messageDontExists);
                        return false;

                    } else {
                        Player target;

                        if (args.length > 2) {
                            String thirdArg = args[2];
                            if (Bukkit.getPlayer(thirdArg) != null) {
                                target = Bukkit.getPlayer(thirdArg);
                            } else {
                                String messagePlayerOffline =
                                        Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.PLAYER_OFFLINE")
                                                .replace("&", "§");
                                player.sendMessage(messagesPrefix + messagePlayerOffline);
                                return false;
                            }
                        } else {
                            target = player;
                        }

                        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(secondArg);
                        String materialName = spellFile.getString("SPELL_INFORMATION.MATERIAL_ITEM");

                        String itemName = spellFile.getString("SPELL_INFORMATION.ITEM_DISPLAY_NAME")
                                .replace("&", "§");

                        String spellDisplayName = spellFile.getString("SPELL_INFORMATION.SPELL_DISPLAY_NAME");

                        List<String> loreList = spellFile.getStringList("SPELL_INFORMATION.ITEM_LORE");
                        for (int i = 0; i < loreList.size(); i++) {
                            String line = loreList.get(i);

                            loreList.set(i, line
                                    .replaceAll("&", "§")
                                    .replaceAll("%ownerName%", player.getName())
                                    .replaceAll("%spellName%", spellDisplayName));
                        }

                        ItemStack spellItem = itemManager.getSpellItem(player.getName(), secondArg, materialName, itemName, loreList);
                        target.getInventory().addItem(spellItem);
                        return false;
                    }
                }

            } else if (firstArg.equalsIgnoreCase("bypass")) {

                String bypassPermission = Main.getInstance().getFilesManager().getConfigurationFile().getString("PERMISSIONS.COMMANDS.BYPASS");
                if (!player.hasPermission(bypassPermission)) {
                    sendMagicSniperNoPermissionMessage(player);
                    return false;
                }

                YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());
                if (playerDataFile.getBoolean("PLAYER_DATA.BYPASS_COOLDOWNS")) {
                    playerDataFile.set("PLAYER_DATA.BYPASS_COOLDOWNS", false);

                    String messageBypassCooldownDisabled =
                            Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.SPELLS.BYPASS_COOLDOWNS_DISABLED")
                                    .replace("&", "§");
                    player.sendMessage(messagesPrefix + messageBypassCooldownDisabled);

                } else {
                    playerDataFile.set("PLAYER_DATA.BYPASS_COOLDOWNS", true);

                    String messageBypassCooldownEnabled =
                            Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.SPELLS.BYPASS_COOLDOWNS_ENABLED")
                                    .replace("&", "§");
                    player.sendMessage(messagesPrefix + messageBypassCooldownEnabled);
                }

                playerDataFile = playerDataManager.savePlayerDataFile(player, playerDataFile);
                return false;

            } else {
                String messageNoArguments =
                        Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.NO_ARGUMENTS")
                                .replace("&", "§");
                player.sendMessage(messagesPrefix + messageNoArguments);
                return false;
            }

        } else {
                sendMagicSniperCommandHelpMessage(player);
                return false;
        }
    }

    private void sendMagicSniperCommandHelpMessage(Player player) {

        player.sendMessage("§r");
        player.sendMessage("§5┌§m------------ §8§l» §5§lMagicSniper §8§l« §5§m------------§5 ┐");
        player.sendMessage("§r");
        player.sendMessage("§8» §c/ms §8- §7Voir ce message d'aide.");
        player.sendMessage("§8» §c/ms give §4<sort> [joueur] §8- §7Donner un sort à un joueur.");
        player.sendMessage("§8» §c/ms bypass §8- §7(Dés)Activer le contournement des cooldowns.");
        player.sendMessage("§r");
        player.sendMessage("§5└§m--------------------------------------§5 ┘");
        player.sendMessage("§r");
    }


    private void sendMagicSniperNoPermissionMessage(Player player) {

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        String messageNoPermission =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.NO_PERMISSION")
                        .replace("&", "§");
        player.sendMessage(messagesPrefix + messageNoPermission);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("give");
            options.add("bypass");

        } else if (args.length == 2) {
            String firstArg = args[0];

            if (firstArg.equalsIgnoreCase("give")) {

                String folderPath = "spells/";
                File folder = new File(Main.getInstance().getDataFolder(), folderPath);
                File[] files = folder.listFiles();

                if (files == null) {
                    return null;
                }

                for (File file : files) {
                    if (file.isFile()) {
                        String spellName = file.getName().replace(".yml", "");
                        YamlConfiguration spellFile = configFilesManager.getSpellFile(spellName);
                        if (spellFile.getBoolean("SPELL_INFORMATION.ENABLED_SPELL")) {
                            options.add(spellName);
                        }
                    }
                }
            }
        }
        return options;
    }
}
