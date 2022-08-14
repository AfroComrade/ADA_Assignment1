package ada_assignment1;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ADA_Assignment1
{

    public static void main(String[] args)
    {
        int size = 1;
        ThreadPool.get().resize(size);
        
        for (int k = 0; k < 50; k++)
        {
            ThreadPool.get().performTask(new Task(1)
            {


                @Override
                public void run()
                {
                    addListener(new ConcreteObserver());

                    for (int i =0; i < 100; ++i)
                    {
                        try
                        {
                            Thread.sleep(20);
                        } catch (InterruptedException ex)
                        {
                            Logger.getLogger(ADA_Assignment1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("TaskID: " + this.getId() + " Count: " + i);
                        notifyAll(i);
                    }
                }
            });
            
            try
            {
                Thread.sleep(200);
                ThreadPool.get().resize(++size);

                
            } catch (InterruptedException ex)
            {
                Logger.getLogger(ADA_Assignment1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
}
