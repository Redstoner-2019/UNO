package me.redstoner2019.main.data.packets.gamepackets;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.serverhandling.Packet;

public class PlaceCardPacket extends Packet {
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public PlaceCardPacket(Card card) {
        this.card = card;
    }
}
