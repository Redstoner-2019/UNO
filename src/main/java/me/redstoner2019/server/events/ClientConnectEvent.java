package me.redstoner2019.server.events;

import me.redstoner2019.server.odserver.ClientHandler;

public interface ClientConnectEvent {
    void connectEvent(ClientHandler handler) throws Exception;
}
