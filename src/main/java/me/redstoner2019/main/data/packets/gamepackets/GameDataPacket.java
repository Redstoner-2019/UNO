package me.redstoner2019.main.data.packets.gamepackets;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.serverhandling.Packet;

import java.util.List;

public class GameDataPacket extends Packet {
    private boolean canSkip;
    private boolean canDraw;
    private boolean canUNO;
    private boolean isTurn;
    private List<String> nextPlayers;
    private Card lastPlaced;
    private List<Card> ownDeck;
    private int cardsDue;

    public int getCardsDue() {
        return cardsDue;
    }

    public void setCardsDue(int cardsDue) {
        this.cardsDue = cardsDue;
    }

    public boolean isCanSkip() {
        return canSkip;
    }

    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip;
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public boolean isCanUNO() {
        return canUNO;
    }

    public void setCanUNO(boolean canUNO) {
        this.canUNO = canUNO;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public List<String> getNextPlayers() {
        return nextPlayers;
    }

    public void setNextPlayers(List<String> nextPlayers) {
        this.nextPlayers = nextPlayers;
    }

    public Card getLastPlaced() {
        return lastPlaced;
    }

    public void setLastPlaced(Card lastPlaced) {
        this.lastPlaced = lastPlaced;
    }

    public List<Card> getOwnDeck() {
        return ownDeck;
    }

    public void setOwnDeck(List<Card> ownDeck) {
        this.ownDeck = ownDeck;
    }

    public GameDataPacket(boolean canSkip, boolean canDraw, boolean canUNO, boolean isTurn, List<String> nextPlayers, Card lastPlaced, List<Card> ownDeck, int cardsDue) {
        this.canSkip = canSkip;
        this.canDraw = canDraw;
        this.canUNO = canUNO;
        this.isTurn = isTurn;
        this.nextPlayers = nextPlayers;
        this.lastPlaced = lastPlaced;
        this.ownDeck = ownDeck;
        this.cardsDue = cardsDue;
    }
}
