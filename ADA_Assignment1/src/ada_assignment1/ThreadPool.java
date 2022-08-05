package ada_assignment1;

//import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool
{
    private int currentSize;
    private int currentUsed;
    
    // Might need to go over this. What happens if a thread deep in the queue is done? 
    // Do we dequeue until we reach the thread that's done?
    // Then enqueue the dequeued threads upto that point?
    private final LinkedBlockingQueue<Thread> workerQueue;
    private final LinkedBlockingQueue<Task> taskQueue;

    // Threads created here and added to the thread pool
    public ThreadPool(int initialSize) throws InterruptedException {
        currentUsed = 0;
        workerQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < initialSize; i++)
        {
            //threadQueue.put;
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
    
    public void resize(int newSize) {
        if (currentSize > newSize) {
            while (!workerQueue.isEmpty() && workerQueue.size() > newSize)
            {
                workerQueue.remove();
            }
        }
        else
        {
            while (workerQueue.size() < newSize)
            {
                //threadQueue.add(new Thread());
            }
        }
        currentSize = newSize;
    }
    
    public void destroyPool() {
        workerQueue.clear();
        currentUsed = 0;
    }
    
    // When a thread finishes, remember not to destroy it but to stop it and return it to the number of
    //  available threads for use
    public boolean performTask(Runnable task) {
        try
        {
            taskQueue.put(new Task(task));
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (currentUsed >= currentSize) {
        return false;
        }
        
        //taskQueue.poll().
        currentUsed++;
        //threadQueue.poll().
        
        return true;
    }
}
