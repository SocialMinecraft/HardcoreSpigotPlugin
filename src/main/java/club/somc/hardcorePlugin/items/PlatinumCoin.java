package club.somc.hardcorePlugin.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlatinumCoin extends Coin {

    private static final String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzMwN2JmYzhjYThhZDY5YjA0MGY3MzlmMjllY2I1NmFlZDI3ZmUxOWFlMDhlZTZhNDYzZmE3ODM1Yzg5N2EyYSJ9fX0=";
    public static final int VALUE = 1000;
    private static final NamespacedKey key = new NamespacedKey("hardcore-plugin", "platinum_coin");

    public PlatinumCoin(int amount) {
        super(texture, key, "Platinum Coin", VALUE, amount);
    }

    public PlatinumCoin() {
        super(texture, key, "Platinum Coin", VALUE, 1);
    }

    public static boolean is(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
