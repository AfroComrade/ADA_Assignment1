package ada_assignment1;

import java.util.LinkedList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Task<E, F> implements Runnable
{
    private E param;
    private LinkedList<TaskObserver<F>> listeners;
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
    
    public abstract void notifyAll(F progress);
    
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
    
    public class writerTask extends Task<String, Integer>
    {
        // Write a string 100 times to internal holder with 100mil sleep between each print
        Integer progress = 0;
        String totalString;
        
        public writerTask(String param)
        {
            super(param);
            totalString = "";
        }

        @Override
        public void notifyAll(Integer progress)
        {
            for (TaskObserver listener : listeners)
            {
                listener.update(progress, this);
            }
        }

        @Override
        public void run()
        {
            for (int i = 0; i < 100; i++)
            {
                totalString += param;
                try 
                {
                    Thread.sleep(100);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                progress++;
                if ((i % 5) == 0)
                {
                    notifyAll(progress);
                }
            }
            System.out.println(totalString);
        }
        
    }
}
