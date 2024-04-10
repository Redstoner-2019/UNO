package me.redstoner2019.main.serverstuff;

import com.sun.management.OperatingSystemMXBean;
import me.redstoner2019.main.LoggerDump;
import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Game;
import me.redstoner2019.main.data.Player;
import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.main.data.guis.PerformanceProfiler;
import me.redstoner2019.main.data.packets.gamepackets.*;
import me.redstoner2019.main.data.packets.generalpackets.ProfilerUpdate;
import me.redstoner2019.main.data.packets.lobbypackets.*;
import me.redstoner2019.main.data.packets.loginpackets.*;
import me.redstoner2019.serverhandling.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServerMain extends Server {
    private static OperatingSystemMXBean osBean;
    public static HashMap<String, Game> games = new HashMap<String, Game>();
    public static List<Player> players = new ArrayList<>();
    public static int packetsSent = 0;
    public static int packetsrecieved = 0;
    public static void main(String[] args) throws Exception {
        boolean nogui = args.length > 0 && args[0].equals("nogui");
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        LoggerDump.initialize();
        setClientConnectEvent(new ClientConnectEvent() {
            @Override
            public void connectEvent(ClientHandler handler) throws Exception {
                System.out.println("Client has connected " + handler.getSocket().getInetAddress());
                Player player = new Player();
                handler.startPacketSender();
                handler.startPacketListener(new PacketListener() {
                    @Override
                    public void packetRecievedEvent(Object packet) {
                        if(packet == null) return;
                        if(!(packet instanceof Packet)) {
                            handler.sendObject("invalid packet bitch");
                            return;
                        }
                        if(!((Packet)packet).getVersion().equals(Main.getVersion())){
                            handler.sendObject(new DisconnectPacket("Invalid Version. \nServer " + Main.getVersion() + "\nClient " + ((Packet) packet).getVersion(),401));
                            return;
                        }
                        if(packet instanceof LoginPacket p){
                            if(player.isLoggedIn()) return;
                            System.out.println("Server " + Main.getVersion());
                            System.out.println("Client " + p.getVersion());
                            String username = p.getUsername();
                            String password = p.getPassword();
                            String displayName = "";
                            if(p.getCustomDisplayName() != null) displayName = p.getCustomDisplayName();

                            Userdata userdata = Userdata.read(username);
                            if(userdata == null){
                                System.out.println("Account doesnt exist");
                                handler.sendObject(new DisconnectPacket("Account doesnt exist",404));
                            } else {
                                if(!userdata.getPassword().equals(password)){
                                    System.out.println("Incorrect password");
                                    handler.sendObject(new DisconnectPacket("Incorrect Password",401));
                                } else {
                                    System.out.println("Login success");
                                    handler.sendObject(new LoginSuccessPacket());
                                    player.setDisplayName(displayName);
                                    player.setUsername(username);
                                    player.setLoggedIn(true);
                                    player.setHandler(handler);
                                    players.add(player);
                                }
                            }

                        }
                        if(packet instanceof CreateLobbyPacket p){
                            if(!player.getGameID().isEmpty()) return;
                            Game game;
                            if(p.getForceCode().length() > 5) p.setForceCode(p.getForceCode().substring(0,5));
                            if(p.getForceCode().length() < 4) p.setForceCode("");
                            if(games.containsKey(p.getForceCode())) {
                                handler.sendObject(new LobbyJoinResultPacket(401,"Lobby with this code already exists"));
                                return;
                            } else game = Game.createGame(p.getForceCode());
                            game.setOwner(player);
                            game.addPlayer(player);
                            System.out.println(game.getGameCode());
                            player.setGameID(game.getGameCode());

                            games.put(game.getGameCode(),game);
                            handler.sendObject(new LobbyJoinResultPacket(200,"Created Lobby"));
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                            String[] lobbies = new String[games.keySet().size()];
                            Iterator<String> it = games.keySet().iterator();
                            int i = 0;
                            while (it.hasNext()){
                                String s = it.next();
                                lobbies[i] = s;
                                i++;
                            }
                            broadcastMessage(new LobbiesPacket(lobbies));
                        }
                        if(packet instanceof JoinLobbyPacket p){
                            if(!player.getGameID().isEmpty()) return;
                            System.out.println("ID " + p.getID());
                            Game game = games.getOrDefault(p.getID(),null);
                            if(game == null) {
                                handler.sendObject(new LobbyJoinResultPacket(404,"Lobby not found"));
                                return;
                            }
                            if(game.isRunning()){
                                handler.sendObject(new LobbyJoinResultPacket(405,"Game is already running"));
                                return;
                            }
                            player.setGameID(game.getGameCode());
                            game.addPlayer(player);
                            handler.sendObject(new LobbyJoinResultPacket(200,"Joined Lobby"));
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn()));
                        }
                        if(packet instanceof UpdateLobbyPacket p){
                            Game game = games.getOrDefault(player.getGameID(),null);
                            if(game != null && game.getOwner().equals(player)){
                                game.setCardsPerPlayer(p.getCardsPerPlayer());
                                game.setDecks(p.getDecks());
                                game.setStacking(p.isStacking());
                                game.setSevenSwap(p.isSevenSwap());
                                game.setJumpIn(p.isJumpIn());
                            }
                            if(game != null && !game.isRunning()) {
                                handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()), game.getPlayerHashMap(), game.getCardsPerPlayer(), game.getDecks(), game.isStacking(), game.isSevenSwap(), game.isJumpIn()));
                            }
                        }
                        if(packet instanceof RequestLobbiesPacket){
                            String[] lobbies = new String[games.keySet().size()];
                            Iterator<String> it = games.keySet().iterator();
                            int i = 0;
                            while (it.hasNext()){
                                String s = it.next();
                                lobbies[i] = s;
                                i++;
                            }
                            handler.sendObject(new LobbiesPacket(lobbies));
                            handler.sendObject(new StatsPacket(player.getUserdata().getGamesPlayed(),player.getUserdata().getGamesWon(),player.getUserdata().getPlus4Placed()));
                        }
                        if(packet instanceof Ping){
                            handler.sendObject(packet);
                            long freeMemory = Runtime.getRuntime().freeMemory();
                            long totalMemory = Runtime.getRuntime().totalMemory();
                            long usedMemory = totalMemory - freeMemory;
                            double cpuUsage = osBean.getProcessCpuLoad() * 100;
                            handler.sendObject(new ProfilerUpdate((int) bytesToMB(usedMemory),cpuUsage));
                        }
                        if(packet instanceof CreateAccountPacket p){
                            Userdata userdata = Userdata.read(p.getUsername());
                            if(userdata != null) {
                                handler.sendObject(new DisconnectPacket("Username already exists"));
                                return;
                            }
                            userdata = new Userdata();
                            userdata.setGamesPlayed(0);
                            userdata.setGamesWon(0);
                            userdata.setUsername(p.getUsername());
                            userdata.setPassword(p.getPassword());
                            userdata.setDisplayName(p.getDisplayname());
                            userdata.setPlus4Placed(0);
                            Userdata.write(userdata);
                            handler.sendObject(new DisconnectPacket("Account created"));
                        }
                        if(packet instanceof GameStartPacket){
                            System.out.println("Starting gmae");
                            Game game = games.get(player.getGameID());
                            game.start();
                        }
                        if(packet instanceof DrawCardPacket p){
                            if(!games.containsKey(player.getGameID())) {
                                System.out.println("Couldnt find lobby " + player.getGameID());
                                player.reset();
                                player.setGameID("");
                                handler.sendObject(new LobbyJoinResultPacket(404,"Couldnt find lobby"));
                                return;
                            }
                            games.get(player.getGameID()).queue.add(new GamePacket(player,p));
                        }
                        if(packet instanceof LeaveLobbyPacket p){
                            Game game = games.get(player.getGameID());
                            game.getPlayers().remove(player);
                            System.out.println("Player " + player.getUsername() + " left lobby " + player.getGameID());
                            player.reset();
                            player.setGameID("");
                        }
                        if(packet instanceof SkipTurnPacket p){
                            if(!games.containsKey(player.getGameID())) {
                                System.out.println("Couldnt find lobby " + player.getGameID());
                                player.reset();
                                player.setGameID("");
                                handler.sendObject(new LobbyJoinResultPacket(404,"Couldnt find lobby"));
                                return;
                            }
                            games.get(player.getGameID()).queue.add(new GamePacket(player,p));
                        }
                        if(packet instanceof UNOPacket p){
                            if(!games.containsKey(player.getGameID())) {
                                System.out.println("Couldnt find lobby " + player.getGameID());
                                player.reset();
                                player.setGameID("");
                                handler.sendObject(new LobbyJoinResultPacket(404,"Couldnt find lobby"));
                                return;
                            }
                            games.get(player.getGameID()).queue.add(new GamePacket(player,p));
                        }
                        if(packet instanceof PlaceCardPacket p){
                            if(!games.containsKey(player.getGameID())) {
                                System.out.println("Couldnt find lobby " + player.getGameID());
                                player.reset();
                                player.setGameID("");
                                handler.sendObject(new LobbyJoinResultPacket(404,"Couldnt find lobby"));
                                return;
                            }
                            games.get(player.getGameID()).queue.add(new GamePacket(player,p));
                        }
                        if(packet instanceof ProfilerUpdate){

                        }
                    }
                });
                while (!handler.getSocket().isConnected()){
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
                try {
                    if(!nogui) {
                        PerformanceProfiler performanceProfiler = new PerformanceProfiler("Server");
                        performanceProfiler.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true){

                    long freeMemory = Runtime.getRuntime().freeMemory();
                    long totalMemory = Runtime.getRuntime().totalMemory();
                    long usedMemory = totalMemory - freeMemory;

                    if(bytesToMB(usedMemory) > 50) {
                        System.gc();
                    }

                    Iterator<String> it = games.keySet().iterator();
                    while (it.hasNext()){
                        String s = it.next();
                        Game game = games.get(s);
                        if(game.getPlayers().isEmpty()){
                            System.out.println("Removing lobby " + s);
                            games.remove(s);
                        }
                    }
                    //broadcastMessage(new LobbiesPacket(lobbies));
                }

            }
        });
        serverThread.start();
        start();
    }
    public static long bytesToMB(long memory){
        return memory / 1024 / 1024;
    }
}
