package client.logic.queue;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
public class QueueAccessor {

    private final Map<CommandType, ExecutorService> executors;
    private final Map<CommandType, AtomicLong> queuedCosts;
    private final Map<CommandType, AtomicLong> runningCosts;

    public QueueAccessor() {
        this.executors = Collections.unmodifiableMap(new HashMap<CommandType, ExecutorService>() {
            {
                put(CommandType.CPU, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
                put(CommandType.DISK, Executors.newSingleThreadExecutor());
                put(CommandType.NETWORK, Executors.newFixedThreadPool(5));
            }
        });
        queuedCosts = Collections.unmodifiableMap(Arrays.stream(CommandType.values()).collect(Collectors.toMap(c -> c, c -> new AtomicLong(0))));
        runningCosts = Collections.unmodifiableMap(Arrays.stream(CommandType.values()).collect(Collectors.toMap(c -> c, c -> new AtomicLong(0))));
    }

    /**
     * Adds the command to the correct queue and executes it as soon as possible
     */
    public void queue(CommandInterface command) {
        runAndAdjustCosts(command, command.getType(), command.getCost());
    }

    private void runAndAdjustCosts(Runnable command, CommandType commandType, long costs) {
        queuedCosts.get(commandType).addAndGet(costs);
        executors.get(commandType).execute(() -> {
            queuedCosts.get(commandType).addAndGet(-costs);
            runningCosts.get(commandType).addAndGet(costs);
            command.run();
            runningCosts.get(commandType).addAndGet(-costs);
        });
    }

    public long getQueuedCosts(CommandType commandType, boolean includeRunning) {
        return queuedCosts.get(commandType).get() + (includeRunning ? runningCosts.get(commandType).get() : 0);
    }

    public void terminate(long timeout, TimeUnit unit) throws InterruptedException {
        for (ExecutorService executorService : executors.values()) {
            executorService.shutdown();
        }
        for (ExecutorService executorService : executors.values()) {
            executorService.awaitTermination(timeout, unit);
        }
    }

}
