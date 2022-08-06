/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public TaskId assignId()
    {
        return new TaskId(++idCount);
    }

}
