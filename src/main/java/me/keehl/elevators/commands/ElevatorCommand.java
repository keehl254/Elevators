package me.keehl.elevators.commands;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.menus.admin.AdminMenu;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

@SuppressWarnings({"unused", "deprecation"})
public class ElevatorCommand implements CommandExecutor, TabCompleter {

    private final String prefix = ChatColor.AQUA + "" + ChatColor.BOLD + "ELEVATORS " + ChatColor.WHITE;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("elevators")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    this.reloadCommand(sender, args[0]);
                    return true;
                } else if (args[0].equalsIgnoreCase("admin")) {
                    openAdmin(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("give")) {

                    // I feel I could do this better with varargs, but I'll be honest... I don't feel like it. This whole class is basically copied and pasted from the old one.
                    switch (args.length) {
                        case 1:
                            this.onCommand(sender, args[0]);
                            break;
                        case 2:
                            this.onCommand(sender, args[0], args[1]);
                            break;
                        case 3:
                            this.onCommand(sender, args[0], args[1], args[2].toUpperCase());
                            break;
                        case 4:
                            this.onCommand(sender, args[0], args[1], args[2].toUpperCase(), args[3].toUpperCase());
                            break;
                        case 5:
                            this.onCommand(sender, args[0], args[1], args[2].toUpperCase(), args[3].toUpperCase(), args[4]);
                            break;
                        default:
                            boolean silent = args[5].equalsIgnoreCase("-s") || args[5].equalsIgnoreCase("-silent");
                            this.onCommand(sender, args[0], args[1], args[2].toUpperCase(), args[3].toUpperCase(), args[4], silent);
                    }
                    return true;
                }
            }
            sender.sendMessage(this.prefix + "Did you mean: ");
            sender.sendMessage(ChatColor.GOLD + "/elevators reload" + ChatColor.WHITE + "?");
            sender.sendMessage(ChatColor.GOLD + "/elevators admin" + ChatColor.WHITE + "?");
            sender.sendMessage(ChatColor.GOLD + "/elevators give <player> <type> [color] [amount] [silent]" + ChatColor.WHITE + "?");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) { // Why does it want the @NotNull between the array brackets and data type...
        if (!command.getName().equalsIgnoreCase("elevators"))
            return null;

        List<String> completions = new ArrayList<>();
        List<String> finalReturn = new ArrayList<>();

        if (args.length == 1)
            completions.addAll(Arrays.asList("reload", "give", "admin"));
        else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (String name : Elevators.getElevatorTypeService().getExistingElevatorKeys())
                completions.add(name.toLowerCase());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            for (DyeColor dye : DyeColor.values())
                completions.add(dye.toString().toLowerCase());
        } else if(args.length == 5 && args[0].equalsIgnoreCase("give")) {
            completions.add("[amount]");
        }
        else if(args.length == 6 && args[0].equalsIgnoreCase("give")) {
            completions.add("-silent");
        }
        if (completions.isEmpty())
            return null;
        for (String line : completions) {
            if (line.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                finalReturn.add(line);
        }
        return finalReturn;
    }

    private void reloadCommand(CommandSender sender, String reload) {
        if (sender.hasPermission("elevators.reload")) {
            Elevators.reloadElevators();
            sender.sendMessage(this.prefix + "Reloaded the Elevators config!");
        } else {
            Elevators.getLocale().getCantReloadMessage().send(sender);
        }
    }

    private void openAdmin(CommandSender sender) {
        if (!sender.hasPermission("elevators.admin")) {
            Elevators.getLocale().getCantAdministrateMessage().send(sender);
            return;
        }
        if (!(sender instanceof Player))
            ElevatorsAPI.log(Level.WARNING, "This command can only be executed by players!");
        else
            AdminMenu.openAdminMenu((Player) sender);
    }

    private void onCommand(CommandSender sender, String give) {
        if (sender instanceof Player)
            sender.sendMessage(this.prefix + "Please provide a player to give the Elevator to!");
        else
            ElevatorsAPI.log(Level.WARNING, "Please provide more arguments! /elevators give <player> <elevator> [color] [amount]");
    }

    private void onCommand(CommandSender sender, String give, String player) {
        sender.sendMessage(this.prefix + "Please provide an elevator type!");
    }

    private void onCommand(CommandSender sender, String give, String player, String name) {
        this.onCommand(sender, give, player, name, "WHITE");
    }

    private void onCommand(CommandSender sender, String give, String player, String name, String color) {
        this.onCommand(sender, give, player, name, color, "1");
    }

    private void onCommand(CommandSender sender, String give, String player, String name, String color, String stringAmount) {
        this.onCommand(sender, give, player, name, color, stringAmount, false);
    }

    private void onCommand(CommandSender sender, String give, String playerName, String name, String color, String stringAmount, boolean silent) {
        color = color.toUpperCase();
        if (!sender.hasPermission("elevators.give")) {
            Elevators.getLocale().getCantGiveMessage().send(sender);
            return;
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(this.prefix + "That player is offline or does not exist!");
            return;
        }

        DyeColor dye;
        try {
            dye = DyeColor.valueOf(color);
        } catch (Exception e) {
            sender.sendMessage(this.prefix + "Invalid color given!");
            return;
        }

        IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(name);
        if (elevatorType == null) {
            sender.sendMessage(this.prefix + "Invalid elevator type given!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(stringAmount);
            if (amount <= 0)
                throw new Exception();
        } catch (Exception e) {
            sender.sendMessage(this.prefix + "Invalid amount given! Must be integer greater than zero.");
            return;
        }

        Map<ItemStack, Integer> leftover =  ItemStackHelper.addElevatorToInventory(elevatorType, amount, ItemStackHelper.getVariant(Material.BLACK_SHULKER_BOX, dye), player.getInventory());
        if(leftover.isEmpty()) {
            if (!silent) {
                Elevators.getLocale().getGivenElevatorMessage().send(player);
            }
        } else {
            if(!silent) {
                Elevators.getLocale().getNotEnoughRoomGiveMessage().send(player);
            }
            leftover.keySet().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
        }
    }

}