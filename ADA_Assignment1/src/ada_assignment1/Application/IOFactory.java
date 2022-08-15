/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ada_assignment1.Application;

import ada_assignment1.Task;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 *
 * @author Yeran
 */
public class IOFactory
{

    private static IOFactory _instance;

    private IOFactory()
    {
    }

    ;
    
    public static IOFactory get()
    {
        if (_instance == null)
        {
            try
            {
                _instance = new IOFactory();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return _instance;
    }

    public Task createTask(String param, char io, Object stream)
    {
        if (io == '\0')
        {
            return null;
        }

        if (io == 'i')
        {
            return new inputTask(param, stream);
        } else if (io == 'o')
        {
            return new outputTask(param, stream);
        }

        return null;
    }

    private class outputTask extends Task<String, String>
    {

        private BufferedReader br;

        public outputTask(String param, Object stream)
        {
            super(param);
            br = (BufferedReader) stream;
        }

        @Override
        public void run()
        {
            char[] out = new char[0xff];
            for (int i = 0; i < this.param.length(); i++)
            {
                out[i] = ((char) (this.param.charAt(i) - 4));
            }

            this.param = new String(out).trim();
            //br.println(this.param);
            System.out.println(param);

            notifyAll(param);
        }
    }

    private class inputTask extends Task<String, String>
    {

        private PrintWriter pw;

        public inputTask(String param, Object stream)
        {
            super(param);
            pw = (PrintWriter) stream;
        }

        @Override
        public void run()
        {
            char[] out = new char[param.length()];
            for (int i = 0; i < this.param.length(); i++)
            {
                out[i] = ((char) (this.param.charAt(i) + 2));
            }

            this.param = new String(out).trim();
            pw.println(this.param);

            notifyAll(param);
        }
    }
}
