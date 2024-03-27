package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class CreateAccountPacket extends Packet {
    private String username;
    private String displayName;
    private String password;

    public CreateAccountPacket(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
