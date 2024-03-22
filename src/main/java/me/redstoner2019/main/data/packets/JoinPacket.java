package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class JoinPacket extends Packet {
    private String username;

    public JoinPacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "JoinPacket{" +
                "username='" + username + '\'' +
                '}';
    }
}
