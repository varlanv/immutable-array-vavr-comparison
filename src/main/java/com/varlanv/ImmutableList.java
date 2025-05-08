package com.varlanv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.stream.Stream;

public interface ImmutableList<T> {

    static <T> ImmutableList<T> empty() {
        return fromIterable(Collections.emptyList());
    }

    static <T> ImmutableList<T> fromIterable(Iterable<T> items) {
        var array = new Object[10];
        array[0] = items;
        return new ImmutableListImpl<>(array, 1);
    }

    static <T> ImmutableList<T> fromArray(T[] items) {
        return fromIterable(Arrays.asList(items));
    }

    static <T> ImmutableList<T> fromSingle(T item) {
        var array = new Object[10];
        array[0] = item;
        return new ImmutableListImpl<>(array, 1);
    }

    @SafeVarargs
    static <T> ImmutableList<T> fromAll(T... items) {
        return fromArray(items);
    }

    ImmutableList<T> add(T item);

    ImmutableList<T> addIterable(Iterable<T> items);

    void forEach(Consumer<? super T> action);

    void forEachIndexed(ObjIntConsumer<? super T> action);

    int size();

    Stream<T> stream();
}

final class ImmutableListImpl<T> implements ImmutableList<T> {

    private final Object[] array;
    private final int limit;
    private volatile int capacity;

    ImmutableListImpl(Object[] items, int limit) {
        this.array = items;
        this.limit = limit;
        this.capacity = limit;
    }

    @Override
    public ImmutableListImpl<T> add(T item) {
        var cap = capacity;
        if (cap == array.length) {
            return fromAny(item);
        } else {
            synchronized (this) {
                cap = capacity;
                if (cap == array.length) {
                    return fromAny(item);
                } else {
                    array[cap++] = item;
                    this.capacity = ++cap;
                    return new ImmutableListImpl<>(array, limit + 1);
                }
            }
        }
    }

    @Override
    public ImmutableListImpl<T> addIterable(Iterable<T> items) {
        var cap = capacity;
        if (cap == array.length) {
            return fromAny(items);
        } else {
            synchronized (this) {
                cap = capacity;
                if (cap == array.length) {
                    return fromAny(items);
                } else {
                    array[cap++] = items;
                    this.capacity = ++cap;
                    return new ImmutableListImpl<>(array, limit + 1);
                }
            }
        }
    }

    private ImmutableListImpl<T> fromAny(Object item) {
        var newArray = Arrays.copyOf(array, array.length * 2);
        newArray[array.length] = item;
        return new ImmutableListImpl<>(newArray, limit + 1);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (var idx = 0; idx < limit; idx++) {
            var o = array[idx];
            if (o instanceof Iterable<?> iterable) {
                iterable.forEach(it -> {
                    var itCasted = (T) it;
                    action.accept(itCasted);
                });
            } else {
                var itCasted = (T) o;
                action.accept(itCasted);
            }
        }
    }

    @Override
    public void forEachIndexed(ObjIntConsumer<? super T> action) {
//        var counter = 0;
//        for (var idx = 0; idx < limit; idx++) {
//            for (var item : array[idx]) {
//                action.accept(item, counter++);
//            }
//        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }
}

final class InternalUtil {

    static int sizeOfIterable(Iterable<?> iterable) {
        if (iterable instanceof Collection<?> collection) {
            return collection.size();
        }
        var counter = 0;
        for (var ignore : iterable) {
            counter++;
        }
        return counter;
    }
}
