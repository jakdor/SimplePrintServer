import java.io.*;
import java.net.*;

public class Main
{
    private static ServerSocket serverSocket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) throws Exception
    {
        startServer();

        String receiveMessage;
        while(true)
        {
            try{
                Thread.sleep(10);
            }
            catch (Exception e){
                System.out.println("Error: unable to skip 10ms\n" + e.getMessage());
            }

            try{
                if ((receiveMessage = bufferedReader.readLine()) != null) {
                    System.out.println(receiveMessage);

                    if(receiveMessage.equals("@reboot")){
                        serverSocket.close();
                        startServer();
                    }

                }
            }
            catch (Exception e){
                System.out.println("Error: Received null msg\n" + e.getMessage());
            }
        }
    }

    private static void startServer(){
        try {
            serverSocket = new ServerSocket(8080);
            Socket sock = serverSocket.accept();

            OutputStream oStream = sock.getOutputStream();
            printWriter = new PrintWriter(oStream, true);

            InputStream iStream = sock.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(iStream));
        }
        catch (Exception e){
            System.out.println("Error: Socket server not starting\n" + e.getMessage());
        }
    }
}