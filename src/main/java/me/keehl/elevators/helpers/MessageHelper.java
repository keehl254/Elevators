package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.models.hooks.PlaceholderHook;
import me.keehl.elevators.models.AdventureLocaleComponent;
import me.keehl.elevators.models.BungeeLocaleComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MessageHelper {

    public static ILocaleComponent getLocaleComponent(String message) {
        try {
            Class.forName("net.kyori.adventure.text.TextComponent");
            return new AdventureLocaleComponent(message);
        } catch (ClassNotFoundException ignore) {
        }
        return new BungeeLocaleComponent(message);
    }

    public static List<ILocaleComponent> getLocalComponents(List<String> messages) {
        List<ILocaleComponent> components = new ArrayList<>();
        for(String message : messages) {
            components.add(getLocaleComponent(message));
        }

        return components;
    }

    public static String formatElevatorPlaceholders(CommandSender sender, IElevatorEventData searchResult, String message) {

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
                message = message.replace("%elevators_new_floor_name%", Elevators.getDataContainerService().getFloorName(searchResult.getDestination()));

        }
        if(searchResult.getOrigin() != null && searchResult.getOrigin().getShulkerBox() != null) {

            if (message.contains("%elevators_old_floor%"))
                message = message.replace("%elevators_old_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getOrigin(), true)+"");

            if (message.contains("%elevators_top_floor%") && searchResult.getDestination().getShulkerBox() == null)
                message = message.replace("%elevators_top_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getOrigin(), false)+"");

            if (message.contains("%elevators_old_floor_name%"))
                message = message.replace("%elevators_old_floor_name%", Elevators.getDataContainerService().getFloorName(searchResult.getOrigin()));

        }

        return message;

    }

    public static String formatLineColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> formatLore(String message, ChatColor defaultColor) {
        List<String> messages = new ArrayList<>();
        String[] words = message.split(" ");
        messages.add(defaultColor + words[0]);
        for (int i = 1; i < words.length; i++) {
            if ((messages.getLast() + " " + words[i]).length() <= 30)
                messages.set(messages.size() - 1, messages.getLast() + " " + words[i]);
            else
                messages.add(defaultColor + words[i]);
        }
        return messages;
    }

    public static List<String> formatListColors(List<String> messages) {
        if(messages == null) return messages;
        List<String> finalMessages = new ArrayList<>();
        messages.forEach(i -> finalMessages.add(formatLineColors(i)));
        return finalMessages;
    }

    public static String formatPlaceholders(CommandSender sender, String message) {
        if(!(sender instanceof Player player))
            return message;

        PlaceholderHook hook = Elevators.getHooksService().getPlaceholderHook();
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
