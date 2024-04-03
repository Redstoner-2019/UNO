package me.redstoner2019.main.data.packets.loginpackets;

import me.redstoner2019.serverhandling.Packet;

public class LoginPacket extends Packet {
    private String username;
    private String password;
    private String customDisplayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomDisplayName() {
        return customDisplayName;
    }

    public void setCustomDisplayName(String customDisplayName) {
        this.customDisplayName = customDisplayName;
    }

    public LoginPacket(String username, String password, String customDisplayName) {
        this.username = username;
        this.password = password;
        this.customDisplayName = customDisplayName;
    }
}
