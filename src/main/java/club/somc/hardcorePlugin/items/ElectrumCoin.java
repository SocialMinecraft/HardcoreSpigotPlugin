package club.somc.hardcorePlugin.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ElectrumCoin extends Coin {

    private static final String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFlZGYxNmM0MWQxOTRjNzMxZTMzZmRkOWMyYjllNWVkZDQ1MGJjMzNjYTcwNDM2NTI4YTA1Mzg5ZDdmY2RhMiJ9fX0=";
    public static final int VALUE = 50;
    private static final NamespacedKey key = new NamespacedKey("hardcore-plugin", "electrum_coin");

    public ElectrumCoin(int amount) {
        super(texture, key, "Electrum Coin", VALUE, amount);
    }

    public ElectrumCoin() {
        super(texture, key, "Electrum Coin", VALUE, 1);
    }

    public static boolean is(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
