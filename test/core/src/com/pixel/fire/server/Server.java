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

public class Server {

    protected static final Logger logger = Logger.getLogger("log");

    protected static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    protected static int clientsCount = 0;

    protected static Socket[] clients;
    protected static ServerHandler[] clientHandlers;
    private static ServerSocket serverSocket;
    private static boolean isActive = false;
    protected static String serverCommand = "";
    public static void start () throws InterruptedException {

        try( ServerSocket servSock = new ServerSocket(2828)) {
            serverSocket = servSock;
            Log("Server socket created, command console reader created");

            //clients = new Client[4];
            clientHandlers = new ServerHandler[4];
            clients = new Socket[4];

            //Works with client until socket is closed
            while(!serverSocket.isClosed()) {
                Log("Main server found messages");

                    if(serverCommand.equalsIgnoreCase("quit")) {
                        Log("Main server initiate exit...");
                        break;
                    }
                try {clients[clientsCount] = serverSocket.accept();} catch (IOException e) {
                    Log("serverSocket.accept() interrupted. Irrelevant! Continue...");
                    return;
                }
                Log("Connection accepted1...");
                new Thread(new Runnable()
                {
                    @Override public void run()
                    {
                        try
                        {
                            clientHandlers[clientsCount] = new ServerHandler(clientsCount, clients);
                            isActive = true;
                            clientHandlers[clientsCount].run();
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                Thread.sleep(100);
                Log("Connection accepted2...");
                Log("CC1: " + clientsCount);
                clientsCount++;
                Log("CC2: " + clientsCount); //Need to make a cycle to run
                //through all of clients

            }
            serverSocket.close();
            executeIt.shutdown();
        }
        catch(IOException e) {
            Log("An error has happened!");
            e.printStackTrace();
        }
    }
    public static void KillServer() {
        try {
            Log("Shutting server down...");
            serverSocket.close();
            executeIt.shutdown();
            isActive = false;
            Log("Server shut down!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void RefreshData(String action) throws IOException {
        if(action.equals("Quit")) clientsCount--;
        //else clientsCount++; <--- have an increment in Server.start();
        Log("RefreshData: " + clientsCount);
        for(int i = 0; i <= clientsCount; i++) {
            clientHandlers[i].UpdateClientsCount(clientsCount);
            Log("Server.RefreshData():clientHanlders[" + i + "] CC:" + clientsCount);
        }
        if(clientsCount==0){
            isActive = false;
            Log("Server.RefreshData() shouldSuicide = true");
            KillServer();
        }
    }
    public static boolean CheckServerState() {return isActive;}
    private static void Log(String logText) {
        logger.log(Level.INFO, logText);
    }

}
