package me.redstoner2019.uno.main.data.packets.loginpackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class CreateAccountPacket extends Packet {
    private String username = "";
    private String password = "";
    private String displayname = "";

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

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public CreateAccountPacket(String username, String password, String displayname) {
        this.username = username;
        this.password = password;
        this.displayname = displayname;
    }
}
