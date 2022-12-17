package com.pixel.fire.client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private static boolean isServerStarted = false;

    private static boolean shouldSuicide = false;
    private int queueNumber;


    private Socket socket;
    private BufferedReader bufferedReader;
    private DataOutputStream dos;
    private DataInputStream dis;


    public void StartClient() {
        Log("Starting client...");
        this.start();
        if(this.isAlive()) Log("Client started!");
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
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            isServerStarted = true;
            Log("Client connected to socket!\nClient writing-reading channels initialized.");

            bufferedReader = br; dos = out; dis = in;

            //Check if channel works and if its alive
                dos.writeUTF("00");
                dos.flush();
                String incameMessage = dis.readUTF();
                dos.writeUTF(incameMessage);
                dos.writeUTF("10");
                //Log("dis.wrote");
            while(!socket.isOutputShutdown()) {
                //Wait for client data
                if(bufferedReader.ready()) {
                    Log("Client starts writing in channel...");

                    Log("QUIT");
                    if(shouldSuicide) {
                        Log("Client killed connection");
                        dos.writeUTF("quit"); dos.flush();
                        Thread.sleep(2000);
                        //Checks server output
                        if(dis.read() > -1) {
                            //If there is output from server, save it in dis and read it
                            Log("reading...");
                            String input = dis.readUTF();
                            Log(input);
                        }
                        break;
                    }
                    String clientCommand = dis.readUTF();
                    dos.writeUTF(clientCommand);
                    dos.flush();
                    Log("Client sent message " + clientCommand + " to server.");
                }
            }
            Log("Closing connections and channels on client's side - DONE.");
            dos.close();
            dis.close();
            br.close();
            out.close();
            in.close();
            bufferedReader.close();
            socket.close();
        }
        catch (IOException e) {Log("IOException");}
        catch (InterruptedException ie) {};
    }

    public void SendPlayerInfo(float x, float y, boolean left, boolean isGrounded, boolean isIdle,
                               boolean isJumping, boolean isFalling) throws IOException {
        dos.writeUTF("Sending player info to server..."); dos.flush();
        dos.writeUTF(x +" "+ y +" "+ left +" "+ isGrounded +" "+ isIdle + " "+ isJumping +" "+ isFalling +" ");
        dos.flush();
    }

    private void Log(String text) {
        System.out.println(text);
    }

    public void ShutDown() {
        shouldSuicide = true;
    }
    public Client GetClient() {return this;}
}
