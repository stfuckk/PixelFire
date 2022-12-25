package com.pixel.fire.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client extends Thread
{
    private static boolean isServerStarted = false;
    private boolean shouldSuicide;
    private int ID;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private static String ip;
    private int clientsCount = 1;

    private String enemyState = "-400 0 3 false false true false false false false";
    
    public void StartClient(String ip) 
    {
        Log("Starting client...");
        if (!this.isAlive()) this.start();
        if (this.isAlive())
        {
            Log("Client started!");
            shouldSuicide = false;
            isServerStarted = true;
            Client.ip = ip;
        }
    }

    public boolean isServerStarted()
    {
        return isServerStarted;
    }

    @Override
    public void run() {
        try
        {
            socket = new Socket(ip, 2828);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Log("Client connected to socket!\nClient writing-reading channels initialized.");


            dos = out; dis = in;

            //Check if channel works and if its alive
                dos.writeUTF("00");
                dos.flush();

                ID = Integer.parseInt(dis.readUTF());

                //System.out.println(ID);
                //dos.writeUTF("01"); dos.flush();

            while(!socket.isOutputShutdown())
            {
                String entry = dis.readUTF();


                if(entry.equals("11"))
                {
                    enemyState = dis.readUTF();
                }

                else if (entry.equals("000"))
                {
                    clientsCount = Integer.parseInt(dis.readUTF());
                }

                else if(entry.equals("Suicide connections"))
                {
                    EchoReply("Client killed connection");
                    //dos.writeUTF("10"); dos.flush();**********************************
                    break;
                }
            }
            Log("Closing connections and channels on client's side - DONE.");
            dos.close();
            dis.close();
            out.close();
            in.close();
            socket.close();
        }
        catch (IOException e) {Log("IOException");}
        //catch (InterruptedException e) {};
    }
    public void ShutDown() {
        shouldSuicide = true;
        EchoReply("ShouldSuicide: " + shouldSuicide);
        try {
            dos.writeUTF("10");
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getClientsCount()
    {
        try
        {
            dos.writeUTF("000");
            dos.flush();
            clientsCount = Integer.parseInt(dis.readUTF());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public int clientsCount()
    {
        return clientsCount;
    }
//==================================METHODS FOR INTERACTING WITH THE GAME
    public void SendPlayerInfo(float x, float y,int playerLives, boolean left, boolean isGrounded, boolean isIdle,
                               boolean isJumping, boolean isFalling, boolean isDead, boolean justShot){
        try
        {
            dos.writeUTF("01");
            dos.flush();
            //try {this.sleep(10);} catch(InterruptedException e) {System.out.println("sleep interrupted");}
            dos.writeUTF(x+" "+y+" "+playerLives+" "+left+" "+isGrounded+" "+isIdle+" "+isJumping+" "+isFalling+" "+isDead+" "+justShot);
            dos.flush();
        }
        catch(SocketException e)
        {
            System.out.println("HOY!");
        }
        catch(IOException e) {Log("IOException:"); e.printStackTrace();}

        catch(NullPointerException e)
        {
            System.out.println("BABY");
        }

    }

    public String GetInfo() {return enemyState;}

//==================================SERVICE METHODS
    public void EchoReply(String text) {
       try{
           dos.writeUTF(text); dos.flush();
       }catch (IOException e ) {}
    }
    private void Log(String text) {
        //System.out.println(text);
    }

    public void test() {
        try {dos.writeUTF("101"); dos.flush();} catch (IOException e) {e.printStackTrace();}
    }
}
