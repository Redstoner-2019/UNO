package me.redstoner2019.main.data.packets.lobbypackets;

import me.redstoner2019.serverhandling.Packet;

public class StatsPacket extends Packet {
    private int gamesPlayed;
    private int gamesWon;
    private int placed4;

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getPlaced4() {
        return placed4;
    }

    public void setPlaced4(int placed4) {
        this.placed4 = placed4;
    }

    public StatsPacket(int gamesPlayed, int gamesWon, int placed4) {
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.placed4 = placed4;
    }
}
