package ada_assignment1;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Task<E, F> implements Runnable
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
        
    public void notifyAll(F progress) {
        throw new UnsupportedOperationException();
    }
    
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
