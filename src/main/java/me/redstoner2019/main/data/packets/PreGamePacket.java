package me.redstoner2019.main.data.packets;

import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.serverhandling.Packet;

import java.util.HashMap;

public class PreGamePacket extends Packet {
    private HashMap<String,Boolean> players = new HashMap<>();
    private int countdown = 0;
    private int minPlayers = 0;
    private int cardsPerPlayer = 0;
    private Userdata data;

    public PreGamePacket(HashMap<String, Boolean> players, int countdown, int minPlayers, int cardsPerPlayer, Userdata data) {
        this.players = players;
        this.countdown = countdown;
        this.minPlayers = minPlayers;
        this.cardsPerPlayer = cardsPerPlayer;
        this.data = data;
    }

    public Userdata getData() {
        return data;
    }

    public void setData(Userdata data) {
        this.data = data;
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
        String str = "PreGamePacket{";
        for(String s : players.keySet()){
            str+=s+ " -> " +s.isEmpty() + ", ";
        }
        str+=", countdown=" + countdown +
                ", minPlayers=" + minPlayers +
                ", cardsPerPlayer=" + cardsPerPlayer +
                '}';
        return str;
    }
}