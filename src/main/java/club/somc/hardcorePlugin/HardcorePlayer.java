package club.somc.hardcorePlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.SQLException;
import java.time.OffsetDateTime;

public class HardcorePlayer {

    private Database db;
    private Player player;

    public HardcorePlayer(Database db, Player player) {
        this.db = db;
        this.player = player;
    }

    public void updatePlayerState() throws SQLException {

        // Ensure correct game mode.
        if (isAlive())
            player.setGameMode(GameMode.SURVIVAL);
        else
            player.setGameMode(GameMode.SPECTATOR);
    }

    public void died(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.DIED,
                reason
        );
    }

    public void revive(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.REVIVED,
                reason
        );
        this.updatePlayerState();
        this.giveRespawnKit();
    }

    public void offenseCommited(String reason) throws SQLException {
        this.db.addEvent(
                this.player,
                Database.EventType.OFFENSE,
                reason
        );
        this.updatePlayerState();
    }

    public void giveDaily(int amount, int vip_amount) throws SQLException {
        int total_amount = amount+vip_amount;
        String reason = "Daily Login" + (vip_amount > 0 ? " with VIP Bonus" : "")
                + " of " + total_amount  + " received!";
        this.addToWallet(
                total_amount,
                reason
        );
        this.db.addEvent(
                this.player,
                Database.EventType.DAILY,
                reason
        );
        player.sendMessage(ChatColor.GREEN + reason);
    }

    public OffsetDateTime lastDaily() throws SQLException {
        return db.lastDaily(this.player.getUniqueId());
    }

    public int lastOffense() throws SQLException {
        return db.lastOffensePlaytime(this.player.getUniqueId());
    }

    public boolean isAlive() throws SQLException {
        return this.db.isAlive(player.getUniqueId());
    }

    public int getWallet() throws SQLException {
        return this.db.getWallet(player.getUniqueId());
    }

    public boolean addToWallet(int amount, String description) throws SQLException {
        if (this.getWallet() + amount < 0)
            return false;

        this.db.addEvent(
                this.player,
                Database.EventType.TRANSACTION,
                description
        );

        this.db.addToWallet(player.getUniqueId(), amount);

        return true;
    }

    public void giveRespawnKit() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1*60*20, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1*60*20, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30*20, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30*20, 3));

        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1*60*20, 100));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1*60*20, 100));

        player.getInventory().setItemInOffHand(new ItemStack(Material.ROTTEN_FLESH, 9));
        player.getInventory().setItem(8, new ItemStack(Material.TORCH, 1));

        player.getInventory().setItem(0, new ItemStack(Material.STICK, 1));
        ItemMeta ws = player.getInventory().getItem(0).getItemMeta();
        ws.setDisplayName("Pointy Stick");
        ws.setEnchantmentGlintOverride(true);
        player.getInventory().getItem(0).setItemMeta(ws);
    }


}
