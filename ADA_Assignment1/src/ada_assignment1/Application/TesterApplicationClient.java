/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ada_assignment1.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

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
            //socket = new Socket(HOST_NAME, HOST_PORT);
            
            socket = new Socket(HOST_NAME, HOST_PORT);
        } catch (IOException e)
        {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }
        PrintWriter pw; // output stream to server
        //BufferedReader br; // input stream from server
        try
        {  // create an autoflush output stream for the socket
            new Thread(new ChatRoomListener(socket)).start();
            pw = new PrintWriter(socket.getOutputStream(), true);
            // create a buffered input stream for this socket
            //br = new BufferedReader(new InputStreamReader(
            //       socket.getInputStream()));
            // play the game until value is correctly guessed
            boolean finished = false;
            do
            {
                String userInput = keyboardInput.nextLine();
                //String serverResponse = br.readLine();
                //System.out.println(serverResponse);
                if (userInput.equals("QUIT"))
                {
                    break;
                } else
                {  // get user input and sent it to server
                    pw.println(userInput);
                }
            } while (true);
            pw.close();
            //br.close();
            socket.close();
        } catch (IOException e)
        {
            System.err.println("Client error with game: " + e);
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
                // play the game until value is correctly guessed
                boolean finished = false;
                do
                {
                    String serverResponse = br.readLine();
                    if (serverResponse != null && serverResponse.equals("QUIT"))
                    {
                        finished = true;
                    }
                    System.out.println(serverResponse);
                    Thread.sleep(10);
                } while (!finished);
                br.close();
                socket.close();
            } catch (IOException e)
            {
                System.err.println("Client error with game: " + e);
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
