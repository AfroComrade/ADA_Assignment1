package ada_assignment1.Application;

import ada_assignment1.Task;
import ada_assignment1.ThreadPool;
import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jestr
 */
public class TesterApplicationClient
{
    private final String HOST_NAME = "localhost";
    private final int HOST_PORT = 9999;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LinkedList<Observer> observers;
    
    public TesterApplicationClient()
    {
        observers = new LinkedList<>();
    }
    
    public void startClient()
    {
        Socket socket = null;
        Scanner keyboardInput = new Scanner(System.in);
        try
        {
            socket = new Socket(HOST_NAME, HOST_PORT);
        } catch (IOException e)
        {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }
        PrintWriter pw;
        try
        {  
            new Thread(new ChatRoomListener(socket)).start();
            pw = new PrintWriter(socket.getOutputStream(), true);

            boolean finished = false;
            do
            {
                String userInput = keyboardInput.nextLine();

                if (userInput.equals("QUIT"))
                {
                    finished = true;
                }
                
                Task task = new Task<String, String>(userInput)
                {
                    @Override
                    public void run()
                    {
                        char[] out = new char[0xff];
                        for (int i = 0; i < this.param.length(); i++)
                        {
                            out[i] = ((char) (this.param.charAt(i) + 2));
                        }

                        this.param = new String(out).trim();
                        pw.println(this.param);

                        notifyAll(param);
                    }
                };
                ThreadPool.get().performTask(task);
                
            } while (!finished);

            Thread.sleep(1000);

            System.out.println("Closing socket with server");
            pw.close();
            socket.close();
        } catch (IOException e)
        {
            System.err.println("Client error with game: " + e);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(TesterApplicationClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private class ChatRoomListener implements Runnable
    {

        private Socket socket;

        public ChatRoomListener(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                // create a buffered input stream for this socket
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                boolean finished = false;
                do
                {
                    if (br.ready())
                    {
                        String serverResponse = br.readLine();
//                        if (serverResponse != null && serverResponse.equals("QUIT"))
//                        {
//                            break;
//                        }
                        Task task = new Task<String, String>(serverResponse.trim())
                        {
                            @Override
                            public void run()
                            {
                                char[] out = new char[0xff];
                                for (int i = 0; i < this.param.length(); i++)
                                {
                                    out[i] = ((char) (this.param.charAt(i) - 4));
                                }

                                this.param = new String(out).trim();
                                //pw.println(this.param);
                                System.out.println(param);

                                notifyAll(param);
                            }
                        };
                        ThreadPool.get().performTask(task);
                        //System.out.println(serverResponse);
                    }
                    Thread.sleep(10);
                } while (!finished);
                br.close();
                socket.close();
            } catch (IOException e)
            {
                System.err.println("Client error with game: " + e);
                e.printStackTrace();
            } catch (InterruptedException ex)
            {
                //Logger.getLogger(ChatUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    public static void main(String[] args)
    {
        TesterApplicationClient client = new TesterApplicationClient();
        client.startClient();
    }
}
