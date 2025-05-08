package com.varlanv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ImmutableListTest {

    @Test
    void add_1__forEach__should_contain_all() {
        var delegate = new ArrayList<Integer>();
        ImmutableList.<Integer>empty().add(1).forEach(delegate::add);

        Assertions.assertEquals(1, delegate.get(0));
        Assertions.assertEquals(1, delegate.size());
    }

    @Test
    void add_1_then_add_iterable_3__forEach__should_contain_all() {
        var delegate = new ArrayList<Integer>();
        ImmutableList.<Integer>empty()
                .add(1)
                .addIterable(List.of(2, 3, 4))
                .forEach(delegate::add);

        Assertions.assertEquals(1, delegate.get(0));
        Assertions.assertEquals(2, delegate.get(1));
        Assertions.assertEquals(3, delegate.get(2));
        Assertions.assertEquals(4, delegate.get(3));
        Assertions.assertEquals(4, delegate.size());
    }

    @Test
    void add_11__forEach__should_contain_all() {
        var delegate = new ArrayList<Integer>();
        ImmutableList.<Integer>empty()
                .add(1)
                .add(2)
                .add(3)
                .add(4)
                .add(5)
                .add(6)
                .add(7)
                .add(8)
                .add(9)
                .add(10)
                .add(11)
                .forEach(delegate::add);

        Assertions.assertEquals(1, delegate.get(0));
        Assertions.assertEquals(2, delegate.get(1));
        Assertions.assertEquals(3, delegate.get(2));
        Assertions.assertEquals(4, delegate.get(3));
        Assertions.assertEquals(5, delegate.get(4));
        Assertions.assertEquals(6, delegate.get(5));
        Assertions.assertEquals(7, delegate.get(6));
        Assertions.assertEquals(8, delegate.get(7));
        Assertions.assertEquals(9, delegate.get(8));
        Assertions.assertEquals(10, delegate.get(9));
        Assertions.assertEquals(11, delegate.get(10));
        Assertions.assertEquals(11, delegate.size());
    }
}
