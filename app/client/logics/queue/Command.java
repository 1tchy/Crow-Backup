package client.logics.queue;

public class Command implements CommandInterface {

    private final CommandType type;
    private final long cost;
    private final Runnable runnable;

    public Command(CommandType type, long cost, Runnable runnable) {
        this.type = type;
        this.cost = cost;
        this.runnable = runnable;
    }

    @Override
    public CommandType getType() {
        return type;
    }

    @Override
    public long getCost() {
        return cost;
    }

    @Override
    public void run() {
        runnable.run();
    }

}
