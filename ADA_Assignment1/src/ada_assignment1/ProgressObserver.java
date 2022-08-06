package ada_assignment1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Yeran
 */
public class ProgressObserver implements TaskObserver<Integer>
{
    HashMap<Runnable, Integer> runnables;
    private static ProgressObserver instance;
    int numRunners;
    int totalProgress;
    
    private ProgressObserver()
    {
        runnables = new HashMap<Runnable, Integer>();
        numRunners = 0;
    }
    
    public void addRunnable(Runnable runnable)
    {
        if (instance == null)
            instance = new ProgressObserver();
        
        runnables.put(runnable, 0);
        numRunners++;
    }
    
    @Override
    public void update(int progress, Runnable runnable)
    {
        runnables.replace(runnable, progress);
        
        int avgProgress = 0;
        
        for (Runnable task : runnables.keySet())
        {
            avgProgress += runnables.get(task);
        }
        
        avgProgress = avgProgress/numRunners;
        System.out.println("Average progress = " + avgProgress);
        
    }
}
