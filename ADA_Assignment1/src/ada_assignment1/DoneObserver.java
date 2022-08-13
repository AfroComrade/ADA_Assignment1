/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ada_assignment1;

public class DoneObserver implements TaskObserver<Boolean>
{
    boolean done;
    public final ThreadPool.PooledThread thread;
    
    public DoneObserver(ThreadPool.PooledThread thread)
    {
        done = false;
        this.thread = thread;
    }

    
    @Override
    public void update(Boolean progress)
    {
        done = progress;
        if (done){
            synchronized(this)
            {
                notifyAll();
            }
        }
    }
}
