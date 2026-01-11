package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.util.config.Config;

public interface IConfigHookData extends Config {

    boolean doesAllowCustomization();

    boolean doesBlockNonMemberUseByDefault();

}
