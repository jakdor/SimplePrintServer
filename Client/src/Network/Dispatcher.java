package Network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;

public class Dispatcher {

    private Logger logger;
    private NetworkManager networkManager;

    public Dispatcher(NetworkManager networkManager, Logger logger){
        this.networkManager = networkManager;
        this.logger = logger;
    }

    public void send(String command, int mode, String fileName, String filePath) {

        Path path = Paths.get(filePath);

        try {
            byte[] data = Files.readAllBytes(path);
            networkManager.send(serialize(new Carrier(command, mode, fileName, new String(data))));
        }
        catch (Exception e){
            logger.info("can't send Carrier to server, " + e.toString());
        }
    }

    private String serialize( Serializable serializable ) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
        objectOutputStream.writeObject( serializable );
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }
}
