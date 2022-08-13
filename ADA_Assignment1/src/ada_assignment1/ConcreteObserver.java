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
public class ConcreteObserver<F> implements TaskObserver<F>
{

    @Override
    public void update(F progress)
    {
        System.out.println("Progress: " + progress);
    }
}
