package me.redstoner2019.main.data.packets;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.serverhandling.Packet;

public class PutCardPacket extends Packet {
    public Card card = null;

    public PutCardPacket(Card card) {
        this.card = card;
    }
}
