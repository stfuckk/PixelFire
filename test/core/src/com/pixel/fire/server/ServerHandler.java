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

    private DataOutputStream dos;
    private DataInputStream dis;

    public ServerHandler(Socket clientSocket, int queuePosition) {
        ServerHandler.clientDialog = clientSocket;
        playerPositionInQueue = queuePosition;
    }

    @Override
    public void run() {
        try {
            //Initialize communication channel for server
            DataOutputStream tempDos = new DataOutputStream(clientDialog.getOutputStream());
            Log("DOS created");
            DataInputStream tempDis = new DataInputStream(clientDialog.getInputStream());
            Log("DIS created");
            dos = tempDos; dis = tempDis;

            while(!clientDialog.isClosed()) {
                Log("Server reading from channel...\n");
                String entry = dis.readUTF();
                Log("READ from clientDialog message - " + entry);

                if(entry.equals("00")) {
                    dos.write(playerPositionInQueue); dos.flush();
                }
                if(entry.equals("01")) {
                    String playerInfo = entry;
                }

                if(entry.equalsIgnoreCase("quit")) {
                    Log("Client initialize connections suicide...");
                    dos.writeUTF("Server reply - " + entry + " - OK");
                    Thread.sleep(1000);
                    break;
                }
                dos.flush();
            }
            Log("Client disconnected. \nClosing connections and channels");
            dis.close();
            dos.close();
            clientDialog.close();

            Log("Closing connections and channels - DONE!");
        } catch (IOException e) {e.printStackTrace();}
        catch (InterruptedException e) {
            log.log(Level.INFO, "Interruption exception...");
        }
    }
    private void Log(String text) {
        log.log(Level.INFO, text);
    }
    private void GetPlayerInfo(String entryText) {

    }
}