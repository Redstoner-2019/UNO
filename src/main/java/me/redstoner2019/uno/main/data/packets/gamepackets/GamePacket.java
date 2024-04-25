package me.redstoner2019.uno.main.data.packets.gamepackets;

import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.uno.main.data.Player;

public class GamePacket {
    private Player player;
    private Packet packet;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public GamePacket(Player player, Packet packet) {
        this.player = player;
        this.packet = packet;
    }
}
