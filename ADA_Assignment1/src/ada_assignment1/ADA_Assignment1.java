package ada_assignment1;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ADA_Assignment1
{

    public static void main(String[] args)
    {
        ThreadPool.get();
        
        for (int k = 0; k < 50; k++)
        {
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
                Thread.sleep(5000);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(ADA_Assignment1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
}
