package me.redstoner2019.server.defaultpackets;

import me.redstoner2019.uno.main.Main;

import java.io.Serial;
import java.io.Serializable;

public class Packet implements Serializable {
    public String version = Main.getVersion();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Serial
    private static final long serialVersionUID = 0;
    public String uuid = null;
    public long getChecksum(){
        return 0;
    }
}
