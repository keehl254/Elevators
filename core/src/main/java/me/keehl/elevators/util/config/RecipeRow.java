package me.keehl.elevators.util.config;

import java.util.ArrayList;
import java.util.Collection;

public class RecipeRow<T> extends ArrayList<T> {
    public RecipeRow(Collection<? extends T> c) {
        super(c);
    }

    public RecipeRow() {
        super();
    }
}