package me.redstoner2019.uno.main.data.packets.gamepackets;


import me.redstoner2019.server.defaultpackets.Packet;

public class ActionPacket extends Packet {
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ActionPacket(String action) {
        this.action = action;
    }
}
