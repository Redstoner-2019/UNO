package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class SetColorPacket extends Packet {
    public String color = "";

    public SetColorPacket(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "SetColorPacket{" +
                "color='" + color + '\'' +
                '}';
    }
}
