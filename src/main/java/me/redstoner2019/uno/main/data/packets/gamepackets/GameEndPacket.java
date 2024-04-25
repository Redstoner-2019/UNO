package me.redstoner2019.uno.main.data.packets.gamepackets;

import me.redstoner2019.server.defaultpackets.Packet;

import java.util.List;

public class GameEndPacket extends Packet {
    private String winner;
    private List<String> placement;

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<String> getPlacement() {
        return placement;
    }

    public void setPlacement(List<String> placement) {
        this.placement = placement;
    }

    public GameEndPacket(String winner, List<String> placement) {
        this.winner = winner;
        this.placement = placement;
    }
}
