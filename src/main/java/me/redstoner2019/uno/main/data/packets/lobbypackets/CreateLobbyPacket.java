package me.redstoner2019.uno.main.data.packets.lobbypackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class CreateLobbyPacket extends Packet {
    private String forceCode = "";

    public CreateLobbyPacket() {
    }

    public CreateLobbyPacket(String forceCode) {
        this.forceCode = forceCode;
    }

    public String getForceCode() {
        return forceCode;
    }

    public void setForceCode(String forceCode) {
        this.forceCode = forceCode;
    }
}
