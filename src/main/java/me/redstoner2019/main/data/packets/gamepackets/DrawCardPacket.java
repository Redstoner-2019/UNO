package me.redstoner2019.main.data.packets.gamepackets;

import me.redstoner2019.serverhandling.Packet;

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
