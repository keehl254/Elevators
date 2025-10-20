package me.keehl.elevators.services.hooks;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaProvider;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.service.ProtectionService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProtectHook extends ProtectionHook {

    private final Flag<Boolean> useFlag;
    private final Flag<Boolean> nameFlag;
    private final Flag<Boolean> settingsFlag;

    private final FlagRegistry flagRegistry;
    private final AreaProvider areaProvider;
    private final ProtectionService protectionService;

    public ProtectHook() {
        super("Protect");

        this.flagRegistry = Bukkit.getServicesManager().load(FlagRegistry.class);
        this.areaProvider = Bukkit.getServicesManager().load(AreaProvider.class);
        this.protectionService = Bukkit.getServicesManager().load(ProtectionService.class);

        this.useFlag = registerFlag("elevator_use");
        this.nameFlag = registerFlag("elevator_rename");
        this.settingsFlag = registerFlag("elevator_settings");
    }

    @Override
    public void onInit() {
    }

    private Flag<Boolean> registerFlag(@KeyPattern.Value String flagName) {
        NamespacedKey key = new NamespacedKey(Elevators.getInstance(), flagName);
        Optional<Flag<Boolean>> flagOpt = this.flagRegistry.getFlag(key);
        return flagOpt.orElseGet(() ->
                this.flagRegistry.register(Elevators.getInstance(), flagName, true)
        );
    }

    public void failed(Player player, String message) {
        player.sendRichMessage("<red><dark_gray>[<dark_red><bold>!</bold></dark_red>]</dark_gray> " + message + "</red>");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.areaProvider.getArea(elevator.getLocation());
        if (this.protectionService.canPerformAction(player, area, this.useFlag, null)) return true;
        if (sendMessage) this.failed(player, "You are not allowed to do that here!");
        return false;
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.areaProvider.getArea(elevator.getLocation());
        if (this.protectionService.canPerformAction(player, area, this.nameFlag, null)) return true;
        if (sendMessage) this.failed(player, "You are not allowed to do that here!");
        return false;
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.areaProvider.getArea(elevator.getLocation());
        if (this.protectionService.canPerformAction(player, area, this.settingsFlag, null)) return true;
        if (sendMessage) this.failed(player, "You are not allowed to do that here!");
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether this");
        lore.add(ChatColor.GRAY + "elevator will check");
        lore.add(ChatColor.GRAY + "Protect flags.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"));

        return ItemStackHelper.createItem(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Protect", Material.SHIELD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
