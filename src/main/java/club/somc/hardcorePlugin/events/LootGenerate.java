package club.somc.hardcorePlugin.events;

import club.somc.hardcorePlugin.items.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.Random;
import java.util.logging.Logger;

public class LootGenerate implements Listener {

    private final Logger logger;
    private Random random = new Random();
    private final float coinChance;

    public LootGenerate(Logger logger, float coinChance) {
        this.coinChance = coinChance;
        this.logger = logger;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {

        if (random.nextFloat() < coinChance) {

            int currency = 0;

            //int coins = 0;

            double roll = random.nextDouble();
            if (roll < 0.6) {
                // 60% chance for 1-3 coins
                //coins = random.nextInt(3) + 1;
                currency = random.nextInt(300) + 100;
            } else if (roll < 0.95) {
                // 35% chance for 4-9 coins
                //coins = random.nextInt(6) + 4;
                currency = random.nextInt(600) + 400;
            } else {
                // 5% chance for 10-maxCoins coins
                //coins = 10;
                currency = 1000;
            }

            logger.info("Loot generated: " + currency);

            if (currency > PlatinumCoin.VALUE) {
                int coins = currency / PlatinumCoin.VALUE;
                currency -= PlatinumCoin.VALUE*coins;
                event.getLoot().add(new PlatinumCoin(coins));
            }
            if (currency > GoldCoin.VALUE) {
                int coins = currency / GoldCoin.VALUE;
                currency -= GoldCoin.VALUE*coins;
                event.getLoot().add(new GoldCoin(coins));
            }
            if (currency > ElectrumCoin.VALUE) {
                int coins = currency / ElectrumCoin.VALUE;
                currency -= ElectrumCoin.VALUE*coins;
                event.getLoot().add(new ElectrumCoin(coins));
            }
            if (currency > SilverCoin.VALUE) {
                int coins = currency / SilverCoin.VALUE;
                currency -= SilverCoin.VALUE*coins;
                event.getLoot().add(new SilverCoin(coins));
            }
            if (currency > CopperCoin.VALUE) {
                int coins = currency / CopperCoin.VALUE;
                currency -= CopperCoin.VALUE*coins;
                event.getLoot().add(new CopperCoin(coins));
            }

            if (currency > 0)
                logger.warning("Left over currency on loot generation: " + currency);

            //int coins = random.nextInt(maxCoins - minCoins + 1) + minCoins;
            //event.getLoot().add(new GoldCoin(coins));
        }

    }
}
