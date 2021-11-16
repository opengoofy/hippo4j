package cn.hippo4j.discovery.core;

/**
 * Pair.
 *
 * @author chen.ma
 * @date 2021/8/8 23:04
 */
public class Pair<E1, E2> {

    public E1 first() {
        return first;
    }

    public void setFirst(E1 first) {
        this.first = first;
    }

    public E2 second() {
        return second;
    }

    public void setSecond(E2 second) {
        this.second = second;
    }

    private E1 first;

    private E2 second;

    public Pair(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    }

}
