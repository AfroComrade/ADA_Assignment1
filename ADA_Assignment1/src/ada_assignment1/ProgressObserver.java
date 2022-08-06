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
    int avgProgress;
    
    Runnable current;
    
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
    
    public void printProgress()
    {
        System.out.println("Average progress = " + avgProgress);
    }
    
    public void update(Integer progress, Runnable run)
    {
        current = run;
        update(progress);
    }
    
    @Override
    public void update(Integer progress)
    {
        runnables.replace(current, progress);
        
        avgProgress = 0;
        
        for (Runnable task : runnables.keySet())
        {
            avgProgress += runnables.get(task);
        }
        
        avgProgress = avgProgress/numRunners;
        current = null;
    }
}
