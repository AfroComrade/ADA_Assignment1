package ada_assignment1;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool
{
    private int currentSize;
    private volatile int currentUsed;
    
    // Switched to arraylist so we can better access the threads in the future
    private final ArrayList<PooledThread> workers;
    private final LinkedBlockingQueue<Runnable> taskQueue;

    // Threads created here and added to the thread pool
    public ThreadPool(int initialSize) throws InterruptedException 
    {
        currentUsed = 0;
        workers = new ArrayList();
        
        // This works by adding and starting threads at runtime
        // They won't be closed until later.
        for (int i = 0; i < initialSize; i++)
        {
            PooledThread thread = new PooledThread();
            workers.add(thread);
            thread.start();
        }
        taskQueue = new LinkedBlockingQueue<>();
        
        this.currentSize = initialSize;
    }
    
    public int getSize() {
        return currentSize;
    }
    
    public int getAvailable() {
        return currentSize - currentUsed;
    }
    
    public void resize(int newSize) 
    {
        if (currentSize > newSize) 
        {
            while (workers.size() > newSize)
            {
                PooledThread worker = workers.remove(0);
                worker.stopRequested = true;
            }
        }
        else
        {
            while (workers.size() < newSize)
            {
                PooledThread thread = new PooledThread();
                workers.add(thread);
                thread.start();
            }
        }
        currentSize = newSize;
    }
    
    public void destroyPool() {
        for (PooledThread thread : workers)
        {
            thread.stopRequested = true;
        }
        workers.clear();
        currentUsed = 0;
        currentSize = 0;
    }
    
    // boolean returns if there is currently a thread available to run the task
    public boolean performTask(Runnable task) {
        try
        {
            taskQueue.put(task);
        } 
        catch (Exception ex)
        {
            Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        // currently used threads incremented by the pooled thread that runs the task
        return (currentUsed >= currentSize);
    }
    
    // This is going to keep attempting to run tasks from the queue until stop is requested
    // Wait if there is no task in the queue
    private class PooledThread extends Thread
    {
        // Keep a separate stop requested so we can stop individual threads if we need to resize
        boolean stopRequested = false;
        
        @Override
        public void run()
        {
            try {
                while (!stopRequested)
                {
                    if (taskQueue.isEmpty())
                        wait();
                    else if (!stopRequested && !taskQueue.isEmpty())
                    {
                        currentUsed++;
                        taskQueue.poll().run();
                        currentUsed--;
                    }
                }
            } catch (Exception ex)
            {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
