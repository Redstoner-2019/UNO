package me.redstoner2019.uno.main.data.packets.lobbypackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class UpdateLobbyPacket extends Packet {
    private int cardsPerPlayer;
    private int decks;
    private boolean stacking;
    private boolean sevenSwap;
    private boolean jumpIn;
    private boolean chat;

    @Override
    public String toString() {
        return "UpdateLobbyPacket{" +
                "cardsPerPlayer=" + cardsPerPlayer +
                ", decks=" + decks +
                ", stacking=" + stacking +
                ", sevenSwap=" + sevenSwap +
                ", jumpIn=" + jumpIn +
                '}';
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

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public UpdateLobbyPacket(int cardsPerPlayer, int decks, boolean stacking, boolean sevenSwap, boolean jumpIn, boolean chat) {
        this.cardsPerPlayer = cardsPerPlayer;
        this.decks = decks;
        this.stacking = stacking;
        this.sevenSwap = sevenSwap;
        this.jumpIn = jumpIn;
        this.chat = chat;
    }
}
