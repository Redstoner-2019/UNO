package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.data.guis.ConnectGUI;
import me.redstoner2019.main.data.guis.GUI;
import me.redstoner2019.main.data.packets.ClientDataPacket;
import me.redstoner2019.main.data.packets.JoinPacket;
import me.redstoner2019.main.data.packets.PlayerHasWonPacket;
import me.redstoner2019.serverhandling.*;

import javax.swing.*;

import static me.redstoner2019.main.data.guis.GUI.isCurrentTurn;

public class ClientMain extends Client {
    public static void connect(String ip, String username){
        System.out.println("Connection");
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet, ClientHandler handler) {
                if(packet instanceof ClientDataPacket p){
                    GUI.playerCardStack = p.clientCards;
                    GUI.lastPlaced = p.lastCardPut;
                    isCurrentTurn = p.isTurn;
                    GUI.currentPlayer = p.currentPlayer;
                    GUI.unoButton.setEnabled(isCurrentTurn);
                    GUI.drawButton.setEnabled(p.canDraw && isCurrentTurn);
                    GUI.skipButton.setEnabled(p.canSkip && isCurrentTurn);
                    String nextUpString = "";
                    for(String s : p.nextUp){
                        nextUpString += s + "\n\n";
                    }
                    GUI.nextUp.setText(nextUpString);
                    GUI.placementLabel.setText(p.platzierung);
                    if(!p.platzierung.isEmpty()){
                        JOptionPane.showMessageDialog(null,"You placed " + p.platzierung + "!");
                        handler.disconnect();
                        System.exit(0);
                    }
                }
                if(packet instanceof PlayerHasWonPacket p){
                    System.out.println(p.message);
                    GUI.frame.dispose();
                    JOptionPane.showMessageDialog(null,p.message);
                }
            }
        });
        setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {
                System.out.println("Failed");
                ConnectGUI.connect.setEnabled(true);
                ConnectGUI.frame.setTitle("Connect - Failed to connect - " + reason.getLocalizedMessage());
            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {
                System.out.println("Success");
                ConnectGUI.frame.dispose();
            }
        });

        connect(ip,8008);
        try {
            GUI.main(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendObject(new JoinPacket(username));
    }
}
