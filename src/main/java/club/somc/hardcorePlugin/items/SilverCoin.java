package club.somc.hardcorePlugin.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SilverCoin extends Coin {

    private static final String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM0YjI3YmZjYzhmOWI5NjQ1OTRiNjE4YjExNDZhZjY5ZGUyNzhjZTVlMmUzMDEyY2I0NzFhOWEzY2YzODcxIn19fQ==";
    public static final int VALUE = 10;
    private static final NamespacedKey key = new NamespacedKey("hardcore-plugin", "silver_coin");

    public SilverCoin(int amount) {
        super(texture, key, "Silver Coin", VALUE, amount);
    }

    public SilverCoin() {
        super(texture, key, "Silver Coin", VALUE, 1);
    }

    public static boolean is(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
