package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class PlayerHasWonPacket extends Packet {
    public String message = "";

    public PlayerHasWonPacket(String message) {
        this.message = message;
    }
}
