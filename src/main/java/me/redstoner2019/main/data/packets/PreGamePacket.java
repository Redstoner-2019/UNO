package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

import java.util.HashMap;

public class PreGamePacket extends Packet {
    private HashMap<String,Boolean> players = new HashMap<>();
    private int countdown = 0;
    private int minPlayers = 0;
    private int cardsPerPlayer = 0;

    public PreGamePacket(HashMap<String, Boolean> players, int countdown, int minPlayers, int cardsPerPlayer) {
        this.players = players;
        this.countdown = countdown;
        this.minPlayers = minPlayers;
        this.cardsPerPlayer = cardsPerPlayer;
    }

    public HashMap<String, Boolean> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, Boolean> players) {
        this.players = players;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public void setCardsPerPlayer(int cardsPerPlayer) {
        this.cardsPerPlayer = cardsPerPlayer;
    }

    @Override
    public String toString() {
        return "PreGamePacket{" +
                "players=" + players +
                ", countdown=" + countdown +
                ", minPlayers=" + minPlayers +
                ", cardsPerPlayer=" + cardsPerPlayer +
                '}';
    }
}