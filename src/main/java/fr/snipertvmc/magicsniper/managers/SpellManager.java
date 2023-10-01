package fr.snipertvmc.magicsniper.managers;

import fr.snipertvmc.magicsniper.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SpellManager {

    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    public void togglePlayerSpell(Player player, String spellFileNameComplete) {

        String spellFileName =
                spellFileNameComplete.replace(".yml", "");

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellFileName);

        boolean isSpellEnabled = spellFile.getBoolean("SPELL_INFORMATION.ENABLED_SPELL");
        if (isSpellEnabled) {

            if (getPlayerSpellLevel(player, spellFileName) == 0) {

                String messagesPrefix =
                        Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                                .replace("&", "§");

                String errorCheckConsole =
                        Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.CHECK_CONSOLE")
                                .replaceAll("&", "§");
                player.sendMessage(messagesPrefix + errorCheckConsole);
                return;
            }

           // DÉSACTIVATION DU SORT

           YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());
           List<String> playerEnabledSpells = playerDataFile.getStringList("PLAYER_DATA.ENABLED_SPELLS");

           if(playerEnabledSpells.contains(spellFileName)){
               disablePlayerSpell(player, spellFileName);

           // ACTIVATION DU SORT
           } else {
               enablePlayerSpell(player, spellFileName);
           }
        }
    }


    public int getPlayerSpellLevel(Player player, String spellName) {

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);

        int maxSpellLevel = spellFile.getInt("SPELL_INFORMATION.LEVELS");
        int playerSpellLevel = 0;

        for (int i = maxSpellLevel; i >= 1; i--) {
            String permission = Main.getInstance().getFilesManager().getConfigurationFile().getString("PERMISSIONS." + spellName + ".LEVEL_" + i);
            if (player.hasPermission(permission)) {
                playerSpellLevel = i;
                break; //
            }
        }
        return playerSpellLevel;
    }


    public void enablePlayerSpell(Player player, String spellName) {

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);
        YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());

        int playerSpellLevel = getPlayerSpellLevel(player, spellName);

        String spellEnabled =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.SPELLS.SPELL_ENABLED")
                        .replace("&", "§")
                        .replace("%spellName%", spellName);
        player.sendMessage(messagesPrefix + spellEnabled);

        List<String> playerEnabledSpells = playerDataFile.getStringList("PLAYER_DATA.ENABLED_SPELLS");
        playerEnabledSpells.add(spellName);
        playerDataFile.set("PLAYER_DATA.ENABLED_SPELLS", playerEnabledSpells);
        playerDataFile = Main.getInstance().savePlayerDataFile(player, playerDataFile);

        playerDataManager.addSpellCooldownToPlayerData(player, spellName);

        // EFFET DE BRILLANCE
        ItemStack holdingItem = player.getInventory().getItemInMainHand();
        player.getInventory().removeItem(holdingItem);
        ItemMeta holdingItemMeta = holdingItem.getItemMeta();

        if (holdingItemMeta != null) {
            holdingItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            holdingItem.setItemMeta(holdingItemMeta);
        }

        if (holdingItem.getType() == Material.BOW) {
            holdingItem.addUnsafeEnchantment(Enchantment.WATER_WORKER, 69);
        } else {
            holdingItem.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 69);
        }

        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), holdingItem);
        /// -----------\\\

        boolean isGlobalEffectEnabled = spellFile.getBoolean("SPELL_INFORMATION.GLOBAL_EFFECT.ENABLED");
        if (isGlobalEffectEnabled) {
            // ACTIVATION EFFET DE POTION SI NIVEAU NÉCESSAIRE (GLOBAL EFFECT)
            int globalEffectMinLevel = spellFile.getInt("SPELL_INFORMATION.GLOBAL_EFFECT.MIN_LEVEL");
            if (playerSpellLevel >= globalEffectMinLevel) {

                String potionEffectName = spellFile.getString("SPELL_INFORMATION.GLOBAL_EFFECT.EFFECT_ID");
                int potionEffectAmplifier = spellFile.getInt("SPELL_INFORMATION.GLOBAL_EFFECT.AMPLIFIER");
                boolean potionEffectShowParticles = spellFile.getBoolean("SPELL_INFORMATION.GLOBAL_EFFECT.SHOW_PARTICLES");
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(potionEffectName), PotionEffect.INFINITE_DURATION, potionEffectAmplifier, false, potionEffectShowParticles);
                player.addPotionEffect(potionEffect);
            }
        }
    }


    public void disablePlayerSpell(Player player, String spellName) {

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);
        YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());

        List<String> playerEnabledSpells = playerDataFile.getStringList("PLAYER_DATA.ENABLED_SPELLS");
        playerEnabledSpells.remove(spellName);
        playerDataFile.set("PLAYER_DATA.ENABLED_SPELLS", playerEnabledSpells);
        playerDataFile = Main.getInstance().savePlayerDataFile(player, playerDataFile);

        playerDataManager.removeSpellCooldownFromPlayerData(player, spellName);

        String potionEffectName = spellFile.getString("SPELL_INFORMATION.GLOBAL_EFFECT.EFFECT_ID");
        player.removePotionEffect(PotionEffectType.getByName(potionEffectName));

        // EFFET DE BRILLANCE
        ItemStack holdingItem = player.getInventory().getItemInMainHand();
        player.getInventory().removeItem(holdingItem);

        if (holdingItem.getType() == Material.BOW) {
            holdingItem.removeEnchantment(Enchantment.WATER_WORKER);
        } else {
            holdingItem.removeEnchantment(Enchantment.ARROW_INFINITE);
        }
        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), holdingItem);
        /// -----------\\\


        String spellDisabled =
            Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.SPELLS.SPELL_DISABLED")
                    .replace("&", "§")
                    .replace("%spellName%", spellName);
        player.sendMessage(messagesPrefix + spellDisabled);

    }


    public void useUltimeEffect(Player player, String spellName, Action clickAction) {

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());

        List<String> enabledSpells = playerDataFile.getStringList("PLAYER_DATA.ENABLED_SPELLS");
        if(!enabledSpells.contains(spellName)) {

            String errorEnabledSpell =
                    Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.ENABLED_SPELL")
                            .replace("&", "§");
            player.sendMessage(messagesPrefix + errorEnabledSpell);

        } else {


            YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);

            int playerSpellLevel = 0;
            int levelsSpellCount = spellFile.getInt("SPELL_INFORMATION.LEVELS");

            for (int i = 1; i < levelsSpellCount; i++) {
                String permission = Main.getInstance().getFilesManager().getConfigurationFile().getString("PERMISSIONS." + spellName + ".LEVEL_" + i);
                if (player.hasPermission(permission)) {
                    playerSpellLevel = i;
                }
            }

            if(spellFile.getBoolean("SPELL_INFORMATION.ULTIME_EFFECT.LEVEL_" + playerSpellLevel + ".ENABLED")) {

                int playerSpellCooldown =
                        playerDataFile.getInt("PLAYER_DATA.SPELLS_COOLDOWN." + spellName);
                long ultimeEffectCooldown =
                        spellFile.getLong("SPELL_INFORMATION.ULTIME_EFFECT.LEVEL_" + playerSpellLevel + ".COOLDOWN");

                long timeInSeconds = Instant.now().getEpochSecond();
                if (timeInSeconds >= playerSpellCooldown + ultimeEffectCooldown) {
                    ultimeEffectUseAction(player, spellName, clickAction, playerSpellLevel);

                } else {

                    boolean playerBypassCooldowns = playerDataFile.getBoolean("PLAYER_DATA.BYPASS_COOLDOWNS");
                    if (playerBypassCooldowns) {
                        ultimeEffectUseAction(player, spellName, clickAction, playerSpellLevel);

                    } else {
                        String cooldownWait =
                                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.ERRORS.COOLDOWN_WAIT")
                                        .replace("&", "§");
                        player.sendMessage(messagesPrefix + cooldownWait);
                    }
                }
            }
        }
    }


    private void ultimeEffectUseAction(Player player, String spellName, Action clickAction, int playerSpellLevel) {

        String messagesPrefix =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.MAIN.PREFIX")
                        .replace("&", "§");

        YamlConfiguration playerDataFile = Main.getInstance().getPlayerDataFile(player.getName());
        YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(spellName);

        long timeInSeconds = Instant.now().getEpochSecond();
        playerDataFile.set("PLAYER_DATA.SPELLS_COOLDOWN." + spellName, timeInSeconds);
        playerDataFile = Main.getInstance().savePlayerDataFile(player, playerDataFile);

        List<String> commandsToExecute;

        if (clickAction.equals(Action.LEFT_CLICK_AIR) || clickAction.equals(Action.LEFT_CLICK_BLOCK)) {
            commandsToExecute = spellFile.getStringList("SPELL_INFORMATION.ULTIME_EFFECT.LEVEL_" + playerSpellLevel + ".COMMANDS.ATTACK");
        } else {
            commandsToExecute = spellFile.getStringList("SPELL_INFORMATION.ULTIME_EFFECT.LEVEL_" + playerSpellLevel + ".COMMANDS.DEFENSE");
        }

        for (String command : commandsToExecute) {
            command = command.replaceAll("%playerName%", player.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        String ultimeEffectUsed =
                Main.getInstance().getFilesManager().getMessagesFile().getString("MESSAGES.SPELLS.ULTIME_EFFECT_USED")
                        .replace("&", "§")
                        .replace("%spellName%", spellName);
        player.sendMessage(messagesPrefix + ultimeEffectUsed);
    }


    public List<String> getSpellsList() {

        List<String> spellsList = new ArrayList<>();

        String folderPath = "spells/";
        File folder = new File(Main.getInstance().getDataFolder(), folderPath);
        File[] files = folder.listFiles();

        if (files == null) {
            return null;
        }

        for (File file : files) {
            if (file.isFile()) {
                YamlConfiguration spellFile = Main.getInstance().getFilesManager().getSpellFile(file.getName().replace(".yml", ""));
                if (spellFile.getBoolean("SPELL_INFORMATION.ENABLED_SPELL")) {
                    String spellFileName = file.getName().replace(".yml", "");
                    spellsList.add(spellFileName);
                }
            }
        }
        return spellsList;
    }
}
