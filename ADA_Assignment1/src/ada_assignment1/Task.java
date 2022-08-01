package ada_assignment1;

public class Task<E, F> implements Runnable
{
    E e;
    int id;
    
    public Task(E e) {
        this.e = e;
        id = e.hashCode();
    }
    
    public int getID() {
        return id;
    }
    
    public void nodifyAll() {
        throw new UnsupportedOperationException();
    }
    
    public void addListener(TaskObserver<F> o) {
        throw new UnsupportedOperationException();
    }
    
    public void removeListener(TaskObserver<F> o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void run()
    {
        throw new UnsupportedOperationException();
    }
    
}
