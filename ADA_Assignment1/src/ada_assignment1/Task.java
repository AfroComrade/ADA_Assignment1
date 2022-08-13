package ada_assignment1;

import java.util.LinkedList;

public abstract class Task<E, F> implements Runnable
{
    public E param;
    private LinkedList<TaskObserver<F>> listeners;
    protected TaskId id;
    
    public Task(E param) {
        this.param = param;
        id = UniqueIdentifier.get().assignId();
        listeners = new LinkedList<>();
    }
    
    public int getId() {
        return id.getIdNumber();
    }

    // Note: F represents update/output value that can be changed for other programs
    
    // Another class should handle this, and get back a unique id. That other class can handle ids
    // Note, whatever other class does this, needs to have a mutex algorithm for multiple threads
    // That class also just needs one instance. Singleton pattern. And a mutex on that singleton pattern
    
    // Also maybe not just an int, but something else. Like an ID clas, and a TaskIdentifier class to provide the ID
    
    public void notifyAll(F progress)
    {
        if (listeners.isEmpty())
            return;
        
        for (TaskObserver listener : listeners)
        {
            listener.update(progress);
        }
    }

    
    // This is for the observer pattern. 
    // Task Observer
    public void addListener(TaskObserver<F> o) {
        listeners.add(o);
    }
    
    public void removeListener(TaskObserver<F> o) {
        listeners.remove(o);
    }
    
    @Override
    public abstract void run();
}

    

