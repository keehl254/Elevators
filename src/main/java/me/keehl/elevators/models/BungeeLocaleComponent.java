package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.ElevatorHelper;
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
        String newComponent = this.componentMessage;
        if(Elevators.getHooksService().getPlaceholderHook() != null) {
            newComponent = Elevators.getHooksService().getPlaceholderHook().formatPlaceholders(eventData.getPlayer(), newComponent);
        }

        if(eventData.getPlayer() != null) {
            newComponent = newComponent.replace("%player%", eventData.getPlayer().getName()).replace("<player>", eventData.getPlayer().getName());
        }
        String elevatorTypeKey = eventData.getElevatorType().getTypeKey();
        String elevatorTypeDisplay = eventData.getElevatorType().getDisplayName().toLegacyText();
        newComponent = newComponent.replace("%elevators_type%", elevatorTypeKey).replace("<elevators_type>", elevatorTypeKey);
        newComponent = newComponent.replace("%elevators_type_display%", elevatorTypeDisplay).replace("<elevators_type_display>", elevatorTypeDisplay);

        if(eventData.getDestination() != null) {
            if(newComponent.contains("<elevators_new_floor>") || newComponent.contains("%elevators_new_floor%")) {
                String value = ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), true) + "";
                newComponent = newComponent.replace("%elevators_new_floor%", elevatorTypeDisplay).replace("<elevators_new_floor>", value);
            }
            if(newComponent.contains("<elevators_top_floor>") || newComponent.contains("%elevators_top_floor%")) {
                String value = ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), false) + "";
                newComponent = newComponent.replace("%elevators_type_display%", elevatorTypeDisplay).replace("<elevators_type_display>", value);
            }
            if(newComponent.contains("<elevators_new_floor_name>") || newComponent.contains("%elevators_new_floor_name%")) {
                String value = Elevators.getDataContainerService().getFloorName(eventData.getDestination());
                newComponent = newComponent.replace("%elevators_new_floor_name%", elevatorTypeDisplay).replace("<elevators_new_floor_name>", value);
            }
        }

        if(eventData.getOrigin() != null) {
            if(newComponent.contains("<elevators_old_floor>") || newComponent.contains("%elevators_old_floor%")) {
                String value = ElevatorHelper.getFloorNumberOrCount(eventData.getOrigin(), true) + "";
                newComponent = newComponent.replace("%elevators_new_floor%", elevatorTypeDisplay).replace("<elevators_new_floor>", value);
            }
            if((newComponent.contains("<elevators_top_floor>") || newComponent.contains("%elevators_top_floor%") ) && eventData.getDestination() == null) {
                String value = ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), false) + "";
                newComponent = newComponent.replace("%elevators_type_display%", elevatorTypeDisplay).replace("<elevators_type_display>", value);
            }
            if(newComponent.contains("<elevators_old_floor_name>") || newComponent.contains("%elevators_old_floor_name%")) {
                String value = Elevators.getDataContainerService().getFloorName(eventData.getOrigin());
                newComponent = newComponent.replace("%elevators_old_floor_name%", elevatorTypeDisplay).replace("<elevators_old_floor_name>", value);
            }
        }

        return TextComponent.fromLegacyText(newComponent);
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
