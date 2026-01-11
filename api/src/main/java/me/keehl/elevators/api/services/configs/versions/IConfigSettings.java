package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.services.configs.IExpandableConfig;
import java.util.List;

public interface IConfigSettings extends IExpandableConfig {

    String getUsePermission();

    String getDyePermission();

    ILocaleComponent getDisplayName();

    List<ILocaleComponent> getLoreLines();

    int getMaxDistance();

    int getMaxSolidBlocks();

    int getMaxStackSize();

    boolean shouldClassCheck();

    boolean shouldStopObstruction();

    boolean shouldSupportDying();

    boolean shouldCheckColor();

    boolean shouldCheckPerms();

    boolean getCanExplode();

    boolean getCanEditIndividually();

    List<ILocaleComponent> getHologramLines();

}
