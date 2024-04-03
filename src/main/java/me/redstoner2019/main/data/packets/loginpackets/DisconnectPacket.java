package me.redstoner2019.main.data.packets.loginpackets;

import me.redstoner2019.serverhandling.Packet;

public class DisconnectPacket extends Packet {
    private String disconnectReason;

    public String getDisconnectReason() {
        return disconnectReason;
    }

    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public DisconnectPacket(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }
}
