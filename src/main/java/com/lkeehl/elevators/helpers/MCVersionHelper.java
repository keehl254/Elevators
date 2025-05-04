package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.services.ElevatorHookService;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCVersionHelper {

    private static final Pattern majorMinorPatchPattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final Pattern majorMinorPattern = Pattern.compile("^(\\d+)\\.(\\d+)");

    private static final int supportedVersion = getVersionID("1.13.2");
    private static final int hexVersion = getVersionID("1.16.1");
    private static final int shulkerFacingUseAPI = getVersionID("1.14.1");
    private static final int supportBlockBoundingBoxes = getVersionID("1.17.1");
    private static final int supportNewBuildLimits = getVersionID("1.18.1");
    private static final int shulkerOpenCloseUseAPI = getVersionID("1.16.2");
    private static final int paperCollectItemEffect = getVersionID("1.16.5");
    private static final int supportRemoveRecipe = getVersionID("1.15.2");

    private static final int slimeSizeMetaData = 0;


    private static final int currentVersionID;
    static {
        currentVersionID = getVersionID(Bukkit.getServer().getBukkitVersion());
    }

    public static boolean isVersionUnsupported(){
        return currentVersionID < supportedVersion;
    }

    public static boolean doesVersionSupportHex(){
        return currentVersionID >= hexVersion;
    }

    public static boolean doesVersionSupportShulkerFacingAPI() {
        return currentVersionID >= shulkerFacingUseAPI;
    }

    public static boolean doesVersionSupportBlockBoundingBoxes() {
        return currentVersionID >= supportBlockBoundingBoxes;
    }

    public static boolean doesVersionSupportNewBuildLimits() {
        return currentVersionID >= supportNewBuildLimits;
    }

    public static boolean doesVersionSupportOpenCloseAPI() {
        return currentVersionID >= shulkerOpenCloseUseAPI;
    }

    public static boolean doesVersionSupportPaperCollectEffect() { return ElevatorHookService.isServerRunningPaper() && currentVersionID >= paperCollectItemEffect; }

    public static boolean doesVersionSupportRemoveRecipe() { return currentVersionID >= supportRemoveRecipe; }

    public static int getWorldMinHeight(World world) {
        if(doesVersionSupportNewBuildLimits())
            return world.getMinHeight();
        return 0;
    }

    public static int getVersionID(String key) {
        Matcher matcher = majorMinorPatchPattern.matcher(key.toUpperCase());
        byte major;
        byte minor;
        byte patch = 0;
        if(matcher.find()) {
            major = Byte.parseByte(matcher.group(1));
            minor = Byte.parseByte(matcher.group(2));
            patch = Byte.parseByte(matcher.group(3));
        } else {
            matcher = majorMinorPattern.matcher(key.toUpperCase());
            if(!matcher.find())
                return -1;

            major = Byte.parseByte(matcher.group(1));
            minor = Byte.parseByte(matcher.group(2));
        }

        int ID = major << 8;
        ID |= minor;

        return (ID << 8) | patch;

    }

}
