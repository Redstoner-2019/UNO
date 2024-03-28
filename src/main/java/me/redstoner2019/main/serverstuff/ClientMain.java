package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.guis.ConnectGUI;
import me.redstoner2019.main.data.guis.GUI;
import me.redstoner2019.main.data.packets.*;
import me.redstoner2019.serverhandling.*;

import javax.swing.*;

import java.awt.*;
import java.util.*;

import static me.redstoner2019.main.data.guis.GUI.*;

public class ClientMain extends Client {
    public static void connect(String ip, String username, String password, String displayName){
        System.out.println("Connection");
        setConnectionLostEvent(new ConnectionLostEvent() {
            @Override
            public void onConnectionLostEvent() {
                if(frame == null) return;
                GUI.frame.setVisible(false);
                GUI.frame.dispose();
                playerCardStack.clear();
                currentPlayer = "";
                lastPlaced = null;
                JOptionPane.showMessageDialog(null,"Connection Lost");
                ConnectGUI.frame.setVisible(true);
                ConnectGUI.connect.setText("CONNECT");
                ConnectGUI.connect.setEnabled(true);
                preGame = true;
                countdown = 10;
                prePlayers = new HashMap<>();
            }
        });
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet, ClientHandler handler) {
                if(!(packet instanceof PreGamePacket) && !(packet instanceof ClientDataPacket)){
                    System.out.println(packet.getClass() + " -> " + packet.toString());
                }
                if((packet instanceof PreGamePacket p)){
                    preGame = true;
                    prePlayers = p.getPlayers();
                    minPlayers = p.getMinPlayers();
                    countdown = p.getCountdown();
                    cardsPerPlayer = p.getCardsPerPlayer();
                    data = p.getData();
                    //System.out.println(p);
                }else if(packet instanceof ClientDataPacket p){
                    preGame = false;
                    ArrayList<Card> cards = new ArrayList<>(p.clientCards);
                    if(!playerCardStack.isEmpty()) Collections.sort(cards);

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
                    JOptionPane.showMessageDialog(frame,p.message);
                    ConnectGUI.frame.setVisible(true);
                    ConnectGUI.connect.setText("CONNECT");
                    ConnectGUI.connect.setEnabled(true);
                    preGame = true;
                    countdown = 10;
                    prePlayers = new HashMap<>();
                    //disconnect();
                } else if(packet instanceof DisconnectPacket p) {
                    if(frame == null) {
                        JOptionPane.showMessageDialog(ConnectGUI.frame,p.getReason());
                        ConnectGUI.frame.setVisible(true);
                        ConnectGUI.connect.setText("CONNECT");
                        ConnectGUI.connect.setEnabled(true);
                        return;
                    }
                    String reason = p.getReason();
                    GUI.frame.setVisible(false);
                    GUI.frame.dispose();
                    playerCardStack.clear();
                    currentPlayer = "";
                    lastPlaced = null;
                    JOptionPane.showMessageDialog(frame,reason);
                    ConnectGUI.frame.setVisible(true);
                    ConnectGUI.connect.setText("CONNECT");
                    ConnectGUI.connect.setEnabled(true);
                    preGame = true;
                    countdown = 10;
                    prePlayers = new HashMap<>();
                } else if(packet instanceof ConnectionResultPacket p){
                    System.out.println(p);
                    if(p.getStatus() == 100){
                        ConnectGUI.loginResult.setText(p.getMessage());
                        ConnectGUI.loginResult.setForeground(Color.GREEN);
                        try {
                            GUI.main(new String[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ConnectGUI.frame.setVisible(false);
                    } else if(p.getStatus() == 405) {
                        ConnectGUI.loginResult.setText(p.getMessage());
                        ConnectGUI.loginResult.setForeground(Color.RED);
                        ConnectGUI.connect.setText("CONNECT");
                        ConnectGUI.connect.setEnabled(true);
                    } else {
                        ConnectGUI.loginResult.setText(p.getStatus() + " " + p.getMessage());
                        ConnectGUI.loginResult.setForeground(Color.RED);
                        ConnectGUI.connect.setText("CONNECT");
                        ConnectGUI.connect.setEnabled(true);
                    }
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
                ConnectGUI.frame.setTitle("Connect - Failed to connect - " + reason.getLocalizedMessage() + " - " + Main.VERSION);
            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {

            }
        });

        connect(ip,8008);
        if(isConnected()) sendObject(new JoinPacket(Main.VERSION,username, password, displayName)); else {ConnectGUI.connect.setEnabled(true);}
    }
}
