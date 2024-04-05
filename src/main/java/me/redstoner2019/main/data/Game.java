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
                if(running) return;
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
                while (lastCardPlaced == null || lastCardPlaced.getColor().equals(CardColor.SPECIAL)){
                    lastCardPlaced = DECK.poll();
                    DECK.add(lastCardPlaced);
                }
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
                        boolean isTurn = players.get(0).equals(player);
                        /**
                         * TODO: Manage Packets
                         */
                        if(packet instanceof DrawCardPacket p){
                            if(!isTurn) continue;
                            if(!player.isCanDraw()) continue;
                            if(p.getAmount() == 1){
                                player.setCanSkip(true);
                                player.setCanDraw(false);
                            }
                            for (int i = 0; i < p.getAmount(); i++) {
                                Card card = DECK.poll();
                                player.addCard(card);
                                System.out.println(player.getDisplayName() + " has drawn " + card);
                                if(DECK.size() <= 10){
                                    DECK.addAll(shuffle(Card.getDECK()));
                                }
                            }
                        }
                        if(packet instanceof SkipTurnPacket p){
                            if(!isTurn) continue;
                            if(!player.isCanSkip()) continue;
                            System.out.println(player.getDisplayName() + " Skipped their turn");
                            player.setCanSkip(false);
                            player.setCanDraw(false);
                            nextPlayer();
                        }
                        if(packet instanceof UNOPacket p){
                            if(!isTurn) continue;
                            System.out.println(player.getDisplayName() + ": UNO!");
                            player.setUNO(true);
                            player.setCanUNO(false);
                        }
                        if(packet instanceof PlaceCardPacket p){
                            if(!isTurn) continue;
                            System.out.println(player.getDisplayName() + " placed card " + p.getCard());
                            if(lastCardPlaced.canBePlayed(p.getCard())){
                                DECK.add(lastCardPlaced);
                                lastCardPlaced = p.getCard();
                                player.removeCard(p.getCard());
                                if(lastCardPlaced.getOverrideColor() == null) lastCardPlaced.setOverrideColor(CardColor.RED);
                                if(lastCardPlaced.getNum().equals(CardType.PLUS_4)){
                                    if(!(players.size() > 1)) {
                                        System.out.println("Couldnt draw");
                                        continue;
                                    }
                                    for (int i = 0; i < 4; i++) {
                                        Card card = DECK.poll();
                                        players.get(1).addCard(card);
                                        System.out.println(players.get(1).getDisplayName() + " has drawn " + card);
                                    }
                                }
                                if(lastCardPlaced.getNum().equals(CardType.DRAW)){
                                    if(!(players.size() > 1)) {
                                        System.out.println("Couldnt draw");
                                        continue;
                                    }
                                    for (int i = 0; i < 2; i++) {
                                        Card card = DECK.poll();
                                        players.get(1).addCard(card);
                                        System.out.println(players.get(1).getDisplayName() + " has drawn " + card);
                                    }
                                }
                                if(lastCardPlaced.getNum().equals(CardType.SKIP)){
                                    nextPlayer();
                                }
                                nextPlayer();
                            }
                        }
                    }
                    List<String> nextPlayers = new ArrayList<>();
                    for(Player p : players){
                        nextPlayers.add(p.getDisplayName());
                    }
                    List<Player> toRemove = new ArrayList<>();
                    for (Player p : players){
                        if(!p.getHandler().isConnected()) {
                            if(players.size() == 1){
                                gameRunning = false;
                                break;
                            }
                            toRemove.add(p);
                            continue;
                        }
                        if(players.isEmpty()){
                            gameRunning = false;
                            break;
                        }
                        boolean isTurn = players.get(0).equals(p);
                        boolean canSkip = isTurn && p.isCanSkip();
                        boolean canDraw = isTurn && p.isCanDraw();
                        boolean canUNO = isTurn && p.isCanUNO();

                        p.getHandler().sendObject(new GameDataPacket(canSkip, canDraw, canUNO, isTurn, nextPlayers, lastCardPlaced, List.copyOf(p.getCards())));
                        if(p.getCards().isEmpty()){
                            gameRunning = false;
                            System.out.println(p.getDisplayName() + " has won");
                        }
                    }
                    players.removeAll(toRemove);
                    try {
                        Thread.sleep(10);
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
        tempPlayer.setCanDraw(false);
        tempPlayer.setCanSkip(false);
        tempPlayer.setCanUNO(false);
        players.remove(0);
        players.add(tempPlayer);

        players.get(0).setCanUNO(true);
        players.get(0).setCanSkip(false);
        players.get(0).setCanDraw(true);
    }
}
