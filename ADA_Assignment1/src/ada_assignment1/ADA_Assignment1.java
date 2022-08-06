package ada_assignment1;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ADA_Assignment1
{

    public static void main(String[] args)
    {
        ThreadPool.get();
        ThreadPool.get().performTask(new Task(0)
        {
            
            
            @Override
            public void run()
            {
                addListener(new ConcreteObsever());
                
                for (int i =0; i < 100; ++i)
                {
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException ex)
                    {
                        Logger.getLogger(ADA_Assignment1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println(i);
                    notifyAll(i);
                }
            }
        });
        
        
    }
    
}
