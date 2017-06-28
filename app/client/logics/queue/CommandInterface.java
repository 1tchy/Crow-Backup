package client.logics.queue;

public interface CommandInterface extends Runnable {

    /**
     * @return Der Typ zeigt an, was in diesem Command primär getan wird. Er wird verwendet um den korrekten Thread-Pool zu nutzen.
     */
    CommandType getType();

    /**
     * @return Die ungefähren Kosten dieses Kommands.
     * Bei CPU intensiven Commands sind es die ungefähren Anzahl Operationen und bei Disk und Network die Anzahl Bytes.
     */
    long getCost();

}
