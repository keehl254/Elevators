package me.keehl.elevators.models;

import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.ILocaleComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BungeeLocaleComponent implements ILocaleComponent {

    private final String componentMessage;

    public BungeeLocaleComponent(String message) {
        message = ChatColor.translateAlternateColorCodes('&',message);
        this.componentMessage = message;
    }

    public BungeeLocaleComponent(BaseComponent[] components) {
        this.componentMessage = TextComponent.toLegacyText(components);
    }

    private BaseComponent[] getComponent() {
        return TextComponent.fromLegacyText(this.componentMessage);
    }

    private BaseComponent[] getFormattedComponent(IElevatorEventData eventData) {
        return TextComponent.fromLegacyText(this.componentMessage);
    }

    @Override
    public void send(CommandSender commandSender) {
        try {
            commandSender.sendMessage(this.getComponent());
        }catch (Throwable e) {
            commandSender.sendMessage(this.toLegacyText());
        }
    }

    @Override
    public void sendFormatted(CommandSender commandSender, IElevatorEventData eventData) {
        try {
            commandSender.sendMessage(this.getFormattedComponent(eventData));
        }catch (Throwable e) {
            commandSender.sendMessage(getFormatted(eventData).toLegacyText());
        }
    }

    @Override
    public ILocaleComponent getFormatted(IElevatorEventData eventData) {
        return new BungeeLocaleComponent(this.getFormattedComponent(eventData));
    }

    @Override
    public String toLegacyText() {
        return new TextComponent(this.getComponent()).toLegacyText().substring(2);
    }

    @Override
    public BaseComponent[] getBaseComponent() {
        return this.getComponent();
    }

    public String serialize() {
        return this.toLegacyText().replace(ChatColor.COLOR_CHAR, '&');
    }

}
