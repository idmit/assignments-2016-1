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

    // Object for syncing operations regarding result access
    private final Object syncExecution = new Object();

    // Flag for if result is ready
    private volatile boolean isReady = false;

    private volatile R result;

    // ThreadPool instance that produced this LightFuture instance
    // It's needed for continuation construction
    private final ThreadPoolImpl threadPool;

    // Reference to a dependency if this LightFuture instance is a continuation
    private LightFuture<?> dependency = null;

    LightFutureImpl(Supplier<R> supplier, ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;

        task = () -> {
            try {
                result = supplier.get();
            } catch (Exception e) {
                // Keep an exception to rethrow
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
                // Thread won't wait here for get() because this task
                // doesn't get run before its dependency is ready
                result = function.apply(dependency.get());
            } catch (Exception e) {
                // Keep an exception to rethrow
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
        synchronized (threadPool.getSyncDelayedTasks()) {
            // If this task is a dependency then
            // a thread promoting delayed tasks should be notified
            // Maybe it can promote something
            threadPool.getSyncDelayedTasks().notify();
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

        // If `this` task is ready, promoting thread won't be notified about this
        // So notify it just in case
        synchronized (threadPool.getSyncDelayedTasks()) {
            threadPool.getSyncDelayedTasks().notify();
        }

        return continuation;
    }

    LightFuture<?> getDependency() {
        return dependency;
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
