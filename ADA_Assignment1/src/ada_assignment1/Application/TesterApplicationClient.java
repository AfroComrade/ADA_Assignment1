package ada_assignment1.Application;

import ada_assignment1.Task;
import ada_assignment1.ThreadPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    private PrintWriter pw;
    private BufferedReader br;
    private ChatRoomListener listener;

    public TesterApplicationClient()
    {

    }

    public void startClient()
    {
        socket = null;
        Scanner keyboardInput = new Scanner(System.in);
        try
        {
            socket = new Socket(HOST_NAME, HOST_PORT);
        } catch (IOException e)
        {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }
        try
        {
            listener = new ChatRoomListener(socket);
            new Thread(listener).start();
            pw = new PrintWriter(socket.getOutputStream(), true);

            boolean finished = false;
            do
            {
                String userInput = keyboardInput.nextLine();

                if (userInput.equals("QUIT"))
                {
                    finished = true;
                }

                //new Thread(task).start();
                Task task = IOFactory.get().createTask(userInput, 'i', pw);

                //  NOTE:
                //  Because we're calling the threadpool here to run the task, the client won't actually close until the thread pool closes
                //  This is known. In the future we would create a new ThreadPool for 
                ThreadPool.get().performTask(task);

            } while (!finished);

            Thread.sleep(1000);

            socket.close();
            pw.close();
            listener.finished = true;
            ThreadPool.get().destroyPool();

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
        protected boolean finished;

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
                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                finished = false;
                do
                {
                    if (br.ready())
                    {
                        String serverResponse = br.readLine();

                        Task task = IOFactory.get().createTask(serverResponse.trim(), 'o', br);

                        ThreadPool.get().performTask(task);
                        //System.out.println(serverResponse);
                    }
                    Thread.sleep(10);
                } while (!finished);
                socket.close();
                br.close();
            } catch (IOException e)
            {
                System.err.println("Client error with game: " + e);
                e.printStackTrace();
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
                //Logger.getLogger(ChatUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args)
    {
        TesterApplicationClient client = new TesterApplicationClient();
        client.startClient();
        // There's a thread still running somewhere
        // Not sure if it's in here or if it's because we put a task in ThreadPool
    }
}
