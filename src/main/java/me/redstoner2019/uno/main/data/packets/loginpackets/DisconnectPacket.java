package me.redstoner2019.uno.main.data.packets.loginpackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class DisconnectPacket extends Packet {
    private String disconnectReason;
    private int code = 200;

    public DisconnectPacket(String disconnectReason, int code) {
        this.disconnectReason = disconnectReason;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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
