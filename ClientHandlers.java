import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandlers implements Runnable {

    public static ArrayList<ClientHandlers> clients  = new ArrayList<>();
    public Socket socket;
    private BufferedReader buffReader;
    private BufferedWriter buffWriter;
    private String Name;

    // constructor
    public ClientHandlers(Socket socket) {
        try {
            this.socket = socket;
            this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.Name = buffReader.readLine();
            clients.add(this);
            broadcastMessage("SERVER:" + Name + " has just entered in the room");

        } catch (IOException e) {
            closeAll(socket, buffReader, buffWriter);
        }
    }

    // run method to override
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = buffReader.readLine();
                if(messageFromClient == "exit"){
                    System.out.println("Client "+ Name + " has left the chat");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                else{
                broadcastMessage(messageFromClient);
            }
            } catch (IOException e) {
                closeAll(socket, buffReader, buffWriter);
                break;
            }
        }
    }

    // patr padh ke bhejo
    public void broadcastMessage(String patr) {

        for (ClientHandlers clienthandlers : clients) {
            try {
                if(!clienthandlers.Name.equals(Name)){
                    clienthandlers.buffWriter.write(patr);
                    clienthandlers.buffWriter.newLine();
                    clienthandlers.buffWriter.flush();
                }
            } catch (IOException e) {
                closeAll(socket, buffReader, buffWriter);
            }
        }
    }

    // removing client
    public void removeClientHandler() {
        clients.remove(this);
        broadcastMessage("SERVER" + Name + "has left the chat");
    }

    // closing up (pack uP)
    public void closeAll(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter) {
        removeClientHandler();
        try {
            if (buffReader != null) {
                buffReader.close();
            }
            if (buffWriter != null) {
                buffWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

}