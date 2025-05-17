package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.ColorHelper;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.services.ElevatorConfigService;
import com.lkeehl.elevators.services.interaction.SimpleDisplay;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "SpellCheckingInspection"})
public class BossBarAction extends ElevatorAction {

    private static final Random random = new Random();

    private static final ElevatorActionGrouping<BarColor> barColorGrouping = new ElevatorActionGrouping<>(BarColor.BLUE, BarColor::valueOf, "barcolor","color","c");
    private static final ElevatorActionGrouping<BarStyle> barStyleGrouping = new ElevatorActionGrouping<>(BarStyle.SOLID, BarStyle::valueOf, "barstyle","style","s");
    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public BossBarAction(ElevatorType elevatorType) {
        super(elevatorType, "boss-bar", "message", barColorGrouping, barStyleGrouping, messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message shown in the boss bar.";
        ElevatorActionSetting<String> messageSetting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        messageSetting.setupDataStore("message", PersistentDataType.STRING);
        messageSetting.onClick(this::editMessage);

        desc = "This option controls the segments of the boss bar.";
        ElevatorActionSetting<BarStyle> styleSetting = this.mapSetting(barStyleGrouping, "style","Segments", desc, Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE);
        styleSetting.setupDataStore("style", PersistentDataType.STRING);
        styleSetting.onClick(this::editStyle);
        styleSetting.addAction("Left Click", "Raise Segments");
        styleSetting.addAction("Right Click", "Lower Segments");

        desc = "This option controls the color of the boss bar.";
        ElevatorActionSetting<BarColor> colorSetting = this.mapSetting(barColorGrouping, "color","Color", desc, Material.LIGHT_BLUE_DYE, ChatColor.AQUA);
        colorSetting.setupDataStore("color", PersistentDataType.STRING);
        colorSetting.onClick(this::editColor);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        /*if (elevator instanceof PremiumElevator && ((PremiumElevator) elevator).getSpeed() > 0.0)
            return;*/

        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        int floorCount = ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), false);
        int currentFloor = ElevatorHelper.getFloorNumberOrCount(eventData.getDestination(), true);

        double progress = (1.0F / (floorCount - 1)) * (currentFloor - 1);

        String finalValue = value;
        Runnable onRemove = this.displayMessage(player,eventData.getOrigin(), () -> finalValue, progress);

        Elevators.getFoliaLib().getScheduler().runAtEntityLater(player, onRemove, 30);
    }

    public static BossBar getPlayerBar(Player player, BossBarAction action, Elevator elevator) {
        if (!player.hasMetadata("elevator-boss-bar")) {
            BossBar bar = Bukkit.createBossBar("elevator-boss-bar", action.getGroupingObject(barColorGrouping, elevator), action.getGroupingObject(barStyleGrouping, elevator));
            player.setMetadata("elevator-boss-bar", new FixedMetadataValue(Elevators.getInstance(), bar));
        }
        return (BossBar) player.getMetadata("elevator-boss-bar").getFirst().value();
    }

    public String getMessage() {
        return this.getGroupingObject(messageGrouping);
    }

    public static void changeProgress(Player player, BossBarAction action, Elevator elevator, double progress) {
        if (((Double) progress).isNaN())
            progress = 0.0;
        else if (((Double) progress).isInfinite())
            progress = 1.0;
        getPlayerBar(player, action, elevator).setProgress(Math.max(0.0, Math.min(progress, 1.0)));
    }

    public Runnable displayMessage(Player player, Elevator elevator, Supplier<String> message, double progress) {
        if (((Double) progress).isNaN())
            progress = 0.0;
        else if (((Double) progress).isInfinite())
            progress = 1.0;

        BossBar bar = BossBarAction.getPlayerBar(player, this, elevator);
        bar.setColor(this.getGroupingObject(barColorGrouping, elevator));
        bar.setStyle(this.getGroupingObject(barStyleGrouping, elevator));
        bar.setTitle(message.get());
        bar.setProgress(Math.max(0.0, Math.min(progress, 1.0)));
        if (!bar.getPlayers().contains(player))
            bar.addPlayer(player);

        if (player.hasMetadata("elevators-bossbar-seed"))
            player.removeMetadata("elevators-bossbar-seed", Elevators.getInstance());

        final long seed = random.nextLong();
        player.setMetadata("elevators-bossbar-seed", new FixedMetadataValue(Elevators.getInstance(), seed));
        bar.setVisible(true);
        return () -> {
            if (player.getMetadata("elevators-bossbar-seed").getFirst().asLong() == seed)
                bar.setVisible(false);
        };
    }

    @Override
    public void onStartEditing(Player player, SimpleDisplay display, Elevator elevator) {
        display.setCache("ele-boss-bar-runnable", this.displayMessage(player, elevator, () -> this.getGroupingObject(messageGrouping, elevator), 50));
    }

    @Override
    public void onStopEditing(Player player, SimpleDisplay display, Elevator elevator) {
        Runnable stopBar = display.getOrDefaultCache("ele-boss-bar-runnable", null);
        if (stopBar != null)
            stopBar.run();
    }

    private void editColor(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, BarColor currentValue, Consumer<BarColor> setValueMethod) {

        Inventory inventory = Bukkit.createInventory(null, 18, "Actions > Action > Color");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        display.onReturn(() -> {
            onStopEditing(player, display, null);
            returnMethod.run();
        });

        Consumer<BarColor> onFinish = (color) -> {
            setValueMethod.accept(color);
            getPlayerBar(player, this, null).setColor(color);
        };

        display.setItemSimple(9, ItemStackHelper.createItem(ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE", Material.BLUE_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.BLUE));
        display.setItemSimple(10, ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "RED", Material.RED_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.RED));
        display.setItemSimple(11, ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "GREEN", Material.GREEN_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.GREEN));
        display.setItemSimple(12, ItemStackHelper.createItem(ColorHelper.getColor("FFC0CB") + ChatColor.BOLD + "PINK", Material.PINK_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.PINK));
        display.setItemSimple(13, ItemStackHelper.createItem(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "PURPLE", Material.PURPLE_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.PURPLE));
        display.setItemSimple(14, ItemStackHelper.createItem(ChatColor.WHITE + "" + ChatColor.BOLD + "WHITE", Material.WHITE_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.WHITE));
        display.setItemSimple(15, ItemStackHelper.createItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "YELLOW", Material.YELLOW_DYE, 1), (event, myDisplay) -> onFinish.accept(BarColor.YELLOW));

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

    private void editStyle(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, BarStyle currentValue, Consumer<BarStyle> setValueMethod) {

        int newIndex = currentValue.ordinal() + (clickEvent.isLeftClick() ? 1 : -1);
        newIndex = Math.clamp(newIndex, 0, BarStyle.values().length -1);

        setValueMethod.accept(BarStyle.values()[newIndex]);
        returnMethod.run();
    }

    private void editMessage(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }


}
