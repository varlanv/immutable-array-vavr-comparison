package com.varlanv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ImmutableList<T> {

    static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                ImmutableList::ofIterable
        );
    }

    static <T> ImmutableList<T> empty() {
        return ofIterable(Collections.emptyList());
    }

    static <T> ImmutableList<T> ofIterable(Iterable<T> items) {
        var capacity = 1;
        var array = new Object[capacity];
        array[0] = items;
        return new ImmutableListImpl<>(array, 1);
    }

    static <T> ImmutableList<T> ofArray(T[] items) {
        return ofIterable(Arrays.asList(items));
    }

    static <T> ImmutableList<T> of(T item) {
        var capacity = 1;
        var array = new Object[capacity];
        array[0] = item;
        return new ImmutableListImpl<>(array, 1);
    }

    @SafeVarargs
    static <T> ImmutableList<T> ofAll(T... items) {
        return ofArray(items);
    }

    ImmutableList<T> add(T item);

    ImmutableList<T> addIterable(Iterable<T> items);

    ImmutableList<T> combine(ImmutableList<T> other);

    void forEach(Consumer<? super T> action);

    void forEachIndexed(ObjIntConsumer<? super T> action);

    int size();

    Stream<T> stream();

    <R extends Collection<T>> R copyTo(Supplier<R> supplier);
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
        return addAny(item);
    }

    @Override
    public ImmutableListImpl<T> addIterable(Iterable<T> items) {
        return addAny(items);
    }

    @Override
    public ImmutableList<T> combine(ImmutableList<T> other) {
        return this;
    }

    private ImmutableListImpl<T> fromAny(Object item) {
        var newArray = Arrays.copyOf(array, array.length + (array.length >> 1));
        newArray[array.length] = item;
        return new ImmutableListImpl<>(newArray, limit + 1);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (var idx = 0; idx < limit; idx++) {
            var item = array[idx];
            if (item instanceof Iterable<?> items) {
                items.forEach(it -> {
                    @SuppressWarnings("unchecked")
                    var itCasted = (T) it;
                    action.accept(itCasted);
                });
            } else {
                @SuppressWarnings("unchecked")
                var itCasted = (T) item;
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
        var count = 0;
        for (int i = 0, limit = this.limit; i < limit; i++) {
            var o = array[i];
            if (o instanceof Iterable<?> iterable) {
                for (var object : iterable) {
                    count++;
                }
            } else {
                count++;
            }
        }
        return count;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public <R extends Collection<T>> R copyTo(Supplier<R> supplier) {
        var r = supplier.get();
        forEach(r::add);
        return r;
    }

    private ImmutableListImpl<T> addAny(Object item) {
        Object[] objects = Arrays.copyOf(array, array.length + 1);
        objects[array.length] = item;
        return new ImmutableListImpl<>(objects, objects.length);
//        var cap = capacity;
//        if (cap == array.length) {
//            return fromAny(item);
//        } else {
//            synchronized (this) {
//                cap = capacity;
//                if (cap == array.length) {
//                    return fromAny(item);
//                } else {
//                    array[cap++] = item;
//                    this.capacity = cap;
//                    return new ImmutableListImpl<>(array, limit + 1);
//                }
//            }
//        }
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
