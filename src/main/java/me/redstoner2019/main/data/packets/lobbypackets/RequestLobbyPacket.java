package me.redstoner2019.main.data.packets.lobbypackets;

import me.redstoner2019.serverhandling.Packet;

public class RequestLobbyPacket extends Packet {
    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public RequestLobbyPacket(String ID) {
        this.ID = ID;
    }
}
