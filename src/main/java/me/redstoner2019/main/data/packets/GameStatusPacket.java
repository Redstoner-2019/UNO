package me.redstoner2019.main.data.packets;

import me.redstoner2019.serverhandling.Packet;

public class GameStatusPacket extends Packet {
    public int status = 0;
    /**
     * 0 = Waiting for players
     * 1 = Joined Game, still waiting
     * 2 = Full, waiting for confirmations
     * 3 = Full, unable to join
     * 4 = Game currently running
     */
}
