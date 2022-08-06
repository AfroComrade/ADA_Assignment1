package ada_assignment1;

import java.util.ArrayList;
import java.util.Observer;

public interface TaskObserver<F>
{
    public void update(int progress, Runnable runnable);
}