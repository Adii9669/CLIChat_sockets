import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String Name;

    // constructor
    public Client(Socket socket, String Name) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.Name = Name;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    // message to send yaha thread use hoga
    // yaha newline hata ke dhekna hai later
    public void sendMessage(){
        try{
            bufferedWriter.write(Name);
            bufferedWriter.newLine();
            bufferedWriter.flush();


            Scanner sc = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSend = sc.nextLine();
                bufferedWriter.write(Name + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch(IOException e){
            closeAll(socket, bufferedReader, bufferedWriter);
        }
     }

    // method to read message using thread
    public void readMessage() {
        new Thread(new Runnable() {
            // @Override
            public void run() {
                String msgFromGrpChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGrpChat = bufferedReader.readLine();
                        if(msgFromGrpChat == "exit"){
                            socket.close();
                            System.out.println("Client" + Name + " has Left the chat");
                        }
                        System.out.println(msgFromGrpChat);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();

    }

    // close on client side
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try{
            if(bufferedReader!= null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.getStackTrace();
        }
    }

    // main method 
    public static void main(String[] args)throws UnknownHostException, IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("ENTER YOUR NAME: ");
        String name = sc.nextLine();
        String servername = "2409:40d2:6d:f48a:cfb2:8743:ef62:988d";
        Socket socket = new Socket(servername , 1234);
        Client client = new Client(socket, name);
        client.readMessage();
        client.sendMessage();
        sc.close();
    }
}

