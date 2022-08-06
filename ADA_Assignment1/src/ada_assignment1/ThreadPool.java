package ada_assignment1;

//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool
{
    private static ThreadPool _instance;

    private int currentSize;
    private volatile int currentUsed;

    // Switched to arraylist so we can better access the threads in the future
    private final ArrayList<WorkerThread> workers;
    private final LinkedBlockingQueue<WorkerThread> workerQueue;
    private final LinkedBlockingQueue<Task> taskQueue;

    // Threads created here and added to the thread pool
    public ThreadPool(int initialSize) throws InterruptedException
    {
        currentUsed = 0;
        workers = new ArrayList();
        workerQueue = new LinkedBlockingQueue<>();
        taskQueue = new LinkedBlockingQueue<>();

        // This works by adding and starting threads at runtime
        // They won't be closed until later.
        for (int i = 0; i < initialSize; i++)
        {
            WorkerThread thread = new WorkerThread();
            workers.add(thread);
            //workerQueue.add(thread);
            thread.start();
        }


        this.currentSize = initialSize;
    }

    public static ThreadPool get()
    {
        if (_instance == null)
        {
            try
            {
                _instance = new ThreadPool(20);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return _instance;
    }

    public void enqueueWorkerThread(Thread t)
    {
        workerQueue.add((WorkerThread)t);
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
        if (currentSize > newSize)
        {
            while (workers.size() > newSize)
            {
                WorkerThread worker = workers.remove(0);
                worker.stopRequested = true;
            }
        } else
        {
            while (workerQueue.size() < newSize)
            {
                WorkerThread thread = new WorkerThread();
                workerQueue.add(thread);
                thread.start();
            }
        }
        currentSize = newSize;
    }

    public void destroyPool()
    {
        for (WorkerThread thread : workers)
        {
            thread.stopRequested = true;
        }
        workers.clear();
        workerQueue.clear();
        currentUsed = 0;
        currentSize = 0;
    }

    // boolean returns if there is currently a thread available to run the task
    public boolean performTask(Task task)
    {

        try
        {
            taskQueue.put(task);
            if (!workerQueue.isEmpty())
            {
                synchronized (workerQueue.peek().monitor)
                {
                    workerQueue.poll().monitor.notify();
                }
                //notifyAll();
                return true;
            }
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    // This is going to keep attempting to run tasks from the queue until stop is requested
    // Wait if there is no task in the queue
    private class WorkerThread extends Thread
    {
        final public Object monitor = new Object();
        // Keep a separate stop requested so we can stop individual threads if we need to resize
        boolean stopRequested = false;

        @Override
        public void run()
        {
                while (!stopRequested)
                {
                    if (taskQueue.isEmpty())
                    {
                        try
                        {
                            synchronized (monitor)
                            {
                                System.out.println("Waiting workerThread: " + this.getId());
                                workerQueue.add(this);
                                monitor.wait();
                                System.out.println("Waking up workerThread: " + this.getId());
                            }
                        } catch (Exception ex)
                        {
                            Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else
                    {
                        currentUsed++;
                        taskQueue.poll().run();
                        currentUsed--;
                        
                       // if (!stopRequested)
                        //    workerQueue.add(this);
                    }
                }
        }
    }

}
