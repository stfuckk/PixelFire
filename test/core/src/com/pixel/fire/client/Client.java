package com.pixel.fire.client;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private static boolean isServerStarted = false;

    public void StartClient() {
        System.out.println("Starting client...");
        this.start();
        if(this.isAlive()) System.out.println("Client started!");
        else System.out.println("An error has occurred");
    }

    public boolean isServerStarted()
    {
        return isServerStarted;
    }

    @Override
    public void run() {
        try
        {
            Socket socket = new Socket("127.0.0.1", 2828);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            isServerStarted = true;
            System.out.println("Client connected to socket");
            System.out.println("Client writing channel = dos, reading channel = dis initialized.");

            //Check if channel works and if its alive
            while(!socket.isOutputShutdown()) {
                //Wait for client data
                if(bufferedReader.ready()) {
                    System.out.println("Client starts writing in channel...");
                    //Thread.sleep(1000);
                    String clientCommand = bufferedReader.readLine();

                    //Writing data from client to channel's socket for server
                    dos.writeUTF(clientCommand);
                    dos.flush();
                    System.out.println("Client sent message " + clientCommand + " to server.");
                    //Thread.sleep(1000);

                    //Checks if client is willing to quiter
                    if(clientCommand.equalsIgnoreCase("quit")) {
                        System.out.println("Client killed connection");
                        Thread.sleep(2000);

                        if(dis.read() > -1) {
                            System.out.println("reading...");
                            String input = dis.readUTF();
                            System.out.println(input);
                        }
                        //Quit after reading data from server
                        //this.interrupt();

                        //Checks server output
                        if(dis.read() > -1) {
                            //If there is output from server, save it in dis and read it
                            System.out.println("reading...");
                            String input = dis.readUTF();
                            System.out.println(input);
                        }
                        break;
                    }
                    //If closing connection was not reached continue:
                    System.out.println("Client sent message" +
                            "and start waiting for data from server...");
                }
            }
            System.out.println("Closing connections and channels on client's side - DONE.");
            dos.close();
            dis.close();
            bufferedReader.close();
            socket.close();
        }
        catch (IOException e) {this.interrupt();}
        catch (InterruptedException ie) {};
    }
}
