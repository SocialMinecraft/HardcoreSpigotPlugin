package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.items.Coin;
import club.somc.hardcorePlugin.items.GoldCoin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.Random;

public class LootGenerate implements Listener {

    private Random random = new Random();
    private final float coinChance;

    public LootGenerate(float coinChance) {
        this.coinChance = coinChance;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {

        if (random.nextFloat() < coinChance) {

            int coins = 0;

            double roll = random.nextDouble();
            if (roll < 0.6) {
                // 60% chance for 1-3 coins
                coins = random.nextInt(3) + 1;
            } else if (roll < 0.95) {
                // 35% chance for 4-9 coins
                coins = random.nextInt(6) + 4;
            } else {
                // 5% chance for 10-maxCoins coins
                coins = 10;
            }

            //int coins = random.nextInt(maxCoins - minCoins + 1) + minCoins;
            event.getLoot().add(new GoldCoin(coins));
        }

    }
}
