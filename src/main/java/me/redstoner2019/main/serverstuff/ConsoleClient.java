package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.data.packets.remoteconsole.InitializeConsoleClientPacket;
import me.redstoner2019.main.data.packets.remoteconsole.NewConsoleLinePacket;
import me.redstoner2019.serverhandling.Client;
import me.redstoner2019.serverhandling.PacketListener;

import java.util.Scanner;

public class ConsoleClient extends Client {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        connect(scanner.nextLine(),8008);
        startSender();
        sendObject(new InitializeConsoleClientPacket());
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet) {
                if(packet instanceof NewConsoleLinePacket p){
                    System.out.println(p.getLine());
                }
            }
        });
    }
}
