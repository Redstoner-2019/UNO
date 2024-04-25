package me.redstoner2019.uno.main.data.packets.lobbypackets;

import me.redstoner2019.server.defaultpackets.Packet;

import java.util.HashMap;

public class LobbyInfoPacket extends Packet {
    private String code;
    private boolean owner;
    private HashMap<String, String> players;
    private int cardsPerPlayer;
    private int decks;
    private boolean stacking;
    private boolean sevenSwap;
    private boolean jumpIn;
    private boolean chat;

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return "LobbyInfoPacket{" +
                "code='" + code + '\'' +
                ", owner=" + owner +
                ", players=" + players +
                ", cardsPerPlayer=" + cardsPerPlayer +
                ", decks=" + decks +
                ", stacking=" + stacking +
                ", sevenSwap=" + sevenSwap +
                ", jumpIn=" + jumpIn +
                ", chat=" + chat +
                '}';
    }

    public LobbyInfoPacket(String code, boolean owner, HashMap<String, String> players, int cardsPerPlayer, int decks, boolean stacking, boolean sevenSwap, boolean jumpIn, boolean chat) {
        this.code = code;
        this.owner = owner;
        this.players = players;
        this.cardsPerPlayer = cardsPerPlayer;
        this.decks = decks;
        this.stacking = stacking;
        this.sevenSwap = sevenSwap;
        this.jumpIn = jumpIn;
        this.chat = chat;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, String> players) {
        this.players = players;
    }

    public int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public void setCardsPerPlayer(int cardsPerPlayer) {
        this.cardsPerPlayer = cardsPerPlayer;
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public boolean isStacking() {
        return stacking;
    }

    public void setStacking(boolean stacking) {
        this.stacking = stacking;
    }

    public boolean isSevenSwap() {
        return sevenSwap;
    }

    public void setSevenSwap(boolean sevenSwap) {
        this.sevenSwap = sevenSwap;
    }

    public boolean isJumpIn() {
        return jumpIn;
    }

    public void setJumpIn(boolean jumpIn) {
        this.jumpIn = jumpIn;
    }
}
