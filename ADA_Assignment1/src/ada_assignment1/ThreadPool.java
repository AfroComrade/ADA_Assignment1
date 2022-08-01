package ada_assignment1;

import java.util.ArrayList;

public class ThreadPool
{
    public int currentSize;
    public int currentUsed;
    public ArrayList<Thread> threads;
    
    public ThreadPool(int initialSize) {
        this.currentSize = initialSize;
        currentUsed = 0;
        threads = new ArrayList<Thread>();
    }
    
    public int getSize() {
        return currentSize;
    }
    
    public int getAvailable() {
        return currentSize - currentUsed;
    }
    
    public void resize(int newSize) {
        currentSize = newSize;
    }
    
    public void destroyPool() {
        
    }
    
    public boolean performTask(Runnable task) {
        if (currentUsed >= currentSize)
            return false;
        
        currentUsed++;
        threads.add(new Thread(task));
        threads.get(currentUsed).start();
        return true;
    }
}
