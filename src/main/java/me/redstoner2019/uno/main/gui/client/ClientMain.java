package me.redstoner2019.uno.main.gui.client;

import me.redstoner2019.server.events.ConnectionFailedEvent;
import me.redstoner2019.server.events.ConnectionLostEvent;
import me.redstoner2019.server.events.ConnectionSuccessEvent;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odclient.ODClient;

public class ClientMain extends ODClient {
    public void init(){
        setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {

            }
        });
        setConnectionLostEvent(new ConnectionLostEvent() {
            @Override
            public void onConnectionLostEvent(String reason) {

            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {

            }
        });
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet) {

            }
        });
        startSender();
    }
}
