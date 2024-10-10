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

public class Coin extends ItemStack {

    private final String skin = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBhN2I5NGM0ZTU4MWI2OTkxNTlkNDg4NDZlYzA5MTM5MjUwNjIzN2M4OWE5N2M5MzI0OGEwZDhhYmM5MTZkNSJ9fX0=";
    private static NamespacedKey key = new NamespacedKey("hardcore-plugin", "coin");

    public Coin() {
        super(Material.PLAYER_HEAD, 1);
        this.setProps();
    }

    public Coin(int amount) {
        super(Material.PLAYER_HEAD, amount);
        this.setProps();
    }

    public static boolean isCoin(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    private void setProps() {
        SkullMeta meta = (SkullMeta) this.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        URL urlObject;
        try {
            String decoded = new String(java.util.Base64.getDecoder().decode(skin));
            String url = decoded.substring(decoded.indexOf("http"), decoded.lastIndexOf("\""));
            urlObject = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        textures.setSkin(urlObject);
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        meta.setDisplayName("100 Currency");

        List<String> lore_list = Arrays.asList(ChatColor.GRAY + "Hold and right-click to consume.");
        meta.setLore(lore_list);

        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);

        this.setItemMeta(meta);
    }
}
