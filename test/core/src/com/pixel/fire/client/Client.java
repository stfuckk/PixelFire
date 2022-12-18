package com.pixel.fire.client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private static boolean isServerStarted = false;

    private boolean shouldSuicide;
    private int queueNumber;


    private Socket socket;
    //private BufferedReader bufferedReader;
    private DataOutputStream dos;
    private DataInputStream dis;



    public void StartClient() {
        Log("Starting client...");
        this.start();
        if(this.isAlive()) {
            Log("Client started!");
            shouldSuicide = false;
            isServerStarted = true;
        }
        else Log("An error has occurred");
    }

    public boolean isServerStarted()
    {
        return isServerStarted;
    }

    @Override
    public void run() {
        try
        {
            socket = new Socket("127.0.0.1", 2828);
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Log("Client connected to socket!\nClient writing-reading channels initialized.");

            //bufferedReader = br;
            dos = out; dis = in;

            //Check if channel works and if its alive
                dos.writeUTF("00");
                dos.flush();
                dos.writeUTF("01"); dos.flush();

            EchoReply("Client starts writing in channel...");
            while(!socket.isOutputShutdown()) {
                //Wait for client data
                    if(shouldSuicide) {

                        EchoReply("Client killed connection");
                        dos.writeUTF("10"); dos.flush();
                        break;
                    }
                    /*String clientCommand = dis.readUTF();
                    dos.writeUTF(clientCommand);
                    dos.flush();
                    Log("Client sent message " + clientCommand + " to server."); */

            }
            Log("Closing connections and channels on client's side - DONE.");
            dos.close();
            dis.close();
            //br.close();
            out.close();
            in.close();
            //bufferedReader.close();
            socket.close();
        }
        catch (IOException e) {Log("IOException");}
        //catch (InterruptedException e) {};
    }

    public void SendPlayerInfo(float x, float y, boolean left, boolean isGrounded, boolean isIdle,
                               boolean isJumping, boolean isFalling) {
        try {
            dos.writeUTF("01");
            dos.flush();
            dos.writeUTF(x + " " + y + " " + left + " " + isGrounded + " " + isIdle + " " + isJumping + " " + isFalling + " ");
            dos.flush();
        } catch(IOException e) {Log("IOException:"); e.printStackTrace();}
    }

    private void Log(String text) {
        System.out.println(text);
    }

    public void ShutDown() {
        shouldSuicide = true;
        EchoReply("ShouldSuicide: " + shouldSuicide);
    }
    public Client GetClient() {return this;}

    public void EchoReply(String text) {
       try{
           dos.writeUTF(text); dos.flush();
       }catch (IOException e ) {}
    }
}
