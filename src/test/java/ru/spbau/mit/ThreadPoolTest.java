package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    private class Barrier {
        private int parties;

        Barrier(int parties) {
            this.parties = parties;
        }

        synchronized void await() {
            if (parties > 1) {
                parties -= 1;
                while (parties > 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (parties == 1) {
                synchronized (this) {
                    parties -= 1;
                    this.notifyAll();
                }
            }
        }
    }

    @Test
    public void testNumberOfThreads() {
        final int numberOfThreads = 25;
        final ThreadPoolImpl pool = new ThreadPoolImpl(numberOfThreads);

        // This set should contain `numberOfThreads` unique elements
        Set<String> threadNames = new HashSet<>();

        // Ensure that main thread won't proceed until all tasks have been completed
        final Object syncMain = new Object();
        final AtomicInteger finishedThreadsCount = new AtomicInteger(0);

        // Ensures that threads from the pool get blocked one by one
        Barrier barrier = new Barrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            pool.submit(() -> {
                barrier.await();

                threadNames.add(Thread.currentThread().getName());

                finishedThreadsCount.incrementAndGet();

                synchronized (syncMain) {
                    syncMain.notify();
                }

                return null;
            });
        }

        while (finishedThreadsCount.get() < numberOfThreads) {
            try {
                synchronized (syncMain) {
                    syncMain.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(threadNames.size(), numberOfThreads);

        pool.shutdown();
    }

    @Test
    public void testBasic() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(1);
        final int result = 124750;

        final IntStream stream = IntStream.range(1, 500);

        LightFuture<Integer> future = pool.submit(() -> stream.reduce((a, b) -> a + b).orElse(0));

        // Operation should take long enough for main thread to reach this assertion
        assertFalse(future.isReady());
        assertEquals(result, (int) future.get());

        // Execution won't reach this assertion until task will be ready
        assertTrue(future.isReady());

        pool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void testExceptionThrow() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(1);

        LightFuture<Integer> future = pool.submit(() -> {
            throw new UnsupportedOperationException();
        });

        future.get();
        pool.shutdown();
    }

    @Test
    public void testMany() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(50);

        final int numberOfTasks = 100;
        final int counterResult = 70;

        final int sufficientTime = 10;

        List<LightFuture<Integer>> futures = IntStream.range(0, numberOfTasks).
                mapToObj(idx -> pool.submit(() -> {
                    int result = 0;
                    for (int i = 0; i < counterResult; i++) {
                        result += 1;
                        try {
                            Thread.sleep(sufficientTime);
                        } catch (InterruptedException e) {
                            // Ignore this
                        }
                    }
                    return result;
                })).collect(Collectors.toList());

        for (LightFuture<Integer> future : futures) {
            assertEquals(counterResult, (long) future.get());
        }

        pool.shutdown();
    }

    @Test
    public void testAndThen() throws LightExecutionException, InterruptedException {
        final ThreadPoolImpl pool = new ThreadPoolImpl(2);

        final int sufficientTime = 500;

        final StringBuilder builder = new StringBuilder();

        final String builderResult = "acbd";

        List<LightFuture<Void>> futures = new ArrayList<>();

        futures.add(pool.submit(() -> {
            // Executes right away
            builder.append('a');
            return null;
        }));

        futures.add(pool.submit(() -> {
            try {
                // Sleeps a sufficient number of milliseconds to let the first and third tasks finish
                Thread.sleep(sufficientTime);
            } catch (InterruptedException e) {
                // Ignore this
            }

            builder.append('b');
            return null;
        }));

        futures.add(futures.get(0).thenApply(x -> {
            // Executes right away because the first one executes right away
            builder.append('c');
            return null;
        }));

        futures.add(futures.get(1).thenApply(x -> {
            // The point is that "d" won't be appended even if it doesn't wait explicitly.
            // Delay in the second task execution postpones this.
            builder.append('d');
            return null;
        }));

        for (LightFuture<Void> future : futures) {
            future.get();
        }

        assertEquals(builderResult, builder.toString());

        pool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void testAndThenThrow() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl pool = new ThreadPoolImpl(2);

        LightFuture<Integer> future = pool.submit(() -> {
            throw new UnsupportedOperationException();
        });

        LightFuture<String> continuation = future.thenApply(Object::toString);

        // This call should rethrow an exception
        continuation.get();

        // This won't be executed
        pool.shutdown();
    }
}
