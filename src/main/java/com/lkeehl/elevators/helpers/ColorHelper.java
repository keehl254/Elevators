package com.lkeehl.elevators.helpers;

import org.bukkit.ChatColor;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

public class ColorHelper {

    private static final IndexColorModel colorModel;

    static {
        int[] colorMap = {-16777216, -16777046, -16733696, -16733526, -5636096, -5635926, -22016, -5592406, -11184811, -11184641, -11141291, -11141121, -43691, -43521, -171, -1};
        colorModel = new IndexColorModel(4, 16, colorMap, 0, false, -1, DataBuffer.TYPE_BYTE);
    }
    public static int getRGBFromHex(String hex) {
        int i = Integer.valueOf(hex, 16);
        return (0xFF << 24) | (((i >> 16) & 0xFF) << 16) | (((i >> 8) & 0xFF) << 8) | ((i & 0xFF));
    }

    public static ChatColor nearestColor(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        int rgb = getRGBFromHex(hex);
        return ChatColor.values()[((byte[]) colorModel.getDataElements(rgb, null))[0]];
    }

    public static String getChatStringFromColor(int rgb) {
        if(!MCVersionHelper.doesVersionSupportHex())
            return ChatColor.values()[((byte[]) colorModel.getDataElements(rgb, null))[0]].toString();

        String hex = String.format("%02x%02x%02x", (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        StringBuilder code = new StringBuilder("§x");
        char[] var2 = hex.toCharArray();

        for (char c : var2)
            code.append('§').append(c);
        return code.toString();
    }

    public static String getColor(String hex) {
        if(!MCVersionHelper.doesVersionSupportHex())
            return nearestColor(hex).toString();
        StringBuilder code = new StringBuilder("§x");
        char[] var2 = hex.toCharArray();

        for (char c : var2)
            code.append('§').append(c);
        return code.toString();
    }

}
