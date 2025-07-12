package me.keehl.elevators.util;

public interface TriFunction<K, V, S, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param k the first function argument
     * @param v the second function argument
     * @param s the third function argument
     * @return the function result
     */
    R apply(K k, V v, S s);
}

