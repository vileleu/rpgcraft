package fr.jeunesauvage.itemcustom.spell;

public class Pair<K, V> {
    final K   first;
    final V   second;

    Pair(K arg0, V arg1) {
        first = arg0;
        second = arg1;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
