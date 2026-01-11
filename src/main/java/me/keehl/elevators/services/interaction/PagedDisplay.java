package me.keehl.elevators.services.interaction;

import me.keehl.elevators.api.services.interaction.DisplayClickResult;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PagedDisplay<T> {

    private final int startIndex;
    private final int itemsPerPage;
    private final SimpleDisplay display;
    private final List<T> items;

    private Function<T, ItemStack> createItemFunction = (item) -> null;
    private TriConsumer<T, InventoryClickEvent, PagedDisplay<T>> onClickTriConsumer = (item, event, display) -> {};
    private BiConsumer<PagedDisplay<T>, Integer> onPageLoadConsumer = (display, page) -> {};

    public PagedDisplay(JavaPlugin plugin, Player player, Collection<T> items) {
        this(plugin, player, items,null);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, Collection<T> items, String inventoryTitle) {
        this(plugin, player, items, inventoryTitle, null);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, Collection<T> items, String inventoryTitle, Runnable returnRunnable) {
        this(plugin, player, items, inventoryTitle, returnRunnable, DisplayClickResult.CANCEL);
    }

    public PagedDisplay(JavaPlugin plugin, Player player, Collection<T> items, String inventoryTitle, Runnable returnRunnable, DisplayClickResult defaultClickResult) {

        this.items = new ArrayList<>(items);

        this.startIndex = returnRunnable != null ? 9 : 0;

        int inventorySize;
        if(items.size() >= 54 - this.startIndex) {
            inventorySize = 54;
            this.itemsPerPage = 45 - this.startIndex;
        } else {
            inventorySize = (Math.floorDiv(items.size() + 8, 9) * 9) + this.startIndex;
            this.itemsPerPage = 54 - this.startIndex;
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

    public PagedDisplay<T> onLoad(BiConsumer<PagedDisplay<T>, Integer> onPageLoadConsumer) {
        this.onPageLoadConsumer = onPageLoadConsumer;
        return this;
    }

    public int getMaxPage() {
        return this.items.size() / this.itemsPerPage;
    }

    public SimpleDisplay getDisplay() {
        return this.display;
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
        int clampedPageIndex = Math.min(Math.max(pageIndex, 0), maxPage);

        int startIndex = this.itemsPerPage * clampedPageIndex;

        List<T> pageItems = this.items.stream().skip(startIndex).limit(this.itemsPerPage).toList();
        this.display.getInventory().clear();
        this.display.clearActions();

        for(int i=0; i< pageItems.size(); i++) {
            T item = pageItems.get(i);
            ItemStack icon = this.createItemFunction.apply(item);
            if(icon == null)
                continue;

            this.display.setItemSimple(i + this.startIndex, icon, (event, myDisplay) -> this.onClickTriConsumer.accept(item, event, this));
        }

        if (this.itemsPerPage < 45) {
            int inventorySize = this.display.getInventory().getSize();
            this.display.setItemSimple(inventorySize - 7, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Previous Page", Material.TIPPED_ARROW, 1), (event, myDisplay) ->
                    this.loadPage(clampedPageIndex - 1)
            );
            this.display.getInventory().setItem(inventorySize - 5, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + String.format("Page %d of %d", clampedPageIndex + 1, maxPage + 1), Material.NETHER_STAR, 1));
            this.display.setItemSimple(inventorySize - 3, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Next Page", Material.SPECTRAL_ARROW, 1), (event, myDisplay) ->
                    this.loadPage(clampedPageIndex + 1)
            );
        }

        if(this.display.hasReturn())
            this.display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        this.onPageLoadConsumer.accept(this, clampedPageIndex);
    }

}
