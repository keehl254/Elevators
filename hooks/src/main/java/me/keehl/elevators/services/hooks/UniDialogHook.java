package me.keehl.elevators.services.hooks;

import io.github.projectunified.unidialog.core.opener.DialogOpener;
import io.github.projectunified.unidialog.core.payload.DialogPayload;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.models.hooks.DialogHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public abstract class UniDialogHook extends DialogHook {

    protected final Map<UUID, ElevatorDialogData> userDialogData = new HashMap<>();

    protected static final String CONFIRM_KEY = "ele-hook-confirm";
    protected static final String CANCEL_KEY = "ele-hook-cancel";
    protected static final String RESET_KEY = "ele-hook-reset";

    public abstract void clearDialog(UUID owner);

    void onConfirm(DialogPayload payload) {
        ElevatorDialogData data = this.userDialogData.getOrDefault(payload.owner(), null);
        if(data == null)
            return;

        String value = payload.textValue("input");
        if(value == null || value.isBlank())
            value = "";

        boolean valid;
        try {
            valid = data.validationFunction.apply(value);
        } catch (Exception ex) {
            valid = false;
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to validate dialog input. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }

        if(!valid) {
            data.dialogOpener.open(payload.owner());
            return;
        }

        this.clearDialog(payload.owner());
        try {
            data.resultConsumer.accept(value);
        } catch (Exception ex) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to execute dialog input consumer. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }

        this.userDialogData.remove(payload.owner());
    }


    void onCancel(DialogPayload payload) {
        ElevatorDialogData data = this.userDialogData.getOrDefault(payload.owner(), null);
        if(data == null)
            return;

        this.clearDialog(payload.owner());
        data.onCancel.run();
        this.userDialogData.remove(payload.owner());
    }


    void onReset(DialogPayload payload) {
        ElevatorDialogData data = this.userDialogData.getOrDefault(payload.owner(), null);
        if(data == null)
            return;

        this.clearDialog(payload.owner());
        try {
            data.resultConsumer.accept(null);
        } catch (Exception ex) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to execute dialog input consumer. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }
        this.userDialogData.remove(payload.owner());
    }


    /*
        Classic bungee solution for BaseComponents is not very pretty... I really think that Spigot dropped the ball with
        not trying to implement Adventure or make their own version. Though, I don't see either going well.
     */
    @SuppressWarnings("deprecation")
    TextComponent baseComponentFromAdventure(Component adventureComponent) {
        return new TextComponent(BungeeComponentSerializer.get().serialize(adventureComponent));
    }

    public record ElevatorDialogData(Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, DialogOpener dialogOpener) {
    }

}
