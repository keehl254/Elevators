package me.keehl.elevators.services.hooks;

import io.github.projectunified.unidialog.bungeecord.dialog.BungeeMultiActionDialog;
import io.github.projectunified.unidialog.core.opener.DialogOpener;
import io.github.projectunified.unidialog.spigot.SpigotDialogManager;
import io.github.projectunified.unidialog.spigot.opener.SpigotDialogOpener;
import me.keehl.elevators.Elevators;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotDialogHook extends UniDialogHook {

    private final SpigotDialogManager dialogManager;

    public SpigotDialogHook() {
        this.dialogManager = new SpigotDialogManager(Elevators.getInstance());
    }

    @Override
    public void onInit() {
        this.dialogManager.register();

        this.dialogManager.registerCustomAction(CONFIRM_KEY, this::onConfirm);
        this.dialogManager.registerCustomAction(CANCEL_KEY, this::onCancel);
        this.dialogManager.registerCustomAction(RESET_KEY, this::onReset);
    }


    @Override
    public void createStringInputDialog(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String title, boolean allowReset, String message, String defaultMessage, String inputLabel) {
        BungeeMultiActionDialog<SpigotDialogOpener> dialog = this.dialogManager.createMultiActionDialog();
        dialog.title(baseComponentFromAdventure(Component.text(title)));
        dialog.body(body -> body.text().text(baseComponentFromAdventure(Component.text(message))));
        dialog.input("input", inputBuilder -> inputBuilder.textInput().label(baseComponentFromAdventure(Component.text(inputLabel))).initial(defaultMessage));
        dialog.action(action -> action.label(baseComponentFromAdventure(Component.text("Confirm"))).dynamicCustom(CONFIRM_KEY));

        if(allowReset) {
            dialog.action(action -> action.label(baseComponentFromAdventure(Component.text("Cancel"))).dynamicCustom(RESET_KEY));
        }

        dialog.exitAction(action -> action.label(baseComponentFromAdventure(Component.text("Cancel"))).dynamicCustom(CANCEL_KEY));
        DialogOpener dialogOpener = dialog.opener();

        ElevatorDialogData data = new ElevatorDialogData(validationFunction, resultConsumer, onCancel, dialogOpener);
        this.userDialogData.put(player.getUniqueId(), data);

        dialogOpener.open(player.getUniqueId());
    }

    @Override
    public void clearDialog(UUID owner) {
        this.dialogManager.clearDialog(owner);
    }
}
