package me.redstoner2019.main.data.packets.loginpackets;

import me.redstoner2019.serverhandling.Packet;

public class LoginPacket extends Packet {
    private String token;

    public LoginPacket(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
