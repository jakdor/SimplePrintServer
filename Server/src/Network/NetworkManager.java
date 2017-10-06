package Network;

import Server.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

    private int port = 8000;
    private ServerSocket serverSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private boolean connected = false;

    public void startServer(int port){
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
            Socket sock = serverSocket.accept();

            OutputStream oStream = sock.getOutputStream();
            printWriter = new PrintWriter(oStream, true);

            InputStream iStream = sock.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(iStream));
        }
        catch (Exception e){
            Main.log("Socket server problem / application closed, " + e.toString());
        }
    }

    public void shutdownServer(){
        try {
            serverSocket.close();
        }
        catch (Exception e){
            Main.log("Can't shut down socket server, " + e.toString());
        }
    }

    public String readMessage(){
        String receiveMessage;
        try {
            receiveMessage = bufferedReader.readLine();
            if(receiveMessage != null){
                connected = true;
                System.out.println(receiveMessage);
                if(receiveMessage.equals("@reboot")){
                    shutdownServer();
                    startServer(this.port);
                }
            }
            else if(connected){
                connected = false;
                shutdownServer();
                startServer(this.port);
            }
        }
        catch (Exception e){
            Main.log("Unable to read receive buffer, " + e.toString());
            return "";
        }

        return receiveMessage;
    }
}
