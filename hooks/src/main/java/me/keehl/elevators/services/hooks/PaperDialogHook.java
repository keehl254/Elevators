package me.keehl.elevators.services.hooks;

import io.github.projectunified.unidialog.core.opener.DialogOpener;
import io.github.projectunified.unidialog.paper.PaperDialogManager;
import io.github.projectunified.unidialog.paper.dialog.PaperMultiActionDialog;
import me.keehl.elevators.Elevators;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaperDialogHook extends UniDialogHook {

    private final PaperDialogManager dialogManager;

    public PaperDialogHook() {
        this.dialogManager = new PaperDialogManager(Elevators.getInstance());
    }

    @Override
    public void onInit() {
        this.dialogManager.register();

        this.dialogManager.registerCustomAction(CONFIRM_KEY, this::onConfirm);
        this.dialogManager.registerCustomAction(CANCEL_KEY, this::onCancel);
        this.dialogManager.registerCustomAction(RESET_KEY, this::onReset);
    }

    @Override
    public void clearDialog(UUID owner) {
        this.dialogManager.clearDialog(owner);
    }


    @Override
    public void createStringInputDialog(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String title, boolean allowReset, String message, String defaultMessage, String inputLabel) {
        PaperMultiActionDialog dialog = this.dialogManager.createMultiActionDialog();
        dialog.title(Component.text(title));
        dialog.body(body -> body.text().text(Component.text(message)));
        dialog.input("input", inputBuilder -> inputBuilder.textInput().label(Component.text(inputLabel)).initial(defaultMessage));
        dialog.action(action -> action.label(Component.text("Confirm")).dynamicCustom(CONFIRM_KEY));

        if(allowReset) {
            dialog.action(action -> action.label(Component.text("Cancel")).dynamicCustom(RESET_KEY));
        }

        dialog.exitAction(action -> action.label(Component.text("Cancel")).dynamicCustom(CANCEL_KEY));
        DialogOpener dialogOpener = dialog.opener();

        ElevatorDialogData data = new ElevatorDialogData(validationFunction, resultConsumer, onCancel, dialogOpener);
        this.userDialogData.put(player.getUniqueId(), data);

        dialogOpener.open(player.getUniqueId());
    }
}
