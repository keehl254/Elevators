package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHelper {

    private static final Pattern majorMinorPatchBetaPattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:-[0-9A-Za-z-]+(?:\\.([0-9]*))?)?$");
    private static final Pattern majorMinorPattern = Pattern.compile("^(\\d+)\\.(\\d+)");

    private static final int supportedVersion = getVersionID("1.13.2");
    private static final int hexVersion = getVersionID("1.16.1");
    private static final int shulkerFacingUseAPI = getVersionID("1.14.1");
    private static final int supportBlockBoundingBoxes = getVersionID("1.17.1");
    private static final int supportNewBuildLimits = getVersionID("1.18.1");
    private static final int shulkerOpenCloseUseAPI = getVersionID("1.16.2");
    private static final int paperCollectItemEffect = getVersionID("1.16.5");
    private static final int supportRemoveRecipe = getVersionID("1.15.2");
    private static final int supportConsumerDropItem = getVersionID("1.18.0");
    private static final int supportPredicateChunkEntityGrab = getVersionID("1.16.5");
    private static final int supportAlphaColor = getVersionID("1.17.0");
    private static final int supportDialogs = getVersionID("1.21.6");
    private static final int supportSetMaxStackSize = getVersionID("1.20.6");
    private static final int supportAutoCrafters = getVersionID("1.21.3");

    private static final int slimeSizeMetaData = 0;

    private static final int currentVersionID;
    static {
        currentVersionID = getVersionID(Bukkit.getServer().getBukkitVersion().replace("-SNAPSHOT",""));
    }

    public static boolean isVersionUnsupported(){
        return currentVersionID < supportedVersion;
    }

    public static boolean doesVersionSupportHex(){
        return currentVersionID >= hexVersion;
    }

    public static boolean doesVersionSupportAlphaColor() {
        return currentVersionID >= supportAlphaColor;
    }

    public static boolean doesVersionSupportAutoCrafters() {
        return currentVersionID >= supportAutoCrafters;
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

    public static boolean doesVersionSupportPaperCollectEffect() { return Elevators.getFoliaLib().isPaper() && currentVersionID >= paperCollectItemEffect; }

    public static boolean doesVersionSupportRemoveRecipe() { return currentVersionID >= supportRemoveRecipe; }

    public static boolean doesVersionSupportConsumerDropItem() {
        return currentVersionID >= supportConsumerDropItem;
    }

    public static boolean doesVersionSupportPredicateGetChunkEntities() {
        return currentVersionID >= supportPredicateChunkEntityGrab;
    }

    public static boolean doesVersionSupportDialogs() {
        return currentVersionID >= supportDialogs;
    }

    public static boolean doesVersionSupportSetMaxStackSize() {
        return currentVersionID >= supportSetMaxStackSize;
    }

    public static int getWorldMinHeight(World world) {
        if (!doesVersionSupportNewBuildLimits())
            return 0;

        try {
            Method method = world.getClass().getMethod("getMinHeight");
            method.setAccessible(true);
            return (int) method.invoke(world);
        } catch (Exception ignore) {
            if(world.getEnvironment() == World.Environment.NORMAL)
                return -64;
        }
        return 0;
    }

    public static <T extends Recipe & Keyed> void removeRecipe(T recipe) {

        if(doesVersionSupportRemoveRecipe()) {
            try {
                Method method = Bukkit.getServer().getClass().getMethod("removeRecipe", NamespacedKey.class);
                method.setAccessible(true);
                method.invoke(Bukkit.getServer(), recipe.getKey());
                return;
            } catch (Exception ignore) {
            }
        }

        // Backup or legacy support. Whatever you want to call it.
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        while(recipeIterator.hasNext()) {
            Recipe nextRecipe = recipeIterator.next();
            if(!(nextRecipe instanceof Keyed))
                continue;
            Keyed keyedRecipe = (Keyed) nextRecipe;
            if(keyedRecipe.getKey().toString().equalsIgnoreCase(recipe.getKey().toString()))
                recipeIterator.remove();
        }
    }

    public static void closeShulkerBox(ShulkerBox box) {
        if (!doesVersionSupportOpenCloseAPI())
            return;

        try {
            Method method = box.getClass().getMethod("close");
            method.setAccessible(true);
            method.invoke(box);
        } catch (Exception ignore) {
        }
    }

    public static void openShulkerBox(ShulkerBox box) {
        if (!doesVersionSupportOpenCloseAPI())
            return;

        try {
            Method method = box.getClass().getMethod("open");
            method.setAccessible(true);
            method.invoke(box);
        } catch (Exception ignore) {
        }
    }

    public static void setMaxStackSize(ItemStack item, int maxStackSize) {
        if(!doesVersionSupportSetMaxStackSize())
            return;

        ItemMeta meta = item.getItemMeta();
        try {
            Method method = meta.getClass().getMethod("setMaxStackSize", Integer.class);
            method.setAccessible(true);
            method.invoke(meta, maxStackSize);
        } catch (Exception ignore) {
        }
        item.setItemMeta(meta);
    }

    public static void dropItem(World world, Location location, ItemStack itemStack, Consumer<Item> alterStackConsumer) {
        if(doesVersionSupportConsumerDropItem()) {
            try {
                Method method = world.getClass().getMethod("dropItem", Location.class, ItemStack.class, alterStackConsumer.getClass());
                method.setAccessible(true);
                method.invoke(world, location, itemStack, alterStackConsumer);
            } catch (Exception ignore) {
            }
        }
        Item item = world.dropItem(location, itemStack);
        alterStackConsumer.accept(item);
    }

    public static Collection<BoundingBox> getBoundingBoxes(Block block) {

        if(doesVersionSupportBlockBoundingBoxes()) {
            try {
                Method getShapeMethod = block.getClass().getMethod("getCollisionShape");
                getShapeMethod.setAccessible(true);

                Object voxelShape = getShapeMethod.invoke(block);
                Method getBoundingBoxesMethod = voxelShape.getClass().getMethod("getBoundingBoxes");
                getBoundingBoxesMethod.setAccessible(true);

                return (Collection<BoundingBox>) getBoundingBoxesMethod.invoke(voxelShape);
            } catch (Exception ignore) {
            }
        }

        BoundingBox originalBox = block.getBoundingBox();

        double newMinX = originalBox.getMinX() < 0 ? 1.0 + originalBox.getMinX() % 1 : originalBox.getMinX() % 1;
        double newMinZ = originalBox.getMinZ() < 0 ? 1.0 + originalBox.getMinZ() % 1 : originalBox.getMinZ() % 1;
        double newMinY = originalBox.getMinY() < 0 ? 1.0 + originalBox.getMinY() % 1 : originalBox.getMinY() % 1;

        BoundingBox box = new BoundingBox(newMinX, newMinY, newMinZ, newMinX + originalBox.getWidthX(), newMinY + originalBox.getHeight(), newMinZ + originalBox.getWidthZ());
        return Collections.singletonList(box);
    }

    public static Color getDustColor(int color) {
        if(doesVersionSupportAlphaColor()) {
            try {
                Method method = Color.class.getMethod("fromRGB", int.class);
                method.setAccessible(true);
                return (Color) method.invoke(null, color);
            } catch (Exception ignore) {
            }
        }
        return Color.fromRGB(color & 0x00FFFFFF); // Strip alpha if it is present
    }

    public static int getVersionID(String key) {
        Matcher matcher = majorMinorPatchBetaPattern.matcher(key.toUpperCase());
        byte major;
        byte minor;
        byte patch = 0;
        byte beta = 127;
        if(matcher.find()) {
            major = Byte.parseByte(matcher.group(1));
            minor = Byte.parseByte(matcher.group(2));
            patch = Byte.parseByte(matcher.group(3));
            String betaStr = matcher.group(4);
            if(betaStr != null && !betaStr.isEmpty()){
                beta = Byte.parseByte(matcher.group(4));
            }
        } else {
            matcher = majorMinorPattern.matcher(key.toUpperCase());
            if(!matcher.find())
                return -1;

            major = Byte.parseByte(matcher.group(1));
            minor = Byte.parseByte(matcher.group(2));
        }

        int ID = major << 8;
        ID |= minor;
        ID <<= 8;
        ID |= patch;

        return (ID << 8) | beta;
    }

}
