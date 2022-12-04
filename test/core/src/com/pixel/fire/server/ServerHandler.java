package com.pixel.fire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler implements  Runnable {

    private static Socket clientDialog;

    private int playerPositionInQueue;

    protected static final Logger log = Logger.getLogger("log");

    public ServerHandler(Socket clientSocket, int queuePosition) {
        ServerHandler.clientDialog = clientSocket;
        playerPositionInQueue = queuePosition;
    }

    @Override
    public void run() {
        try {
            //Initialize communication channel for server
            DataOutputStream dos = new DataOutputStream(clientDialog.getOutputStream());
            log.log(Level.INFO, "DOS created");
            DataInputStream dis = new DataInputStream(clientDialog.getInputStream());
            log.log(Level.INFO, "DIS created");

            while(!clientDialog.isClosed()) {
                log.log(Level.INFO, "Server reading from channel...\n");

                String entry = dis.readUTF();
                log.log(Level.INFO, "READ from clientDialog message - " + entry);

                if(entry.equalsIgnoreCase("quit")) {
                    log.log(Level.INFO, "Client initialize connections suicide...");
                    dos.writeUTF("Server reply - " + entry + " - OK");
                    Thread.sleep(1000);
                    break;
                }
                if(entry.equals("Send queue number")) {
                    dos.write(playerPositionInQueue); dos.flush();
                }
                dos.flush();
            }
            log.log(Level.INFO, "Client disconnected." +
                    "\nClosing connections and channels");
            dis.close();
            dos.close();
            clientDialog.close();

            log.log(Level.INFO, "Closing connections and channels - DONE!");
        } catch (IOException e) {e.printStackTrace();}
        catch (InterruptedException e) {
            log.log(Level.INFO, "Interruption exception...");
        }
    }
}