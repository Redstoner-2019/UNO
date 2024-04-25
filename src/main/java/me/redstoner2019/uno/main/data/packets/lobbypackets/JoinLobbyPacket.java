package me.redstoner2019.uno.main.data.packets.lobbypackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class JoinLobbyPacket extends Packet {
    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public JoinLobbyPacket(String ID) {
        this.ID = ID;
    }
}
