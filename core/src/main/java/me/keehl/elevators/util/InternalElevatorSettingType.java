package me.keehl.elevators.util;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

public enum InternalElevatorSettingType {

    CAN_EXPLODE("can-explode"),
    CHECK_COLOR("check-color"),
    INDIVIDUAL_EDIT("individual-edit"),
    CHECK_PERMS("check-perms"),
    CLASS_CHECK("check-type"),
    DISPLAY_NAME("change-display-name"),
    DYE_PERMISSION("change-dye-perm"),
    HOLO_LINES("change-holo"),
    LORE_LINES("change-lore"),
    MAX_DISTANCE("change-max-distance"),
    MAX_SOLID_BLOCKS("change-max-solid-blocks"),
    MAX_STACK_SIZE("change-max-stack-size"),
    STOP_OBSTRUCTION("stop-obstruction"),
    SUPPORT_DYING("change-support-dying"),
    USE_PERMISSION("change-use-perm");

    private final String settingName;

    InternalElevatorSettingType(@Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName) {
        this.settingName = settingName;
    }

    @Subst("test_key") public String getSettingName() {
        return this.settingName;
    }

}
