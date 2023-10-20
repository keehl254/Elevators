package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.services.configs.ConfigLocale;
import com.lkeehl.elevators.services.hooks.PlaceholderAPIHook;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class MessageHelper {

    private static BiConsumer<Player, String> sendMessageConsumer;

    private static boolean adventureEnabled;
    static {
        try {
            Class.forName("net.kyori.adventure");
            try (BukkitAudiences audience = BukkitAudiences.create(Elevators.getInstance())) {
                sendMessageConsumer = (player, message) -> audience.player(player).sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        } catch (ClassNotFoundException ignore) {
            sendMessageConsumer = (player, message) -> player.sendMessage(message);
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

    public static void sendCantGiveMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.cantGiveMessage, elevatorEventData);
    }

    public static void sendCantReloadMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.cantReloadMessage, elevatorEventData);
    }

    public static void sendNotEnoughRoomGiveMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.notEnoughRoomGiveMessage, elevatorEventData);
    }

    public static void sendGivenElevatorMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.givenElevatorMessage, elevatorEventData);
    }

    public static void sendWorldDisabledMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.worldDisabledMessage, elevatorEventData);
    }

    public static void sendElevatorNowProtectedMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.elevatorNowProtected, elevatorEventData);
    }

    public static void sendElevatorNowUnprotectedMessage(Player player, ElevatorEventData elevatorEventData) {
        MessageHelper.sendFormattedLocale(player, i -> i.elevatorNowUnprotected, elevatorEventData);
    }

    public static void sendFormattedLocale(Player player, Function<ConfigLocale, String> messageFunc, ElevatorEventData elevatorEventData) {
        String message = messageFunc.apply(ConfigService.getRootConfig().locale);
        String defaultMessage = messageFunc.apply(ConfigService.getDefaultLocaleConfig());

        message = message == null ? defaultMessage : message;
        message = formatElevatorPlaceholders(player, elevatorEventData, message);

        MessageHelper.sendFormattedMessage(player, message);
    }

    public static void sendFormattedMessage(Player player, String message) {
        message = formatPlaceholders(player, message);
        message = formatColors(message);

        sendMessageConsumer.accept(player, message);
    }

    public static String formatElevatorPlaceholders(Player player, ElevatorEventData searchResult, String message) {
        message = message.replace("%player%", player.getName());
        message = message.replace("%elevators_type%", searchResult.getElevatorType().getTypeKey());
        if(searchResult.getDestination() != null) {

            if (message.contains("%elevators_new_floor%"))
                message = message.replace("%elevators_new_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getDestination(), searchResult.getElevatorType(), true)+"");

            if (message.contains("%elevators_top_floor%"))
                message = message.replace("%elevators_top_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getDestination(), searchResult.getElevatorType(), false)+"");

            if (message.contains("%elevators_new_floor_name%"))
                message = message.replace("%elevators_new_floor_name%", DataContainerService.getFloorName(searchResult.getDestination(), searchResult.getElevatorType()));

        }
        if(searchResult.getOrigin() != null) {

            if (message.contains("%elevators_old_floor%"))
                message = message.replace("%elevators_new_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getDestination(), searchResult.getElevatorType(), true)+"");

            if (message.contains("%elevators_top_floor%") && searchResult.getDestination() == null)
                message = message.replace("%elevators_top_floor%", ElevatorHelper.getFloorNumberOrCount(searchResult.getOrigin(), searchResult.getElevatorType(), false)+"");

            if (message.contains("%elevators_old_floor_name%"))
                message = message.replace("%elevators_old_floor_name%", DataContainerService.getFloorName(searchResult.getOrigin(), searchResult.getElevatorType()));

        }

        return message;

    }

    public static String formatColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> formatColors(List<String> messages) {
        List<String> finalMessages = new ArrayList<>(messages);
        messages.forEach(i -> finalMessages.add(formatColors(i)));
        return finalMessages;
    }

    public static String formatPlaceholders(Player player, String message) {

        PlaceholderAPIHook hook = HookService.getPlaceholderAPIHook();
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
        } catch (DecoderException ignored) {
        }
        return text;
    }


}
