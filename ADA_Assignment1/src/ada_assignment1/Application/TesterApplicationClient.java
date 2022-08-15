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
    private PrintWriter out;
    private BufferedReader in;
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
        PrintWriter pw;
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
                
                Task task = createEncrptionDispatchTask(userInput, pw);
                //new Thread(task).start();
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
    private Task createEncrptionDispatchTask(String userInput, PrintWriter pw)
    {
        return new Task<String, String>(userInput)
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
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                finished = false;
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
                        //ThreadPool.get().performTask(task);
                        new Thread(task).start();
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
