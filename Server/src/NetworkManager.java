import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class NetworkManager {

    private int port = 8000;
    private ServerSocket serverSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    void startServer(int port){
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
            Main.logger.info("Error: Socket server not starting, " + e.toString());
        }
    }

    void shutdownServer(){
        try {
            serverSocket.close();
        }
        catch (Exception e){
            Main.logger.info("Error shutting down socket server, " + e.toString());
        }
    }

    String readMessage(){
        String receiveMessage;
        try {
            if((receiveMessage = bufferedReader.readLine()) != null){
                System.out.println(receiveMessage);
                if(receiveMessage.equals("@reboot")){
                    shutdownServer();
                    startServer(this.port);
                }
            }
        }
        catch (Exception e){
            Main.logger.info("Unable to read receive buffer, " + e.toString());
            return "";
        }

        return receiveMessage;
    }
}
