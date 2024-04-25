package me.redstoner2019.uno.main.serverstuff;

import com.sun.management.OperatingSystemMXBean;
import me.redstoner2019.authserver.client.AuthenticatorServer;
import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.server.events.ClientConnectEvent;
import me.redstoner2019.server.events.ConnectionFailedEvent;
import me.redstoner2019.server.events.ConnectionSuccessEvent;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odserver.ClientHandler;
import me.redstoner2019.server.odserver.ODServer;
import me.redstoner2019.server.util.ConnectionProtocol;
import me.redstoner2019.server.util.Util;
import me.redstoner2019.uno.main.LoggerDump;
import me.redstoner2019.uno.main.Main;
import me.redstoner2019.uno.main.data.Game;
import me.redstoner2019.uno.main.data.Player;
import me.redstoner2019.uno.main.data.data.Userdata;
import me.redstoner2019.uno.main.data.guis.PerformanceProfiler;
import me.redstoner2019.uno.main.data.packets.gamepackets.*;
import me.redstoner2019.uno.main.data.packets.generalpackets.ProfilerUpdate;
import me.redstoner2019.uno.main.data.packets.lobbypackets.*;
import me.redstoner2019.uno.main.data.packets.loginpackets.*;
import me.redstoner2019.uno.main.data.packets.remoteconsole.InitializeConsoleClientPacket;
import me.redstoner2019.uno.main.data.packets.remoteconsole.RunCommandPacket;
import org.json.JSONObject;

//45.93.249.98

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;

import static me.redstoner2019.server.odserver.ODServer.*;

public class ServerMain extends ODServer {
    private static OperatingSystemMXBean osBean;
    public static HashMap<String, Game> games = new HashMap<String, Game>();
    public static List<Player> players = new ArrayList<>();
    public static int packetsSent = 0;
    public static int packetsrecieved = 0;
    public static List<ClientHandler> consoleClients = new ArrayList<>();
    public static AuthenticatorServer authenticatorServer = new AuthenticatorServer();
    public static void main(String[] args) throws Exception {
        boolean nogui = args.length > 0 && args[0].equals("nogui");
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        String latestVersion = Main.getLatestVersion();
        if(!latestVersion.equals(Main.getVersion())){
            System.err.println("Newer Version on github: " + latestVersion + " > " + Main.getVersion());
            System.err.println("Download at https://github.com/Redstoner-2019/UNO/releases/tag/"+latestVersion);
        }
        //LoggerDump.initialize();

        File serverFile = new File("server.properties");
        if(!serverFile.exists()) {
            serverFile.createNewFile();
            Util.writeStringToFile(Util.prettyJSON("{}"),serverFile);

        }
        JSONObject serverData = new JSONObject(Util.readFile(serverFile));

        if(!serverData.has("auth-server")){
            serverData.put("auth-server","45.93.249.98");
        }

        Util.writeStringToFile(Util.prettyJSON(serverData.toString()),serverFile);

        authenticatorServer.authenticationServerIp = serverData.getString("auth-server");
        authenticatorServer.setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception e) {
                Util.log("Couldnt connect to auth server");
                System.exit(404);
            }
        });
        authenticatorServer.setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {
                Util.log("Connection success");
            }
        });

        authenticatorServer.setup();

        setClientConnectEvent(new ClientConnectEvent() {
            @Override
            public void connectEvent(ClientHandler handler) throws Exception {
                final boolean[] consoleClient = {false};
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
                        try{
                            if(!((Packet)packet).getVersion().equals(Main.getVersion())){
                                handler.sendObject(new DisconnectPacket("Invalid Version. \nServer " + Main.getVersion() + "\nClient " + ((Packet) packet).getVersion(),401));
                                return;
                            }
                        }catch (Exception e){
                            handler.sendObject(new DisconnectPacket("Unable to read packet version",501));
                            return;
                        }

                        if(packet instanceof RunCommandPacket p){

                        }
                        if(packet instanceof ChatPacket p){
                            Calendar calendar = Calendar.getInstance();
                            for(Player pl : games.get(player.getGameID()).getPlayers()){
                                pl.getHandler().sendObject(new ChatPacket(String.format("%02d:%02d:%02d (%s) ",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), player.getGameID()) + player.getDisplayName() + ": " + p.getMessage()));
                                pl.getHandler().sendObject(new ActionPacket("Chat Recieved"));
                            }
                        }
                        if(packet instanceof InitializeConsoleClientPacket){
                            consoleClients.add(handler);
                            consoleClient[0] = true;
                        }
                        if(consoleClient[0]) return;
                        if(packet instanceof LoginPacket p){
                            if(player.isLoggedIn()) return;
                            System.out.println("Server " + Main.getVersion());
                            System.out.println("Client " + p.version);

                            JSONObject data = authenticatorServer.getTokenInfo(p.getToken());

                            if(data.get("available").equals("true")){
                                String username = data.getString("username");
                                String displayname = data.getString("displayname");
                                handler.sendObject(new LoginSuccessPacket());
                                player.setDisplayName(displayname);
                                player.setUsername(username);
                                player.setLoggedIn(true);
                                player.setHandler(handler);
                                players.add(player);
                                System.out.println("valid token");
                            } else {
                                handler.sendObject(new DisconnectPacket("Invalid session token",403));
                                System.out.println("Invalid token");
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
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn(),game.isChatEnabled()));
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
                            handler.sendObject(new LobbyInfoPacket(game.getGameCode(), player.equals(game.getOwner()),game.getPlayerHashMap(),game.getCardsPerPlayer(),game.getDecks(),game.isStacking(),game.isSevenSwap(),game.isJumpIn(),game.isChatEnabled()));
                        }
                        if(packet instanceof UpdateLobbyPacket p){
                            Game game = games.getOrDefault(player.getGameID(),null);
                            if(game != null && game.getOwner().equals(player)){
                                game.setCardsPerPlayer(p.getCardsPerPlayer());
                                game.setDecks(p.getDecks());
                                game.setStacking(p.isStacking());
                                game.setSevenSwap(p.isSevenSwap());
                                game.setJumpIn(p.isJumpIn());
                                game.setChatEnabled(p.isChat());
                            }
                            if(game != null && !game.isRunning()) {
                                for (Player pl : game.getPlayers()) {
                                    pl.getHandler().sendObject(new LobbyInfoPacket(game.getGameCode(), pl.equals(game.getOwner()), game.getPlayerHashMap(), game.getCardsPerPlayer(), game.getDecks(), game.isStacking(), game.isSevenSwap(), game.isJumpIn(), game.isChatEnabled()));
                                }
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
                            //TODO: ReAdd stats
                            //handler.sendObject(new StatsPacket(player.getUserdata().getGamesPlayed(),player.getUserdata().getGamesWon(),player.getUserdata().getPlus4Placed()));
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
        setup(8008, ConnectionProtocol.UDP);
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
                }
            }
        });
        //serverThread.start();
        start();
    }
    public static long bytesToMB(long memory){
        return memory / 1024 / 1024;
    }
}
