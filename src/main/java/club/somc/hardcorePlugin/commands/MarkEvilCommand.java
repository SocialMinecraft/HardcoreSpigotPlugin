package club.somc.hardcorePlugin.commands;

import club.somc.hardcorePlugin.EvilManager;
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

public class MarkEvilCommand implements CommandExecutor, TabCompleter {

    private EvilManager evilManager;
    private Logger logger;

    public MarkEvilCommand(Logger logger, EvilManager evilManager) {
        this.logger = logger;
        this.evilManager = evilManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Must provide player name and reason.");
            return false;
        }

        Player mark = Bukkit.getPlayer(strings[0]);
        if (mark == null) {
            commandSender.sendMessage(ChatColor.RED + "Player not found. Maybe offline?");
            return false;
        }
        String reason = String.join(" ", Arrays.stream(strings).skip(1).toArray(String[]::new));

        logger.info("Marking Player Evil: " + mark.getName() + " " + reason);

        try {
            evilManager.addOffense(mark, reason);
            evilManager.updatePlayer(mark);
            commandSender.sendMessage(ChatColor.GREEN + "Marked as evil.");
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
