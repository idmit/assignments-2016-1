package ru.spbau.mit;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ThreadPoolImpl implements ThreadPool {

    // Flag for if thread pool is still working
    private volatile boolean working = true;

    // Object for syncing operations with queue of waiting tasks and its size
    private final Object syncWorkingTasks = new Object();

    // Tasks placed into this queue get removed as soon as there is a free thread in this pool
    private Queue<LightFutureImpl<?>> waitingTasks = new LinkedList<>();
    private volatile int waitingTasksNumber = 0;

    // Object for syncing operations with list of delayed tasks
    // It is separate from list itself beacause it can be accessed from outside
    private final Object syncDelayedTasks = new Object();

    // Tasks placed into this list get removed as soon as their dependency is ready
    private final Queue<LightFutureImpl<?>> delayedTasks = new LinkedList<>();

    /**
     * Slot instance is trying to get a task waiting in the queue
     * It stops trying when ThreadPool gets shut down
     */
    private class Slot implements Runnable {
        @Override
        public void run() {
            // While thread pool is working, ask for waiting tasks
            while (ThreadPoolImpl.this.working) {
                try {
                    // If there are no tasks waiting then thread waits to fill the slot
                    // It will continue if new task has been submitted or thread pool got shut down
                    LightFutureImpl<?> task = getWaitingTask();

                    // Start running task if thread pool hasn't been shut down
                    if (task != null) {
                        task.run();
                    }
                } catch (InterruptedException e) {
                    // Ignore this
                }
            }
        }
    }

    public ThreadPoolImpl(int n) {
        // Create `n` threads with corresponding slots
        IntStream.range(0, n).forEach((x) -> new Thread(new Slot()).start());

        // Start a promoting thread
        // It continues to work until thread pool is shut down
        // It waits and gets notified only if some task has been completed or
        // if a continuation has been created
        (new Thread(() -> {
            // While thread pool is working, promote delayed tasks to waiting tasks
            while (ThreadPoolImpl.this.working) {
                try {
                    // Iterate through delayed tasks and check if they are ready to evaluate
                    synchronized (syncDelayedTasks) {
                        syncDelayedTasks.wait();

                        Iterator<LightFutureImpl<?>> it = delayedTasks.iterator();
                        while (it.hasNext()) {
                            LightFutureImpl<?> delayedTask = it.next();
                            if (delayedTask.getDependency().isReady()) {
                                it.remove();
                            }
                            queueTask(delayedTask);
                        }
                    }
                } catch (InterruptedException e) {
                    // Ignore this
                }
            }
        })).start();
    }

    private <R> void queueTask(LightFutureImpl<R> task) {
        synchronized (syncWorkingTasks) {
            // Queue submitted task
            waitingTasks.add(task);
            // Increment is thread-safe because of `synchronized` block, not because of `volatile` keyword
            waitingTasksNumber += 1;

            // Notify the first thread that is waiting for a task to be queued
            syncWorkingTasks.notify();
        }
    }

    Object getSyncDelayedTasks() {
        return syncDelayedTasks;
    }

    <R> void delayTask(LightFutureImpl<R> task) {
        synchronized (syncDelayedTasks) {
            // Queue delayed task
            delayedTasks.add(task);
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        // Thread pool doesn't accept tasks, if it has been shut down
        if (!working) {
            return null;
        }

        // Create a future instance to return
        LightFutureImpl<R> task = new LightFutureImpl<>(supplier, this);

        queueTask(task);

        // Return future as a valid result
        return task;
    }

    @Override
    public void shutdown() {
        // Write operation is atomic
        this.working = false;

        synchronized (syncWorkingTasks) {
            // Notify all threads waiting for tasks to be queued
            syncWorkingTasks.notifyAll();
        }
    }

    private LightFutureImpl<?> getWaitingTask() throws InterruptedException {
        // Acquire exclusive access to queue and counter
        synchronized (syncWorkingTasks) {
            // If there are no waiting tasks, thread waits
            // Thread will be notified on thread pool shutting down or on task queueing
            while (this.working && waitingTasksNumber == 0) {
                syncWorkingTasks.wait();
            }

            // If thread is notified, but thread pool has been already shut down, no tasks are available
            if (!this.working) {
                return null;
            }

            // Otherwise thread gets to fill it's slot with a task
            // Decrement is thread-safe because of `synchronized` block, not because of `volatile` keyword
            waitingTasksNumber -= 1;
            return waitingTasks.poll();
        }
    }
}
