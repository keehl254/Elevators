package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.ElevatorHelper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdventureLocaleComponent implements ILocaleComponent {

    private final String componentMessage;

    public AdventureLocaleComponent(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (message.contains(ChatColor.COLOR_CHAR + "")) {
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
        String newComponent = this.componentMessage;
        if(Elevators.getHooksService().getPlaceholderHook() != null) {
            newComponent = Elevators.getHooksService().getPlaceholderHook().formatPlaceholders(eventData.getPlayer(), newComponent);
        }

        TagResolver.Builder resolver = TagResolver.builder();
        if(eventData.getPlayer() != null) {
            resolver.tag("player", Tag.preProcessParsed(eventData.getPlayer().getName()));
        }
        resolver.tag("elevators_type", Tag.preProcessParsed(eventData.getOrigin().getElevatorType(false).getTypeKey()));
        resolver.tag("elevators_type_display", Tag.inserting(MiniMessage.miniMessage().deserialize(eventData.getOrigin().getElevatorType(false).getDisplayName().serialize())));

        if(eventData.getDestination() != null) {
            if(newComponent.contains("<elevators_new_floor>"))
                resolver.tag("elevators_new_floor", Tag.preProcessParsed(ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), true)+""));
            if(newComponent.contains("<elevators_top_floor>"))
                resolver.tag("elevators_top_floor", Tag.preProcessParsed(ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), false)+""));
            if(newComponent.contains("<elevators_new_floor_name>"))
                resolver.tag("elevators_new_floor_name", Tag.preProcessParsed(Elevators.getDataContainerService().getFloorName(eventData.getDestination())));
        }

        if(eventData.getOrigin() != null) {
            if(newComponent.contains("<elevators_old_floor>"))
                resolver.tag("elevators_old_floor", Tag.preProcessParsed(ElevatorHelper.getFloorNumberOrCount(eventData.getOrigin(), true)+""));
            if(newComponent.contains("<elevators_top_floor>") && eventData.getDestination() == null)
                resolver.tag("elevators_top_floor", Tag.preProcessParsed(ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), false)+""));
            if(newComponent.contains("<elevators_old_floor_name>"))
                resolver.tag("elevators_old_floor_name", Tag.preProcessParsed(Elevators.getDataContainerService().getFloorName(eventData.getOrigin())));
        }

        return MiniMessage.miniMessage().deserialize(newComponent, resolver.build());
    }

    @Override
    public void send(CommandSender commandSender) {
        if (!(commandSender instanceof Audience audience))
            return;
        audience.sendMessage(this.getComponent());
    }

    @Override
    public void sendFormatted(CommandSender commandSender, IElevatorEventData eventData) {
        if (!(commandSender instanceof Audience audience))
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
