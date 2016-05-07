package ru.spbau.mit;

import java.util.function.Function;
import java.util.function.Supplier;

class LightFutureImpl<R> implements LightFuture<R>, Runnable {

    // Runnable performing result evaluation
    private final Runnable task;

    // Flag for ensuring that supplier evaluates only once
    private volatile boolean alreadyRun = false;

    // Value keeping any exception thrown by supplier
    private volatile Throwable thrownObject;

    // Object for syncing operations regarding result
    private final Object syncExecution = new Object();

    // Flag for if result is ready
    private volatile boolean isReady = false;

    private volatile R result;

    private ThreadPoolImpl threadPool;
    LightFuture<?> dependency = null;

    LightFutureImpl(Supplier<R> supplier, ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;

        task = () -> {
            try {
                result = supplier.get();
            } catch (Exception e) {
                thrownObject = e;
            }

            onCompletion();
        };
    }

    private <X> LightFutureImpl(LightFuture<X> dependency, Function<? super X, ? extends R> function,
                                ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;
        this.dependency = dependency;

        task = () -> {
            try {
                result = function.apply(dependency.get());
            } catch (Exception e) {
                thrownObject = e;
            }

            onCompletion();
        };
    }

    private void onCompletion() {
        synchronized (syncExecution) {
            isReady = true;
            // Notify all threads waiting for the result
            syncExecution.notifyAll();
        }
        synchronized (threadPool.syncDelayedTasks) {
            threadPool.syncDelayedTasks.notify();
        }
    }

    @Override
    public boolean isReady() {
        // Read operation is atomic because of `volatile` keyword
        return isReady;
    }

    @Override
    public R get() throws LightExecutionException, InterruptedException {
        synchronized (syncExecution) {
            // Wait until the result is ready
            while (!isReady) {
                syncExecution.wait();
            }
        }

        // If something was thrown during the execution then rethrow it as a light exception
        if (thrownObject != null) {
            throw new LightExecutionException(thrownObject);
        }

        return result;
    }

    @Override
    public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> function) {
        LightFutureImpl<U> continuation = new LightFutureImpl<>(this, function, this.threadPool);

        threadPool.delayTask(continuation);

        synchronized (threadPool.syncDelayedTasks) {
            threadPool.syncDelayedTasks.notify();
        }

        return continuation;
    }

    @Override
    public void run() {
        // Given task is being executed only once
        if (alreadyRun) {
            return;
        }

        alreadyRun = true;
        task.run();
    }
}
