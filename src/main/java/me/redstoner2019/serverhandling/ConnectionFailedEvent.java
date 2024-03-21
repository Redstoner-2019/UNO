package me.redstoner2019.serverhandling;

public interface ConnectionFailedEvent {
    public void onConnectionFailedEvent(Exception reason);
}
