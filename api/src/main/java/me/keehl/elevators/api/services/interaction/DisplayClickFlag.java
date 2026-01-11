package me.keehl.elevators.api.services.interaction;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.function.Function;

public enum DisplayClickFlag {
        IN_MENU(e -> e.getClickedInventory() == e.getInventory()),
        LEFT_CLICK(InventoryClickEvent::isLeftClick),
        RIGHT_CLICK(InventoryClickEvent::isRightClick),
        SHIFT_CLICK(InventoryClickEvent::isShiftClick);

        private final Function<InventoryClickEvent, Boolean> validationFunction;

        DisplayClickFlag(Function<InventoryClickEvent, Boolean> validationFunction) {
            this.validationFunction = validationFunction;
        }

        public boolean isValid(InventoryClickEvent event) {
            return this.validationFunction.apply(event);
        }

        public static boolean isValid(InventoryClickEvent event, DisplayClickFlag... flags) {
            return Arrays.stream(flags).allMatch(i -> i.isValid(event));
        }
    }