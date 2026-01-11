package me.keehl.elevators.models;

import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.MessageHelper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdventureLocaleComponent implements ILocaleComponent {

    private final String componentMessage;

    public AdventureLocaleComponent(String message) {
        message = ChatColor.translateAlternateColorCodes('&',message);
        if(message.contains(ChatColor.COLOR_CHAR + "")) {
            message = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(message));
        }
        this.componentMessage = message;
    }

    private AdventureLocaleComponent(Component component) {
        this.componentMessage = MiniMessage.miniMessage().serialize(component);
    }

    private Component getComponent() {
        return MiniMessage.miniMessage().deserialize(this.componentMessage);
    }

    private Component getFormattedComponent(IElevatorEventData eventData) {
        String newComponent = MessageHelper.formatPlaceholders(eventData.getPlayer(), this.componentMessage);

        return MiniMessage.miniMessage().deserialize(newComponent);
    }

    @Override
    public void send(CommandSender commandSender) {
        if(!(commandSender instanceof Audience audience))
            return;
        audience.sendMessage(this.getComponent());
    }

    @Override
    public void sendFormatted(CommandSender commandSender, IElevatorEventData eventData) {
        if(!(commandSender instanceof Audience audience))
            return;
        audience.sendMessage(this.getFormattedComponent(eventData));
    }

    @Override
    public AdventureLocaleComponent getFormatted(IElevatorEventData eventData) {
        return new AdventureLocaleComponent(this.getFormattedComponent(eventData));
    }

    @Override
    public String toLegacyText() {
        return LegacyComponentSerializer.legacySection().serialize(this.getComponent());
    }

    @Override
    public BaseComponent[] getBaseComponent() {
        return BungeeComponentSerializer.get().serialize(this.getComponent());
    }

    public String serialize() {
        return this.componentMessage;
    }

}
