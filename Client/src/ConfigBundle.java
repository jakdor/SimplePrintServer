import java.io.Serializable;
import java.util.Vector;

public class ConfigBundle implements Serializable {

    private Vector<Config> configs;

    private final static long serialVersionUID = 1234634567893802694L;

    private ConfigBundle(){
    }

    public ConfigBundle(Vector<Config> configs) {
        this.configs = configs;
    }

    public Vector<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(Vector<Config> configs) {
        this.configs = configs;
    }
}
