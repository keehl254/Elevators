package me.keehl.elevators.api.services.configs.versions;

public class DefaultConfigHookData implements IConfigHookData {

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

    @Override
    public void setAllowCustomization(boolean allowCustomization) {
        this.allowCustomization = allowCustomization;
    }

    @Override
    public void setBlockNonMemberUseByDefault(boolean blockNonMemberUseByDefault) {
        this.blockNonMemberUseDefault = blockNonMemberUseByDefault;
    }
}
