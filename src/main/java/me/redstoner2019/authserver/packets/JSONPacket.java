package me.redstoner2019.authserver.packets;

import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.server.util.Util;

public class JSONPacket extends Packet {
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public JSONPacket(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return Util.prettyJSON(json);
    }
}
