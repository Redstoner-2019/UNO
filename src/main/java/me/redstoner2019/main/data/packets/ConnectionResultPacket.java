package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class ConnectionResultPacket extends Packet{
    private int status;
    private String message;

    public ConnectionResultPacket(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConnectionResultPacket{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
