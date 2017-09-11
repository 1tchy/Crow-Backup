package client.viewmodels;

import java.util.Observable;
import java.util.Observer;

public class ObservableWrapper<T> extends Observable {
    private T obj;

    public void set(T obj) {
        this.obj = obj;
        setChanged();
        notifyObservers(obj);
    }

    public T get() {
        return obj;
    }

    public synchronized void addObserver(Observer o, boolean invokeIfNotEmpty) {
        super.addObserver(o);
        if (invokeIfNotEmpty && obj != null) {
            o.update(this, obj);
        }
    }
}
