package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.api.services.configs.versions.IConfigHookData;

public class ConfigHookData implements IConfigHookData {

    public boolean allowCustomization = true;

    public boolean blockNonMemberUseDefault = true;

    @Override
    public boolean doesAllowCustomization() {
        return this.allowCustomization;
    }

    @Override
    public boolean doesBlockNonMemberUseByDefault() {
        return this.blockNonMemberUseDefault;
    }
}
