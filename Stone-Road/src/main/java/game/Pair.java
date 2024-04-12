package game;

import java.io.Serializable;

public class Pair<T,V> implements Serializable {
    private T x;
    private V y;

    public Pair(T first, V second) {
        this.x = first;
        this.y = second;
    }

    public T getFirst() {
        return x;
    }

    public V getSecond() {
        return y;
    }

    public void setFirst(T first) {
        this.x = first;
    }

    public void setSecond(V second) {
        this.y = second;
    }
}
