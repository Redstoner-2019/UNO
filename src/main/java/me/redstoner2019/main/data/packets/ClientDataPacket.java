package me.redstoner2019.main.data.packets;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.serverhandling.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientDataPacket extends Packet {
    public List<Card> clientCards = new ArrayList<>();
    public Card lastCardPut = null;
    public boolean isTurn = false;
    public String currentPlayer = "";
    public boolean canDraw = false;
    public boolean canSkip = false;
    public List<String> nextUp = new ArrayList<>();
    public String platzierung = "";

    public ClientDataPacket(List<Card> clientCards, Card lastCardPut, boolean isTurn, String currentPlayer, boolean canDraw, boolean canSkip, List<String> nextUp, String platzierung) {
        this.clientCards = clientCards;
        this.lastCardPut = lastCardPut;
        this.isTurn = isTurn;
        this.currentPlayer = currentPlayer;
        this.canDraw = canDraw;
        this.canSkip = canSkip;
        this.nextUp = nextUp;
        this.platzierung = platzierung;
    }
}
