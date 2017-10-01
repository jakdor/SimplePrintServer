import java.io.*;
import java.net.*;

class NetworkManager {

    private PrintWriter printWriter;
    private BufferedReader receiveRead;

    void connect(String ConnectIP, int port){
        try {
            Socket sock = new Socket(ConnectIP, port);

            OutputStream outputStream = sock.getOutputStream();
            this.printWriter = new PrintWriter(outputStream, true);

            InputStream inputStream = sock.getInputStream();
            this.receiveRead = new BufferedReader(new InputStreamReader(inputStream));
        }
        catch (Exception e){
            Main.logger.info("Can't connect to server, " + e.toString());
        }
    }

    public String receive(){
        try {
            String receiveMessage;
            if ((receiveMessage = this.receiveRead.readLine()) != null){
                return receiveMessage;
            }
        }
        catch (Exception e){
            Main.logger.info("receive() failed to read buffer, " + e.toString());
        }

        return null;
    }

    void send(String input){
        try {
            this.printWriter.println(input);
            this.printWriter.flush();
        }
        catch (Exception e){
            Main.logger.info("failed to send msg to server, " + e.toString());
        }
    }
}

