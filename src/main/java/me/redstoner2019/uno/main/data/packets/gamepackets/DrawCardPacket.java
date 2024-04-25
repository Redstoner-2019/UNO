package me.redstoner2019.uno.main.data.packets.gamepackets;

import me.redstoner2019.server.defaultpackets.Packet;

public class DrawCardPacket extends Packet {
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public DrawCardPacket(int amount) {
        this.amount = amount;
    }
    public DrawCardPacket() {
        amount = 1;
    }
}
