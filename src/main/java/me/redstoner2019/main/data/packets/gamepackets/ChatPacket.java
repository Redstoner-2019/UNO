package me.redstoner2019.main.data.packets.gamepackets;

import me.redstoner2019.serverhandling.Packet;

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
