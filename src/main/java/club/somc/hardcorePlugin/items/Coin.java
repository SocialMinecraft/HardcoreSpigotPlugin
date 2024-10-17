package club.somc.hardcorePlugin.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class Coin extends ItemStack {

    private final String texture;
    private final int value;
    private final NamespacedKey key;
    private final String name;

    public Coin(String texture, NamespacedKey key, String name, int value, int amount) {
        super(Material.PLAYER_HEAD, amount);
        this.texture = texture;
        this.value = value;
        this.key = key;
        this.name = name;
        this.setProps();
    }

    /*public Coin() {
        super(Material.PLAYER_HEAD, 1);
        this.setProps();
    }

    public Coin(int amount) {
        super(Material.PLAYER_HEAD, amount);
        this.setProps();
    }*/

    /*public static boolean isCoin(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }*/

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private void setProps() {
        SkullMeta meta = (SkullMeta) this.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        URL urlObject;
        try {
            String decoded = new String(java.util.Base64.getDecoder().decode(texture));
            String url = decoded.substring(decoded.indexOf("http"), decoded.lastIndexOf("\""));
            urlObject = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        textures.setSkin(urlObject);
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        meta.setDisplayName(name);

        List<String> lore_list = Arrays.asList(
                ChatColor.GOLD + String.valueOf(value) + " Currency",
                ChatColor.GRAY + "Hold and right-click to consume."
        );
        meta.setLore(lore_list);

        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

        this.setItemMeta(meta);
    }
}
