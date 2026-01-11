package me.keehl.elevators.api.util.config;

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