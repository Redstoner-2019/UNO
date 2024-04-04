package me.redstoner2019.main.data;

import me.redstoner2019.main.data.packets.gamepackets.*;
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
    public Queue<GamePacket> queue = new ArrayDeque<>();

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
                /**
                 * Game running
                 */
                boolean gameRunning = true;
                Card lastCardPlaced = null;
                Queue<Card> DECK = new ArrayDeque<>();
                for (int i = 0; i < decks; i++) {
                    for(Card c : shuffle(Card.getDECK())){
                        DECK.add(c);
                    }
                }
                lastCardPlaced = DECK.poll();
                DECK.add(lastCardPlaced);
                for(Player p : players){
                    for (int i = 0; i < cardsPerPlayer; i++) {
                        p.addCard(DECK.poll());
                    }
                }
                while (gameRunning){
                    while (!queue.isEmpty()){
                        GamePacket gamePacket = queue.poll();
                        Player player = gamePacket.getPlayer();
                        Packet packet = gamePacket.getPacket();
                        /**
                         * TODO: Manage Packets
                         */
                        if(packet instanceof DrawCardPacket p){
                            
                        }
                        if(packet instanceof SkipTurnPacket p){
                            nextPlayer();
                        }
                        if(packet instanceof UNOPacket p){

                        }
                        if(packet instanceof PlaceCardPacket p){
                            nextPlayer();
                        }
                    }
                    List<String> nextPlayers = new ArrayList<>();
                    for(Player p : players){
                        nextPlayers.add(p.getDisplayName());
                    }
                    int turn = 1;
                    Iterator<Player> playerIterator =  players.iterator();
                    while (playerIterator.hasNext()){
                        Player p = playerIterator.next();
                        if(!p.getHandler().isConnected()) {
                            if(players.size() == 1){
                                gameRunning = false;
                                break;
                            }
                            players.remove(p);
                            continue;
                        }
                        if(players.isEmpty()){
                            gameRunning = false;
                            break;
                        }
                        boolean isTurn = players.get(0).equals(p);
                        boolean canSkip = isTurn;
                        boolean canDraw = isTurn;
                        boolean canUNO = false;

                        p.getHandler().sendObject(new GameDataPacket(canSkip, canDraw, canUNO, isTurn, nextPlayers, lastCardPlaced, p.getCards()));
                        if(p.getCards().isEmpty()){
                            gameRunning = false;
                            System.out.println(p.getDisplayName() + " has won");
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                /**
                 * Game ended
                 */
                System.out.println("Game end");
                for(Player p : players){
                    p.getHandler().sendObject(new GameEndPacket());
                    p.getCards().clear();
                }
                running = false;
            }
        });
        t.start();
    }
    public static List<Card> shuffle(List<Card> list){
        List<Card> listShuffled = new ArrayList<>();
        Random random = new Random();
        for(Card o : list){
            if(listShuffled.isEmpty()){
                listShuffled.add(o);
            } else {
                listShuffled.add(random.nextInt(listShuffled.size()),o);
            }
        }
        return listShuffled;
    }
    private void nextPlayer(){
        Player tempPlayer = players.get(0);
        players.remove(0);
        players.add(tempPlayer);
    }
}
