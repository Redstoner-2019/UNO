package me.redstoner2019.main.data;

import me.redstoner2019.main.data.packets.gamepackets.GameEndPacket;
import me.redstoner2019.main.data.packets.gamepackets.GameStartPacket;
import me.redstoner2019.main.data.packets.loginpackets.Ping;
import me.redstoner2019.serverhandling.Packet;

import java.util.*;

public class Game {
    private String gameCode;
    private List<Player> players = new ArrayList<>();
    private int cardsPerPlayer = 7;
    private int decks = 2;
    private boolean stacking = false;
    private boolean sevenSwap = false;
    private boolean jumpIn = false;
    private Player owner = null;
    private boolean running = false;
    public Queue<Packet> queue = new ArrayDeque<>();

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public HashMap<String, String> getPlayerHashMap(){
        HashMap<String, String> players = new HashMap<>();
        for(Player p : getPlayers()){
            players.put(p.getUsername(),p.getDisplayName());
        }
        return players;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Game(String code){
        this.gameCode = code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public void setCardsPerPlayer(int cardsPerPlayer) {
        this.cardsPerPlayer = cardsPerPlayer;
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public boolean isStacking() {
        return stacking;
    }

    public void setStacking(boolean stacking) {
        this.stacking = stacking;
    }

    public boolean isSevenSwap() {
        return sevenSwap;
    }

    public void setSevenSwap(boolean sevenSwap) {
        this.sevenSwap = sevenSwap;
    }

    public boolean isJumpIn() {
        return jumpIn;
    }

    public void setJumpIn(boolean jumpIn) {
        this.jumpIn = jumpIn;
    }
    public void addPlayer(Player p){
        if(!players.contains(p)) players.add(p);
    }

    public static Game createGame(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        String gameCode = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            gameCode+=chars[random.nextInt(chars.length)];
        }
        Game game = new Game(gameCode);
        return game;
    }
    public String getGameCode() {
        return gameCode;
    }
    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }
    public void start(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Game Start");
                running = true;
                for(Player p : players){
                    p.getHandler().sendObject(new GameStartPacket());
                    System.out.println("Send Game Start");
                }
                queue.offer(new Ping(-1));
                queue.offer(new Ping(0));
                queue.offer(new Ping(1));
                while (!queue.isEmpty()){
                    Ping p = (Ping) queue.poll();
                    System.out.println(p.getTime());
                }


                /**
                 * Game running
                 */
                System.out.println("Game end");
                for(Player p : players){
                    p.getHandler().sendObject(new GameEndPacket());
                }
                running = false;
            }
        });
        t.start();
    }
}
