/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author jestr
 */
public class TesterApplicationServer
{
    private final int HOST_PORT = 9999;
    private boolean stopRequested;
    static private LinkedList<Socket> connections;

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
        private Queue<String> strings;

        public ChatRoom(Socket socket)
        {
            this.socket = socket;
            connections.add(socket);

            try
            {
                this.strings = new LinkedBlockingQueue<>();

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
                pw.println("Welcome user!");
                String response;
                do
                {
                    String clientInputs = br.readLine();
                    strings.add(clientInputs);
                    response = strings.remove();
                    
                    for (Socket x : connections)
                    {
                        if (this.socket != x)
                        {
                            PrintWriter printer = new PrintWriter(x.getOutputStream(), true);
                            printer.println(response);
                        }
                    }
                } while (!response.equals("QUIT"));
                pw.close();
                br.close();
                System.out.println("Closing connection with "
                        + socket.getInetAddress());
                socket.close();
            } catch (IOException e)
            {
                System.err.println("Server error with game: " + e);
            }
        }
    }

    public static void main(String[] args)
    {
        TesterApplicationServer server = new TesterApplicationServer();
        server.startServer();
    }
}
