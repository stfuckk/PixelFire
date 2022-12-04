package com.pixel.fire.server;

import com.pixel.fire.client.Client;

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

    protected static ExecutorService executeIt = Executors.newFixedThreadPool(4);

    protected static int clients = 0;


    public static void start () throws InterruptedException {

        try(final ServerSocket serverSocket = new ServerSocket(2828)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            logger.log(Level.INFO,"Server socket created, command console reader created");

            //Works with client until socket is closed
            while(!serverSocket.isClosed()) {
                if(br.ready()) {
                    logger.log(Level.INFO,"Main server found messages");
                    String serverCommand = br.readLine();
                    if(serverCommand.equalsIgnoreCase("quit")) {
                        logger.log(Level.INFO,"Main server initiate exit...");
                        serverSocket.close();
                        break;
                    }
                }
                Socket clientSocket = serverSocket.accept();
                executeIt.execute(new ServerHandler(clientSocket, clients));
                logger.log(Level.INFO,"Connection accepted...");
                clients++;
            }
            executeIt.shutdown();
        }
        catch(IOException e) {
            logger.log(Level.INFO, "An error has happened!");
            e.printStackTrace();
        }
    }
}
