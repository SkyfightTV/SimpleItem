package fr.skyfighttv.simpleitem;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.*;

public class ItemCreator {
    public static ItemStack getSkull(String url) {
        url = "http://textures.minecraft.net/texture/" + url;
        ItemStack skull = new ItemStack(Material.LEGACY_SKULL, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Emote");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    private final ItemStack is;

    public ItemCreator(Material m) {
        this(m, 1);
    }

    public ItemCreator(ItemStack is) {
        this.is = is;
    }

    public ItemCreator(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemCreator(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    public ItemCreator clone() {
        return new ItemCreator(is);
    }

    public ItemCreator setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     */
    public ItemCreator setName(String name) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add an unsafe enchantment.
     *
     * @param ench  The enchantment to add.
     * @param level The level to put the enchant on.
     */
    public ItemCreator addUnsafeEnchantment(Enchantment ench, int level) {
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     */
    public ItemCreator removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     *
     * @param owner The name of the skull's owner.
     */
    public ItemCreator setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            assert im != null;
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    /**
     * Add an enchant to the item.
     *
     * @param ench  The enchant to add
     * @param level The level
     */
    public ItemCreator addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     */
    public ItemCreator addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     */
    public ItemCreator setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemCreator setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemCreator setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemCreator removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     */
    public ItemCreator removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size()) return this;
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     */
    public ItemCreator addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        assert im != null;
        if (im.hasLore()) lore = new ArrayList<>(Objects.requireNonNull(im.getLore()));
        lore.add(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @param pos  The index of where to put it.
     */
    public ItemCreator addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemCreator setPotion(PotionType potionType, boolean level) {
        if(is.getItemMeta() instanceof PotionMeta)
            ((PotionMeta) is.getItemMeta()).setBasePotionData(new PotionData(potionType, false, level));
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     *
     * @param color The color to set it to.
     */
    public ItemCreator setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemCreator addBookEnchanting(Enchantment enchantment, int level) {
        EnchantmentStorageMeta im = (EnchantmentStorageMeta) is.getItemMeta();
        im.addStoredEnchant(enchantment, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemCreator addItemFlag(ItemFlag itemFlag) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(itemFlag);
        is.setItemMeta(im);
        return this;
    }

    public ItemCreator setType(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemCreator setUnbreakable(boolean unbreakable) {
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setUnbreakable(unbreakable);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Retrieves the itemstack from the SimpleItem.
     *
     * @return The itemstack created/modified by the SimpleItem instance.
     */
    public ItemStack toItemStack() {
        return is;
    }
}
