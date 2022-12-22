package com.pixel.fire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler implements  Runnable {



    private final Socket clientDialog;

    private ServerHandler[] allHandlers;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final int ID;
    private int clientsCount = 1;

    protected final Logger log = Logger.getLogger("log");


    public ServerHandler(int queuePosition, Socket client) throws IOException {
        ID = queuePosition;
        clientDialog = client;
    }

    @Override
    public void run() {
        try {
            Log("ServerHandler::ServerHandler()");

            dos = new DataOutputStream(clientDialog.getOutputStream());
            Log("DOS created");
             dis = new DataInputStream(clientDialog.getInputStream());
            Log("DIS created");

            while(!clientDialog.isClosed() && Server.CheckServerState()) {
                Log("Reading...\n");
                String entry = dis.readUTF();
                Log("Client command: " + entry);

                if(entry.equals("00")) {
                    dos.writeUTF(String.valueOf(ID)); dos.flush(); Log("ID:" + ID);
                }
                else if(entry.equals("01")) {
                    String playerInfo = dis.readUTF();
                    Log("Player info: " + playerInfo);
                    SendPlayerDataToServer(playerInfo);
                }
                else if(entry.equals("10")) {
                    Log("Client initialize connections suicide...");
                    dos.writeUTF("Suicide connections"); dos.flush();
                    Server.RefreshData("Quit");
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
    private void SendPlayerDataToServer(String entryText) throws InterruptedException {
        if(clientsCount == 2) {
            if(ID == 0)
                allHandlers[1].UpdateEnemies(entryText);
            else if(ID == 1)
                allHandlers[0].UpdateEnemies(entryText);
        }
        else if(clientsCount == 1) allHandlers[0].UpdateEnemies(entryText);
    }

//==================================METHODS NOT USED BY THIS CLASS (I.E. USED BY SERVER/OTHER HANDLERS)
    public void UpdateEnemies(String info) throws InterruptedException{
        try {
            dos.writeUTF("11"); dos.flush();
            Thread.sleep(1);
            dos.writeUTF(info); dos.flush();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public void UpdateClientsCount(int inputCounter) {this.clientsCount = inputCounter;}
    public void UpdateHandlersMassive(ServerHandler[] handlers) {this.allHandlers = handlers;}
    public int returnClientsCount() {return clientsCount;}

//==================================SERVICE METHODS
    private void Log(String text) {
        log.log(Level.INFO, text);
    }
}