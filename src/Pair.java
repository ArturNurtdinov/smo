class Pair<K,V> {
    private K first;
    private V second;

    Pair(K key, V value) {
        this.first = key;
        this.second = value;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}