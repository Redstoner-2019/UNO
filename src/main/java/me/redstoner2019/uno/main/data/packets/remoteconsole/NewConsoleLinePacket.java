package me.redstoner2019.uno.main.data.packets.remoteconsole;

import me.redstoner2019.server.defaultpackets.Packet;

public class NewConsoleLinePacket extends Packet {
    private String line;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public NewConsoleLinePacket(String line) {
        this.line = line;
    }
}
