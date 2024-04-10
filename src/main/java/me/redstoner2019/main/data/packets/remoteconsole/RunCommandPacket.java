package me.redstoner2019.main.data.packets.remoteconsole;

import me.redstoner2019.serverhandling.Packet;

public class RunCommandPacket extends Packet {
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public RunCommandPacket(String command) {
        this.command = command;
    }
}
