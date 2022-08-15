/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ada_assignment1.Application;

import ada_assignment1.Task;
import ada_assignment1.TaskObserver;
import ada_assignment1.ThreadPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jestr
 */
public class TesterApplicationServer
{

    private final int HOST_PORT = 9999;
    private boolean stopRequested;
    private static LinkedList<Socket> connections;

    public TesterApplicationServer()
    {
        connections = new LinkedList<>();
        stopRequested = false;
    }

    public void startServer()
    {
        stopRequested = false;
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(HOST_PORT);
            System.out.println("Server started at "
                    + InetAddress.getLocalHost() + " on port " + HOST_PORT);
            System.out.println(InetAddress.getLocalHost().getHostName());

        } catch (IOException e)
        {
            System.err.println("Server can't listen on port: " + e);
            System.exit(-1);
        }

        try
        {
            while (!stopRequested)
            {  // block until the next client requests a connection
                // note that the server socket could set an accept timeout
                Socket socket = serverSocket.accept();
               
                System.out.println("Connection made with "
                        + socket.getInetAddress());
                // start a game with this connection, note that a server
                // might typically keep a reference to each game
                ChatRoom chatter = new ChatRoom(socket);
                Thread thread = new Thread(chatter);
                
                // Resize threadpool based on connections to server.
                ThreadPool.get().resize(connections.size());
                
                thread.start();
            }
            serverSocket.close();
        } catch (IOException e)
        {
            System.err.println("Can't accept client connection: " + e);
        }
        System.out.println("Server finishing");
    }

    public void requestStop()
    {
        stopRequested = true;
    }

    private class ChatRoom implements Runnable
    {

        private Socket socket;
        private PrintWriter pw;
        private BufferedReader br;
        private Queue<String> receivedStrings;
        private Queue<String> outputQueue;


        public ChatRoom(Socket socket)
        {
            this.socket = socket;
            connections.add(socket);

            try
            {
                this.receivedStrings = new LinkedBlockingQueue<>();
                this.outputQueue = new LinkedBlockingQueue<>();

                pw = new PrintWriter(socket.getOutputStream(), true);

                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
            } catch (Exception e)
            {
            };
        }

        @Override
        public void run()
        {
            try
            {
                // Sending encrypted welcome
                char[] welcome = new char["Welcome User!".length()];
                for (int i = 0; i < "Welcome User!".length(); i++)
                {
                    welcome[i] = ((char) ("Welcome User!".charAt(i) + 4));
                }
                pw.println(new String(welcome));
                // ---
                
                String response;
                boolean exit = false;
                do
                {
                    response = null;

                    if (br.ready())
                    {
                        response = br.readLine();
                    }

                    if (response != null)
                    {
                        Task task = new Task<String, String>(response)
                        {
                            @Override
                            public void run()
                            {
                                addListener(new TaskObserver<String>()
                                {
                                    @Override
                                    public void update(String progress)
                                    {
                                        System.out.println(progress);
                                    }
                                });
                                
                                notifyAll("Received: " + param);
                                char[] out = new char[this.param.length()];
                                for (int i = 0; i < this.param.length(); i++)
                                {
                                    out[i] = ((char) (this.param.charAt(i) - 2));
                                }

                                this.param = new String(out);
                                receivedStrings.add(param);
                                notifyAll("Decrypted: " + param);
                            }
                        };
                        ThreadPool.get().performTask(task);
                    }

                    if (!receivedStrings.isEmpty())
                    {
                        String str = receivedStrings.poll().trim();
                        
                        if (str.contains("QUIT"))
                        {
                             break;
                        }
                        
                        Task task = new Task<String, String>(str)
                        {
                            @Override
                            public void run()
                            {
                                addListener(new TaskObserver<String>()
                                {
                                    @Override
                                    public void update(String progress)
                                    {
                                        System.out.println(progress);
                                    }
                                });

                                notifyAll("Received: " + param);
                                char[] out = new char[this.param.length()];
                                for (int i = 0; i < this.param.length(); i++)
                                {
                                    out[i] = ((char) (this.param.charAt(i) + 4));
                                }

                                this.param = new String(out).trim();
                                notifyAll("Sending re-encrypted: " + param);
                                
                                outputQueue.add(param);
                            }
                        };
                        ThreadPool.get().performTask(task);
                    }
                    
                    if (!outputQueue.isEmpty())
                    {
                        String out = outputQueue.poll();
                        for (Socket x : connections)
                        {
                            if (this.socket != x)
                            {
                                PrintWriter printer = new PrintWriter(x.getOutputStream(), true);
                                printer.println(out);
                            }
                        }
                    }
                    Thread.sleep(10);
                } while (!exit);
                

                System.out.println("Closing connection with "
                        + socket.getInetAddress());
                pw.close();
                br.close();
                socket.close();
                
                connections.remove(socket);
            } catch (IOException e)
            {
                System.err.println("Server error with game: " + e);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(TesterApplicationServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args)
    {
        TesterApplicationServer server = new TesterApplicationServer();
        server.startServer();
    }
}
