package me.redstoner2019.server.defaultpackets;


import me.redstoner2019.server.util.ConnectionProtocol;

public class ConnectRequestPacket extends Packet {
    private ConnectionProtocol protocol;

    public ConnectionProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    public ConnectRequestPacket(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "ConnectRequestPacket{" +
                "protocol=" + protocol +
                '}';
    }
}
