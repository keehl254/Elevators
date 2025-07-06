package me.keehl.elevators.services.hooks;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.localization.BentoBoxLocale;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BentoBoxHook extends ProtectionHook {

    Flag useFlag, editNameFlag, editSettingsFlag;

    public BentoBoxHook() {
        super("BentoBox");

        this.useFlag = (new Flag.Builder("USE_ELEVATOR", Material.RED_SHULKER_BOX)).mode(Flag.Mode.BASIC).build();
        this.editNameFlag = (new Flag.Builder("EDIT_ELEVATOR_FLOOR_NAME", Material.RED_SHULKER_BOX)).mode(Flag.Mode.BASIC).build();
        this.editSettingsFlag = (new Flag.Builder("EDIT_ELEVATOR_SETTINGS", Material.RED_SHULKER_BOX)).mode(Flag.Mode.BASIC).build();

        BentoBox.getInstance().getFlagsManager().registerFlag(this.useFlag);
        BentoBox.getInstance().getFlagsManager().registerFlag(this.editNameFlag);
        BentoBox.getInstance().getFlagsManager().registerFlag(this.editSettingsFlag);

        for (Locale objLocale : BentoBox.getInstance().getLocalesManager().getAvailableLocales(true)) {
            BentoBoxLocale locale = BentoBox.getInstance().getLocalesManager().getLanguages().get(objLocale);
            if (!locale.contains("protection.flags.USE_ELEVATOR.name")) {
                locale.set("protection.flags.USE_ELEVATOR.name", "Use elevators");
                locale.set("protection.flags.USE_ELEVATOR.description", "Toggle elevators");
            }
            if(!locale.contains("protection.flags.EDIT_ELEVATOR_FLOOR_NAME.name")) {
                locale.set("protection.flags.EDIT_ELEVATOR_FLOOR_NAME.name", "Edit Elevator floor name");
                locale.set("protection.flags.EDIT_ELEVATOR_FLOOR_NAME.description", "Edit the name of the Elevator floor");
            }
            if(!locale.contains("protection.flags.EDIT_ELEVATOR_SETTINGS.name")) {
                locale.set("protection.flags.EDIT_ELEVATOR_SETTINGS.name", "Edit Elevators settings");
                locale.set("protection.flags.EDIT_ELEVATOR_SETTINGS.description", "Edit the settings of the Elevators");
            }
        }
    }

    @Override
    public void onInit() {
    }

    public boolean isIsland(Location location) {
        return BentoBox.getInstance().getIslands().getIslandAt(location).isPresent();
    }
    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        Location location = elevator.getLocation();
        Island island = BentoBox.getInstance().getIslands().getIslandAt(location).orElse(null);
        if (island == null)
            return true;
        if (!island.getProtectionBoundingBox().contains(location.getX(), location.getY(), location.getZ()))
            return true;

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());

        if(island.isAllowed(user, this.useFlag))
            return true;

        if(sendMessage)
            user.sendMessage("general.errors.insufficient-rank", TextVariables.RANK, user.getTranslation(BentoBox.getInstance().getRanksManager().getRank(island.getRank(user))));
        return false;
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        Location location = elevator.getLocation();
        Island island = BentoBox.getInstance().getIslands().getIslandAt(location).orElse(null);
        if (island == null)
            return true;
        if (!island.getProtectionBoundingBox().contains(location.getX(), location.getY(), location.getZ()))
            return true;

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());

        if(island.isAllowed(user, this.editNameFlag))
            return true;

        if(sendMessage)
            user.sendMessage("general.errors.insufficient-rank", TextVariables.RANK, user.getTranslation(BentoBox.getInstance().getRanksManager().getRank(island.getRank(user))));
        return false;
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        Location location = elevator.getLocation();
        Island island = BentoBox.getInstance().getIslands().getIslandAt(location).orElse(null);
        if (island == null)
            return true;
        if (!island.getProtectionBoundingBox().contains(location.getX(), location.getY(), location.getZ()))
            return true;

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());

        if(island.isAllowed(user, this.editSettingsFlag))
            return true;

        if(sendMessage)
            user.sendMessage("general.errors.insufficient-rank", TextVariables.RANK, user.getTranslation(BentoBox.getInstance().getRanksManager().getRank(island.getRank(user))));
        return false;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        Island island = BentoBox.getInstance().getIslands().getIslandAt(elevator.getLocation()).orElse(null);
        if (island == null)
            return null;

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether island");
        lore.add(ChatColor.GRAY + "guests are blocked from");
        lore.add(ChatColor.GRAY + "using this Elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Bento Box", Material.DIAMOND, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
