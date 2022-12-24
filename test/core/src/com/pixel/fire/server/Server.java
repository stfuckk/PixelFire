package com.pixel.fire.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{
    protected static final Logger logger = Logger.getLogger("log");


    protected static int clientsCount = 0;

    protected static Socket[] clientSockets;
    protected static ServerHandler[] clientHandlers;
    private static ServerSocket serverSocket;
    private static boolean isActive = false;
    protected static String serverCommand = "";
    public static void start () throws InterruptedException {

        try( ServerSocket servSock = new ServerSocket(2828)) {
            serverSocket = servSock;
            Log("Server socket created, command console reader created");

            clientHandlers = new ServerHandler[2];
            clientSockets = new Socket[2];

            //Works with client until socket is closed
            while(!serverSocket.isClosed())
            {
                if (serverCommand.equalsIgnoreCase("quit")) {
                    Log("Main server initiate exit...");
                    break;
                }

                try
                {
                    clientSockets[clientsCount] = serverSocket.accept();
                    Log("Connection accepted...");
                }
                catch (IOException e)
                {
                    Log("serverSocket.accept() interrupted. Irrelevant! Continue...");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            clientHandlers[clientsCount] = new ServerHandler(clientsCount, clientSockets[clientsCount]);
                            isActive = true;
                            clientHandlers[clientsCount].run();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                Thread.sleep(100);

                clientsCount++;

                for (int i = 0; i < clientsCount; i++)
                {
                    clientHandlers[i].UpdateClientsCount(clientsCount);
                    clientHandlers[i].UpdateHandlersMassive(clientHandlers);
                    Log("Clients on handler[" + (i) + "]: " +clientHandlers[i].returnClientsCount());
                }
            }
            serverSocket.close();
        }
        catch(IOException e) {
            Log("An error has happened!");
            e.printStackTrace();
        }
    }

//==================================METHODS USED BY OTHER CLASSES (I.E. SERVER HANDLER)
    public static void KillServer() {
        try {
            Log("Shutting server down...");
            serverSocket.close();
            isActive = false;
            Log("Server shut down!");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void RefreshData(String action) throws IOException {
        if(action.equals("Quit")) clientsCount--;
        Log("RefreshData: " + clientsCount);
        for(int i = 0; i < clientsCount; i++) {
            clientHandlers[i].UpdateClientsCount(clientsCount);
            clientHandlers[i].UpdateHandlersMassive(clientHandlers);
            Log("Server.RefreshData():clientHanlders[" + i + "] CC:" + clientsCount);
        }

        if(clientsCount==0){
            isActive = false;
            Log("Server.RefreshData() shouldSuicide = true");
            KillServer();
        }
    }
    public static boolean CheckServerState() {return isActive;}

//==================================SERVICE METHODS
    private static void Log(String logText) {
        logger.log(Level.INFO, logText);
    }

}
