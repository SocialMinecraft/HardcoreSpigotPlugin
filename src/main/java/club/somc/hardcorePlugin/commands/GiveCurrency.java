package club.somc.hardcorePlugin.commands;

import club.somc.hardcorePlugin.Database;
import club.somc.hardcorePlugin.HardcorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class GiveCurrency implements CommandExecutor, TabCompleter {

    private Database database;
    private Logger logger;

    public GiveCurrency(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (strings.length < 3) {
            commandSender.sendMessage(ChatColor.RED + "Must provide player name, amount, and reason.");
            return false;
        }

        Player giveTo = Bukkit.getPlayer(strings[0]);
        if (giveTo == null) {
            commandSender.sendMessage(ChatColor.RED + "Player not found. Maybe offline?");
            return false;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Invalid amount.");
            return false;
        }
        String reason = String.join(" ", Arrays.stream(strings).skip(2).toArray(String[]::new));

        logger.info("Giving " + amount + " currency to: " + giveTo.getName() + " " + reason);

        try {
            HardcorePlayer hardcorePlayer = new HardcorePlayer(database, giveTo);
            hardcorePlayer.addToWallet(amount, reason);
            commandSender.sendMessage(ChatColor.GREEN + "Currency given.");
        } catch (SQLException e) {
            commandSender.sendMessage(ChatColor.RED + "System Error. Check Server log.");
            logger.warning("SQL Exception: " + e.getMessage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        switch (strings.length) {
            case 1:
                break;
            case 2:
                return List.of("[amount]");
            case 3:
                return List.of("[Reason]");
            default:
                return List.of();
        }

        List<String> list = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (strings.length == 0 || p.getName().startsWith(strings[0])) {
                list.add(p.getName());
            }
        }
        return list;
    }
}
