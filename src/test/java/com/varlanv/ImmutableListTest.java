package com.varlanv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ImmutableListTest {

    @RepeatedTest(50)
    void add_concurrent__from_array() {
        var random = new Random();
        var parallelism = random.nextInt(2, 50);
        var repetitions = random.nextInt(2, 50);
        var initialSize = random.nextInt(0, 100);
        var array = IntStream.rangeClosed(1, initialSize).boxed().toArray(Integer[]::new);
        var result = ConcurrentSpec.spec(parallelism, () -> ImmutableList.ofArray(array))
                .repeatedAction(repetitions, (subject, idx) -> {
                    var res = subject.add(idx);
                    Assertions.assertEquals(initialSize, subject.size());
                    Assertions.assertEquals(List.of(array), subject.copyTo(ArrayList::new));
                    var actual = res.copyTo(ArrayList::new);
                    var expected = Stream.of(List.of(array), List.of(idx)).flatMap(Collection::stream).toList();
                    Assertions.assertEquals(initialSize + 1, res.size());
                    Assertions.assertEquals(initialSize + 1, actual.size());
                    Assertions.assertEquals(expected, actual);
                })
                .start();
        Assertions.assertEquals(initialSize, result.size());
        Assertions.assertEquals(List.of(array), result.copyTo(ArrayList::new));
    }

    @RepeatedTest(50)
    void add_concurrent__from_added() {
        var random = new Random();
        var parallelism = random.nextInt(2, 50);
        var repetitions = random.nextInt(2, 50);
        var initialSize = random.nextInt(0, 100);
        var array = IntStream.range(0, initialSize).boxed().toArray(Integer[]::new);
        var subjectBefore = ImmutableList.empty();
        for (var i = 0; i < initialSize; i++) {
            subjectBefore = subjectBefore.add(i);
        }
        var subjectFinal = subjectBefore;
        var result = ConcurrentSpec.spec(parallelism, () -> subjectFinal)
                .repeatedAction(repetitions, (subject, idx) -> {
                    var res = subject.add(idx);
                    Assertions.assertEquals(initialSize, subject.size());
                    Assertions.assertEquals(List.of(array), subject.copyTo(ArrayList::new));
                    var actual = res.copyTo(ArrayList::new);
                    var expected = Stream.of(List.of(array), List.of(idx)).flatMap(Collection::stream).toList();
                    Assertions.assertEquals(initialSize + 1, res.size());
                    Assertions.assertEquals(initialSize + 1, actual.size());
                    Assertions.assertEquals(expected, actual);
                })
                .start();
        Assertions.assertEquals(initialSize, result.size());
        Assertions.assertEquals(List.of(array), result.copyTo(ArrayList::new));
    }

    @Test
    void copyTo__ofEmpty__returns_empty() {
        var actual = ImmutableList.empty().copyTo(ArrayList::new);

        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void drainTo__of_1__returns_1() {
        var actual = ImmutableList.of(1).copyTo(ArrayList::new);

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(1, actual.get(0));
    }

    @Test
    void drainTo__of_1__called_many_times__always_returns_1() {
        var actual = ImmutableList.of(1);

        IntStream.rangeClosed(1, 20).forEach(it -> {
            var list = actual.copyTo(ArrayList::new);
            Assertions.assertEquals(1, actual.size());
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals(1, list.get(0));
        });

    }

    @Test
    void toImmutableList__from_empty_stream__should_be_empty() {
        var actual = Stream.of().collect(ImmutableList.toImmutableList());
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    void toImmutableList__from_stream_of_11__should_contain_11() {
        var actual = IntStream.rangeClosed(0, 10).boxed().collect(ImmutableList.toImmutableList());
        var result = actual.copyTo(ArrayList::new);
        Assertions.assertEquals(11, actual.size());
        Assertions.assertEquals(0, result.get(0));
        Assertions.assertEquals(1, result.get(1));
        Assertions.assertEquals(2, result.get(2));
        Assertions.assertEquals(3, result.get(3));
        Assertions.assertEquals(4, result.get(4));
        Assertions.assertEquals(5, result.get(5));
        Assertions.assertEquals(6, result.get(6));
        Assertions.assertEquals(7, result.get(7));
        Assertions.assertEquals(8, result.get(8));
        Assertions.assertEquals(9, result.get(9));
        Assertions.assertEquals(10, result.get(10));
    }

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
    void add_1_then_add_iterable_3__forEach__size_should_be_4() {
        var actual = ImmutableList.<Integer>empty()
                .add(1)
                .addIterable(List.of(2, 3, 4))
                .size();

        Assertions.assertEquals(4, actual);
    }

    @Test
    void add_1_then_add_iterable_3__then_2_forEach__size_should_be_4() {
        var actual = ImmutableList.<Integer>empty()
                .add(1)
                .addIterable(List.of(2, 3, 4))
                .add(5)
                .add(6)
                .size();

        Assertions.assertEquals(6, actual);
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

    @Test
    void add_11__size__should_equal_11() {
        var actual = ImmutableList.<Integer>empty()
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
                .size();

        Assertions.assertEquals(11, actual);
    }

    static final class ConcurrentSpec<SUBJECT> {

        private final int parallelism;
        private final Supplier<SUBJECT> subjectSupplier;
        private final List<Map.Entry<Integer, ObjIntConsumer<SUBJECT>>> actions;

        private ConcurrentSpec(int parallelism, Supplier<SUBJECT> subjectSupplier) {
            this.parallelism = parallelism;
            this.subjectSupplier = subjectSupplier;
            this.actions = new ArrayList<>();
        }

        static <SUBJECT> ConcurrentSpec<SUBJECT> spec(int parallelism, Supplier<SUBJECT> subjectSupplier) {
            return new ConcurrentSpec<>(parallelism, subjectSupplier);
        }

        ConcurrentSpec<SUBJECT> action(Consumer<SUBJECT> action) {
            actions.add(Map.entry(1, (subjet, idx) -> action.accept(subjet)));
            return this;
        }

        ConcurrentSpec<SUBJECT> repeatedAction(int repetitions, ObjIntConsumer<SUBJECT> action) {
            actions.add(Map.entry(repetitions, action));
            return this;
        }

        SUBJECT start() {
            try {
                var subject = subjectSupplier.get();
                var tasksBarrier = new CyclicBarrier(actions.size());
                var executorService = Executors.newFixedThreadPool(parallelism);
                try {
                    for (var action : actions) {
                        executorService.submit(() -> {
                            try {
                                var repetitions = action.getKey();
                                var task = action.getValue();
                                tasksBarrier.await();
                                if (repetitions.equals(1)) {
                                    task.accept(subject, 1);
                                } else {
                                    var barrier = new CyclicBarrier(repetitions);
                                    for (var i = 0; i < repetitions; i++) {
                                        var iFinal = i;
                                        executorService.submit(() -> {
                                            try {
                                                barrier.await();
                                                task.accept(subject, iFinal);
                                            } catch (InterruptedException | BrokenBarrierException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    executorService.shutdown();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        throw new TimeoutException("Failed to terminate executor service in 5 seconds");
                    }
                    return subject;
                } finally {
                    executorService.shutdown();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
