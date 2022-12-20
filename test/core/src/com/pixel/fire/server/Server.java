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


    public static void start () throws InterruptedException {

        try(final ServerSocket serverSocket = new ServerSocket(2828)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            logger.log(Level.INFO,"Server socket created, command console reader created");

            //clients = new Client[4];
            clientHandlers = new ServerHandler[4];
            clients = new Socket[4];

            //Works with client until socket is closed
            while(!serverSocket.isClosed()) {
                    logger.log(Level.INFO,"Main server found messages");
                    String serverCommand = "";

                    if(serverCommand.equalsIgnoreCase("quit")) {
                        logger.log(Level.INFO,"Main server initiate exit...");
                        serverSocket.close();
                        break;
                    }
                clients[clientsCount] = serverSocket.accept();
                logger.log(Level.INFO,"Connection accepted1...");
                new Thread(new Runnable()
                {
                    @Override public void run()
                    {
                        try
                        {
                            clientHandlers[clientsCount] = new ServerHandler(clientsCount, clients);
                            clientHandlers[clientsCount].run();
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                Thread.sleep(100);
                logger.log(Level.INFO,"Connection accepted2...");
                logger.log(Level.INFO, "CC1: " + clientsCount);
                clientsCount++;
                logger.log(Level.INFO, "CC2: " + clientsCount);
                clientHandlers[clientsCount - 1].UpdateClientsCount(clientsCount);
                if(clientHandlers[clientsCount - 1].GetClientsCount() == 0) {
                    break;
                }
            }
            executeIt.shutdown();
            serverSocket.close();
            br.close();
        }
        catch(IOException e) {
            logger.log(Level.INFO, "An error has happened!");
            e.printStackTrace();
        }
    }

}
