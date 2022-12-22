package com.pixel.fire.client;

import com.pixel.fire.Objects.Player.Enemy;

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

    private Enemy enemy;
    
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
            socket = new Socket("127.0.0.1", 2828);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Log("Client connected to socket!\nClient writing-reading channels initialized.");


            dos = out; dis = in;

            //Check if channel works and if its alive
                dos.writeUTF("00");
                dos.flush();

                ID = dis.read();

                dos.writeUTF("01"); dos.flush();

            while(!socket.isOutputShutdown()) {
                    if(dis.readUTF().equals("Suicide connections")) {
                        EchoReply("Client killed connection");
                        //dos.writeUTF("10"); dos.flush();
                        break;
                    }
                    else if(dis.readUTF().equals("11")) {
                        enemy.setState(dis.readUTF());
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
//==================================METHODS FOR INTERACTING WITH THE GAME
    public void SendPlayerInfo(float x, float y, boolean left, boolean isGrounded, boolean isIdle,
                               boolean isJumping, boolean isFalling){
        try {
            dos.writeUTF("01");
            dos.flush();
            try {this.sleep(1);} catch(InterruptedException e) {System.out.println("sleep interrupted");}
            dos.writeUTF(x + " " + y + " " + left + " " + isGrounded + " " + isIdle + " " + isJumping + " " + isFalling + " ");
            dos.flush();
        } catch(SocketException e) {
            System.out.println("HOY!");
        } catch(IOException e) {Log("IOException:"); e.printStackTrace();}
    }

    public void SetEnemyState(String state) {
        enemy.setState(state);
    }

//==================================SERVICE METHODS
    public void EchoReply(String text) {
       try{
           dos.writeUTF(text); dos.flush();
       }catch (IOException e ) {}
    }
    private void Log(String text) {
        //System.out.println(text);
    }
}
