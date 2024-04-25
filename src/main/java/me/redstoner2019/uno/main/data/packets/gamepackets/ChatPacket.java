package me.redstoner2019.uno.main.data.packets.gamepackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class ChatPacket extends Packet {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatPacket(String message) {
        this.message = message;
    }
}
