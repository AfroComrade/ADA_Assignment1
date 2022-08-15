package ada_assignment1;

//import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool
{
    private static ThreadPool _instance;

    private volatile int currentSize;
    private volatile int numOfThreads;
    private volatile int currentUsed;

    private final LinkedBlockingQueue<Runnable> taskQueue;

    // Threads created here and added to the thread pool
    private ThreadPool(int initialSize) throws InterruptedException
    {
        numOfThreads = 0;
        taskQueue = new LinkedBlockingQueue<>();
        this.currentSize = initialSize;

        // This works by adding and starting threads at runtime
        // They won't be closed until later.
        for (int i = 0; i < initialSize; i++)
        {
            spawnThread();
        }
    }

    public static ThreadPool get()
    {
        if (_instance == null)
        {
            try
            {
                _instance = new ThreadPool(2);
            } catch (Exception e)
            {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        return _instance;
    }

    public int getSize()
    {
        return currentSize;
    }

    public int getAvailable()
    {
        return currentSize - currentUsed;
    }

    public void resize(int newSize)
    {
        currentSize = newSize;
        // DEBUG
        System.out.println("New threadsize: " + newSize + " change: (" + (newSize - numOfThreads) + ")");
        synchronized (taskQueue)
        {
            
            taskQueue.notifyAll();
        }
        while (numOfThreads < currentSize)
        {
            spawnThread();
        }
    }

    public void spawnThread()
    {
        synchronized (this)
        {
            WorkerThread thread = new WorkerThread();
            thread.start();
            numOfThreads++;
            this.notifyAll();
        }
    }

    public void destroyPool()
    {
        System.out.println("Pool destruction in progress");
        resize(0);
    }

    // boolean returns if there is currently a thread available to run the task
    public boolean performTask(Runnable task)
    {

        //if (!task.getClass().isInstance(Task.class)) // Broke the thread pool
        //    return false;
        
        try
        {
            synchronized (taskQueue)
            {
                taskQueue.put(task);
                taskQueue.notifyAll();
            }
        } catch (Exception ex)
        {
            Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        // currently used threads incremented by the pooled thread that runs the task
        return (currentUsed >= currentSize);
    }

    public class WorkerThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                while (numOfThreads <= currentSize)
                {
                    synchronized (taskQueue)
                    {
                        if (taskQueue.isEmpty())
                        {
                            // DEBUG
                            //System.out.println("Waiting");
                            taskQueue.wait();
                        }
                    }

                    if (!taskQueue.isEmpty())
                    {
                        Runnable task;
                        synchronized (taskQueue)
                        {
                            task = taskQueue.poll();
                        }

                        currentUsed++;
                        if (task != null)
                        {
                            task.run();
                        }
                        currentUsed--;
                    }
                }
                //System.out.println("Deleting. NumThreads:" + numOfThreads);
                numOfThreads--;
                //System.out.println("New threads: " + numOfThreads);
            } catch (Exception ex)
            {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
