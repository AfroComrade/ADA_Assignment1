package ada_assignment1;

/**
 *
 * @author jestr
 */
public class UniqueIdentifier
{

    private static UniqueIdentifier _instance;
    private int idCount;

    private UniqueIdentifier()
    {
        idCount = 0;
    }

    public static UniqueIdentifier get()
    {
        if (_instance == null)
        {
            _instance = new UniqueIdentifier();
        }

        return _instance;
    }

    // We will likely neet a mutex algorithm for this
    // Incase multiple threads call this instance at the same time
    public synchronized TaskId assignId()
    {
        return new TaskId(++idCount);
    }

}
