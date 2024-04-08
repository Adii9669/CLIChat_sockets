import java.io.*;
import java.net.*;


public class Server {

    private ServerSocket serverSocket;


    //constructor
    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }


    //start server
    public void serverStart(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client just Entered the chat...");

                ClientHandlers clienthandler = new ClientHandlers(socket);
                
                Thread thread = new Thread(clienthandler);
                thread.start();
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }

    //close server
    public void closeServer(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //main 
    public static void main (String[] args)throws Exception{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server= new Server(serverSocket);
        server.serverStart();
        // server.closeServer();
    }
}