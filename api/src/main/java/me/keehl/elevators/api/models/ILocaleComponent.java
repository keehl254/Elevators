package me.keehl.elevators.api.models;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

public interface ILocaleComponent {

    void send(CommandSender commandSender);

    void sendFormatted(CommandSender commandSender, IElevatorEventData eventData);

    ILocaleComponent getFormatted(IElevatorEventData eventData);

    String toLegacyText();

    BaseComponent[] getBaseComponent();

    String serialize();

}
