package ada_assignment1;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Task<E, F> implements Runnable
{
    private E param;
    private TaskId id;
    
    public Task(E param) {
        this.param = param;
        id = UniqueIdentifier.get().assignId();
    }
    
    public int getId() {
        return id.getIdNumber();
    }

    // Note: F represents update/output value that can be changed for other programs
    
    // Another class should handle this, and get back a unique id. That other class can handle ids
    // Note, whatever other class does this, needs to have a mutex algorithm for multiple threads
    // That class also just needs one instance. Singleton pattern. And a mutex on that singleton pattern
    
    // Also maybe not just an int, but something else. Like an ID clas, and a TaskIdentifier class to provide the ID
    
    public void notifyAll(F progress) {
        throw new UnsupportedOperationException();
    }
    
    // This is for the observer pattern. 
    // Task oBSERVER
    public void addListener(TaskObserver<F> o) {
        throw new UnsupportedOperationException();
    }
    
    public void removeListener(TaskObserver<F> o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void run()
    {
        do
        {
            try
            {
                wait();
                
            } catch (InterruptedException ex)
            {
                Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (true);
    }
    
}
