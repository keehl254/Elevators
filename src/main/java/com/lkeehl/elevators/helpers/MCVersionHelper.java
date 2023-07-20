package com.lkeehl.elevators.helpers;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCVersionHelper {

    private static final Pattern versionIDPattern = Pattern.compile("V(\\d+)_(\\d+)_R(\\d+)");

    private static final int supportedVersion = getVersionID("V1_13_R2");
    private static final int hexVersion = getVersionID("V1_16_R1");
    private static final int shulkerFacingUseAPI = getVersionID("V1_14_R1");
    private static final int supportBlockBoundingBoxes = getVersionID("V1_17_R1");
    private static final int supportNewBuildLimits = getVersionID("v1_18_R1");
    private static final int shulkerOpenCloseUseAPI = getVersionID("V1_16_R2");

    private static final int slimeSizeMetaData = 0;


    private static final int currentVersionID;
    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);

        currentVersionID = getVersionID(version);
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

    public static int getWorldMinHeight(World world) {
        if(doesVersionSupportNewBuildLimits())
            return world.getMinHeight();
        return 0;
    }

    public static int getVersionID(String key) {
        Matcher matcher = versionIDPattern.matcher(key.toUpperCase());
        if(!matcher.find())
            return -1;

        byte major = Byte.parseByte(matcher.group(1));
        byte minor = Byte.parseByte(matcher.group(2));
        byte patch = Byte.parseByte(matcher.group(3));

        int ID = major << 8;
        ID |= minor;

        return (ID << 8) | patch;

    }

}
