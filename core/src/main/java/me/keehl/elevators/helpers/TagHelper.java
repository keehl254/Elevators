package me.keehl.elevators.helpers;

import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TagHelper {

    public static Tag<Material> SHULKER_BOXES;
    public static Tag<Material> ITEMS_CREEPER_DROP_MUSIC_DISCS;
    public static Tag<Material> ITEMS_BOOKSHELF_BOOKS;

    static {
        SHULKER_BOXES = Bukkit.getTag("blocks", NamespacedKey.minecraft("shulker_boxes"), Material.class);
        if(SHULKER_BOXES == null || SHULKER_BOXES.getValues().isEmpty()) {
            List<Material> boxes = Arrays.stream(DyeColor.values()).map(x -> ItemStackHelper.getVariant(Material.RED_SHULKER_BOX, x)).collect(Collectors.toList());
            boxes.add(Material.SHULKER_BOX);

            SHULKER_BOXES = new FakeTag<>(NamespacedKey.minecraft("shulker_boxes"), new HashSet<>(boxes));
        }
        ITEMS_CREEPER_DROP_MUSIC_DISCS = Bukkit.getTag("blocks", NamespacedKey.minecraft("creeper_drop_music_discs"), Material.class);
        if(ITEMS_CREEPER_DROP_MUSIC_DISCS == null || ITEMS_CREEPER_DROP_MUSIC_DISCS.getValues().isEmpty()) {
            List<Material> discs = new ArrayList<>();
            discs.add(Material.MUSIC_DISC_11);
            discs.add(Material.MUSIC_DISC_13);
            discs.add(Material.MUSIC_DISC_BLOCKS);
            discs.add(Material.MUSIC_DISC_CAT);
            discs.add(Material.MUSIC_DISC_CHIRP);
            discs.add(Material.MUSIC_DISC_FAR);
            discs.add(Material.MUSIC_DISC_MALL);
            discs.add(Material.MUSIC_DISC_MELLOHI);
            discs.add(Material.MUSIC_DISC_STAL);
            discs.add(Material.MUSIC_DISC_STRAD);
            discs.add(Material.MUSIC_DISC_WAIT);
            discs.add(Material.MUSIC_DISC_WARD);

            ITEMS_CREEPER_DROP_MUSIC_DISCS = new FakeTag<>(NamespacedKey.minecraft("creeper_drop_music_discs"), new HashSet<>(discs));
        }
        ITEMS_BOOKSHELF_BOOKS = Bukkit.getTag("blocks", NamespacedKey.minecraft("bookshelf_books"), Material.class);
        if(ITEMS_BOOKSHELF_BOOKS == null || ITEMS_BOOKSHELF_BOOKS.getValues().isEmpty()) {
            List<Material> books = new ArrayList<>();
            books.add(Material.BOOK);
            books.add(Material.ENCHANTED_BOOK);
            books.add(Material.WRITABLE_BOOK);
            books.add(Material.KNOWLEDGE_BOOK);

            ITEMS_BOOKSHELF_BOOKS = new FakeTag<>(NamespacedKey.minecraft("bookshelf_books"), new HashSet<>(books));
        }

    }

    private static class FakeTag<T extends Keyed> implements Tag<T> {

        private final Set<T> collection;
        private final NamespacedKey key;

        public FakeTag(NamespacedKey key, Set<T> collection) {
            this.collection = collection;
            this.key = key;
        }

        @Override
        public boolean isTagged(@NotNull T item) {
            return this.collection.contains(item);
        }

        @Override
        public @NotNull Set<T> getValues() {
            return this.collection;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return this.key;
        }
    }

}
