package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Elevator implements IElevator {

    private final ShulkerBox shulkerBox;

    private final IElevatorType elevatorType;

    public Elevator(ShulkerBox shulkerBox, IElevatorType elevatorType) {
        this.shulkerBox = shulkerBox;
        this.elevatorType = elevatorType;

        if(shulkerBox == null)
            return;

        // I build against 1.14.4, which for some reason has getColor as NonNull. This is changed in later versions.
        //noinspection ConstantValue
        if (shulkerBox.getColor() == null)
            throw new IllegalArgumentException("Elevators cannot be created from an undyed shulker box.");
    }

    public @NotNull ShulkerBox getShulkerBox() {
        return this.shulkerBox;
    }

    public @NotNull IElevatorType getSnapshotElevatorType() {
        return this.elevatorType;
    }

    public @NotNull Optional<IElevatorType> resolveElevatorTypeLive() {
        return Optional.ofNullable(Elevators.getElevatorTypeService().getElevatorType(this.elevatorType.getTypeKey()));
    }

    public @NotNull Location getLocation() {
        return this.shulkerBox.getLocation();
    }

    public @NotNull DyeColor getDyeColor() {
        return this.shulkerBox.getColor();
    }

    public boolean isValid() {
        if(this.getLocation().getBlock().getType() != this.shulkerBox.getType())
            return false;

        return resolveElevatorTypeLive().filter(iElevatorType -> iElevatorType == this.elevatorType).isPresent();
    }

}
