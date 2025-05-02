package com.lkeehl.elevators.services.interaction;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Function;

public class PagedDisplay<T> {

    private final int itemsPerPage;
    private final SimpleDisplay display;
    private final List<T> items;

    private Function<T, ItemStack> createItemFunction = (item) -> null;
    private TriConsumer<T, InventoryClickEvent, PagedDisplay<T>> onClickTriConsumer = (item, event, display) -> {};

    public PagedDisplay(JavaPlugin plugin, Player player, List<T> items) {
        this(plugin, player, items,null);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, List<T> items, String inventoryTitle) {
        this(plugin, player, items, inventoryTitle, null);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, List<T> items, String inventoryTitle, Runnable returnRunnable) {
        this(plugin, player, items, inventoryTitle, returnRunnable, SimpleDisplay.DisplayClickResult.CANCEL);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, List<T> items, String inventoryTitle, Runnable returnRunnable, SimpleDisplay.DisplayClickResult defaultClickResult) {

        this.items = items;

        int inventorySize = 9;
        if(items.size() >= 45) {
            inventorySize = 54;
            this.itemsPerPage = 36;
        } else {
            inventorySize = (Math.floorDiv(items.size() + 8, 9) * 9) + 9;
            this.itemsPerPage = 45;
        }

        Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);

        this.display = new SimpleDisplay(plugin, player, inventory, returnRunnable, defaultClickResult);

    }

    public PagedDisplay<T> onCreateItem(Function<T, ItemStack> createItemFunction) {
        this.createItemFunction = createItemFunction;
        return this;
    }

    public PagedDisplay<T> onClick(TriConsumer<T, InventoryClickEvent, PagedDisplay<T>> onClick) {
        this.onClickTriConsumer = onClick;
        return this;
    }

    public int getMaxPage() {
        return this.items.size() / itemsPerPage;
    }

    public void stopReturn() {
        this.display.stopReturn();
    }

    public void open() {
        this.loadPage(0);
        this.display.open();
    }

    public void close(boolean executeReturn) {
        this.display.close(executeReturn);
    }

    public void returnOrClose() {
        this.display.returnOrClose();
    }

    public void loadPage(int pageIndex) {
        int maxPage = getMaxPage();
        int clampedPageIndex = Math.clamp(pageIndex, 0, maxPage);

        int startIndex = this.itemsPerPage * clampedPageIndex;

        List<T> pageItems = this.items.stream().skip(startIndex).limit(this.itemsPerPage).toList();
        this.display.getInventory().clear();
        this.display.clearActions();

        for(int i=0; i< pageItems.size(); i++) {
            T item = pageItems.get(i);
            ItemStack icon = this.createItemFunction.apply(item);
            if(icon == null)
                continue;

            display.setItemSimple(i+9, icon, (event, myDisplay) -> this.onClickTriConsumer.accept(item, event, this));
        }

        int inventorySize = this.display.getInventory().getSize();
        display.setItemSimple(inventorySize - 7, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Previous Page", Material.TIPPED_ARROW, 1), (event, myDisplay) ->
                this.loadPage(clampedPageIndex - 1)
        );
        display.getInventory().setItem(inventorySize - 5, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + String.format("Page %d of %d", clampedPageIndex+1, maxPage+1), Material.NETHER_STAR, 1));
        display.setItemSimple(inventorySize - 3, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Next Page", Material.SPECTRAL_ARROW, 1), (event, myDisplay) ->
                this.loadPage(clampedPageIndex + 1)
        );

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
    }

}
