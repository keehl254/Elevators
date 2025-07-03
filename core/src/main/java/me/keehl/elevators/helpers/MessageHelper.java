package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.hooks.PlaceholderHook;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.configs.versions.configv5_1_0.ConfigLocale;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MessageHelper {

    private static BiConsumer<Player, String> sendPlayerMessageConsumer;
    private static Consumer< String> sendConsoleMessageConsumer;

    private static boolean adventureEnabled;
    static {
        try {
            Class.forName("net.kyori.adventure");
            try (BukkitAudiences audience = BukkitAudiences.create(Elevators.getInstance())) {
                sendPlayerMessageConsumer = (player, message) -> audience.player(player).sendMessage(MiniMessage.miniMessage().deserialize(message));
                sendConsoleMessageConsumer = message -> audience.console().sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        } catch (ClassNotFoundException ignore) {
            sendPlayerMessageConsumer = CommandSender::sendMessage;
            sendConsoleMessageConsumer = Bukkit.getConsoleSender()::sendMessage;
        }
    }

    public static void sendCantCreateMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.cantCreateMessage, elevatorEventData);
    }

    public static void sendCantDyeMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.cantDyeMessage, elevatorEventData);
    }

    public static void sendCantUseMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.cantUseMessage, elevatorEventData);
    }

    public static void sendCantGiveMessage(CommandSender sender, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(sender, i -> i.cantGiveMessage, elevatorEventData);
    }

    public static void sendCantReloadMessage(CommandSender sender, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(sender, i -> i.cantReloadMessage, elevatorEventData);
    }

    public static void sendNotEnoughRoomGiveMessage(CommandSender sender, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(sender, i -> i.notEnoughRoomGiveMessage, elevatorEventData);
    }

    public static void sendGivenElevatorMessage(CommandSender sender, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(sender, i -> i.givenElevatorMessage, elevatorEventData);
    }

    public static void sendCantAdministrateMessage(CommandSender sender, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(sender, i -> i.cantAdministrateMessage, elevatorEventData);
    }

    public static void sendWorldDisabledMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.worldDisabledMessage, elevatorEventData);
    }

    public static void sendElevatorChangedMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.elevatorChangedKickedOut, elevatorEventData);
    }

    public static void sendFormattedLocale(CommandSender sender, Function<ConfigLocale, String> messageFunc, ElevatorEventData elevatorEventData) {
        String message = messageFunc.apply(ElevatorConfigService.getRootConfig().locale);
        String defaultMessage = messageFunc.apply(ElevatorConfigService.getDefaultLocaleConfig());

        message = message == null ? defaultMessage : message;
        message = formatElevatorPlaceholders(sender, elevatorEventData, message);

        MessageHelper.sendFormattedMessage(sender, message);
    }

    public static void sendFormattedMessage(CommandSender sender, String message, Object... items) {
        message = String.format(message, items);
        message = formatPlaceholders(sender, message);
        message = formatColors(message);

        if(sender instanceof Player)
            sendPlayerMessageConsumer.accept((Player) sender, message);
        else
            sendConsoleMessageConsumer.accept(message);
    }

    public static String formatElevatorPlaceholders(CommandSender sender, ElevatorEventData searchResult, String message) {

        if(sender instanceof Player)
            message = message.replace("%player%", sender.getName());
        else
            message = message.replace("%player%", "Console");

        if(searchResult == null)
            return message;

        message = message.replace("%elevators_type%", searchResult.getOrigin().getElevatorType().getTypeKey());
        if(searchResult.getDestination() != null && searchResult.getDestination().getShulkerBox() != null) {

            if (message.contains("%elevators_new_floor%"))
                message = message.replace("%elevators_new_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getDestination(), true)+"");

            if (message.contains("%elevators_top_floor%"))
                message = message.replace("%elevators_top_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getDestination(), false)+"");

            if (message.contains("%elevators_new_floor_name%"))
                message = message.replace("%elevators_new_floor_name%", ElevatorDataContainerService.getFloorName(searchResult.getDestination()));

        }
        if(searchResult.getOrigin() != null && searchResult.getOrigin().getShulkerBox() != null) {

            if (message.contains("%elevators_old_floor%"))
                message = message.replace("%elevators_old_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getOrigin(), true)+"");

            if (message.contains("%elevators_top_floor%") && searchResult.getDestination().getShulkerBox() == null)
                message = message.replace("%elevators_top_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getOrigin(), false)+"");

            if (message.contains("%elevators_old_floor_name%"))
                message = message.replace("%elevators_old_floor_name%", ElevatorDataContainerService.getFloorName(searchResult.getOrigin()));

        }

        return message;

    }

    public static String formatColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> formatLore(String message, ChatColor defaultColor) {
        List<String> messages = new ArrayList<>();
        String[] words = message.split(" ");
        messages.add(defaultColor + words[0]);
        for (int i = 1; i < words.length; i++) {
            if ((messages.get(messages.size()-1) + " " + words[i]).length() <= 30)
                messages.set(messages.size() - 1, messages.get(messages.size()-1) + " " + words[i]);
            else
                messages.add(defaultColor + words[i]);
        }
        return messages;
    }

    public static List<String> formatColors(List<String> messages) {
        if(messages == null) return messages;
        List<String> finalMessages = new ArrayList<>();
        messages.forEach(i -> finalMessages.add(formatColors(i)));
        return finalMessages;
    }

    public static String formatPlaceholders(CommandSender sender, String message) {
        if(!(sender instanceof Player))
            return message;
        Player player = (Player) sender;

        PlaceholderHook hook = ElevatorHookService.getPlaceholderHook();
        if(hook == null)
            return message;

        return hook.formatPlaceholders(player, message);
    }

    @Nonnull
    public static String hideText(@Nonnull String text) {

        StringBuilder output = new StringBuilder();

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        String hex = ColorHelper.encodeHexString(bytes);

        for (char c : hex.toCharArray())
            output.append(ChatColor.COLOR_CHAR).append(c);

        return output.toString();
    }

    @Nonnull
    public static String revealText(@Nonnull String text) {

        if (text.isEmpty())
            return text;
        if (text.length() % 2 != 0)
            text += " ";

        char[] chars = text.toCharArray();

        char[] hexChars = new char[chars.length / 2];

        IntStream.range(0, chars.length).filter(value -> value % 2 != 0).forEach(value -> hexChars[value / 2] = chars[value]);

        try {
            return new String(ColorHelper.decodeHex(hexChars), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
        return text;
    }

    public static String fixEnum(String input) {
        input = input.toLowerCase();
        String[] words = input.split("_");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1);
                result.append(formattedWord).append(" ");
            }
        }

        return result.toString();
    }


}
