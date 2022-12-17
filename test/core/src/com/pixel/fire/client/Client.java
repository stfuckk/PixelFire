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

            if(br.ready()) {
                dos.writeUTF("00"); dos.flush();
                queueNumber = dis.read();
            }

            //Check if channel works and if its alive
            if(!socket.isOutputShutdown()) {
                dos.writeUTF("00");
                dos.flush();
                queueNumber = Integer.parseInt(dis.readUTF());
            }
            while(!socket.isOutputShutdown()) {
                //Wait for client data
                if(bufferedReader.ready()) {
                    Log("Client starts writing in channel...");

                    String clientCommand = bufferedReader.readLine();

                    dos.writeUTF(clientCommand);
                    dos.flush();
                    Log("Client sent message " + clientCommand + " to server.");

                    if(clientCommand.equalsIgnoreCase("quit")) {
                        Log("Client killed connection");
                        Thread.sleep(2000);
                        //Checks server output
                        if(dis.read() > -1 && !shouldSuicide) {
                            //If there is output from server, save it in dis and read it
                            Log("reading...");
                            String input = dis.readUTF();
                            Log(input);
                        }
                        break;
                    }
                    //If closing connection was not reached continue:
                    Log("Client sent message and start waiting for data from server...");
                }
            }
            Log("Closing connections and channels on client's side - DONE.");
            dos.close();
            dis.close();
            bufferedReader.close();
            socket.close();
        }
        catch (IOException e) {this.interrupt();}
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

    public void ShutDown() {shouldSuicide = true;}
    public Client GetClient() {return this;}
}
