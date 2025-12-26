package me.keehl.elevators.services.hooks;

import io.github.projectunified.unidialog.core.dialog.Dialog;
import io.github.projectunified.unidialog.core.opener.DialogOpener;
import io.github.projectunified.unidialog.paper.PaperDialogManager;
import io.github.projectunified.unidialog.paper.dialog.PaperMultiActionDialog;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ResourceHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class PaperDialogHook extends UniDialogHook {

    private final PaperDialogManager dialogManager;

    public PaperDialogHook() {
        this.dialogManager = new PaperDialogManager(Elevators.getInstance());
    }

    @Override
    public void onInit() {
        this.dialogManager.register();

        this.dialogManager.registerCustomAction("elevators", CONFIRM_KEY, this::onConfirm);
        this.dialogManager.registerCustomAction("elevators", CANCEL_KEY, this::onCancel);
        this.dialogManager.registerCustomAction("elevators", RESET_KEY, this::onReset);
    }

    @Override
    public void clearDialog(UUID owner) {
        this.dialogManager.clearDialog(owner);
    }


    @Override
    public void createStringInputDialog(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, Runnable onQuit, String title, boolean allowReset, String message, String defaultMessage, String inputLabel) {

        PaperMultiActionDialog dialog = this.dialogManager.createMultiActionDialog();
        dialog.canCloseWithEscape(true);
        dialog.afterAction(Dialog.AfterAction.WAIT_FOR_RESPONSE);
        dialog.title(Component.text(title));
        dialog.body(body -> body.text().text(Component.text(message)));
        dialog.input("input", inputBuilder -> inputBuilder.textInput().label(Component.text(inputLabel)).initial(defaultMessage));
        dialog.action(action -> action.label(Component.text("Confirm")).dynamicCustom("elevators", CONFIRM_KEY));

        if (allowReset) {
            dialog.action(action -> action.label(Component.text("Reset")).dynamicCustom("elevators", RESET_KEY));
        }

        dialog.exitAction(action -> action.label(Component.text("Cancel")).dynamicCustom("elevators", CANCEL_KEY));
        try {
            DialogOpener dialogOpener = dialog.opener();

            ElevatorDialogData data = new ElevatorDialogData(validationFunction, resultConsumer, onCancel, onQuit, dialogOpener);
            this.userDialogData.put(player.getUniqueId(), data);

            dialogOpener.open(player.getUniqueId());
        } catch (Exception ex) {
            onQuit.run();
            Elevators.log(Level.SEVERE, "Failed to open dialog. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }
    }
}
