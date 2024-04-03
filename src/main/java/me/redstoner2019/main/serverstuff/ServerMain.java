package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.LoggerDump;
import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Game;
import me.redstoner2019.main.data.Player;
import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.main.data.packets.gamepackets.GameStartPacket;
import me.redstoner2019.main.data.packets.lobbypackets.*;
import me.redstoner2019.main.data.packets.loginpackets.DisconnectPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginSuccessPacket;
import me.redstoner2019.main.data.packets.loginpackets.Ping;
import me.redstoner2019.serverhandling.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServerMain extends Server {
    public static HashMap<String, Game> games = new HashMap<String, Game>();
    public static List<Player> players = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        LoggerDump.initialize();
        setClientConnectEvent(new ClientConnectEvent() {
            @Override
            public void connectEvent(ClientHandler handler) throws Exception {
                System.out.println("Client has connected " + handler.getSocket().getInetAddress());
                Player player = new Player();
                /*String username = null;
                String displayName = "";
                boolean isLoggedIn = false;
                String lobbyCreated = "";*/
                handler.startPacketListener(new PacketListener() {
                    @Override
                    public void packetRecievedEvent(Packet packet) {
                        if(!packet.getVersion().equals(Main.getVersion())){
                            System.out.println("Wrong Version " + packet.getVersion());
                            handler.disconnect();
                        }
                        if(packet instanceof LoginPacket p){
                            String username = p.getUsername();
                            String password = p.getPassword();
                            String displayName = "";
                            if(p.getCustomDisplayName() != null) displayName = p.getCustomDisplayName();

                            Userdata userdata = Userdata.read(username);
                            if(userdata == null){
                                handler.sendObject(new DisconnectPacket("Account doesnt exist"));
                                handler.disconnect();
                            } else {
                                if(!userdata.getPassword().equals(password)){
                                    handler.sendObject(new DisconnectPacket("Incorrect Password"));
                                    handler.disconnect();
                                } else {
                                    handler.sendObject(new LoginSuccessPacket());
                                    player.setDisplayName(displayName);
                                    player.setUsername(username);
                                    player.setLoggedIn(true);
                                    player.setHandler(handler);
                                    players.add(player);
                                }
                            }

                        }
                        if(packet instanceof CreateLobbyPacket){
                            Game game = Game.createGame();
                            game.setOwner(player);
                            game.addPlayer(player);
                            System.out.println(game.getGameCode());
                            player.setGameID(game.getGameCode());

                            games.put(game.getGameCode(),game);
                            //handler.sendObject(new LoginSuccessPacket());
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                        }
                        if(packet instanceof JoinLobbyPacket p){
                            Game game = games.getOrDefault(p.getID(),null);
                            if(game == null) {
                                return;
                            }
                            player.setGameID(game.getGameCode());
                            game.addPlayer(player);
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                        }
                        if(packet instanceof UpdateLobbyPacket p){
                            Game game = games.getOrDefault(player.getGameID(),null);
                            if(game.getOwner().equals(player)){
                                game.setCardsPerPlayer(p.getCardsPerPlayer());
                                game.setDecks(p.getDecks());
                                game.setStacking(p.isStacking());
                                game.setSevenSwap(p.isSevenSwap());
                                game.setJumpIn(p.isJumpIn());
                            }
                            System.out.println("Recieved");
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                        }
                        if(packet instanceof RequestLobbiesPacket){
                            Game game = games.getOrDefault(player.getGameID(), null);
                            if(game == null) return;
                            String[] lobbies = new String[games.keySet().size()];
                            Iterator<String> it = games.keySet().iterator();
                            int i = 0;
                            while (it.hasNext()){
                                String s = it.next();
                                lobbies[i] = s;
                                i++;
                            }
                            handler.sendObject(new LobbiesPacket(lobbies));
                        }
                        if(packet instanceof Ping){
                            handler.sendObject(packet);
                        }
                        if(packet instanceof GameStartPacket){
                            System.out.println("Starting gmae");
                            Game game = games.get(player.getGameID());
                            game.start();
                        }
                    }
                });
                while (handler.isConnected()){
                    Game game = games.getOrDefault(player.getGameID(),null);
                    if(game == null) continue;
                    //handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.getUsername().equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                    Thread.sleep(100);
                }
            }
        });
        setup(8008);
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Iterator<String> it = games.keySet().iterator();
                    while (it.hasNext()){
                        String s = it.next();
                        Game game = games.get(s);
                        if(game.getPlayers().isEmpty()){
                            games.remove(s);
                        }
                    }
                }

            }
        });
        serverThread.start();
        start();
    }
}
