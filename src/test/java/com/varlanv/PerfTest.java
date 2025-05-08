package com.varlanv;

import io.vavr.collection.Array;
import io.vavr.collection.Stream;
import io.vavr.collection.Vector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class PerfTest {

    static final List<Integer> list = IntStream.range(0, 100_000).boxed().toList();
    ImmutableList<Integer> immutableList = ImmutableList.ofIterable(list);

    @Test
    void native_add_one_big_list() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("immutable")
                        .withWarmupCycles(50)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = new ArrayList<>(List.of(1));
                            integers.add(2);
                            integers.add(3);
                            integers.add(4);
                            integers.add(5);
                            integers.add(6);
                            integers.addAll(list);
                            integers.forEach(item -> counter.incrementAndGet());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
//        Result{average=477.07μs, min=0ns, max=3.14ms}
    }

    @Test
    void immutable_add_one_big_list() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("immutable")
                        .withWarmupCycles(50)
                        .withIterations(500)
                        .withAction(() -> {
                            ImmutableList.of(1)
                                    .add(2)
                                    .add(3)
                                    .add(4)
                                    .add(5)
                                    .add(6)
                                    .addIterable(list)
                                    .forEach(item -> counter.incrementAndGet());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
//        Result{average=296.82μs, min=0ns, max=361.65μs}
    }

    @Test
    void vavr_add_one_big_list() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("immutable")
                        .withWarmupCycles(50)
                        .withIterations(500)
                        .withAction(() -> {
                            Vector.of(1)
                                    .append(2)
                                    .append(3)
                                    .append(4)
                                    .append(5)
                                    .append(6)
                                    .appendAll(list)
                                    .forEach(item -> counter.incrementAndGet());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // Result{average=700.27μs, min=0ns, max=3.45ms}
    }

    @Test
    void immutable_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("immutable")
                        .withWarmupCycles(100)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = ImmutableList.of(1);
                            for (var i : list) {
                                integers = integers.add(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=633.78ms
    }

    @Test
    void native_arraylist_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("native")
                        .withWarmupCycles(100)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = new ArrayList<>(List.of(1));
                            for (var i : list) {
                                integers.add(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=517.37μs
    }

    @Test
    void native_linked_list_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("native")
                        .withWarmupCycles(100)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = new LinkedList<>(List.of(1));
                            for (var i : list) {
                                integers.add(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=341.12μs
    }

    @Test
    void vavr_vector_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("vavr")
                        .withWarmupCycles(50)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = Vector.of(1);
                            for (var i : list) {
                                integers = integers.append(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=7.43ms
    }

    @Test
    void vavr_array_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("vavr array")
                        .withWarmupCycles(2)
                        .withIterations(2)
                        .withAction(() -> {
                            var integers = Array.of(1);
                            for (var i : list) {
                                integers = integers.append(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=2.38s
    }

    @Test
    void vavr_stream_add_one_element_many_times() {
        var counter = new AtomicInteger();
        Bench.bench().addSubject(spec -> spec
                        .named("vavr array")
                        .withWarmupCycles(50)
                        .withIterations(500)
                        .withAction(() -> {
                            var integers = Stream.of(1);
                            for (var i : list) {
                                integers = integers.append(i);
                            }
                            counter.addAndGet(integers.size());
                        }))
                .runAndPrintResult();
        System.out.println(counter);
        // average=5.56ms
    }
}
