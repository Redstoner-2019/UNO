package me.redstoner2019.uno.main.data.packets.lobbypackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class LobbiesPacket extends Packet {
    private String[] lobbies;

    public String[] getLobbies() {
        return lobbies;
    }

    public void setLobbies(String[] lobbies) {
        this.lobbies = lobbies;
    }

    public LobbiesPacket(String[] lobbies) {
        this.lobbies = lobbies;
    }
}
