package me.redstoner2019.main.data.packets.lobbypackets;

import me.redstoner2019.serverhandling.Packet;

import java.util.ArrayList;
import java.util.List;

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
