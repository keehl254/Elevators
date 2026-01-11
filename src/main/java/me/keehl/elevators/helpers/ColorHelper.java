package me.keehl.elevators.helpers;

import org.bukkit.ChatColor;

import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

public class ColorHelper {

    private static final IndexColorModel colorModel;

    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    static {
        int[] colorMap = {-16777216, -16777046, -16733696, -16733526, -5636096, -5635926, -22016, -5592406, -11184811, -11184641, -11141291, -11141121, -43691, -43521, -171, -1};
        colorModel = new IndexColorModel(4, 16, colorMap, 0, false, -1, DataBuffer.TYPE_BYTE);
    }
    public static int getRGBFromHex(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
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
        if(!VersionHelper.doesVersionSupportHex())
            return ChatColor.values()[((byte[]) colorModel.getDataElements(rgb, null))[0]].toString();

        String hex = String.format("%02x%02x%02x", (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        StringBuilder code = new StringBuilder("§x");
        char[] var2 = hex.toCharArray();

        for (char c : var2)
            code.append('§').append(c);
        return code.toString();
    }

    public static String getColor(String hex) {
        if(!VersionHelper.doesVersionSupportHex())
            return nearestColor(hex).toString();
        StringBuilder code = new StringBuilder("§x");
        char[] var2 = hex.toCharArray();

        for (char c : var2)
            code.append('§').append(c);
        return code.toString();
    }

    public static byte[] decodeHex(final char[] data) throws Exception {

        final int len = data.length;

        if ((len & 0x01) != 0)
            throw new Exception("Odd number of characters.");

        final byte[] out = new byte[len >> 1];

        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

}
