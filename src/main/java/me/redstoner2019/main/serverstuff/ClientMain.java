package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.guis.ConnectGUI;
import me.redstoner2019.main.data.guis.GUI;
import me.redstoner2019.main.data.packets.ClientDataPacket;
import me.redstoner2019.main.data.packets.JoinPacket;
import me.redstoner2019.main.data.packets.PlayerHasWonPacket;
import me.redstoner2019.main.data.packets.PreGamePacket;
import me.redstoner2019.serverhandling.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import static me.redstoner2019.main.data.guis.GUI.*;

public class ClientMain extends Client {
    public static void connect(String ip, String username){
        System.out.println("Connection");
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet, ClientHandler handler) {
                if((packet instanceof PreGamePacket p)){
                    preGame = true;
                    prePlayers = p.getPlayers();
                    minPlayers = p.getMinPlayers();
                    countdown = p.getCountdown();
                    cardsPerPlayer = p.getCardsPerPlayer();
                }else if(packet instanceof ClientDataPacket p){
                    preGame = false;
                    ArrayList<Card> cards = new ArrayList<>(p.clientCards);
                    if(!playerCardStack.isEmpty()) Collections.sort(cards, new Comparator<Card>() {
                        @Override
                        public int compare(Card o1, Card o2) {
                            return (o1.getColorAsINT()- o2.getColorAsINT()) - (o1.getNum()-o2.getNum());
                        }
                    });

                    Collections.reverse(cards);

                    GUI.playerCardStack = cards;

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
                    if(!p.platzierung.isEmpty()){
                        JOptionPane.showMessageDialog(null,"You placed " + p.platzierung + "!");
                        handler.disconnect();
                        System.exit(0);
                    }
                }else if(packet instanceof PlayerHasWonPacket p){
                    GUI.frame.setVisible(false);
                    GUI.frame.dispose();
                    playerCardStack.clear();
                    currentPlayer = "";
                    lastPlaced = null;
                    JOptionPane.showMessageDialog(null,p.message);
                    ConnectGUI.main(new String[0]);
                    disconnect();
                } else {
                    System.out.println(packet.getClass());
                }
            }
        });
        setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {
                System.out.println("Failed");
                ConnectGUI.connect.setEnabled(true);
                ConnectGUI.connect.setText("CONNECT");
                ConnectGUI.frame.setTitle("Connect - Failed to connect - " + reason.getLocalizedMessage());
            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {
                System.out.println("Success");
                try {
                    GUI.main(new String[0]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                while (frame == null || !frame.isVisible()){
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("waiting");
                }
                ConnectGUI.frame.dispose();
            }
        });

        connect(ip,8008);
        if(isConnected()) sendObject(new JoinPacket(username)); else {ConnectGUI.connect.setEnabled(true);}
    }
}
