package club.somc.hardcorePlugin.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GoldCoin extends Coin {

    private static final String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBhN2I5NGM0ZTU4MWI2OTkxNTlkNDg4NDZlYzA5MTM5MjUwNjIzN2M4OWE5N2M5MzI0OGEwZDhhYmM5MTZkNSJ9fX0=";
    public static final int VALUE = 100;
    private static final NamespacedKey key = new NamespacedKey("hardcore-plugin", "gold_coin");

    public GoldCoin(int amount) {
        super(texture, key, "Gold Coin", VALUE, amount);
    }

    public GoldCoin() {
        super(texture, key, "Gold Coin", VALUE, 1);
    }

    public static boolean is(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
