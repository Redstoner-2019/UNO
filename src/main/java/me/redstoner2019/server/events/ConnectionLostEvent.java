package me.redstoner2019.server.events;

public interface ConnectionLostEvent {
    void onConnectionLostEvent(String reason);
}
