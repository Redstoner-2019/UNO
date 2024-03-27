package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class JoinPacket extends Packet {
    private String displayName;
    private String username;
    private String version;
    private String password;

    public JoinPacket(String version, String username, String password, String displayName) {
        this.username = username;
        this.version = version;
        this.password = password;
        this.displayName = displayName;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
