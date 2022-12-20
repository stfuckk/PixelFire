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
    private int clientsCount = 1;

    protected static final Logger log = Logger.getLogger("log");


    public ServerHandler(int queuePosition, Socket[] socketMassive) throws IOException {
        ID = queuePosition;
        allClients[ID] = new clients(socketMassive[queuePosition]);
        clientDialog = socketMassive[ID];
    }

    @Override
    public void run() {
        try {
            Log("ServerHandler::ServerHandler()");
            //Initialize communication channel for server
            DataOutputStream dos = new DataOutputStream(clientDialog.getOutputStream());
            Log("DOS created");
            DataInputStream dis = new DataInputStream(clientDialog.getInputStream());
            Log("DIS created");

            while(!clientDialog.isClosed()) {
                Log("Reading...\n");
                String entry = dis.readUTF();
                Log("Client command: " + entry);

                if(entry.equals("00")) {
                    dos.writeUTF(String.valueOf(ID)); dos.flush(); Log("ID:" + ID);
                }
                else if(entry.equals("01")) {
                    String playerInfo = entry;
                    Update(playerInfo);
                }
                else if(entry.equals("10")) {
                    Log("Client initialize connections suicide...");
                    dos.writeUTF("Suicide connections"); dos.flush();
                    clientsCount--;
                    Thread.sleep(10);
                    break;
                }
                dos.flush();
            }
            Log("Client disconnected. \nClosing connections and channels");
            dis.close();
            dos.close();
            clientDialog.close();

            Log("Closing connections and channels - DONE!");
        } catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void Log(String text) {
        //log.log(Level.INFO, text);
    }

    private void Update(String entryText) {
        Log("Sending player's info to other clients...");
        Log("Server.clientsCount:" + clientsCount);
        //Log("Updated info: " + entryText);
    }
    public void UpdateClientsCount(int clientsCount) {
        this.clientsCount = clientsCount;
    }
    public int GetClientsCount() {return clientsCount;}
}