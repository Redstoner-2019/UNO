package me.redstoner2019.main.data.packets.loginpackets;

import me.redstoner2019.serverhandling.Packet;

public class Ping extends Packet {
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Ping(long time) {
        this.time = time;
    }
}
