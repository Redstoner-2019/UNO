package me.redstoner2019.uno.main.serverstuff;

import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odclient.ODClient;
import me.redstoner2019.server.util.ConnectionProtocol;
import me.redstoner2019.uno.main.data.packets.remoteconsole.InitializeConsoleClientPacket;
import me.redstoner2019.uno.main.data.packets.remoteconsole.NewConsoleLinePacket;

import java.util.Scanner;

public class ConsoleClient extends ODClient {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        /*connect(scanner.nextLine(),8008, ConnectionProtocol.UDP);
        startSender();
        sendObject(new InitializeConsoleClientPacket());
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet) {
                if(packet instanceof NewConsoleLinePacket p){
                    System.out.println(p.getLine());
                }
            }
        });*/
    }
}
