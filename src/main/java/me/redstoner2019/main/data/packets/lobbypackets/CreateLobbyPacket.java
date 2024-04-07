package me.redstoner2019.main.data.packets.lobbypackets;

import me.redstoner2019.serverhandling.Packet;

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
