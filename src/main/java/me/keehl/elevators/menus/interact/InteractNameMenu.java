package me.keehl.elevators.menus.interact;

import me.keehl.dialogbuilder.api.dialog.AfterAction;
import me.keehl.dialogbuilder.api.dialog.DialogActionResult;
import me.keehl.dialogbuilder.api.dialog.IMultiActionDialog;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.services.interaction.ISimpleDisplay;
import me.keehl.elevators.helpers.*;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public class InteractNameMenu {

    private static void openDialogMenu(ISimpleDisplay previousDisplay, Player player, IElevator elevator) {
        try {
            Optional<String> currentName = Elevators.getDataContainerService().getFloorNameOpt(elevator);

            Function<String, DialogActionResult> setFloorNameFunc = (input) -> {
                Elevators.getDataContainerService().setFloorName(elevator, input);
                previousDisplay.stopReturn();
                InteractMenu.openInteractMenu(player, elevator);

                return DialogActionResult.UNREGISTER;
            };

            IMultiActionDialog dialog = ElevatorMenuHelper.getDialogManager().createMultiActionDialog();
            dialog.canCloseWithEscape(true);
            dialog.afterAction(AfterAction.WAIT_FOR_RESPONSE);
            dialog.title("Elevators");
            dialog.body(body -> body.text().text(Elevators.getLocale().getEnterFloorNameMessage().serialize()));
            dialog.input("input", inputBuilder -> inputBuilder.textInput().label("Floor Name").initial(currentName.orElse("")));
            dialog.action(action -> action.label("Confirm").dynamicCustom((payload) -> {
                String input = payload.textValue("input");
                return setFloorNameFunc.apply(input);
            }));
            dialog.action(action -> action.label("Reset").dynamicCustom((payload) ->
                    setFloorNameFunc.apply(null))
            );
            dialog.exitAction(action -> action.label("Cancel").dynamicCustom((payload) -> {
                previousDisplay.stopReturn();
                InteractMenu.openInteractMenu(player, elevator);
                return DialogActionResult.UNREGISTER;
            }));

            dialog.opener().open(player.getUniqueId());
        }catch (Exception e) {
            ElevatorsAPI.log(Level.WARNING, "Error showing dialog. Please create an issue ticket on my GitHub with your config if you would like assistance: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));

            previousDisplay.stopReturn();
            InteractNameMenu.openTextInput(player, elevator);
        }
    }

    public static void openTextInput(Player player, IElevator elevator) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();
        input.onComplete(result -> {
            Elevators.getDataContainerService().setFloorName(elevator, result);
            InteractMenu.openInteractMenu(player, elevator);
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> InteractMenu.openInteractMenu(player, elevator));
        Elevators.getLocale().getEnterFloorNameMessage().send(player);
        input.start();
    }

    public static void openInteractNameMenu(ISimpleDisplay previousDisplay, Player player, IElevator elevator) {
        if (!elevator.isValid()) {
            InteractMenu.openInteractMenu(player, elevator);
            return;
        }

        if (VersionHelper.doesVersionSupportDialogs()) {
            InteractNameMenu.openDialogMenu(previousDisplay, player, elevator);
            return;
        }

        previousDisplay.stopReturn();
        InteractNameMenu.openTextInput(player, elevator);
    }

}
