package me.redstoner2019.uno.main.data.packets.gamepackets;

import me.redstoner2019.uno.main.data.Card;
import me.redstoner2019.server.defaultpackets.Packet;

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
