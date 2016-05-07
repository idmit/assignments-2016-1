package ru.spbau.mit;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by idmit on 07/05/16.
 */
class LightFutureImpl<R> implements LightFuture<R>, Runnable {

    // Supplier returning result
    private final Supplier<R> supplier;

    // Flag for ensuring that supplier evaluates only once
    private boolean alreadyRun = false;

    // Value keeping any exception thrown by supplier
    private volatile Throwable thrownObject;

    // Object for syncing operations regarding result
    private final Object syncExecution = new Object();

    // Flag for if result is ready
    private volatile boolean isReady = false;

    private volatile R result;

    LightFutureImpl(Supplier<R> supplier) {
        this.supplier = supplier;
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
    public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
        return null;
    }

    @Override
    public void run() {
        // Given task is being executed only once
        if (alreadyRun) {
            return;
        }

        alreadyRun = true;

        try {
            result = supplier.get();
        } catch (Exception e) {
            thrownObject = e;
        }

        synchronized (syncExecution) {
            isReady = true;
            // Notify all threads waiting for the result
            syncExecution.notifyAll();
        }
    }
}
