package fr.jeunesauvage;

import java.util.Objects;

public class Pair<K, V> {
    final K   first;
    final V   second;

    public Pair(K arg0, V arg1) {
        first = arg0;
        second = arg1;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair pair)) return false;
        return first == pair.first && second == pair.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
