
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    protected static final Logger logger = Logger.getLogger("log");

    public static void main (String[] args) throws InterruptedException {

        try(ServerSocket serverSocket = new ServerSocket(2828)) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection established!");

            //Channel for output
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            logger.log(Level.INFO,"DOS created");

            //Channel for input
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            System.out.println("DIS created");

            //Works with client until socket is closed
            while(!clientSocket.isClosed()) {
                logger.log(Level.INFO,"Server reading from channel");
                System.out.println("");

                //Waiting for client data
                String entry = input.readUTF();
                logger.log(Level.INFO, "READ: " + entry);
                System.out.println("");

                //Checks if client is willing to quit
                if(entry.equalsIgnoreCase("quit")) {
                    logger.log(Level.INFO, "Client initialize connections suicide...");
                    output.writeUTF("Server reply: " + entry + " - OK");
                        output.flush();
                        Thread.sleep(3000);
                        break;
                }
                //Echo-reply to client
                output.writeUTF("Server reply: " + entry + " - OK");
                logger.log(Level.INFO, "Server wrote message to client." +
                        "\nMessage: " + entry);
                System.out.println("");

                output.flush();

            }

            logger.log(Level.INFO, "Client disconnected");
            logger.log(Level.INFO, "Closing connections and channels. ");

            input.close();
            output.close();

            clientSocket.close();

            logger.log(Level.INFO, "Client disconnectd. Closing connections and channels - DONE.");
        }
        catch(IOException e) {
            logger.log(Level.INFO, "An error has happened!");
            e.printStackTrace();
        }
    }
}
