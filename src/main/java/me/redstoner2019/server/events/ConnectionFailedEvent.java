package me.redstoner2019.server.events;

public interface ConnectionFailedEvent {
    void onConnectionFailedEvent(Exception reason);
}
