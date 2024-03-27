package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class DisconnectPacket extends Packet {
    private String reason;

    public DisconnectPacket(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
