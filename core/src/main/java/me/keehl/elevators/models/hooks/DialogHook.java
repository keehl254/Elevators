package me.keehl.elevators.models.hooks;

import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class DialogHook implements ElevatorHook {

    public abstract void createStringInputDialog(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String title, boolean allowReset, String message, String defaultMessage, String inputLabel);

}
