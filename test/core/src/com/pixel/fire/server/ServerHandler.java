package com.pixel.fire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler implements  Runnable {

    static class clients {
        public Socket clientSocket;
        public DataOutputStream dos;
        public DataInputStream dis;

        public clients(Socket socket) throws IOException {
            clientSocket = socket;
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dis = new DataInputStream(clientSocket.getInputStream());
        }
    }

    private static Socket clientDialog;

    private static clients[] allClients = new clients[4];

    private final int ID;

    protected static final Logger log = Logger.getLogger("log");


    public ServerHandler(int queuePosition, Socket[] socketMassive) throws IOException {
        ID = queuePosition;
        allClients[ID] = new clients(socketMassive[queuePosition]);
        clientDialog = socketMassive[ID];
    }

    @Override
    public void run() {
        try {
            //Initialize communication channel for server
            DataOutputStream dos = new DataOutputStream(clientDialog.getOutputStream());
            Log("DOS created");
            DataInputStream dis = new DataInputStream(clientDialog.getInputStream());
            Log("DIS created");

            while(!clientDialog.isClosed()) {
                Log("Server reading from channel...\n");
                String entry = dis.readUTF();
                Log("READ from clientDialog message - " + entry);

                if(entry.equals("00")) {
                    dos.write(ID); dos.flush();
                }
                if(entry.equals("01")) {
                    String playerInfo = entry;
                    Update(playerInfo);
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

    private void Update(String entryText) {
        Log("Sending player's info to other clients...");

    }
}