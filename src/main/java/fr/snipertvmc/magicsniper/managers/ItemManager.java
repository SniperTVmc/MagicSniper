package fr.snipertvmc.magicsniper.managers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemManager {

	public boolean isSpellItem(ItemStack spellItem) {
		NBTItem nbtItem = new NBTItem(spellItem);
		return nbtItem.getBoolean("isSpellItem");
	}


	public String getSpellOwner(ItemStack spellItem) {
		NBTItem nbtItem = new NBTItem(spellItem);
		return nbtItem.getString("ownerName");
	}


	public String getSpellName(ItemStack spellItem) {
		NBTItem nbtItem = new NBTItem(spellItem);
		return nbtItem.getString("spellName");
	}


	public ItemStack getSpellItem(String ownerName, String spellName, String materialName, String itemName, List<String> itemLore) {
		ItemStack spellItem = new ItemStack(Material.getMaterial(materialName));
		NBTItem spellItemNBT = new NBTItem(spellItem);

		spellItemNBT.setBoolean("isSpellItem", true);
		spellItemNBT.setString("ownerName", ownerName);
		spellItemNBT.setString("spellName", spellName);
		spellItem = spellItemNBT.getItem();

		ItemMeta spellItemMeta = spellItem.getItemMeta();
		spellItemMeta.setDisplayName(itemName);
		spellItemMeta.setLore(itemLore);
		spellItem.setItemMeta(spellItemMeta);

		return spellItem;
	}
}
