/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ada_assignment1;

/**
 *
 * @author Yeran
 */
public class WriterTask extends Task<String, Boolean>
{
    // Write a string 100 times to internal holder with 100mil sleep between each print

    String totalString;

    public WriterTask(String param)
    {
        super(param);
        totalString = "";
    }

    @Override
    public void run()
    {
        for (int i = 0; i < 3; i++)
        {
            totalString += this.param;
            System.out.println(totalString);
            try
            {
                Thread.sleep(500);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {
        WriterTask wt = new WriterTask("Hello");
        
        ThreadPool pool = ThreadPool.get();
        
        pool.performTask(wt);
        
        for (int i = 0; i < 4; i++)
        {
            pool.performTask(new WriterTask("task:"+i+", "));
        }
        System.out.println(pool.getAvailable());
        
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(pool.getAvailable());
        
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Getavail: " + pool.getAvailable());
        
        try{
            Thread.sleep(6000);
        } catch (Exception e)
        {
            
        }
        
        System.out.println("Resize:");
        
        pool.resize(4);
        
        for (int i = 5; i < 11; i++)
        {
            pool.performTask(new WriterTask("task:"+i+", "));
        }

        
        try{
            Thread.sleep(6000);
        } catch (Exception e)
        {
            
        }

        
        System.out.println("Dest");
        pool.destroyPool();
        

    }

}
