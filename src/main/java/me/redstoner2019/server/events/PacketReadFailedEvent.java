package me.redstoner2019.server.events;

import me.redstoner2019.server.odserver.ClientHandler;

public interface PacketReadFailedEvent {
    void onPacketReadFailed(String error, ClientHandler handler);
}
